package db;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import log.LogHelper;
import log.LogLevel;
import org.h2.tools.RunScript;
import org.modelmapper.ModelMapper;
import utility.common.Pair;
import utility.common.XUtils;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * データベースアクセスを行うクラス。
 * @param <T> 結果セットがマッピングされるクラスの型
 */
public class JDBC<T> {

    private DBInfo dbInfo;

    public JDBC() {
        Config config = ConfigFactory.load();
        DBInfo dbInfo = new DBInfo();
        dbInfo.setDriver(config.getString("db.driver"));
        dbInfo.setHost(config.getString("db.host"));
        dbInfo.setUser(config.getString("db.user"));
        dbInfo.setPassword(config.getString("db.password"));
        this.dbInfo = dbInfo;
    }

    public JDBC(DBInfo dbInfo) {
        this.dbInfo = dbInfo;
    }

    /**
     * データベースへ接続する。
     *
     * @return コネクション
     * @throws SQLException 接続に失敗した場合送出される
     */
    public Connection connect() throws SQLException {
        try {
            Class.forName(dbInfo.getDriver());
        } catch (Exception e) {
            String message = "ドライバが見つかりませんでした";
            LogHelper.write(LogLevel.ERROR, message, e);
            throw new SQLException(message);
        }
        return DriverManager.getConnection(dbInfo.getHost(), dbInfo.getUser(), dbInfo.getPassword());
    }

    /**
     * H2データベースの初期化を行う。
     *
     */
    public void initH2DB() throws Exception {
        if (dbInfo.getHost().startsWith("jdbc:h2:")) {
            Path path = Paths.get("./init_db.txt");
            List<String> line = Files.readAllLines(path);
            if (line.isEmpty() || !dbInfo.getHost().equals(line.get(0))) {
                RunScript.execute(connect(), new FileReader("./init_h2db.sql"));
                Files.write(path, dbInfo.getHost().getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
                LogHelper.write(LogLevel.INFO, "DB初期化完了");
            } else {
                LogHelper.write(LogLevel.INFO, "すでに初期化されてるようなのでやめときました");
            }
        } else {
            LogHelper.write(LogLevel.INFO, "H2DBを使っていないようなのでやめときました");
        }
    }

    /**
     * SELECTを行う。
     *
     * @param sql SQL
     * @param clazz 結果セットをマッピングするクラス
     * @return SELECT結果
     */
    public List<T> find(String sql, Class<T> clazz) {
        try (Connection connection = connect();
             Statement smt = connection.createStatement();
             ResultSet rs = smt.executeQuery(sql)) {
            return toList(rs, clazz);
        } catch (SQLException e) {
            LogHelper.write(LogLevel.ERROR, "SQLエラー", e);
            return new ArrayList<>();
        }
    }

    /**
     * 指定したクラスへ結果セットをマッピングする。
     *
     * @param rs 結果セット
     * @param clazz マッピングするクラス
     * @return マッピングされたクラス
     * @throws SQLException マッピングに失敗した場合送出される
     */
    public List<T> toList(ResultSet rs, Class<T> clazz) throws SQLException {
        ModelMapper mapper = new ModelMapper();
        return getColumnData(rs).stream().map(r -> mapper.map(r, clazz)).collect(Collectors.toList());
    }

    /**
     * INSERTを行う。
     * <br>
     * 失敗した場合はロールバックする。
     *
     * @param sql SQL
     * @param conditions INパラメータにバインドするオブジェクト
     * @return INSERT件数
     */
    public int insert(String sql, Object... conditions) {
        return statement(sql, conditions);
    }

    /**
     * 一括更新を行う。a\
     * <br>
     * ひとつでも失敗した場合は全てロールバックする。
     *
     * @param sql SQL
     * @param conditions INパラメータにバインドするオブジェクト
     * @return SQLごとの実行件数
     */
    public List<Integer> batchUpdate(List<String> sql, List<Object[]> conditions) {
        if (sql.size() != conditions.size()) {
            RuntimeException e = new IllegalArgumentException("SQLと条件の数があっていません");
            LogHelper.write(LogLevel.ERROR, e);
            throw e;
        }
        List<Pair> zip = IntStream.range(0, sql.size())
            .mapToObj(i -> new Pair<String, Object>(sql.get(i), conditions.get(i)))
            .collect(Collectors.toList());

        List<Integer> updateCounts = new ArrayList<>();
        try (Connection connection = connect()) {
            connection.setAutoCommit(false);
            boolean allSuccess = true;
            for (Pair p : zip) {
                try (PreparedStatement psmt = connection.prepareStatement((String) p.left())) {
                    int i = 1;
                    for (Object o : (Object[]) p.right()) {
                        psmt.setObject(i, o);
                        i++;
                    }
                    updateCounts.add(psmt.executeUpdate());
                } catch (Exception e) {
                    LogHelper.write(LogLevel.ERROR, "SQLエラー", e);
                    allSuccess = false;
                }
            }
            if (allSuccess) {
                connection.commit();
            } else {
                connection.rollback();
                updateCounts.clear();
            }
            return updateCounts;
        } catch (Exception e) {
            LogHelper.write(LogLevel.ERROR, "SQLエラー", e);
            return updateCounts;
        }
    }

    /**
     * UPDATEを行う。
     * <br>
     * 失敗した場合はロールバックする。
     *
     * @param sql SQL
     * @param conditions INパラメータにバインドするオブジェクト
     * @return 更新件数
     */
    public int update(String sql, Object... conditions) {
        return statement(sql, conditions);
    }

    private int statement(String sql, Object... conditions) {
        try (Connection connection = connect()) {
            try (PreparedStatement psmt = connection.prepareStatement(sql)) {
                connection.setAutoCommit(false);
                int index = 1;
                for (Object o : conditions) {
                    psmt.setObject(index, o);
                    index++;
                }
                int result = psmt.executeUpdate();
                connection.commit();
                return result;
            } catch (Exception e) {
                LogHelper.write(LogLevel.ERROR, "SQLエラー", e);
                connection.rollback();
                return 0;
            }
        } catch (Exception e) {
            LogHelper.write(LogLevel.ERROR, "SQLエラー", e);
            return 0;
        }
    }

    private List<Map<String, Object>> getColumnData(ResultSet rs) throws SQLException {
        List<Map<String, Object>> columnDataList = new ArrayList<>();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        String columnName;
        while (rs.next()) {
            Map<String, Object> fieldDataMap = new LinkedHashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                columnName = rsmd.getColumnName(i);
                fieldDataMap.put(XUtils.toUpperCaseAfterDelimiter(columnName, "_"), rs.getObject(columnName));
            }
            columnDataList.add(fieldDataMap);
        }
        return columnDataList;
    }
}
