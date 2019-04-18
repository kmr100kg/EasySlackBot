package reaction.combination;

import com.atilika.kuromoji.ipadic.Token;
import reaction.Reaction;

import java.util.List;

public abstract class ChainReaction implements Reaction {

    private int maxRetryCount;
    private int currentRetryCount;

    protected ChainReaction() {
        this.currentRetryCount = 1;
        this.maxRetryCount = 3;
    }

    protected ChainReaction(int maxRetryCount) {
        this.currentRetryCount = 1;
        this.maxRetryCount = maxRetryCount;
    }

    @Override
    public int analyze(List<Token> tokens) {
        return 100;
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
