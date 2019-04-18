package slacklet;

import org.riversun.slacklet.Slacklet;
import org.riversun.slacklet.SlackletRequest;
import org.riversun.slacklet.SlackletResponse;
import reaction.Reaction;
import reaction.ReactionService;
import reaction.combination.template.ChainReaction1;
import reaction.combination.template.ChainReaction2;
import reaction.combination.template.SampleCombReaction;
import reaction.simple.original.SampleReaction;
import reaction.simple.template.MessageReaction;
import reaction.simple.template.ReturnTokenReaction;

import java.util.Arrays;
import java.util.List;

public class SampleSlacklet extends Slacklet {

    /**
     * メッセージが投稿されたときに実行される。
     */
    @Override
    public void onMessagePosted(SlackletRequest req, SlackletResponse resp) {
        // NOP
    }

    /**
     * メンションされたときに実行される。
     */
    @Override
    public void onMentionedMessagePosted(SlackletRequest req, SlackletResponse resp) {
        new ReactionService().doReaction(req, resp);
    }

    /**
     * ダイレクトメッセージが送られたときに実行される。
     */
    @Override
    public void onDirectMessagePosted(SlackletRequest req, SlackletResponse resp) {
        new ReactionService().doReaction(req, resp);
    }

}
