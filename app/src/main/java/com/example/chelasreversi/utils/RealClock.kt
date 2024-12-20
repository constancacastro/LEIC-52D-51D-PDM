package com.example.chelasreversi.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant

interface Clock {
    fun now(): Instant
}

object RealClock : Clock {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun now(): Instant =
        Instant.ofEpochSecond(Instant.now().epochSecond)
}