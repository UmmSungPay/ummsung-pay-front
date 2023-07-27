package com.project.ummsungpay

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_card_manage.button_left
import kotlinx.android.synthetic.main.activity_card_manage.button_right
import java.util.Locale

class CardManageActivity : AppCompatActivity() {

    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_manage)

        var index: Int = 0 //카드 이동용 인덱스
        var arrayOfText = arrayOf("카드목록 확인", "즐겨찾기 카드", "카드 삭제") //메뉴 이동용 배열

        //액티비티별 intent
        val intent1 = Intent(this, CardListActivity::class.java)
        val intent2 = Intent(this, BookmarkActivity::class.java)
        val intent3 = Intent(this, CardDeleteActivity::class.java)

        val arrayOfIntent = arrayOf(intent1, intent2, intent3) //intent의 배열
        
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

        //메뉴 이동
        button_left.setOnClickListener{
            if (index == 0) {
                index = 1
            } else if (index == 1) {
                index = 3
            } else {
                index -= 1
            }
            startTTS(arrayOfText[index-1])

        }

        //메뉴 이동
        button_right.setOnClickListener {
            if (index == 0) {
                index = 1
            } else if (index == 3) {
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