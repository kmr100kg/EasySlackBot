package reaction.combination.template;

import org.riversun.slacklet.SlackletRequest;
import org.riversun.slacklet.SlackletResponse;
import reaction.combination.ChainReaction;
import utility.common.XUtils;

public class ChainReaction1 extends ChainReaction {
    @Override
    public boolean run(SlackletRequest req, SlackletResponse resp) {
        String userMessage = XUtils.trimMention(req.getContent());
        boolean result = "いーち！".equals(userMessage);
        if (result) {
            resp.reply("にー！");
        } else {
            String retryCount = super.getCurrentRetryCount() + "/" + super.getMaxRetryCount();
            resp.reply("バカヤロー！もう一回こいオラァ！ " + retryCount);
        }
        return result;
    }
}
