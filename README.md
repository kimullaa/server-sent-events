# Server Sent Events

Server Sent EventsをServletで実装するサンプル

* サーバ側
    - CountUpServletのcount変数を5sごとにインクリメントします。

* クライアント側
    - /countUpにブラウザからアクセスすると更新があったタイミングで結果が戻ります。

# ポイント
* サーバはコネクションをクローズしない
* クライアントはSSEのAPIが提供されているのでそれを使う
* content-typeは"text/event-stream"
* 通信データはdata:xxxxの形で行う

# 起動方法

* mvn tomcat7:run
