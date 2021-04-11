package com.example.sloaapp

import android.os.Handler
import android.os.Message

class IntroThread(private val handler: Handler) : Thread() {
    override fun run() {
        val msg = Message()
        try {
            sleep(1) // 인트로 시간 (1000이 1초)
            msg.what = 1
            handler.sendEmptyMessage(msg.what)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}