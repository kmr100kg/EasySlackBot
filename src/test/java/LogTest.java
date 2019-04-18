import log.LogHelper;
import log.LogLevel;
import org.junit.Test;
import org.riversun.xternal.simpleslackapi.SlackUser;
import utility.common.KuromojiTokenizer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class LogTest {

    private void writeLog(LogLevel needLogLevel, String logLevelStr) {
        String uuid = "UUID=" + UUID.randomUUID();
        LogHelper.write(needLogLevel, uuid);
        try {
            List<String> log = Files.readAllLines(Paths.get("./logs/application_test.log"));
            String last = log.get(log.size() - 1);
            assertTrue("指定したログレベルであること", last.contains(logLevelStr));
            assertTrue("UUIDが含まれていること", last.contains(uuid));
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    private String writeExceptionLog(String userText, Throwable t) {
        if (userText == null) {
            LogHelper.write(LogLevel.ERROR, t);
        } else {
            LogHelper.write(LogLevel.ERROR, userText, t);
        }
        try {
            return String.join("", Files.readAllLines(Paths.get("./logs/application_test.log")));
        } catch (IOException e) {
            e.printStackTrace();
            fail();
            return "";
        }
    }

    @Test
    public void debugLogTest() {
        writeLog(LogLevel.DEBUG, "[DEBUG]");
    }

    @Test
    public void infoLogTest() {
        writeLog(LogLevel.INFO, "[INFO]");
    }

    @Test
    public void warnLogTest() {
        writeLog(LogLevel.WARN, "[WARN]");
    }

    @Test
    public void errorLogTest1() {
        String uuid = "UUID=" + UUID.randomUUID();
        String exceptionMessage = "Test Exception 1";
        String log = writeExceptionLog(uuid, new Exception(exceptionMessage));

        assertTrue("UUIDが含まれていること", log.contains(uuid));
        assertTrue("例外メッセージが含まれていること", log.contains(exceptionMessage));

    }

    @Test
    public void errorLogTest2() {
        String exceptionMessage = "Test Exception 1";
        String log = writeExceptionLog(null, new Exception(exceptionMessage));

        assertTrue("例外メッセージが含まれていること", log.contains(exceptionMessage));
    }

    @Test
    public void writeTokenizeLogTest() {
        String content = "ログテスト";
        try {
            SlackUser slackUser = new MockSlackUser();
            LogHelper.writeTokenizeLog(slackUser, content, KuromojiTokenizer.get().tokenize(content));
            List<String> log = Files.readAllLines(Paths.get("./logs/application_test.log"));
            String last = log.get(log.size() - 1);
            assertTrue("INFOレベルであること", last.contains("[INFO]"));
            assertTrue("トークンログが書き込まれていること", last.contains("[USER_ID]0001|[USER_NAME]Slack一郎|[CONTENT]ログテスト|[TOKENS][ログ, テスト]"));
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

}
