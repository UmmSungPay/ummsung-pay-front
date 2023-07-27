package com.project.ummsungpay

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_pin_complete.button_next
import java.util.Locale

class PinCompleteActivity : AppCompatActivity() {

    private var tts: TextToSpeech? = null //tts 관련 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_complete)

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