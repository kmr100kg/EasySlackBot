package utility.slack;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import entity.slack.ChannelInfo;
import entity.slack.Channels;
import log.LogHelper;
import log.LogLevel;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Slackのチャンネル操作を行うユーティリティクラス。
 * <br>
 * 【注意事項】<br>
 * ①フリープランでのみ正常動作する。<br>
 * ②内部でコールしているSlack Files APIは20件/分までしかリクエストを許容しないため、
 * 上限を超えるメソッド呼び出しを行った場合エラーになる。<br>
 * メソッドのjavadocにTier2と書かれている場合は上記考慮が必要となる。
 * (https://api.slack.com/docs/rate-limits)
 *
 */
public class SlackChannelUtil {

    private static final Config config = ConfigFactory.load();
    private static final String SLACK_TOKEN_KEY = "token";
    private static final String SLACK_TOKEN_VALUE = config.getString("slack.api.token");
    private static final String SLACK_CHANNELS_LIST = "https://slack.com/api/channels.list";


    private SlackChannelUtil() {
    }

    /**
     * チャンネル情報を取得する。(Tier2)
     *
     * 取得に失敗した場合は空のチャンネル情報を返す。
     * @return チャンネル情報
     */
    public static ChannelInfo getChannelInfo() {
        String url = SLACK_CHANNELS_LIST + "?" + SLACK_TOKEN_KEY + "=" + SLACK_TOKEN_VALUE;
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = new OkHttpClient().newCall(request).execute()) {
            return new ObjectMapper().readValue(Objects.requireNonNull(response.body()).string(), ChannelInfo.class);
        } catch (IOException e) {
            LogHelper.write(LogLevel.ERROR, "チャンネルの取得に失敗しました", e);
            return new ChannelInfo();
        }
    }

    /**
     * チャンネル名からチャンネルIDを取得する。
     *
     * @param channelName チャンネル名
     * @return チャンネルID
     */
    public static String getChannelId(String channelName) {
        return getChannelInfo().getChannels().stream()
                .filter(r -> channelName.equals(r.getName())).map(Channels::getId).collect(Collectors.joining());
    }

    /**
     * チャンネルに所属するメンバーを取得する。
     *
     * @param channelName チャンネル名
     * @return メンバー名
     */
    public static List<String> getMembers(String channelName) {
        return getChannelInfo().getChannels().stream()
                .filter(r -> channelName.equals(r.getName()))
                .flatMap(r -> r.getMembers().stream()).collect(Collectors.toList());
    }

}
