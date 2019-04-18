package reaction.simple.template;

import com.atilika.kuromoji.ipadic.Token;
import org.riversun.slacklet.SlackletRequest;
import org.riversun.slacklet.SlackletResponse;
import reaction.simple.SimpleReaction;
import utility.common.XUtils;

import java.util.List;

public class UnknownReaction extends SimpleReaction {
    @Override
    public boolean run(SlackletRequest req, SlackletResponse resp) {
        resp.reply(XUtils.getRandomString(config.getStringList("message.common.unknown")));
        return true;
    }

    @Override
    public int analyze(List<Token> tokens) {
        return 100;
    }
}
