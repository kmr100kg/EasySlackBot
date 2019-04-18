package task.schedule;

import org.riversun.slacklet.SlackletService;

import java.util.Arrays;
import java.util.List;

public class Scheduler {

    private Integer corePoolSize;
    private List<ScheduledTask> tasks;
    private SlackletService slackletService;

    public Scheduler(SlackletService slackletService, List<ScheduledTask> tasks) {
        this.slackletService = slackletService;
        this.tasks = tasks;
        this.corePoolSize = tasks.size() + 1;
    }

    public Scheduler(SlackletService slackletService, ScheduledTask... scheduledTasks) {
        this.slackletService = slackletService;
        this.tasks = Arrays.asList(scheduledTasks);
        this.corePoolSize = tasks.size() + 1;
    }

    public Scheduler(SlackletService slackletService, List<ScheduledTask> tasks, Integer corePoolSize) {
        this.slackletService = slackletService;
        this.tasks = tasks;
        this.corePoolSize = corePoolSize;
    }

    public void run() {
        ScheduledTaskService sts = new ScheduledTaskService(corePoolSize);
        tasks.forEach(t -> {
            t.slackletService = this.slackletService;
            sts.scheduleWithFixedDelay(t, t.getInitialDelay(), t.getDelay(), t.getTimeUnit());
        });
    }
}
