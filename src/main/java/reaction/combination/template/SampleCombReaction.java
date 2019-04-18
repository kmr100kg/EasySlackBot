package reaction.combination.template;

import com.atilika.kuromoji.ipadic.Token;
import org.riversun.slacklet.SlackletRequest;
import org.riversun.slacklet.SlackletResponse;
import reaction.Reaction;
import reaction.combination.ChainReaction;
import reaction.combination.CombinationReaction;
import utility.common.XUtils;

import java.util.List;

public class SampleCombReaction extends CombinationReaction {

    public SampleCombReaction(ChainReaction... reactions) {
        super(reactions);
    }

    @Override
    public int analyze(List<Token> tokens) {
        return XUtils.getSurfaceMessages(tokens).contains("ボンバイエ") ? 100 : 0;
    }

    @Override
    public boolean run(SlackletRequest req, SlackletResponse resp) {
        resp.reply("いくぞおおおおお！");
        return true;
    }
}
