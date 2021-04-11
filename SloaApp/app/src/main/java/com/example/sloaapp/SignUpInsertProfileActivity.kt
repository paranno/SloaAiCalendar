package com.example.sloaapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_signup_insert_profile.*

class SignUpInsertProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_insert_profile)

        goto_calendar_button.setOnClickListener {
            val nextIntent = Intent(this, MonthCalendarActivity::class.java)
            startActivity(nextIntent) // 캘린더 창으로 전환
        }
        go_back_button.setOnClickListener {
            val nextIntent = Intent(this, IdentityVerificationActivity::class.java)
            startActivity(nextIntent) //본인 인증 창으로 전환
        }

    }
}