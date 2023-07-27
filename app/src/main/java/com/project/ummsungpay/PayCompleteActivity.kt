package com.project.ummsungpay

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import kotlinx.android.synthetic.main.activity_pay_complete.button_toFirst
import java.util.Locale

class PayCompleteActivity : ComponentActivity() {

    //tts 관련 변수
    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay_complete)

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

        Handler(Looper.getMainLooper()).postDelayed({
            startTTS("결제가 완료되었습니다. 화면을 터치하면 메인 화면으로 이동합니다.")
        }, 500)

        button_toFirst.setOnClickListener{
            finish()
        }
    }

    private fun startTTS(txt: String) { //tts 실행 함수
        tts!!.speak(txt, TextToSpeech.QUEUE_FLUSH, null, "")
    }
}