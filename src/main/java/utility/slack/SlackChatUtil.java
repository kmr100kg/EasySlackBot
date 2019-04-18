package utility.slack;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import log.LogHelper;
import log.LogLevel;
import okhttp3.*;

import java.io.IOException;

public class SlackChatUtil {

    private static final Config config = ConfigFactory.load();
    private static final String SLACK_TOKEN_KEY = "token";
    private static final String SLACK_TOKEN_VALUE = config.getString("slack.api.token");
    private static final String SLACK_CHAT_DELETE = "https://slack.com/api/chat.delete";
    private static final String SLACK_CHAT_POST = "https://slack.com/api/chat.postMessage";

    public static boolean delete(String channelId, long timestamp) {
        RequestBody requestBody = new FormBody.Builder()
                .add(SLACK_TOKEN_KEY, SLACK_TOKEN_VALUE)
                .add("channel", channelId)
                .add("ts", String.valueOf(timestamp)).build();
        Request request = new Request.Builder().url(SLACK_CHAT_DELETE).post(requestBody).build();
        return getResult(request, "メッセージ削除");
    }

    public static boolean post(String channelId, String message) {
        RequestBody requestBody = new FormBody.Builder()
                .add("channel", channelId)
                .add("text", message)
                .build();
        Request request = new Request.Builder().url(SLACK_CHAT_POST).post(requestBody).build();
        return getResult(request, "メッセージ投稿");
    }

    private static boolean getResult(Request request, String actionName) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        try (Response response = client.newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                return false;
            }
            String responseJson = responseBody.string();
            boolean result = responseJson.contains("\"ok\":true");
            if (result) {
                LogHelper.write(LogLevel.INFO, "【成功】" + actionName);
                return true;
            } else {
                LogHelper.write(LogLevel.INFO, "【失敗】" + actionName);
                return false;
            }
        } catch (IOException e) {
            LogHelper.write(LogLevel.ERROR, "メッセージの削除に失敗しました", e);
            return false;
        }
    }

}
