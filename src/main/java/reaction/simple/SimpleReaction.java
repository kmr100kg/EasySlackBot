package reaction.simple;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import reaction.Reaction;

/**
 * 一方的なリアクション
 */
public abstract class SimpleReaction implements Reaction {

    private int maxRetryCount;
    private int currentRetryCount;
    protected Config config = ConfigFactory.load();

    protected SimpleReaction() {
        this.maxRetryCount = 3;
    }

    protected SimpleReaction(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    public int getCurrentRetryCount() {
        return currentRetryCount;
    }

    public void setCurrentRetryCount(int currentRetryCount) {
        this.currentRetryCount = currentRetryCount;
    }

}

