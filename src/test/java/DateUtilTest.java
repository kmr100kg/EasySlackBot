import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.Test;
import utility.common.DateUtil;
import utility.common.XUtils;

import java.text.ParseException;

import static org.junit.Assert.*;

public class DateUtilTest {

    /**
     * UNIX時間を返すこと。
     */
    @Test
    public void getUnixTimeTest() {
        String format = "yyyy/MM/dd HH:mm:ss";

        long startUtc = DateUtil.getUnixTime("1970/01/01 00:00:00", format, "UTC");
        assertEquals(0, startUtc);

        long endUtc = DateUtil.getUnixTime("2038/01/19 03:14:07", format, "UTC");
        assertEquals(2147483647, endUtc);

        long startJst = DateUtil.getUnixTime("1970/01/01 09:00:00", format, "Asia/Tokyo");
        assertEquals(0, startJst);

        long startEnd = DateUtil.getUnixTime("2038/01/19 12:14:07", format, "Asia/Tokyo");
        assertEquals(2147483647, startEnd);

        try {
            DateUtil.getUnixTime("2019/01/01 00:00:00", "aaa");
        } catch (RuntimeException e) {
            assertTrue(e.getCause() instanceof ParseException);
        }
    }

    /**
     * UNIX時間をJSTで返すこと。
     */
    @Test
    public void getStrDateTest() {
        String startDate = DateUtil.getStrDate(0);
        assertEquals("1970/01/01 09:00:00", startDate);
        String endDate = DateUtil.getStrDate(2147483647);
        assertEquals("2038/01/19 12:14:07", endDate);
    }
}
