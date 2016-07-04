package com.digitaldimensia.rcp.server

import com.digitaldimensia.rcp.server.model.Version
import com.digitaldimensia.rcp.server.model.VersionResponse
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.kotlin.lang.config
import io.vertx.kotlin.lang.ifSuccess
import io.vertx.kotlin.lang.netServer
import io.vertx.kotlin.lang.open

/**
 * Main socket server for emulating the RCP
 *
 * @author Gil Markham (gil@groupon.com)
 * @since 0.0.1
 */
class ServerVerticle : AbstractVerticle() {
    val log = LoggerFactory.getLogger(ServerVerticle::class.java)
    override fun start(startFuture: Future<Void>?) {
        val filesystem = vertx.fileSystem()
        filesystem.open(path = "file") { result ->
            result.ifSuccess("fail") { file ->
                "success"
            }

        }
        netServer(config.getInteger("port", 7223)) { socket ->
            log.info("Client connected!")
            socket.closeHandler { log.info("Socket closed!") }
            socket.exceptionHandler { log.warn("Unexpected exception!", it) }
            socket.handler { buffer ->
                val request = buffer.getString(0, buffer.length(), "UTF8")
                log.info("Received: $request")
                val json = JsonObject(request)
                if (json.isEmpty) {
                    log.warn("Unknown request!")
                } else {
                    json.fieldNames().forEach { fieldName ->
                        val response = when (fieldName) {
                            "getVer" -> toResponse("ver", Version(name = "RCP_MK2", fname = "Race Capture Pro Mk2", major = 2, minor = 9, bugfix = 1, serial = "1234"))
                            else -> "{}"
                        }
                        log.info("Responding with: $response")
                        socket.write("$response\r\n")
                    }

                }
            }
        }
    }

    fun toResponse(root: String, obj: Any): String {
        return "{\"$root\":${Json.encode(obj)}}"
    }
}
