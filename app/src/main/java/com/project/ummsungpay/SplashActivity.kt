package com.project.ummsungpay

import android.os.Bundle
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Locale

class SplashActivity : AppCompatActivity() {

    //tts 관련 변수
    private var tts: TextToSpeech? = null
    private val REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //최초실행 여부 확인 관련 변수
        val pref : SharedPreferences = getApplicationContext().getSharedPreferences("com.project.ummsungpay", MODE_PRIVATE)
        val token: String? = pref.getString("token", null)

        //INTERNET(tts) 사용권한 얻기
        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.INTERNET), REQUEST_CODE)
        }

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

        //안내멘트
        Handler(Looper.getMainLooper()).postDelayed({
            startTTS("음성페이를 실행합니다.")
        }, 500)

        //최초실행 여부 확인
        Handler(Looper.getMainLooper()).postDelayed({
            if (token == "False" || token == null) { //첫 실행이라면 온보딩으로 이동
                val intentFirst = Intent(this, Onboarding1Activity::class.java)
                startActivity(intentFirst)
                pref.edit().putString("token", "true").apply()
                finish()
            } else { //첫 실행이 아니라면 로그인으로 이동
                val intentNotFirst = Intent(this, LoginActivity::class.java)
                startActivity(intentNotFirst)
                finish()
            }

        }, 3000) //스플래시 화면 3초동안 유지
    }

    private fun startTTS(txt: String) { //tts 실행 함수
        tts!!.speak(txt, TextToSpeech.QUEUE_FLUSH, null, "")
    }

}