package utility.common;

import com.atilika.kuromoji.TokenBase;
import com.atilika.kuromoji.ipadic.Token;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 作成途中で必要になったメソッドを切り出したクラス。
 */
public final class XUtils {

    private XUtils() {}

    /**
     * 送られてきたメッセージからメンション部分を取り除く。
     *
     * @param content メッセージ
     * @return メッセージ本文
     */
    public static String trimMention(String content) {
        return content.replaceAll("<@.*>(\\s|　)", "");
    }

    /**
     * 解析結果から形態素の文字列リストを取得する。
     *
     * @param tokens 解析結果
     * @return 文字列リスト
     */
    public static List<String> getSurfaceMessages(List<Token> tokens) {
        return tokens.stream().map(TokenBase::getSurface).collect(Collectors.toList());
    }

    /**
     * 指定したデリミタ直後にあるアルファベットを大文字にする。
     *
     * @param str 文字列
     * @param delimiter デリミタ
     * @return デリミタ直後が大文字になった文字列
     */
    public static String toUpperCaseAfterDelimiter(String str, String delimiter) {
        if (str == null || delimiter == null) {
            throw new IllegalArgumentException("str or delimiter must not be null");
        }
        int delimiterCount = StringUtils.countMatches(str, delimiter);
        if (delimiterCount < 1) {
            return str;
        }
        String[] array = str.split(delimiter);
        for (int i = 1; i < array.length; i++) {
            array[i] = StringUtils.capitalize(array[i]);
        }
        return String.join("", array);
    }

    /**
     * 指定した文字列リストからランダムな文字列を返す。
     *
     * @param strings 文字列リスト
     * @return ランダムな文字列
     */
    public static String getRandomString(List<String> strings) {
        List<String> list = new ArrayList<>(strings);
        Collections.shuffle(list);
        return list.get(0);
    }
}
