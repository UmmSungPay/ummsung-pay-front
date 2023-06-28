package com.project.ummsungpay

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.button_left
import kotlinx.android.synthetic.main.activity_main.button_right
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private var tts: TextToSpeech? = null
    private val REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var index: Int = 0
        var arrayOfText = arrayOf("결제", "카드관리", "카드추가", "마이페이지")

        val intent1 = Intent(this, CardPayActivity::class.java)
        val intent2 = Intent(this, CardManageActivity::class.java)
        val intent3 = Intent(this, CamPermissionActivity::class.java)
        val intent4 = Intent(this, MypageActivity::class.java)

        val arrayOfIntent = arrayOf(intent1, intent2, intent3, intent4)

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
                index = 4
            } else {
                index -= 1
            }
            startTTS(arrayOfText[index-1])

        }

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