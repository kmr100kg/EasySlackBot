package reaction;

import com.atilika.kuromoji.ipadic.Token;
import com.typesafe.config.ConfigFactory;
import org.riversun.slacklet.SlackletRequest;
import org.riversun.slacklet.SlackletResponse;

import java.util.List;

/**
 * メッセージを受け取ったときのリアクション。
 */
public interface Reaction {
    /**
     * リアクションする。
     * @param req リクエスト
     * @param resp レスポンス
     * @return タスクが意図した通りに終了した場合はtrue,そうでない場合はfalseを返してください
     */
    boolean run(SlackletRequest req, SlackletResponse resp);

    /**
     * トークンに対するリアクションが実装されているかどうか調べて点数を返す。
     * @param tokens トークン
     * @return 0～100の範囲で返してください
     */
    int analyze(List<Token> tokens);

    /**
     * タイムアウト時間（秒）を返す。
     *
     * デフォルトでは設定ファイルの値を返す。
     * @return タイムアウト時間
     */
    default long timeout() {
        return ConfigFactory.load().getLong("reaction.timeout");
    }
}
