package com.example.sloaapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.appcompat.app.AppCompatActivity

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        val introThread = IntroThread(handler)
        introThread.start()
    }
    var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            if (msg.what == 1) {
                val intent = Intent(this@IntroActivity, SignInActivity::class.java)
                startActivity(intent)
            }
        }
    }
}