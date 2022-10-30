package uk.co.sullenart.panda.mqtt

import io.reactivex.Completable
import java.net.InetSocketAddress
import java.net.Socket

class RxMqttClient {
    private var socket = Socket()

    fun connect(broker: String, clientId: String, port: Int = 1883): Completable =
        disconnect().andThen(
            Completable.create { emitter ->
                // Must create a new socket, can't reconnect an old one
                socket = Socket().apply {
                    connect(InetSocketAddress(broker, port)/*, timeout*/)
                }
                ConnectPacket(clientId).send(socket.getOutputStream())

                val reply = Packet.create(socket.getInputStream()/*, timeout*/)
                if (reply is ConnackPacket) {
                    if (reply.isAccepted) {
                        emitter.onComplete()
                    } else {
                        emitter.onError(Exception("Connection not accepted"))
                    }
                } else {
                    emitter.onError(Exception("Wrong packet received"))
                }
            }
        )

    fun disconnect(): Completable =
        Completable.create { emitter ->
            if (socket.isClosed || !socket.isConnected) {
                // Silently fail so we can call disconnect multiple times
                emitter.onComplete()
                return@create
            }

            DisconnectPacket().send(socket.getOutputStream())
            socket.close()
            emitter.onComplete()
        }

    fun publish(topic: String, content: String): Completable =
        Completable.create { emitter ->
            if (socket.isClosed || !socket.isConnected) {
                emitter.onError(Exception("Socket closed or not connected"))
                return@create
            }

            PublishPacket(topic, content).send(socket.getOutputStream())
            emitter.onComplete()
        }
}