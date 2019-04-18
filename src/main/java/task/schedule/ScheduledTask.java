package task.schedule;

import org.riversun.slacklet.SlackletService;

import java.util.concurrent.TimeUnit;

/**
 * スケジュール機能の基底クラス。
 */
public abstract class ScheduledTask implements Runnable {
    private long initialDelay;
    private long delay;
    private TimeUnit timeUnit;
    private int failCount = 0;
    protected SlackletService slackletService;

    protected ScheduledTask() {
        this(0L, 1L, TimeUnit.SECONDS);
    }

    private ScheduledTask(long initialDelay, long delay, TimeUnit timeUnit) {
        this.initialDelay = initialDelay;
        this.delay = delay;
        this.timeUnit = timeUnit;
    }

    public long getInitialDelay() {
        return initialDelay;
    }

    public void setInitialDelay(long initialDelay) {
        this.initialDelay = initialDelay;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public boolean notOverFailCount(int count) {
        return !(failCount < count);
    }

    public void incrementFailCount() {
        this.failCount = failCount + 1;
    }
}
