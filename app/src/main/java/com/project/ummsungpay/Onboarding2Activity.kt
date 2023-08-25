package com.project.ummsungpay

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_onboarding1.button_next
import java.util.Locale

class Onboarding2Activity : ComponentActivity() {

    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding2)

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
            startTTS("""화면의 왼쪽과 오른쪽을 터치해 메뉴와 카드를 선택하고, 어떤 메뉴와 카드인지 들을 수 있습니다.
                |화면을 길게 터치하면 선택한 메뉴로 이동하거나 카드를 선택할 수 있습니다.
                |화면을 터치해 주세요.""".trimMargin())
        }, 500)

        //화면 터치 -> 온보딩3으로 이동
        button_next.setOnClickListener{
            val intent = Intent(this, Onboarding3Activity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        }
    }

    private fun startTTS(txt: String) { //tts 실행 함수
        tts!!.speak(txt, TextToSpeech.QUEUE_FLUSH, null, "")
    }
}