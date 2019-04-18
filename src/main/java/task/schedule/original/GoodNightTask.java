package task.schedule.original;

import task.schedule.ScheduledTask;
import utility.common.DateUtil;

public class GoodNightTask extends ScheduledTask {
    @Override
    public void run() {
        if (DateUtil.isSameTime("21:00:00")) {
            slackletService.sendMessageTo("post channel name", "おやすみなさい！");
        }
    }
}
