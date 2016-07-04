package com.digitaldimensia.rcp.server.model

/**
 * Data class representing a beacon json payload
 *
 * @author Gil Markham (gil@groupon.com)
 * @since 0.0.1
 */
data class Beacon(val name: String, val port: Int, val serial: String, val ip: List<String>)

