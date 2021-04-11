package com.example.sloaapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_identity_verification.*

class IdentityVerificationActivity : AppCompatActivity() {
    // Firebase Authentication 및 SNS로그인 API 관련 변수들
    // 로그인을 관리해주는 클래스
    var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_identity_verification)
        auth = FirebaseAuth.getInstance() //파이어베이스 인증 관리 인스턴스를 얻는다.

        /* 회원가입 창이나 / 로그인 창으로 돌아가는 버튼
        회원가입 액티비티에서 왔을 경우와 로그인창에서 ID 비번찾기 기능으로 왔을 경우로 나뉨
        -> 이전 액티비티에서 인텐트로 플래그를 받아 조건문으로 판단한다.
        */

        go_back_button.setOnClickListener {
            val nextIntent = Intent(this, SignUpActivity::class.java)
            startActivity(nextIntent) //회원가입창으로 전환
        }
        goto_profile_button.setOnClickListener {
            val nextIntent = Intent(this, SignUpInsertProfileActivity::class.java)
            startActivity(nextIntent) //회원가입 과정(프로필설정)으로 전환
        }
    }

}