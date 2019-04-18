package reaction.simple.template;

import com.atilika.kuromoji.ipadic.Token;
import db.JDBC;
import entity.reaction.MessageSummary;
import org.riversun.slacklet.SlackletRequest;
import org.riversun.slacklet.SlackletResponse;
import reaction.simple.SimpleReaction;
import utility.common.XUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class MessageReaction extends SimpleReaction {

    private final List<MessageSummary> matches = new ArrayList<>();

    @Override
    public int analyze(List<Token> tokens) {
        matches.clear();
        List<String> surfaceMessages = XUtils.getSurfaceMessages(tokens);

        String query = "SELECT * FROM message_summary";
        List<MessageSummary> summaries = new JDBC<MessageSummary>().find(query, MessageSummary.class);

        for (MessageSummary summary : summaries) {
            boolean mustMorpheme = hasMustMorpheme(summary.getMustMorpheme(), surfaceMessages);
            boolean overThreshold = wordHitCount(summary.getQuestion(), surfaceMessages) >= summary.getThreshold();
            if (mustMorpheme && overThreshold) {
                matches.add(summary);
            }
        }

        matches.sort(Comparator.comparing(MessageSummary::getPriority));
        return matches.isEmpty() ? 0 : 100;
    }

    @Override
    public boolean run(SlackletRequest req, SlackletResponse resp) {
        if (matches.isEmpty()) {
            resp.reply(XUtils.getRandomString(config.getStringList("message.common.unknown")));
        } else {
            resp.reply(matches.get(0).getAnswer());
        }
        return true;
    }

    private boolean hasMustMorpheme(String mustMorpheme, List<String> surfaceMessages) {
        String[] mustMorphemes = mustMorpheme.split(",");
        int hitCount = 0;
        for (String morpheme : mustMorphemes) {
            if (surfaceMessages.contains(morpheme)) hitCount++;
        }
        return hitCount >= mustMorphemes.length;
    }

    private int wordHitCount(String question, List<String> surfaceMessages) {
        List<String> questions = Arrays.asList(question.split(","));
        int hitCount = 0;
        for (String message : surfaceMessages) {
            if (questions.contains(message)) hitCount++;
        }
        return hitCount;
    }

}
