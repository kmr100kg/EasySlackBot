package reaction;

import com.atilika.kuromoji.ipadic.Token;
import com.typesafe.config.ConfigFactory;
import log.LogHelper;
import log.LogLevel;
import org.riversun.slacklet.SlackletRequest;
import org.riversun.slacklet.SlackletResponse;
import reaction.combination.template.ChainReaction1;
import reaction.combination.template.ChainReaction2;
import reaction.combination.template.SampleCombReaction;
import reaction.simple.original.SampleReaction;
import reaction.simple.template.MessageReaction;
import reaction.simple.template.ReturnTokenReaction;
import utility.common.KuromojiTokenizer;
import utility.common.XUtils;

import java.util.Arrays;
import java.util.List;

/**
 * リアクションを実行するサービス。
 */
public class ReactionService {

    private List<Reaction> reactions;

    public ReactionService() {
        Reaction sampleCombReaction = new SampleCombReaction(new ChainReaction1(), new ChainReaction2());
        this.reactions = Arrays.asList(new ReturnTokenReaction(), new MessageReaction(), new SampleReaction(), sampleCombReaction);
    }


    /**
     * リアクションを実行する。
     * @param req リクエスト
     * @param resp レスポンス
     */
    public void doReaction(SlackletRequest req, SlackletResponse resp) {
        String content = XUtils.trimMention(req.getContent());
        List<Token> tokens = KuromojiTokenizer.get().tokenize(content);

        LogHelper.writeTokenizeLog(req.getSender(), content, tokens);

        try {
            ReactionManager.getInstance().add(req.getSender().getId(), tokens, reactions).execute(req, resp);
        } catch (Exception e) {
            LogHelper.write(LogLevel.ERROR, e);
            resp.reply(ConfigFactory.load().getString("message.common.fail"));
        }
    }
}
