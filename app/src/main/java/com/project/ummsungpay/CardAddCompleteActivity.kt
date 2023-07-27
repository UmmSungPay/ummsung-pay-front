package com.project.ummsungpay

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.os.postDelayed
import kotlinx.android.synthetic.main.activity_card_add_complete.button_toFirst
import java.util.Locale

class CardAddCompleteActivity : AppCompatActivity() {

    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_add_complete)


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
            startTTS("카드 추가가 완료되었습니다. 화면을 터치하면 메인화면으로 이동합니다.")
        }, 500)

        button_toFirst.setOnClickListener{
            finish()
        }
    }

    private fun startTTS(txt: String) {
        tts!!.speak(txt, TextToSpeech.QUEUE_FLUSH, null, "")
    }
}