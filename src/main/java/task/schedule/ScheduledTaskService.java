package task.schedule;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * スケジュール駆動するサービス。
 */
public class ScheduledTaskService extends ScheduledThreadPoolExecutor {
    public ScheduledTaskService(int corePoolSize) {
        super(corePoolSize);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
    }

    @Override
    protected void terminated() {
        super.terminated();
    }
}
