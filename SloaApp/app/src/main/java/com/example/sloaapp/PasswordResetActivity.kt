package com.example.sloaapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_password_reset.*

class PasswordResetActivity : AppCompatActivity() {
    // Firebase Authentication 및 SNS로그인 API 관련 변수들
    // 로그인을 관리해주는 클래스
    var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_reset)

        auth = FirebaseAuth.getInstance() //파이어베이스 인증 관리 인스턴스를 얻는다.

        go_back_button.setOnClickListener {
            val nextIntent = Intent(this, SignInActivity::class.java)
            startActivity(nextIntent) /* 로그인 창으로 전환 */
        }

        goto_signin_button.setOnClickListener {
            if(sendPasswordResetMail()) { //boolean 값에 따라 액티비티 전환을 결정
                val nextIntent = Intent(this, SignInActivity::class.java)
                startActivity(nextIntent) /* 비밀번호 찾기 메일을 보낸 후 로그인 창으로 전환 */
            }
        }
    }
    private fun sendPasswordResetMail() :Boolean {
        val userEmailAddress = password_reset_email_edittext.text.toString()
        if (userEmailAddress.isNotEmpty()) {
            auth?.sendPasswordResetEmail(userEmailAddress)
                   ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this,"입력된 사용자 메일로 비밀번호 찾기 메일을 전송했습니다.", Toast.LENGTH_LONG).show()
                        }
                    }
            return true //메일 전송 성공
        } else {
            Toast.makeText(this,"이메일 란에 이메일을 입력해주세요.", Toast.LENGTH_LONG).show()
            return false // 메일 전송 실패
        }
    }
}
