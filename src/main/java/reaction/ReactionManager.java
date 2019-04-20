package reaction;
import com.atilika.kuromoji.ipadic.Token;
import com.typesafe.config.ConfigFactory;
import org.riversun.slacklet.SlackletRequest;
import org.riversun.slacklet.SlackletResponse;
import reaction.combination.CombinationReaction;
import reaction.simple.SimpleReaction;
import reaction.simple.template.UnknownReaction;
import utility.common.DateUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * リアクションを管理するクラス。
 */
public final class ReactionManager {

    private static final int MAX_POINT = 100;
    private static final Map<String, Reaction> reactionMap = new ConcurrentHashMap<>();
    private static ReactionManager reactionManager;

    public static synchronized ReactionManager getInstance() {
        if (reactionManager == null) {
            reactionManager = new ReactionManager();
        }
        return reactionManager;
    }

    public ReactionManager add(String userId, List<Token> tokens, List<Reaction> reactions) {
        reactionMap.put(userId, analyze(userId, tokens, reactions));
        return reactionManager;
    }

    /**
     * リアクションを実行する。
     * @return リアクションマネージャ
     */
    public ReactionManager execute(SlackletRequest req, SlackletResponse resp) {
        String userId = req.getSender().getId();
        Reaction nextReaction = Optional.ofNullable(reactionMap.get(userId)).orElse(new UnknownReaction());
        if (nextReaction instanceof SimpleReaction) {
            nextReaction.run(req, resp);
            remove(userId);
        } else if (nextReaction instanceof CombinationReaction) {
            CombinationReaction nextCombReaction = (CombinationReaction) nextReaction;
            if (nextCombReaction.nonOver()) {
                if (nextCombReaction.isDoChain()) {
                    // 初回以降のリアクション
                    boolean result = nextCombReaction.next().run(req, resp);
                    nextCombReaction.setRunTime();
                    if (result) {
                        nextCombReaction.remove();
                        if (nextCombReaction.isChainEmpty()) {
                            // 処理成功＆キューが空ならキャッシュ削除
                            remove(userId);
                        }
                    } else {
                        nextCombReaction.incrementRetryCount();
                        if (nextCombReaction.overMaxRetryCount()) {
                            // 処理失敗＆失敗上限を超えたら失敗メッセージを送信
                            remove(userId);
                            resp.reply(ConfigFactory.load().getString("message.common.retryOver"));
                        }
                    }
                } else {
                    // 初回のリアクション
                    nextCombReaction.run(req, resp);
                    nextCombReaction.setRunTime();
                    nextCombReaction.setDoChain(true);
                }
            } else {
                // タイムアウトした場合はキャッシュ削除
                remove(userId);
                resp.reply(ConfigFactory.load().getString("message.common.timeout"));
            }
        }
        return reactionManager;
    }

    private void remove(String userId) {
        reactionMap.remove(userId);
    }

    private Reaction analyze(String userId, List<Token> tokens, List<Reaction> reactions) {
        Map<Integer, Reaction> analyzed = new HashMap<>();
        Reaction reaction = reactionMap.get(userId);
        if (reaction != null) {
            return reaction;
        }
        for (Reaction r : reactions) {
            analyzed.putIfAbsent(r.analyze(tokens), r);
        }
        return analyzed.getOrDefault(MAX_POINT, new UnknownReaction());
    }
}
