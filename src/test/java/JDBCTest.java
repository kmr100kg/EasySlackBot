import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import db.DBInfo;
import db.JDBC;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class JDBCTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @BeforeClass
    public static void initialize() {
        String createTable = "CREATE TABLE test_entity(id int, name varchar(255), unique_number long, active boolean, expiration_date timestamp, last_login datetime)";
        dml(createTable);
        // findTest
        String createRow1 = "INSERT INTO test_entity VALUES(1, 'テスト一郎', 1111111111, true, '2000-01-01 01:01:01', '1999-12-31 01:01:01')";
        // updateTest
        String createRow2 = "INSERT INTO test_entity VALUES(2, 'テスト花子', 2222222222, false, '2000-01-01 01:01:01', now())";
        // updateListTest
        String createRow3 = "INSERT INTO test_entity VALUES(3, 'テスト四郎', 3333333333, false, now(), now())";
        String createRow4 = "INSERT INTO test_entity VALUES(4, 'テスト五郎', 4444444444, false, now(), now())";
        dml(createRow1);
        dml(createRow2);
        dml(createRow3);
        dml(createRow4);
        System.out.println("initialize completed! test start...");
    }

    @AfterClass
    public static void destroy() {
        System.out.println("done!");
    }

    private static JDBC<TestEntity> getConnection() {
        return new JDBC<>();
    }

    private static void dml(String sql) {
        try (Connection connection = getConnection().connect();
             PreparedStatement psmt = connection.prepareStatement(sql)) {
            psmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void constructorTest() {
        Config config = ConfigFactory.load();
        JDBC<TestEntity> jdbc = new JDBC<>();
        Class<? extends JDBC> clazz = jdbc.getClass();
        try {
            Field field = clazz.getDeclaredField("dbInfo");
            field.setAccessible(true);
            DBInfo dbInfo = (DBInfo) field.get(jdbc);
            assertEquals(config.getString("db.driver"), dbInfo.getDriver());
            assertEquals(config.getString("db.host"), dbInfo.getHost());
            assertEquals(config.getString("db.user"), dbInfo.getUser());
            assertEquals(config.getString("db.password"), dbInfo.getPassword());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void notFoundDriverTest() {
        DBInfo dbInfo = new DBInfo();
        JDBC<TestEntity> jdbc = new JDBC<>(dbInfo);
        try {
            jdbc.connect();
            fail();
        } catch (SQLException e) {
            assertEquals("ドライバが見つかりませんでした", e.getMessage());
        }
    }

    @Test
    public void findTest() {
        JDBC<TestEntity> jdbc = getConnection();
        TestEntity te = jdbc.find("SELECT * FROM test_entity", TestEntity.class).get(0);
        assertEquals(Integer.valueOf("1"), te.getId());
        assertEquals("テスト一郎", te.getName());
        assertEquals(Long.valueOf("1111111111"), te.getUniqueNumber());
        assertEquals(true, te.getActive());
        assertEquals("2000-01-01 01:01:01.0", te.getExpirationDate().toString());
    }

    @Test
    public void findErrorTest() {
        JDBC<TestEntity> jdbc = getConnection();
        List<TestEntity> results = jdbc.find("SELECT * FROM test_hoge", TestEntity.class);
        assertTrue(results.isEmpty());
    }

    @Test
    public void insertTest() {
        JDBC<TestEntity> jdbc = getConnection();
        int count = jdbc.insert("INSERT INTO test_entity VALUES(5, 'テスト五郎', 5555555555, true, '2020-01-01 01:01:01', ?)", "2018-12-31 01:01:01");
        TestEntity te = jdbc.find("SELECT * FROM test_entity WHERE id = 5", TestEntity.class).get(0);
        assertEquals(count, 1);
        assertEquals(Integer.valueOf("5"), te.getId());
        assertEquals("テスト五郎", te.getName());
        assertEquals(Long.valueOf("5555555555"), te.getUniqueNumber());
        assertEquals(true, te.getActive());
        assertEquals("2020-01-01 01:01:01.0", te.getExpirationDate().toString());
        assertEquals("2018-12-31 01:01:01.0", te.getLastLogin().toString());
    }

    @Test
    public void insertErrorTest() {
        JDBC<TestEntity> jdbc = getConnection();
        int count = jdbc.insert("INSERT INTO test_entity VALUES(7, 'テスト五郎', ?, true, '2020-01-01 01:01:01', '2020-01-01 01:01:01')", "aaa");
        assertEquals(count, 0);
        List<TestEntity> te = jdbc.find("SELECT * FROM test_entity WHERE id = 7", TestEntity.class);
        assertTrue(te.isEmpty());
    }

    @Test
    public void updateTest() {
        JDBC<TestEntity> jdbc = getConnection();
        int count = jdbc.update("UPDATE test_entity SET name = 'テスト二郎', active = true, expiration_date = '3000-01-01 01:01:01' WHERE id = 2");
        assertEquals(count, 1);
        TestEntity te = jdbc.find("SELECT * FROM test_entity WHERE id = 2", TestEntity.class).get(0);
        assertEquals(Integer.valueOf("2"), te.getId());
        assertEquals("テスト二郎", te.getName());
        assertEquals(true, te.getActive());
        assertEquals("3000-01-01 01:01:01.0", te.getExpirationDate().toString());
    }

    @Test
    public void batchUpdateTest() {
        JDBC<TestEntity> jdbc = getConnection();
        List<String> sqls = Arrays.asList(
            "UPDATE test_entity SET active = true WHERE id = ?",
            "UPDATE test_entity SET active = true WHERE id = ?");
        List<Object[]> conditions = Arrays.asList(new Object[]{3}, new Object[]{4});
        List<Integer> counts = jdbc.batchUpdate(sqls, conditions);
        counts.forEach(r -> assertEquals(r.intValue(), 1));
        TestEntity te1 = jdbc.find("SELECT * FROM test_entity WHERE id = 3", TestEntity.class).get(0);
        TestEntity te2 = jdbc.find("SELECT * FROM test_entity WHERE id = 4", TestEntity.class).get(0);
        assertEquals(true, te1.getActive());
        assertEquals(true, te2.getActive());
    }

    @Test
    public void batchUpdateErrorTest1() {
        JDBC<TestEntity> jdbc = getConnection();
        List<String> sqls = Arrays.asList(
                "UPDATE test_entity SET unique_number = 8888888888 WHERE id = ?",
                "UPDATE test_entity SET unique_number = '' WHERE id = ?");
        List<Object[]> conditions = Arrays.asList(new Object[]{3}, new Object[]{4});
        List<Integer> counts = jdbc.batchUpdate(sqls, conditions);
        assertTrue(counts.isEmpty());
        TestEntity te1 = jdbc.find("SELECT * FROM test_entity WHERE id = 3", TestEntity.class).get(0);
        TestEntity te2 = jdbc.find("SELECT * FROM test_entity WHERE id = 4", TestEntity.class).get(0);
        assertEquals(Long.valueOf("3333333333"), te1.getUniqueNumber());
        assertEquals(Long.valueOf("4444444444"), te2.getUniqueNumber());
    }

    @Test
    public void batchUpdateErrorTest2() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("SQLと条件の数があっていません");
        JDBC<TestEntity> jdbc = getConnection();
        List<String> sqls = Arrays.asList(
                "UPDATE test_entity SET unique_number = 8888888888 WHERE id = ?",
                "UPDATE test_entity SET unique_number = '' WHERE id = ?");
        List<Object[]> conditions = Arrays.asList(new Object[]{3}, new Object[]{4}, new Object[]{5});
        jdbc.batchUpdate(sqls, conditions);
    }
}
