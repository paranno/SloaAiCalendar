package com.example.sloaapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_daily_coaching.*
import kotlinx.android.synthetic.main.activity_month_calendar.*
import kotlinx.android.synthetic.main.activity_signin.*

class DailyCoachingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_coaching)

        chat_btn.setOnClickListener { // 목표관리 모듈
            val nextIntent = Intent(this, CommunityActivity::class.java)
            startActivity(nextIntent) // 회원 가입창으로 전환
        }
    }
}