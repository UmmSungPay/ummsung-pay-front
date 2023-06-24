package com.project.ummsungpay

import android.os.Bundle
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import java.util.Locale

class SplashActivity : ComponentActivity() {

    private var tts: TextToSpeech? = null
    private val REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val pref : SharedPreferences = getApplicationContext().getSharedPreferences("com.project.ummsungpay", MODE_PRIVATE)
        val token: String? = pref.getString("token", null)

        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.INTERNET), REQUEST_CODE)
        }

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

        Handler(Looper.getMainLooper()).postDelayed({
            startTTS("음성페이를 시작합니다.")
        }, 1000)

        Handler(Looper.getMainLooper()).postDelayed({
            if (token == "False" || token == null) {
                val intentFirst = Intent(this, Onboarding1Activity::class.java)
                intentFirst.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intentFirst)
                pref.edit().putString("token", "true").apply()
                finish()
            } else {
                val intentNotFirst = Intent(this, MainActivity::class.java)
                intentNotFirst.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intentNotFirst)
                finish()
            }

        }, 3000)
    }

    private fun startTTS(txt: String) {
        tts!!.speak(txt, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}