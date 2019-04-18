import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import slacklet.SampleSlacklet;
import db.JDBC;
import log.LogHelper;
import log.LogLevel;
import org.riversun.slacklet.SlackletService;
import task.schedule.Scheduler;
import task.schedule.original.GoodNightTask;
import task.schedule.template.TrainInfoSender;

public class Main {

    public static void main(String[] args) {
        Config config = ConfigFactory.load();
        try {
            // NOTE: H2DBを使わない場合このコードは必要ありません
            new JDBC<>().initH2DB();
            String token = config.getString("slack.bot.token");
            SlackletService slackletService = new SlackletService(token);
            slackletService.addSlacklet(new SampleSlacklet());
            slackletService.start();

            new Scheduler(slackletService, new TrainInfoSender(), new GoodNightTask()).run();
        } catch (Exception e) {
            LogHelper.write(LogLevel.ERROR, config.getString("message.common.startFail"), e);
        }
    }
}
