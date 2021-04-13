package com.example.sloaapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_signup.*

import java.util.*

class SignUpActivity : AppCompatActivity() {
    // Firebase Authentication 및 SNS로그인 API 관련 변수들
    // 로그인을 관리해주는 클래스
    var auth: FirebaseAuth? = null

    // GoogleLogin 관리 클래스
    var googleSignInClient: GoogleSignInClient? = null

    // Facebook 로그인 처리 결과 관리 클래스
    var callbackManager: CallbackManager? = null

    //GoogleLogin
    val GOOGLE_LOGIN_CODE = 9001 // Intent Request ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        auth = FirebaseAuth.getInstance() //파이어베이스 인증 관리 인스턴스를 얻는다.

        email_signup_button.setOnClickListener { // 다음 버튼 ( 회원가입 수행 )
            signUpEmail(email_edittext.text.toString(),password_edittext.text.toString(),password_check_edittext.text.toString())
            // ID, PW, checkPW
        }

        goto_signin_button.setOnClickListener { // 이메일로 로그인 | 로그인 창으로 이동하는 버튼
            val nextIntent = Intent(this, SignInActivity::class.java)
            startActivity(nextIntent) // 로그인 창으로 전환
        }
        google_sign_in_button.setOnClickListener { //구글 로그인 버튼
            //First step
            googleLogin()
        }
        facebook_login_button.setOnClickListener { //페이스북 로그인 버튼
            //First step
            facebookLogin()
        }
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN) //구글 로그인 옵션
                .requestIdToken("879825227557-go53up60m131smb5o66k7s3mp21tun1d.apps.googleusercontent.com")//getString(R.string.default_web_client_id)
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)
        //printHashKey()
        callbackManager = CallbackManager.Factory.create()
    }

    override fun onStart() {
        super.onStart()
        //moveMainPage(auth?.currentUser)
        /*
        자동 로그인 기능인데 페이스북 자동로그인이 안풀려서 일단 주석씌워둠
        아마 로그아웃하는 API가 따로 있지 않을까 싶음 | 나중에 설정에 구현
         */
    }

    fun googleLogin(){
        var signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent,GOOGLE_LOGIN_CODE)
    }
    fun facebookLogin(){
        LoginManager.getInstance()
                .logInWithReadPermissions(this, Arrays.asList("public_profile","email"))

        LoginManager.getInstance()
                .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                    override fun onSuccess(result: LoginResult?) {
                        //Second step
                        handleFacebookAccessToken(result?.accessToken)
                    }

                    override fun onCancel() {

                    }

                    override fun onError(error: FacebookException?) {

                    }

                })
    }
    // Facebook 토큰을 Firebase로 넘겨주는 코드
    fun handleFacebookAccessToken(token: AccessToken?) {
        val credential = FacebookAuthProvider.getCredential(token?.token!!)
        auth?.signInWithCredential(credential)
                ?.addOnCompleteListener {
                    task ->
                    if(task.isSuccessful){
                        //Login
                        judgeUserAndGoNextPage(task.result?.user)
                    }else{
                        //Show the error message
                        Toast.makeText(this,task.exception?.message, Toast.LENGTH_LONG).show()
                    }
                }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode,resultCode,data)
        if(requestCode == GOOGLE_LOGIN_CODE){
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if(result!!.isSuccess){
                var account = result!!.signInAccount
                //Second step
                firebaseAuthWithGoogle(account)
            }
        }
    }
    fun firebaseAuthWithGoogle(account : GoogleSignInAccount?){
        var credential = GoogleAuthProvider.getCredential(account?.idToken,null)
        auth?.signInWithCredential(credential)
                ?.addOnCompleteListener {
                    task ->
                    if(task.isSuccessful){
                        //Login
                        judgeUserAndGoNextPage(task.result?.user)
                    }else{
                        //Show the error message
                        Toast.makeText(this,task.exception?.message, Toast.LENGTH_LONG).show()
                    }
                }
    }
    fun signUpEmail(userID : String, userPW : String , checkPW :String) { //이메일로 가입한다.
        if (userPW != checkPW) {
            Toast.makeText(this, "두 비밀번호가 서로 맞지 않습니다.", Toast.LENGTH_LONG).show()
        } else {
            try {
                auth?.createUserWithEmailAndPassword(userID, userPW)
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                judgeUserAndGoNextPage(task.result?.user)
                            } else if (task.exception?.message.isNullOrEmpty()) {
                                // 파이어베이스 인증과정 예외 발생 시 처리코드
                                Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                            }
                        }
            } catch (e: IllegalArgumentException) { //아무것도 입력되지 않았을 경우의 예외
                Toast.makeText(this, "빈 란을 모두 채워주세요.", Toast.LENGTH_LONG).show()
            }
        }
    }
    fun judgeUserAndGoNextPage(user: FirebaseUser?){ //SNS로그인 시 USER유효성 판단하고 캘린더 액티비티로 전환한다.
        if(user != null){
            startActivity(Intent(this,IdentityVerificationActivity::class.java)) // 가입입력정보에 문제가 없으면 본인인증 액티비티로 이동한다.
            finish()
        }
    }
}
