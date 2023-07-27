package com.project.ummsungpay

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import kotlinx.android.synthetic.main.activity_main.button_left
import kotlinx.android.synthetic.main.activity_main.button_right
import java.util.Locale

class MainActivity : AppCompatActivity() {

    //tts 관련 변수
    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        var index: Int = 0 //메뉴 이동용 인덱스
        var arrayOfText = arrayOf("결제", "카드관리", "카드추가", "마이페이지") //메뉴 이동용 배열

        //액티비티별 intent
        val intent1 = Intent(this, CardChooseActivity::class.java)
        val intent2 = Intent(this, CardManageActivity::class.java)
        val intent3 = Intent(this, CamPermissionActivity::class.java)
        val intent4 = Intent(this, MypageActivity::class.java)

        val arrayOfIntent = arrayOf(intent1, intent2, intent3, intent4) //intent의 배열

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

        //메뉴 인덱스 이동
        button_left.setOnClickListener{
            if (index == 0) {
                index = 1
            } else if (index == 1) {
                index = 4
            } else {
                index -= 1
            }
            startTTS(arrayOfText[index-1])

        }

        //메뉴 인덱스 이동
        button_right.setOnClickListener {
            if (index == 0) {
                index = 1
            } else if (index == 4) {
                index = 1
            } else {
                index += 1
            }
            startTTS(arrayOfText[index-1])
        }

        //메뉴 선택
        button_left.setOnLongClickListener {
            startActivity(arrayOfIntent[index-1])
            return@setOnLongClickListener (true)
        }

        //메뉴 선택
        button_right.setOnLongClickListener {
            startActivity(arrayOfIntent[index-1])
            return@setOnLongClickListener (true)
        }
    }

    private fun startTTS(txt: String) { //tts 실행 함수
        tts!!.speak(txt, TextToSpeech.QUEUE_FLUSH, null, "")
    }

}