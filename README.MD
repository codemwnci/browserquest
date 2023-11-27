## BrowserQuest in Kotlin / SparkJava

I follow a number of technologies one of which is PhaserJS. I saw an article that used PhaserJS and WebSockets (NodeJS/SocketIO) to implement (as a technical demo) an MMORPG in the browser.

The original article can be found here -> http://www.dynetisgames.com/2017/03/06/how-to-make-a-multiplayer-online-game-with-phaser-socket-io-and-node-js/

Having recently got into Kotlin, wanting to experiment with WebSockets and also being a fan of the SparkJava framework, I set about trying to implement the same functionality, but replacing the NodeJS backend with Kotlin and SparkJava. This repository is the working version of my experiment.

### Main Changes / Lessons
The main elements that were changed from the original article are

1. NodeJS serverside was replaced with Kotlin and Spark. This was reasonably easy.
1. I implemented the serverside in base WebSockets (not SocketIO), so I had to reimplement the Client object in standard WebSockets, to remove the SocketIO code. I also had to play around with the JSON data that got sent back and forth to send message type and message data.
1. OnWebSocketClosed serverside caused a bit of a problem. I copied the Java version of the SparkJava WebSocket example as the template for the Kotlin serverside. This worked fine for OnWebSocketConnect and OnWebSocketMessage. However, in the OnWebSocketClosed signature, the reason parameter is optional and therefore nullable. A simple change to the signature to allow null was all that was needed.
1. Kotlin WHEN clause makes the handling of different message types clean and easily understandable.

## Licence
Apache 2.0 Licence

Copyright &copy; Wayne Ellis 2017

