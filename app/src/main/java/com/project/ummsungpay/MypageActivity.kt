package com.project.ummsungpay

import android.content.Intent
import android.os.Build
import android.os.Bundle
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

        var index: Int = 0
        var arrayOfText = arrayOf("PIN 재등록", "로그아웃")


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
                index = 2
            } else {
                index -= 1
            }
            startTTS(arrayOfText[index-1])

        }

        button_right.setOnClickListener {
            if (index == 0) {
                index = 1
            } else if (index == 2) {
                index = 1
            } else {
                index += 1
            }
            startTTS(arrayOfText[index-1])
        }

        button_left.setOnLongClickListener {
            if (index == 2) {
                signOut()
            } else {
                val intent = Intent(this, PinSettingActivity::class.java)
                startActivity(intent)
            }
            return@setOnLongClickListener (true)
        }

        button_right.setOnLongClickListener {
            if (index == 2) {
                signOut()
            } else {
                val intent = Intent(this, PinSettingActivity::class.java)
                startActivity(intent)
                finish()
            }
            return@setOnLongClickListener (true)
        }
    }

    private fun signOut() {
        firebaseAuth.signOut()
        googleSignInClient?.signOut()

        var logoutIntent = Intent(this, LoginActivity::class.java)
        logoutIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(logoutIntent)
    }

    private fun startTTS(txt: String) {
        tts!!.speak(txt, TextToSpeech.QUEUE_FLUSH, null, "")
    }

}