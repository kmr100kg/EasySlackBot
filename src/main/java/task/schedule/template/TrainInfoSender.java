package task.schedule.template;

import log.LogHelper;
import log.LogLevel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import task.schedule.ScheduledTask;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static utility.common.DateUtil.isContainsTime;
import static utility.common.DateUtil.isWeekDay;

public class TrainInfoSender extends ScheduledTask {

    private static final String CHANNEL_NAME = "your channel name";

    private static final List<String> REPORT_TIME = Arrays.asList(
        "06:00:00", "06:30:00", "07:00:00",
        "07:30:00", "08:00:00", "08:30:00",
        "09:00:00",
        "18:00:00", "19:00:00", "20:00:00"
    );
    private static final String YAHOO_URL = "https://transit.yahoo.co.jp/traininfo/area/4/";

    @Override
    public void run() {
        if (isWeekDay() && isContainsTime(REPORT_TIME) && notOverFailCount(3)) {
            try {
                Document document = Jsoup.connect(YAHOO_URL).get();
                Elements elements = document.select(".trouble tr");
                String result = elements.stream().map(e -> e.getElementsByTag("a").text() + " "
                    + e.select(".colTrouble").text() + "\n"
                    + e.getElementsByTag("a").attr("href"))
                    .collect(Collectors.joining("\n"));
                if (elements.isEmpty()) {
                    result = "現在、遅延情報はありません";
                }
                slackletService.sendMessageTo(CHANNEL_NAME, result);
            } catch (IOException e) {
                LogHelper.write(LogLevel.ERROR, e);
                slackletService.sendMessageTo(CHANNEL_NAME, "運行情報の取得に失敗しました");
                incrementFailCount();
            }
        }
    }
}
