package com.project.ummsungpay

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_pin_complete.button_next
import java.util.Locale

class PinCompleteActivity : AppCompatActivity() {

    private var tts: TextToSpeech? = null //tts 관련 변수
    //파이어베이스 데이터베이스
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth //파이어베이스 숫자 아이디

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_complete)

        firebaseAuth = FirebaseAuth.getInstance() //firebase auth 객체

        //파이어베이스 데이터베이스
        database = Firebase.database
        databaseReference = database.getReference("users")

        val password = intent.getIntegerArrayListExtra("pwFinal").toString()

        val firebaseId = firebaseAuth.currentUser?.uid.toString()

        databaseReference.child(firebaseId).child("password").setValue(password)

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

        //안내 멘트
        Handler(Looper.getMainLooper()).postDelayed({
            startTTS("PIN 설정이 완료되었습니다. 화면을 터치하면 메인화면으로 이동합니다.")
        }, 500)

        button_next.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    private fun startTTS(txt: String) { //tts 실행 함수
        tts!!.speak(txt, TextToSpeech.QUEUE_FLUSH, null, "")
    }
}