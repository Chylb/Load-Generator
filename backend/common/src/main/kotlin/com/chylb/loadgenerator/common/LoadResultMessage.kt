package com.chylb.loadgenerator.common

class LoadResultMessage(
    val requestKey: String? = null,
    val responseTimeSum: Long = 0,
    val maxResponseTime: Long = 0,
    val startTimestamp: Long = 0,
    val error: String? = null,
)
{}