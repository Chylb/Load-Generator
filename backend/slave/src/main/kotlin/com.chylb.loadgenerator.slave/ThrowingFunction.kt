package com.chylb.loadgenerator.slave

fun interface ThrowingFunction<T, R> {
    @Throws(Exception::class)
    fun apply(t: T): R
}