package log;

import com.atilika.kuromoji.ipadic.Token;
import org.riversun.xternal.simpleslackapi.SlackUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utility.common.XUtils;

import java.text.MessageFormat;
import java.util.List;

public class LogHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogHelper.class);

    private LogHelper() {
    }

    public static void write(LogLevel logLevel, Throwable t) {
        write(logLevel, "", t);
    }

    public static void write(LogLevel logLevel, String message) {
        write(logLevel, message, null);
    }

    public static void write(LogLevel logLevel, String message, Throwable t) {
        switch (logLevel) {
            case DEBUG:
                LOGGER.debug(message, t);
                break;
            case INFO:
                LOGGER.info(message, t);
                break;
            case WARN:
                LOGGER.warn(message, t);
                break;
            case ERROR:
                LOGGER.error(message, t);
                break;
            default:
                LOGGER.error(message, t);
                break;
        }
    }

    public static void writeTokenizeLog(SlackUser slackUser, String content, List<Token> tokens) {
        String id = slackUser.getId();
        String name = slackUser.getUserName();
        String message = MessageFormat
            .format("[USER_ID]{0}|[USER_NAME]{1}|[CONTENT]{2}|[TOKENS]{3}",
                    id, name, content, XUtils.getSurfaceMessages(tokens));
        LOGGER.info(message);
    }
}
