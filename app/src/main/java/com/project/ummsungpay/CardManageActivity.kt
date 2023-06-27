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
    private val REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_manage)

        var index: Int = 0
        var arrayOfText = arrayOf("카드목록 확인", "즐겨찾기 카드", "카드 삭제")

        val intent1 = Intent(this, CardListActivity::class.java)
        val intent2 = Intent(this, BookmarkActivity::class.java)
        val intent3 = Intent(this, CardDeleteActivity::class.java)

        val arrayOfIntent = arrayOf(intent1, intent2, intent3)

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

        button_left.setOnLongClickListener {
            startActivity(arrayOfIntent[index-1])
            return@setOnLongClickListener (true)
        }

        button_right.setOnLongClickListener {
            startActivity(arrayOfIntent[index-1])
            return@setOnLongClickListener (true)
        }
    }

    private fun startTTS(txt: String) {
        tts!!.speak(txt, TextToSpeech.QUEUE_FLUSH, null, "")
    }
}