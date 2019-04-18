package reaction.simple.original;

import com.atilika.kuromoji.ipadic.Token;
import org.riversun.slacklet.SlackletRequest;
import org.riversun.slacklet.SlackletResponse;
import reaction.simple.SimpleReaction;
import utility.common.XUtils;

import java.util.List;

public class SampleReaction extends SimpleReaction {
    @Override
    public boolean run(SlackletRequest req, SlackletResponse resp) {
        resp.reply("は？無理に決まってんだろ");
        return true;
    }

    @Override
    public int analyze(List<Token> tokens) {
        String userMessage = String.join("", XUtils.getSurfaceMessages(tokens));
        return "帰ってもいいですか？".equals(userMessage) ? 100 : 0;
    }
}
