import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.Test;
import utility.common.XUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

public class XUtilsTest {

    private static final Config config = ConfigFactory.load();

    /**
     * ユーザ名に付与されている@IDをトリムすること。
     */
    @Test
    public void testTrimUserName() {
        String content1 = "<@XXXXXXXXX> insert 1900";
        String content2 = "<@XXXXXXXXX>　insert 1900";
        String content3 = "<@XXXXXXXXX>insert 1900";
        assertEquals(XUtils.trimMention(content1), "insert 1900");
        assertEquals(XUtils.trimMention(content2), "insert 1900");
        assertEquals(XUtils.trimMention(content3), content3);
    }

    /**
     * キャメルケースになること
     */
    @Test
    public void toUpperCaseAfterDelimiterTest() {

        try {
            XUtils.toUpperCaseAfterDelimiter(null, null);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("str or delimiter must not be null", e.getMessage());
        }

        String delimiter = "_";
        String normal = "sample_test_sample";
        String normalResult = XUtils.toUpperCaseAfterDelimiter(normal, delimiter);
        assertEquals("sampleTestSample", normalResult);

        String abnormal1 = "_test__sample_";
        String abnormalResult1 = XUtils.toUpperCaseAfterDelimiter(abnormal1, delimiter);
        assertEquals("TestSample", abnormalResult1);

        String abnormal2 = "sample";
        String abnormalResult2 = XUtils.toUpperCaseAfterDelimiter(abnormal2, delimiter);
        assertEquals(abnormal2, abnormalResult2);
    }

}
