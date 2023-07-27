package com.project.ummsungpay

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.button_left
import java.util.Locale

class LoginActivity : AppCompatActivity() {

    //tts 관련 변수
    private var tts: TextToSpeech? = null
    //로그인 관련 변수
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 99

    public override fun onStart() { //유저가 이미 앱에 구글 로그인을 했는지 확인
        super.onStart()

        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) { //이미 로그인이 되어있을 시
            toCompleteActivity(firebaseAuth.currentUser) //메인 액티비티로 이동
        } else {
            //안내 멘트
            Handler(Looper.getMainLooper()).postDelayed({
                startTTS("구글 로그인은 화면의 왼쪽을, 네이버 로그인은 화면의 오른쪽을 터치해주세요.")
            }, 500)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //tts
        tts = TextToSpeech(this) {
            if (it == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.KOREAN)
                if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                }
                else {
                }
            } else {
            }
        }

        //왼쪽 버튼 -> 구글 로그인
        button_left.setOnClickListener {
            signIn()
        }

        //구글 로그인 옵션 구성. requestIdToken 및 email 요청
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        firebaseAuth = FirebaseAuth.getInstance() //firebase auth 객체

    }

    fun toCompleteActivity(user: FirebaseUser?) { //LoginCompleteActivity로 이동
        if (user != null) {
            startActivity(Intent(this, LoginCompleteActivity::class.java))
            finish()
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                startTTS("로그인에 실패했습니다.")
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        //Toast.makeText(this, acct.id!!, Toast.LENGTH_SHORT).show()

        //Google SignInAccount 객체에서 ID 토큰을 가져와 Firebase Auth로 교환하고 Firebase에 인증
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startTTS("로그인 되었습니다.")
                    toCompleteActivity(firebaseAuth?.currentUser)
                } else {
                    startTTS("로그인에 실패했습니다.")
                }
            }
    }

    private fun signIn() { //로그인
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    fun signOut() { //로그아웃
        firebaseAuth.signOut() //firebase sign out
        googleSignInClient.signOut() //google sign out
            .addOnCompleteListener(this) {
                //google sign out 이후 동작
            }
    }


    private fun revokeAccess() { //회원 탈퇴
        firebaseAuth.signOut() //firebase sign out
        googleSignInClient.revokeAccess()
            .addOnCompleteListener(this) {
                //회원 탈퇴 후 동작
            }
    }

    private fun startTTS(txt: String) { //tts 실행 함수
        tts!!.speak(txt, TextToSpeech.QUEUE_FLUSH, null, "")
    }
}