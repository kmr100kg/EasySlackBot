package utility.slack;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import entity.slack.FileOption;
import entity.slack.Files;
import entity.slack.SlackFileInfo;
import log.LogHelper;
import log.LogLevel;
import okhttp3.*;
import org.jsoup.Jsoup;
import utility.common.DateUtil;
import utility.common.Pair;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Slackのファイル操作を行うユーティリティクラス。
 * <br>
 * 【注意事項】<br>
 * ①フリープランでのみ正常動作する。<br>
 * ②内部でコールしているSlack Files APIは50件/分までしかリクエストを許容しないため、
 * 上限を超えるメソッド呼び出しを行った場合はエラーになる。<br>
 * メソッドのjavadocにTier3と書かれている場合は上記考慮が必要になる。
 * (https://api.slack.com/docs/rate-limits)
 * <br><br>
 * 【その他】<br>
 * 各メソッドのjavadocに以下の注釈を記載している。<br>
 * ※1<br>
 * 意味：Tier3制約によって5000件を超えるファイルは一度に取得できないことを示す。<br>
 * ※2<br>
 * 意味：SlackAPIのPermission設定でAdministratorを許可している必要がある。<br>
 *
 */
public class SlackFileUtil {

    private static final Config config = ConfigFactory.load();
    private static final String SLACK_TOKEN_KEY = "token";
    private static final String SLACK_TOKEN_VALUE = config.getString("slack.api.token");
    private static final String SLACK_FILE_LIST = "https://slack.com/api/files.list";
    private static final String SLACK_FILE_UPLOAD = "https://slack.com/api/files.upload";
    private static final String SLACK_FILE_DELETE = "https://slack.com/api/files.delete";
    private static final String SLACK_FILE_SHARED_PUBLIC = "https://slack.com/api/files.sharedPublicURL";
    private static final String SLACK_FILE_REVOKE_PUBLIC = "https://slack.com/api/files.revokePublicURL";
    private static final long SLACK_FREE_PLAN_STORAGE_SIZE = 5000000000L;

    private SlackFileUtil() {}

    /**
     * 全パブリックファイルのファイル情報を取得する。(Tier3)(※1)
     *
     * @return ファイル情報
     */
    public static List<Files> getAllFiles() {
        return getAllSlackFileInfo().stream().flatMap(s -> s.getFiles().stream()).collect(Collectors.toList());
    }

    /**
     * 全パブリックファイルのファイル情報を取得する。(Tier3)(※1)
     *
     * @param conditions 検索条件
     * @return 全ファイル情報
     */
    public static List<Files> getAllFiles(Map<String, String> conditions) {
        return getAllSlackFileInfo(conditions).stream().flatMap(s -> s.getFiles().stream()).collect(Collectors.toList());
    }

    /**
     * 全パブリックファイルのファイル情報を取得する。(Tier3)(※1)
     *
     * @return 全ファイル情報
     */
    public static List<SlackFileInfo> getAllSlackFileInfo() {
        return getAllSlackFileInfo(new HashMap<>());
    }

    /**
     * 全ファイルのファイル情報（ページング情報なども含む）を取得する。(Tier3)(※1)
     *
     * @param conditions 検索条件
     * @return 全ファイル情報
     */
    public static List<SlackFileInfo> getAllSlackFileInfo(Map<String, String> conditions) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        HttpUrl.Builder httpUrlBuilder = Objects.requireNonNull(HttpUrl.parse(SLACK_FILE_LIST)).newBuilder();
        conditions.put(SLACK_TOKEN_KEY, SLACK_TOKEN_VALUE);
        conditions.forEach(httpUrlBuilder::addQueryParameter);

        Request.Builder requestBuilder = new Request.Builder();
        Response response = null;
        List<SlackFileInfo> slackFileInfoList = new ArrayList<>();
        try {
            response = client.newCall(requestBuilder.url(httpUrlBuilder.build()).build()).execute();
            ObjectMapper mapper = new ObjectMapper();
            SlackFileInfo slackFileInfo = mapper.readValue(Objects.requireNonNull(response.body()).string(), SlackFileInfo.class);
            slackFileInfoList.add(slackFileInfo);

            Integer totalPageNum = slackFileInfo.getPaging().getPages();
            Integer currentPageNum = slackFileInfo.getPaging().getPage();
            if (totalPageNum == null || totalPageNum.intValue() == currentPageNum.intValue()) {
                return slackFileInfoList;
            }

            for (int i = currentPageNum + 1; i <= totalPageNum; i++) {
                httpUrlBuilder.addQueryParameter("page", String.valueOf(i));
                response = client.newCall(requestBuilder.url(httpUrlBuilder.build()).build()).execute();
                slackFileInfo = mapper.readValue(Objects.requireNonNull(response.body()).string(), SlackFileInfo.class);
                slackFileInfoList.add(slackFileInfo);
            }
            return slackFileInfoList;
        } catch (IOException e) {
            LogHelper.write(LogLevel.ERROR, e);
            return slackFileInfoList;
        } finally {
            if (response != null && response.body() != null) {
                response.body().close();
            }
        }
    }

    /**
     * パブリックファイルを削除する。(Tier3)
     *
     * @param fileId ファイルIDtryToUpdateAlternationNumber
     * @return 削除できたかどうか
     */
    public static boolean delete(String fileId) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        RequestBody requestBody = new FormBody.Builder()
                .add(SLACK_TOKEN_KEY, SLACK_TOKEN_VALUE)
                .add("file", fileId).build();
        Request request = new Request.Builder().url(SLACK_FILE_DELETE).post(requestBody).build();
        try (Response response = client.newCall(request).execute()) {
            String responseJson = Objects.requireNonNull(response.body()).string();
            boolean result = responseJson.contains("\"ok\":true");
            if (result) {
                LogHelper.write(LogLevel.INFO, "削除に成功しました FileId=" + fileId);
            } else {
                LogHelper.write(LogLevel.INFO, "削除に失敗しました FileId=" + fileId);
                LogHelper.write(LogLevel.INFO, "\"" + responseJson + "\"");
            }
            return result;
        } catch (IOException e) {
            LogHelper.write(LogLevel.ERROR, e);
            return false;
        }
    }

    /**
     * 指定したファイル情報のサイズ（バイト）を取得する。
     * @param slackFileInfo ファイル情報
     * @return ファイルサイズ
     */
    public static long getAllSize(SlackFileInfo slackFileInfo) {
        return getAllSize(Collections.singletonList(slackFileInfo));
    }

    /**
     * 指定したファイル情報のサイズ（バイト）を取得する。
     * @param slackFileInfoList ファイル情報
     * @return ファイルサイズ
     */
    public static long getAllSize(List<SlackFileInfo> slackFileInfoList) {
       return slackFileInfoList.stream().mapToLong(s -> s.getFiles().stream().mapToLong(Files::getSize).sum()).sum();
    }

    /**
     * 指定したファイル情報のサイズ（バイト）を取得する。
     * @param files ファイル情報
     * @return フィルサイズ
     */
    public static long getAllSizeByFiles(List<Files> files) {
        return files.stream().mapToLong(Files::getSize).sum();
    }

    /**
     * Slackストレージの空き容量（バイト）を返す。(Tier3)
     * @return 空き容量
     */
    public static long getFreeSpace() {
        long used = getAllSize(getAllSlackFileInfo());
        return SLACK_FREE_PLAN_STORAGE_SIZE - used;
    }

    /**
     * ファイル情報を古い順にソートして返す。
     * @param files ファイル情報
     * @return 古い順のファイル情報
     */
    public static List<Files> sortedOld(List<Files> files) {
        return files.stream()
                .sorted(Comparator.comparingLong(Files::getCreated))
                .collect(Collectors.toList());
    }

    /**
     * ファイル情報を新しい順にソートして返す。
     * @param files ファイル情報
     * @return 新しい順のファイル情報
     */
    public static List<Files> sortedNew(List<Files> files) {
        List<Files> sorted = sortedOld(files);
        Collections.reverse(sorted);
        return sorted;
    }

    /**
     * 指定した期間にULされたパブリックファイルを取得する。(Tier3)(※1)
     * <br>
     * {@code List<Files> list = SlackFileUtil.getSlackFileInfoByDate("2018/01/01 00:00:00", "2019/01/01 24:00:00")}<br>
     * {@code List<Files> list = SlackFileUtil.getSlackFileInfoByDate("2018/01/01", "2019/01/01")}
     *
     * @param from yyyy/MM/dd HH:mm:ss
     * @param to yyyy/MM/dd HH:mm:ss
     * @return ファイル情報
     */
    public static List<Files> getSlackFileInfoByDate(String from, String to) {
        String yyyyMMdd = "yyyy/MM/dd";
        String yyyyMMddHHmmss = "yyyy/MM/dd HH:mm:ss";
        String fromFormat = yyyyMMddHHmmss;
        String toFormat = yyyyMMddHHmmss;

        String regStr = "^\\d{4}/\\d{2}/\\d{2}$";
        if (from.matches(regStr)) fromFormat = yyyyMMdd;
        if (to.matches(regStr)) toFormat = yyyyMMdd;

        return getSlackFileInfoByDate(DateUtil.getUnixTime(from, fromFormat), DateUtil.getUnixTime(to, toFormat));
    }

    /**
     * 指定した期間にULされたパブリックファイルを取得する。(Tier3)(※1)
     *
     * @param from unix時間
     * @param to unix時間
     * @return ファイル情報
     */
    public static List<Files> getSlackFileInfoByDate(long from, long to) {
        Map<String, String> conditions = new HashMap<>();
        conditions.put("ts_from", String.valueOf(from));
        conditions.put("ts_to", String.valueOf(to));

        return getAllFiles(conditions);
    }

    /**
     * 指定したファイル名のパブリックファイルをダウンロードする。(Tier3)(※1)
     * <br>
     * 複数HITした場合はすべてダウンロードする。
     *
     * @param fileName ファイル名
     * @param saveDir 保存先
     * @return ファイル情報（left=DL失敗,right=DL成功）
     */
    public static Pair<List<Files>, List<Files>> download(String fileName, String saveDir) {
        List<Files> targetFiles = getAllFiles().stream()
            .filter(r -> {
                String name;
                // SlackAPIから返されるファイル名は拡張子を含んでいるため
                if (r.getName().lastIndexOf(".") > -1) {
                    name = r.getName().substring(0, r.getName().lastIndexOf("."));
                } else {
                    name = r.getName();
                }
                return name.equals(fileName);
            }).collect(Collectors.toList());

        return download(targetFiles, saveDir);
    }

    /**
     * SlackにULされた全パブリックファイルをダウンロードする。(Tier3)(※1)
     *
     * @param saveDir 保存先
     * @return ファイル情報（left=DL失敗,right=DL成功）
     */
    public static Pair<List<Files>, List<Files>> downloadAll(String saveDir) {
        return download(getAllFiles(), saveDir);
    }

    /**
     * 指定した期間にULされたパブリックファイルをダウンロードする。(Tier3)(※1)
     *
     * @param from yyyy/MM/dd HH:mm:ss
     * @param to yyyy/MM/dd HH:mm:ss
     * @param saveDir 保存先
     * @return ファイル情報（left=DL失敗,right=DL成功）
     */
    public static Pair<List<Files>, List<Files>> download(String from, String to, String saveDir) {
        return download(getSlackFileInfoByDate(from, to), saveDir);
    }

    /**
     * 指定した期間にULされたパブリックファイルをダウンロードする。(Tier3)(※1)
     *
     * @param from UNIX時間（ミリ秒）
     * @param to UNIX時間（ミリ秒）
     * @param saveDir 保存先
     * @return ファイル情報（left=DL失敗,right=DL成功）
     */
    public static Pair<List<Files>, List<Files>> download(long from, long to, String saveDir) {
        return download(getSlackFileInfoByDate(from, to), saveDir);
    }

    private enum Const {
        SHARE,
        REVOKE
    }

    /**
     * ファイルの共有リンクを有効にする。(Tier3)
     *
     * @param fileId ファイルID
     * @return 0:失敗 1:成功 2:既に設定済
     */
    public static int share(String fileId) {
        return shareOrRevoke(fileId, Const.SHARE);
    }

    /**
     * ファイルの共有リンクを無効にする。(Tier3)
     *
     * @param fileId ファイルID
     * @return 0:成功 1:失敗 2:既に設定済
     */
    public static int revoke(String fileId) {
        return shareOrRevoke(fileId, Const.REVOKE);
    }

    private static int shareOrRevoke(String fileId, Const type) {
        String url;
        if (type == Const.SHARE) {
            url = SLACK_FILE_SHARED_PUBLIC;
        } else if (type == Const.REVOKE) {
            url = SLACK_FILE_REVOKE_PUBLIC;
        } else {
            return 1;
        }
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                                          .add(SLACK_TOKEN_KEY, SLACK_TOKEN_VALUE)
                                          .add("file", fileId).build();
        Request request = new Request.Builder().url(url).post(requestBody).build();
        try (Response response = client.newCall(request).execute()) {
            String responseJson = Objects.requireNonNull(response.body()).string();
            String alreadyPublic = "\"error\":\"already_public\"";
            if (responseJson.contains("\"id\":")) {
                return 0;
            } else if (responseJson.contains(alreadyPublic)) {
                return 2;
            } else {
                return 1;
            }
        } catch (IOException e) {
            LogHelper.write(LogLevel.ERROR, "ファイルの" + type + "に失敗しました。 FileId = " + fileId, e);
            return 1;
        }
    }

    private static String validateSaveDir(String source) {
        String maybeDir = !source.endsWith("/") ? source + "/" : source;
        if (java.nio.file.Files.isDirectory(Paths.get(maybeDir))) return maybeDir;
        RuntimeException e = new IllegalArgumentException("ディレクトリパスが不正です");
        LogHelper.write(LogLevel.ERROR, e);
        throw e;
    }

    /**
     * 指定したパブリックファイルをダウンロードする。(Tier3)(※1)
     * <br>
     * このメソッドはファイルダウンロードURL(以下、共有リンク)を一時的に有効にする。<br>
     * ダウンロード完了後に再度無効にする設定を行うが、万が一それに失敗した場合はSlackが定めるタイムアウト時間まで共有リンクが有効のままになるため注意が必要。<br>
     * また、何かしらのエラーが発生しても次のDLに進むため、呼び出し元でfailしたファイルがないか確認することをお勧めする。
     * <br>
     * 【その他】<br>
     *  <ul>
     *    <li>iphoneでULされたファイルは同じファイル名になる場合が多いのでファイル名にDL日時を付け足す</li>
     *    <li>もともと共有リンクが有効だったファイルはDL終了後も無効にしない</li>
     *    <li>ファイル名が空文字の場合は「noname」という名前で保存する</li>
     *  </ul>
     *  
     * @param files ファイル情報
     * @param saveDir 保存先
     * @return ファイル情報（left=DL失敗,right=DL成功）
     */
    public static Pair<List<Files>, List<Files>> download(List<Files> files, String saveDir) {
        String validSaveDir = validateSaveDir(saveDir);
        OkHttpClient client = new OkHttpClient();
        List<Files> successDownloads = new ArrayList<>();
        List<Files> failDownloads = new ArrayList<>();
        for (Files f : files) {
            // 対象ファイルの共有リンクを有効にする
            int shareStatus = share(f.getId());
            if (shareStatus == 1) {
                failDownloads.add(f);
                continue;
            }
            Optional<String> sharedFileUrl = Optional.empty();
            try {
                // Slack側で共有リンク設定が終わっていない場合があるため0.3秒待つ
                sleep(300);
                // 共有ページに埋め込まれたファイルURLを取得
                sharedFileUrl = Jsoup.connect(f.getPermalinkPublic())
                    .get().body().select("a[href]").stream().map(r -> r.attr("abs:href"))
                    .filter(n -> n.contains("?pub_secret=")).findFirst();
            } catch (IOException | InterruptedException e) {
                LogHelper.write(LogLevel.ERROR, "共有リンクの取得に失敗しました", e);
            }
            // 共有リンクを取得できなかった場合はスキップ
            if (!sharedFileUrl.isPresent()) {
                // その際、もともと共有リンクが無効だった場合は元に戻す
                if (shareStatus != 2) revoke(f.getId());
                failDownloads.add(f);
                continue;
            }
            Request request = new Request.Builder().url(sharedFileUrl.get()).build();
            try (InputStream in = Objects.requireNonNull(client.newCall(request).execute().body()).byteStream()) {
                LogHelper.write(LogLevel.INFO, "ダウンロードファイル名：" + f.getName() + " ファイルタイプ：" + f.getFileType());
                String fileName = f.getName();
                // ファイル名が空の場合もあるため
                if ("".equals(f.getName())) {
                    fileName = "noname" + f.getFileType();
                }
                String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                String filePath = validSaveDir + now + "_" + fileName;
                saveInputStream(in, Paths.get(filePath));
                successDownloads.add(f);
            } catch (IOException e) {
                LogHelper.write(LogLevel.ERROR, "ファイルダウンロードに失敗しました。 FileId = " + f.getId(), e);
                failDownloads.add(f);
            } finally {
                // もともと共有リンクが無効だった場合は無効に戻す
                if (shareStatus != 2) revoke(f.getId());
            }
        }

        return getResultPair(failDownloads, successDownloads);
    }

    /**
     * 指定した数のパブリックファイルを古い順でダウンロードしたのちSlackから削除する。(Tier3)(※1)(※2)
     *
     * @param number 削除する数
     * @param saveDir 保存先
     * @return ファイル情報（left=DL成功, right=削除成功）
     */
    public static Pair<List<Files>, List<Files>> downloadAndDelete(int number, String saveDir) {
        List<Files> sortedAllFiles = sortedOld(getAllFiles());
        return downloadAndDelete(sortedAllFiles, number, saveDir);
    }

    /**
     * 指定した数のパブリックファイルを古い順でダウンロードしたのちSlackから削除する。(Tier3)(※1)(※2)
     *
     * @param deleteFiles 削除対象のファイル
     * @param number 削除する数
     * @param saveDir 保存先
     * @return ファイル情報（left=DL成功, right=削除成功）
     */
    public static Pair<List<Files>, List<Files>> downloadAndDelete(List<Files> deleteFiles, int number, String saveDir) {
        int validNum = deleteFiles.size() >= number ? number : deleteFiles.size();
        List<Files> maybeDelete = deleteFiles.subList(0, validNum);
        List<Files> downloadedFiles = download(maybeDelete, validateSaveDir(saveDir)).right();
        return getDownloadAndDeleteResult(downloadedFiles);
    }

    /**
     * 指定したサイズを超えた時点までのパブリックファイルを古い順でダウンロードしたのちSlackから削除する。(Tier3)(※1)(※2)
     *
     * @param deleteSize 削除するサイズ(byte)
     * @param saveDir 保存先
     * @return ファイル情報（left=DL成功, right=削除成功）
     */
    public static Pair<List<Files>, List<Files>> downloadAndDelete(long deleteSize, String saveDir) {
        List<Files> sortedAllFiles = sortedOld(getAllFiles());
        return downloadAndDelete(sortedAllFiles, deleteSize, saveDir);
    }

    /**
     * 指定したサイズを超えた時点までのパブリックファイルを古い順でダウンロードしたのちSlackから削除する。(Tier3)(※1)(※2)
     *
     * @param deleteFiles 削除対象のファイル
     * @param deleteSize 削除するサイズ(byte)
     * @param saveDir 保存先
     * @return ファイル情報（left=DL成功, right=削除成功）
     */
    public static Pair<List<Files>, List<Files>> downloadAndDelete(List<Files> deleteFiles, long deleteSize, String saveDir) {
        List<Files> maybeDelete = new ArrayList<>();
        long totalSize = 0;
        for (Files f : deleteFiles) {
            totalSize += f.getSize();
            maybeDelete.add(f);
            if (totalSize > deleteSize) break;
        }
        List<Files> downloadedFiles = download(maybeDelete, saveDir).right();
        return getDownloadAndDeleteResult(downloadedFiles);
    }

    private static Pair<List<Files>, List<Files>> getDownloadAndDeleteResult(List<Files> downloadedFiles) {
        List<Files> deletedFiles = new ArrayList<>();
        downloadedFiles.forEach(f -> { if (delete(f.getId())) deletedFiles.add(f); });

        return getResultPair(downloadedFiles, deletedFiles);
    }

    /**
     * ファイルをアップロードする。(Tier3)
     *
     * @param option ファイルオプション
     * @return 成功：true 失敗：false
     */
    public static boolean upload(FileOption option) {
        Map<String, String> options = new HashMap<>();
        options.put("channels", String.join(",", option.getChannels()));
        options.put("title", option.getTitle());
        options.put("filename", option.getFileName());
        options.put("initial_comment", option.getComment());
        return upload(option.getFile(), options);
    }

    /**
     * ファイルをアップロードする。(Tier3)
     * @param file ファイル
     * @param options オプション
     * @return 成功：true 失敗：false
     */
    public static boolean upload(File file, Map<String, String> options) {
        if (!file.exists() || !file.isFile()) {
            LogHelper.write(LogLevel.ERROR, "不正なファイルパスです。 設定されたパス=" + file.toString());
            return false;
        }

        options.put(SLACK_TOKEN_KEY, SLACK_TOKEN_VALUE);

        String boundary = String.valueOf(UUID.randomUUID());
        String fileName = options.remove("filename");

        MediaType text = MediaType.parse("text/plain; charset=utf-8");
        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder(boundary).setType(MultipartBody.FORM);
        options.forEach((k, v) -> multipartBuilder.addPart(
                Headers.of("Content-Disposition", "form-data; name=\"" + k + "\""),
                RequestBody.create(text,v.getBytes(StandardCharsets.UTF_8))
        ));

        if (fileName == null || fileName.isEmpty()) fileName = file.getName();
        RequestBody requestBody = multipartBuilder.addFormDataPart("file", fileName,
                RequestBody.create(MediaType.parse("application/octet-stream"), file)
        ).build();

        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(SLACK_FILE_UPLOAD);
        requestBuilder.post(requestBody);
        Request request = requestBuilder.build();

        OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(90, TimeUnit.SECONDS)
                .writeTimeout(80, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            String resultJson = Objects.requireNonNull(response.body()).string();
            return resultJson.contains("\"ok\":true");
        } catch (IOException e) {
            LogHelper.write(LogLevel.ERROR, "ファイルアップロードに失敗しました", e);
            return false;
        }
    }

    private static void sleep(int millis) throws InterruptedException {
        Thread.sleep(millis);
    }
    
    private static Pair<List<Files>, List<Files>> getResultPair(List<Files> left, List<Files> right) {
        return new Pair<>(left, right);
    }

    private static boolean saveInputStream(InputStream in, Path path) {
        return saveInputStream(in, path, new CopyOption[]{});
    }

    private static boolean saveInputStream(InputStream in, Path path, CopyOption... copyOption) {
        try {
            java.nio.file.Files.copy(in, path, copyOption);
            return true;
        } catch (IOException e) {
            LogHelper.write(LogLevel.ERROR, "書き込みに失敗しました", e);
            return false;
        }
    }

}
