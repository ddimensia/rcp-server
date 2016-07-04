package com.digitaldimensia.rcp.server

import com.digitaldimensia.rcp.server.model.Beacon
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.datagram.DatagramSocket
import io.vertx.core.datagram.DatagramSocketOptions
import io.vertx.core.json.Json
import io.vertx.core.logging.LoggerFactory
import io.vertx.kotlin.lang.config
import java.net.Inet4Address
import java.net.NetworkInterface

/**
 * Handles sending the udp beacon that the Race Capture app uses to discover the tcp
 * port to communicate with.
 *
 * @author Gil Markham (gil@groupon.com)
 * @since 0.0.1
 */
class BeaconVerticle : AbstractVerticle() {
    private val logger = LoggerFactory.getLogger(BeaconVerticle::class.java)
    private var datagramSocket: DatagramSocket? = null

    override fun start(startFuture: Future<Void>) {
        val port = config.getInteger("beaconPort", 7223)
        val interval = config.getLong("beaconInterval", 3000)
        val ips = NetworkInterface.getNetworkInterfaces().toList()
                .filterNot { it.isLoopback || !it.isUp }
                .flatMap { it.inetAddresses.toList().filter {it is Inet4Address}.map { it.hostAddress } }
        val beacon = Beacon(name = "RCP_MK2", port = port, serial = "12345", ip = ips)
        datagramSocket = vertx.createDatagramSocket(DatagramSocketOptions().setBroadcast(true))
        vertx.setPeriodic(interval) { period ->
            datagramSocket?.send("{\"beacon\": ${Json.encode(beacon)}}", port, "0.0.0.0") { result ->
                if (result.succeeded()) {
                    logger.info("Beacon Success: ${Json.encode(beacon)}")
                } else {
                    logger.info("Beacon failure", result.cause())
                }
            }
        }
        startFuture.complete(null)
    }

    override fun stop(stopFuture: Future<Void>) {
        datagramSocket?.close { result ->
            stopFuture.complete(null)
        }
    }
}
