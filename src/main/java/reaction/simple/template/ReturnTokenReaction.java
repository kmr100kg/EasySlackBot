package reaction.simple.template;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;
import org.riversun.slacklet.SlackletRequest;
import org.riversun.slacklet.SlackletResponse;
import reaction.simple.SimpleReaction;
import utility.common.KuromojiTokenizer;
import utility.common.XUtils;

import java.util.List;

public class ReturnTokenReaction extends SimpleReaction {

    @Override
    public boolean run(SlackletRequest req, SlackletResponse resp) {
        String content = XUtils.trimMention(req.getContent());
        Tokenizer tokenizer = KuromojiTokenizer.get();
        String tokenizeMessage = String.join(",", XUtils.getSurfaceMessages(tokenizer.tokenize(content)))
                .replace("/,token, ,", "");
        resp.reply(tokenizeMessage);
        return true;
    }

    @Override
    public int analyze(List<Token> tokens) {
        String content = String.join("", XUtils.getSurfaceMessages(tokens));
        if (content.matches("^/token\\s.*$")) {
            return 100;
        }
        return 0;
    }
}
