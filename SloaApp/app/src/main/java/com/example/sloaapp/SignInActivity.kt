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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_signin.*
import java.util.*


class SignInActivity : AppCompatActivity() {

    // Firebase Authentication 및 SNS로그인 API 관련 변수들
    // 로그인을 관리해주는 파이어베이스 인스턴스 변수
    var auth: FirebaseAuth? = null

    // GoogleLogin 관리 클래스
    var googleSignInClient: GoogleSignInClient? = null

    // Facebook 로그인 처리 결과 관리 클래스
    var callbackManager: CallbackManager? = null

    //GoogleLogin
    val GOOGLE_LOGIN_CODE = 9001 // Intent Request ID
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        auth = FirebaseAuth.getInstance() //파이어베이스 인증 관리 인스턴스를 얻는다.

        email_login_button.setOnClickListener { //시작하기 버튼
            signInEmail()
        }
        email_signup_button.setOnClickListener { // 회원가입 버튼
            val nextIntent = Intent(this, SignUpActivity::class.java)
            startActivity(nextIntent) // 회원 가입창으로 전환
        }
        email_pw_search_button.setOnClickListener { // PW 찾기 버튼
            val nextIntent = Intent(this, PasswordResetActivity::class.java)
            startActivity(nextIntent) // 본인 인증 창으로 전환
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
        //judgeUserAndGoNextPage(auth?.currentUser)
        /*
        자동 로그인 기능인데 페이스북 자동로그인이 안풀려서 일단 주석씌워둠
        아마 로그아웃하는 API가 따로 있지 않을까 싶음 | 나중에 설정에 구현
         */
    }
    /*
    fun printHashKey() {
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.i("TAG", "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("TAG", "printHashKey()", e)
        } catch (e: Exception) {
            Log.e("TAG", "printHashKey()", e)
        }
    }
    */

    fun googleLogin(){
        var signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent,GOOGLE_LOGIN_CODE)
    }
    fun facebookLogin(){
        LoginManager.getInstance()
            .logInWithReadPermissions(this, Arrays.asList("public_profile","email"))

        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult>{
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
                    Toast.makeText(this,task.exception?.message,Toast.LENGTH_LONG).show()
                }
            }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode,resultCode,data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_LOGIN_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Toast.makeText(this,"구글 로그인 성공 | "+account.id ,Toast.LENGTH_LONG).show()
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this,"구글 로그인 실패 | " + e,Toast.LENGTH_LONG).show()
            }
        }/*
        if(requestCode == GOOGLE_LOGIN_CODE){
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if(result!!.isSuccess){
                var account = result!!.signInAccount
                //Second step
                firebaseAuthWithGoogle(account)
            }
        }
        */
    }
    fun firebaseAuthWithGoogle(account : GoogleSignInAccount?){
        var credential = GoogleAuthProvider.getCredential(account?.idToken,null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener { task ->
                if(task.isSuccessful){
                    //Login
                    judgeUserAndGoNextPage(task.result?.user)
                }else{
                    //Show the error message
                    Toast.makeText(this,task.exception?.message,Toast.LENGTH_LONG).show()
                }
            }
    }

    fun signInEmail(){ // 이메일로 로그인한다.
        try {
        auth?.signInWithEmailAndPassword(email_edittext.text.toString(),password_edittext.text.toString())
            ?.addOnCompleteListener {
                task ->
                if (task.isSuccessful) {
                    // 로그인
                    judgeUserAndGoNextPage(task.result?.user)
                } else {
                    // 파이어베이스 인증과정 예외 발생 시 처리코드
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
        } catch(e: IllegalArgumentException){ //아무것도 입력되지 않았을 경우의 예외
            Toast.makeText(this,"아이디와 비밀번호를 입력해주세요.",Toast.LENGTH_LONG).show()
        }
    }
    fun judgeUserAndGoNextPage(user:FirebaseUser?){
        if(user != null){
            startActivity(Intent(this,MonthCalendarActivity::class.java))
            finish()
        }
    }
}
