package com.digitaldimensia.rcp.server.model

/**
 * RCP Version
 *
 * @author Gil Markham (gil@groupon.com)
 * @since 0.0.1
 */
data class Version(val name: String, val fname: String, val major: Int, val minor: Int, val bugfix: Int, val serial: String)
