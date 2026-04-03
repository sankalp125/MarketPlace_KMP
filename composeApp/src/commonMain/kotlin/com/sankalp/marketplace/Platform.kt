package com.sankalp.marketplace

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform