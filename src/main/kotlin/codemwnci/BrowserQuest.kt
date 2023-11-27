package codemwnci

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.*
import spark.Spark.*
import java.util.concurrent.atomic.AtomicLong

/**
 * BrowserQuest implemention
 * Extends a NodeJS tutorial to use Kotlin and SparkJava
 * Tutorial here - http://www.dynetisgames.com/2017/03/06/how-to-make-a-multiplayer-online-game-with-phaser-socket-io-and-node-js/
 * Clientside uses Phaser / Websockets - modified to use plain websocket implementation rather than SocketIO
 * Serverside - rewritten in Kotlin, using SparkJava (to replace the NodeJS SocketIO implementation)
 */

fun main(args: Array<String>) {
    port(9000)
    staticFileLocation("/public")
    webSocket("/ws", BrowserQuestWSHandler::class.java)
    init()
}


class Message(val msg: String, val data: Any)
class Player(val id: Long, var x: Int, var y: Int)
class Id(val id: Long)

fun rndPos(low: Int, high: Int) =  Math.floor(Math.random() * (high - low) + low).toInt()

@WebSocket
class BrowserQuestWSHandler {

    val players = HashMap<Session, Player>()
    var lastPlayerID = AtomicLong(0)

    @OnWebSocketConnect
    fun connected(session: Session) = println("session connected")

    @OnWebSocketClose
    fun closed(session: Session, statusCode: Int, reason: String?) {
        println("Player has left")
        broadcast("remove", Id(players.get(session)!!.id))
    }

    @OnWebSocketMessage
    fun message(session: Session, json: String) {
        val JSON = ObjectMapper().readTree(json)
        val message = JSON.get("msg").asText()

        when (message) {
            "newplayer" -> {
                players.put(session, Player(lastPlayerID.getAndIncrement(), rndPos(100,400), rndPos(100,400)))
                emit(session, "allplayers", playerList())
                broadcastExcept(session, "newplayer", players.get(session)!!)
            }
            "click" -> {
                val player = players.get(session)!!
                player.x = JSON.get("data").get("x").asInt()
                player.y = JSON.get("data").get("y").asInt()
                broadcast("move", player)
            }
            "test" -> println("test received")
        }
        println("Got: $message")
    }

    fun playerList() = players.filter { it.key.isOpen }.values
    fun emit(session: Session, type: String, data: Any) = session.remote.sendString( jacksonObjectMapper().writeValueAsString(Message(type, data)) )
    fun broadcast(type: String, data: Any) = players.filter { it.key.isOpen }.forEach() { emit(it.key, type, data) }
    fun broadcastExcept(session: Session, type: String, data: Any) = players.filter { it.key.isOpen && it.key != session}.forEach() { emit(it.key, type, data) }
}
