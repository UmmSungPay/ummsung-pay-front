package com.project.ummsungpay

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_mypage.button_left
import kotlinx.android.synthetic.main.activity_mypage.button_right
import java.util.Locale

class MypageActivity : AppCompatActivity() {


    private var tts: TextToSpeech? = null //tts 관련 변수
    //로그아웃 관련 변수
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        mypageActivity = this //탈퇴 후 액티비티 종료를 위함

        var index: Int = 0
        var arrayOfText = arrayOf("사용안내 재청취", "로그아웃", "탈퇴")


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

        //안내멘트
        Handler(Looper.getMainLooper()).postDelayed({
            startTTS("마이페이지로 들어왔습니다.")
        }, 500)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        firebaseAuth = FirebaseAuth.getInstance() //firebase auth 객체

        button_left.setOnClickListener{
            if (index == 0) {
                index = 1
            } else if (index == 1) {
                index = 4
            } else {
                index -= 1
            }
            startTTS(arrayOfText[index-1])

        }

        button_right.setOnClickListener {
            if (index == 0) {
                index = 1
            } else if (index == 4) {
                index = 1
            } else {
                index += 1
            }
            startTTS(arrayOfText[index-1])
        }

        val intent3 = Intent(this, DeleteAccountActivity::class.java)

        button_left.setOnLongClickListener {

            if (index == 0 || index == 1) {
                startTTS("""화면의 왼쪽과 오른쪽을 터치해 메뉴와 카드를 선택 할 수 있습니다. 
                    |화면을 길게 터치하면 선택한 메뉴로 이동합니다.""".trimMargin())
            } else if (index == 2) {
                signOut()
            } else {
                startActivity((intent3))
            }
            return@setOnLongClickListener (true)
        }

        button_right.setOnLongClickListener {

            if (index == 0 || index == 1) {
                startTTS("""화면의 왼쪽과 오른쪽을 터치해 메뉴와 카드를 선택 할 수 있습니다. 
                    |화면을 길게 터치하면 선택한 메뉴로 이동합니다.""".trimMargin())
            } else if (index == 2) {
                signOut()
            } else {
                startActivity((intent3))
            }
            return@setOnLongClickListener (true)
        }
    }

    private fun signOut() {
        //firebaseAuth.signOut()
        FirebaseAuth.getInstance().signOut()
        googleSignInClient?.signOut()

        var logoutIntent = Intent(this, LoginActivity::class.java)
        logoutIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startTTS("로그아웃 되었습니다. 로그인 화면으로 이동합니다.")
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(logoutIntent)
            finish()
        }, 4000)
    }

    private fun startTTS(txt: String) {
        tts!!.speak(txt, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    companion object {
        var mypageActivity : MypageActivity? = null
    }

}