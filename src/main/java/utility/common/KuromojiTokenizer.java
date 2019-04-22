package utility.common;

import com.atilika.kuromoji.ipadic.Tokenizer;
import log.LogHelper;
import log.LogLevel;

import java.io.IOException;
import java.io.InputStream;

public final class KuromojiTokenizer {

    private static Tokenizer tokenizer;

    private KuromojiTokenizer() {}

    public static synchronized Tokenizer get() {
        if (tokenizer == null) {
            try (InputStream in = KuromojiTokenizer.class.getClassLoader().getResourceAsStream("user_dic.csv")) {
                tokenizer = new Tokenizer.Builder()
                        .userDictionary(in)
                        .build();
            } catch (IOException e) {
                LogHelper.write(LogLevel.ERROR, "user_dic.csvの読込みに失敗しました", e);
                tokenizer = new Tokenizer.Builder().build();
            }
        }
        return tokenizer;
    }
}
