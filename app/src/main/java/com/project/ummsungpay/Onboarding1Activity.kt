package com.project.ummsungpay

import android.os.Bundle
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_onboarding1.button_next
import java.util.Locale


class Onboarding1Activity : AppCompatActivity() {

    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding1)

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
            startTTS("음성페이에 오신 것을 환영합니다. 화면을 터치해 주세요.")
        }, 500)

        //화면 터치 -> 온보딩2로 이동
        button_next.setOnClickListener {
            val intent = Intent(this, Onboarding2Activity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        }
    }

    private fun startTTS(txt: String) { //tts 실행 함수
        tts!!.speak(txt, TextToSpeech.QUEUE_FLUSH, null, "")
    }
}