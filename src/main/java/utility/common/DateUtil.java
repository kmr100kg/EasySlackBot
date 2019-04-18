package utility.common;

import log.LogHelper;
import log.LogLevel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 日付系のユーティリティクラス。
 */
public final class DateUtil {

    private DateUtil() {}

    /**
     * 何曜日かを「月曜日」～「日曜日」の文字列で返す。
     * @return 曜日
     */
    public static String getStrDay() {
        return LocalDateTime.now().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.JAPAN);
    }

    /**
     * 土日であるかどうかを返す。
     * @return 土日である場合true
     */
    public static boolean isDayOff() {
        String dayOfWeek = getStrDay();
        return "土曜日".equals(dayOfWeek) || "日曜日".equals(dayOfWeek);
    }

    /**
     * 土日以外であるかどうかを返す。
     * @return 土日以外である場合true
     */
    public static boolean isWeekDay() {
        return !isDayOff();
    }

    /**
     * メソッド実行時の時刻が指定した時刻と同じであるかを返す。
     * @param time 時刻の文字列
     * @param timeFormat 時間のフォーマット
     * @return 同じである場合true
     */
    public static boolean isSameTime(String time, String timeFormat) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern(timeFormat));
        return now.equals(time);
    }

    /**
     * メソッド実行時の時刻が指定した時刻と同じであるかを返す。
     * <br>
     * 時刻のフォーマットには「HH:mm:ss」が使用される。
     * @param time 時刻の文字列
     * @return 同じである場合true
     */
    public static boolean isSameTime(String time) {
        return isSameTime(time, "HH:mm:ss");
    }

    /**
     * メソッド実行時の時刻が指定した時刻に含まれているかどうかを返す。
     * @param times 時刻
     * @param timeFormat 時刻のフォーマット
     * @return 含まれている場合true
     */
    public static boolean isContainsTime(List<String> times, String timeFormat) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern(timeFormat));
        return times.contains(now);
    }

    /**
     * メソッド実行時の時刻が指定した時刻に含まれているかどうかを返す。
     * <br>
     * 時刻のフォーマットには「HH:mm:ss」が使用される。
     * @param times 時刻
     * @return 含まれている場合true
     */
    public static boolean isContainsTime(List<String> times) {
        return isContainsTime(times, "HH:mm:ss");
    }

    /**
     * メソッド実行時の日時が指定した日時に含まれているかどうかを返す。
     * @param dates 日付
     * @param times 時間
     * @return 含まれている場合true
     */
    public static boolean isContainsDatesAndTimes(List<String> dates, List<String> times) {
        LocalDateTime ldt = LocalDateTime.now();
        String today = ldt.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")).replace(ldt.getYear() + "/", "");
        return dates.contains(today) && isContainsTime(times);
    }

    /**
     * 今日の始まりをUNIX時間(JST)で返す。
     * @return 今日の始まり
     */
    public static long getStartOfDay() {
        return LocalDateTime.now().toLocalDate().atStartOfDay().toEpochSecond(ZoneOffset.of("+09:00"));
    }

    /**
     * 現在時をUNIX時間(JST)で返す。
     * @return 現在時
     */
    public static long getNow() {
        return LocalDateTime.now().toEpochSecond(ZoneOffset.of("+09:00"));
    }

    /**
     * UNIX時間(JST)を返す。
     * @param date 日付
     * @param format フォーマット
     * @return パースに失敗した場合は現在時刻を返す
     */
    public static long getUnixTime(String date, String format) {
        return getUnixTime(date, format, "Asia/Tokyo");
    }

    /**
     * 指定したタイムゾーンのUNIX時間(秒)を返す。
     * @param date 日付
     * @param format フォーマット
     * @param timeZoneId タイムゾーン
     * @return パースに失敗した場合はRuntimeExceptionを送出する
     */
    public static long getUnixTime(String date, String format, String timeZoneId) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone(timeZoneId));
        sdf.setLenient(false);
        try {
            return sdf.parse(date).getTime() / 1000;
        } catch (ParseException e) {
            LogHelper.write(LogLevel.ERROR, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * UNIX時間(秒)を日本時間のyyyy/MM/dd HH:mm:ss形式で返す。
     * @param unixTime UNIX時間
     * @return 日付文字列
     */
    public static String getStrDate(long unixTime) {
        return getStrDate(unixTime, "yyyy/MM/dd HH:mm:ss", "Asia/Tokyo");
    }

    /**
     * UNIX時間(秒)を指定したフォーマットで返す。
     * @param unixTime UNIX時間
     * @param format フォーマット
     * @param timeZoneId ゾーンID
     * @return 日付文字列
     */
    public static String getStrDate(long unixTime, String format, String timeZoneId) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(unixTime),
                TimeZone.getTimeZone(timeZoneId).toZoneId()).format(DateTimeFormatter.ofPattern(format));
    }
}
