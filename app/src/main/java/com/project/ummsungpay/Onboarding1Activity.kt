package com.project.ummsungpay

import android.os.Bundle
import androidx.activity.ComponentActivity
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_onboarding1.button_next
import java.util.Locale

class Onboarding1Activity : ComponentActivity() {

    private var tts: TextToSpeech? = null
    private val REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding1)

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
            startTTS("음성페이에 오신 것을 환영합니다. 다음으로 이동하기 위해 화면을 터치해 주세요.")
        }, 1000)

        button_next.setOnClickListener{
            val intent = Intent(this, Onboarding2Activity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        }
    }

    private fun startTTS(txt: String) {
        tts!!.speak(txt, TextToSpeech.QUEUE_FLUSH, null, "")
    }
}