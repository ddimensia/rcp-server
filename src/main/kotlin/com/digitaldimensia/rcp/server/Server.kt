package com.digitaldimensia.rcp.server

import io.vertx.kotlin.lang.DefaultVertx

/**
 * Server main
 *
 * @author Gil Markham (gil@groupon.com)
 * @since 0.0.1
 */

fun main(args: Array<String>) {
    DefaultVertx {
        deployVerticle(BeaconVerticle())
        deployVerticle(ServerVerticle())
    }
}
