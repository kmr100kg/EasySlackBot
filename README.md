# EasySlackBot

riversunさんの[slacklet-examples](https://github.com/riversun/slacklet-examples)に色々実装を足したものです。

簡単＆お手軽にSlackBotを構築することができます。

## 前提

このプロジェクトは以下設定が完了していることを前提に実装されています。

- Slackのユーザ登録
- SlackBotトークンの発行

また、Slack○○Utilを使う場合はさらに設定が必要です。

- SlackAPIトークンの発行

## 動作環境
* Java8
* maven3

## どんなことができるの？

こんなことができます。

### シンプルな会話

![gohome](https://user-images.githubusercontent.com/22782386/56376478-f5a36300-6242-11e9-90f6-7c50a7c7eebe.gif)

---

### 連続した会話

![bombaie](https://user-images.githubusercontent.com/22782386/56376480-f5a36300-6242-11e9-9654-fd4bf9a7ac17.gif)

---

### スケジュールタスク

![goodnight](https://user-images.githubusercontent.com/22782386/56376479-f5a36300-6242-11e9-9fee-21009f6055db.gif)

---

他にも以下のようなユーティリティを実装しています。

* SlackFileUtil

    ファイルのダウンロードやアップロード、削除といった操作が行えます。

    例：
    
    ```SlackFileUtil.upload(...)```, ```SlackFileUtil.download(...)```

* SlackChannelUtil

    チャンネルIDや所属ユーザの取得などチャンネル関連の操作が行えます。

    例：
    
    ```SlackChannelUtil.getMembers(channelName)```

* JDBC

    お手軽にCRUD処理が行えます。JPAがお好きな方は依存とpersistence.xmlを追加してあるのでそちらをどうぞ！
    
    例：
    
    ```Hoge = new JDBC<Hoge>.find("SELECT * FROM hoge", Hoge.class)```

## 実行方法
    
resources配下にあるapplication.confにあなたのSlackbotトークンとSlackAPIトークンを記述してください。

```
slack {
  bot {
    token = "ほげほげ"
  }
  api {
    token = "ふがふが"
  }
}
```

IDEでプロジェクトを開いている場合はMainクラスを実行してください。
jarで起動したい場合は以下のコマンドを実行してください。

```
cd プロジェクトルート
// target配下にeasy-slackbot-dependencies.jarができます
mvn package
java -jar sample-slackbot-dependencies.jar
```

## 実装例

### シンプルな会話 ###

#### ①自分で実装する ####

`SimpleReaction`の実装クラスを`ReactionService`に追加するだけです。
```
public class SampleReaction extends SimpleReaction {
    @Override
    public int analyze(List<Token> tokens) {
        String userMessage = String.join("", XUtils.getSurfaceMessages(tokens));
        return "帰ってもいいですか？".equals(userMessage) ? 100 : 0;
    }
    
    @Override
    public boolean run(SlackletRequest req, SlackletResponse resp) {
        resp.reply("は？無理に決まってんだろ");
        return true;
    }
}

// ReactionServiceのコンストラクタ
public ReactionService() {
    this.reactions = Arrays.asList(new SampleReaction());
}

// SampleSlacklet
public void onMentionedMessagePosted(SlackletRequest req, SlackletResponse resp) {
    new ReactionService().doReaction(req, resp);
}
```
上記実装でメッセージが「帰ってもいいですか？」だった場合に「は？無理に決まってんだろ」と返します。

詳しくは、`SampleReaction`・`ReactionService`・`SampleSlacklet`の実装を参照してください。

#### ②MessageReactionを使用する ####

ユーザから「〇〇は？」と聞かれて「××です」と返すだけのやり取りをしたい場合 `SimpleReaction`を一個一個実装していくのは大変です。
そんなときは`MessageReaction`の使用をオススメします。
簡単な設定だけで会話を実現させることができます。

- まず`message_summary`テーブルを作成します。デフォルト(H2DBを使用する場合)では予め作成されます。

| id                     | overview  | question             | must_morpheme  | answer | threshold | priority |
|------------------------|-----------|----------------------|----------------|--------|-----------|----------|
| メッセージのID（一意） | 説明      | 質問（カンマ区切り） | 必須の形態素   | 回答   | 閾値      | 優先度   |

```
CREATE TABLE IF NOT EXISTS `message_summary`(
    `id` int(11) NOT NULL,
    `overview` varchar(255) NOT NULL DEFAULT '',
    `question` varchar(255) NOT NULL,
    `must_morpheme` varchar(255) NOT NULL,
    `answer` varchar(255) NOT NULL,
    `threshold` int(11) NOT NULL,
    `priority` int(11) NOT NULL,
    PRIMARY KEY (`id`)
);
```

- 会話内容の設定を追加します

```
INSERT INTO `message_summary` VALUES
(1, '給料日の回答' ,'給料,日,い,つ,？' ,'給料,日' ,'オメーの給料ねーから！' ,2 ,1);
```

- `ReactionService`にMessageReactionを追加します（デフォルトでは追加されています）

```
// ReactionServiceのコンストラクタ
public ReactionService() {
    this.reactions = Arrays.asList(new MessageReaction());
}
```

以上で会話を実現できます↓

```
Q.「給料日は？」 or 「給料日はいつですか」 or 「給料は何日に振り込まれますか」
A. オメーの給料ねーから！
```

`MessageReaction`はメッセージの形態素と登録した形態素（question）が閾値以上一致した場合に回答を返します。

上記の例だと

- 「給料,日」という形態素が質問文中に存在すること
- 「給料,日,い,つ,？」という形態素のうち何れかが3回以上出現すること

が回答条件となります。
質問に対する回答が複数ヒットしうる場合は優先度が高い回答が返されます。

なお、形態素解析には[kuromoji](https://github.com/atilika/kuromoji)を使用しています。

#### （補足） ####

解析の結果が知りたいときは下記コマンドが便利です。

（`ReturnTokenReaction`に実装されています）

```
@sample_bot /token 今日の晩御飯は何かなぁ～？

今日,の,晩,御飯,は,何,か,なぁ,～？
```

#### （さらに補足） ####

解析によって分割させたくない文字（例えば`山手線`や`5ch`など）は、resources配下にある
`user_dic.csv`に設定を追加することで分割されなくなります。

```
# 単語,　形態素解析結果の単語, 読み, 品詞
山手線, 山手線, ヤマノテセン, 名詞
5ch, 5ch, ゴチャンネル, 名詞 
```

***  

### 連続した会話 ###

`CombinationReaction`の実装クラスと`ChainReaction`の実装クラスを`ReactionService`に追加するだけです。

```
// 最初のメッセージを受取るクラス
public class SampleCombReaction extends CombinationReaction {

    public SampleCombReaction(ChainReaction... reactions) {
        super(reactions);
    }

    @Override
    public int analyze(List<Token> tokens) {
        return XUtils.getSurfaceMessages(tokens).contains("ボンバイエ") ? 100 : 0;
    }

    @Override
    public boolean run(SlackletRequest req, SlackletResponse resp) {
        resp.reply("いくぞおおおおお！");
        return true;
    }
}

// やりとり（チェイン）を行うクラス
public class ChainReaction1 extends ChainReaction {
    @Override
    public boolean run(SlackletRequest req, SlackletResponse resp) {
        String userMessage = XUtils.trimMention(req.getContent());
        boolean result = "いーち！".equals(userMessage);
        if (result) {
            resp.reply("にー！");
        } else {
            String retryCount = super.getCurrentRetryCount() + "/" + super.getMaxRetryCount();
            resp.reply("バカヤロー！もう一回こいオラァ！ " + retryCount);
        }
        return result;
    }
}

public class ChainReaction2 extends ChainReaction {
    @Override
    public boolean run(SlackletRequest req, SlackletResponse resp) {
        String userMessage = XUtils.trimMention(req.getContent());
        boolean result = "さーん！".equals(userMessage);
        if (result) {
            resp.reply("ダアアアアアアアアアアアアアア！！！！！！！");
        } else {
            String retryCount = super.getCurrentRetryCount() + "/" + super.getMaxRetryCount();
            resp.reply("バカヤロー！もう一回こいオラァ！ " + retryCount);
        }
        return result;
    }
}

// ReactionServiceのコンストラクタ
public ReactionService() {
    Reaction sampleCombReaction = new SampleCombReaction(new ChainReaction1(), new ChainReaction2());
    this.reactions = Arrays.asList(sampleCombReaction);
}
```

上記実装で

1. ユーザが「ボンバイエ」と入力
2. BOTが「いくぞおおおおお！」と返す
3. ユーザが「いーち！」と入力
4. BOTが「にー！」と返す
5. ユーザが「さーん！」と入力
6. BOTが「ダアアアアアアアアアアアアアア！！！！！！！」と返す

という一連の流れを実現できます。
さらに、途中で入力を間違えた場合は指定した回数分リトライさせることができます。

詳細は、`SampleCombReaction`・`ChainReaction1,2`・`ReactionService`の実装を参照してください。

***

### スケジュールタスク ###

`ScheduledTask`の実装クラスを`Scheduler`に追加するだけです。

デフォルトでは一秒ずつ```run()```が走ります。

```
public class GoodNightTask extends ScheduledTask {
    @Override
    public void run() {
        if (DateUtil.isSameTime("21:00:00")) {
            slackletService.sendMessageTo("チャンネル名", "おやすみなさい！");
        }
    }
}

// Mainクラス
SlackletService slackletService = new SlackletService(token);
new Scheduler(slackletService, new GoodNightTask()).run();
```

上記実装で、21:00:00に「おやすみなさい！」とメッセージを送る処理を実現できます。

より複雑なサンプルとしてYahoo路線情報から運行情報を取得する```TrainInfoSender```を実装しています。

## 今後の予定

１．連続した会話にタイムアウト処理を追加する予定です

２．ユーザごとに実行できるリアクションを区別させられるようにする予定です

３．HogeHogeユーティリティを追加していきます





