package com.project.ummsungpay

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import androidx.core.app.ActivityCompat
import androidx.core.os.postDelayed
import kotlinx.android.synthetic.main.activity_bookmark.button_left
import kotlinx.android.synthetic.main.activity_bookmark.button_right
import kotlinx.android.synthetic.main.activity_bookmark.data_name
import kotlinx.android.synthetic.main.activity_bookmark.data_number
import kotlinx.android.synthetic.main.activity_bookmark.data_validity
import java.util.Locale

class BookmarkActivity : AppCompatActivity() {

    private var tts: TextToSpeech? = null
    private val REQUEST_CODE = 1
    var index : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark)

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

        data_name.text = cardList[0].card_name
        data_number.text = cardList[0].card_number
        data_validity.text = cardList[0].card_validity

        var cardHowMany = cardList.size

        Handler(Looper.getMainLooper()).postDelayed({
            if (bookmark == -1) {
                startTTS("""현재 즐겨찾기로 등록된 카드가 없습니다. 
                |화면의 왼쪽이나 오른쪽을 터치해 카드를 선택한 뒤, 길게 터치하여 등록하세요.""".trimMargin())
            } else {
                var bookmarkNow : String = cardList[bookmark].card_name
                startTTS("""현재 즐겨찾기 카드는 $bookmarkNow 입니다.
                |변경하시려면 화면의 왼쪽이나 오른쪽을 터치해 카드를 선택한 뒤, 길게 터치하여 변경하세요.""".trimMargin())
            }
        }, 1000)

        button_left.setOnClickListener{
            if (index == 0) {
                index = 1
            } else if (index == 1) {
                index = cardHowMany
            } else {
                index -= 1
            }
            startTTS(cardList[index-1].card_name)
            refreshUI()
        }

        button_right.setOnClickListener{
            if (index == 0) {
                index = 1
            } else if (index == cardHowMany) {
                index = 1
            } else {
                index += 1
            }
            startTTS(cardList[index-1].card_name)
            refreshUI()
        }

        button_left.setOnLongClickListener {
            bookmark = index - 1
            startTTS("즐겨찾기 카드로 등록되었습니다. 이전 화면으로 돌아갑니다.")
            Handler(Looper.getMainLooper()).postDelayed({
                finish()
            }, 4000)

            return@setOnLongClickListener(true)
        }

        button_right.setOnLongClickListener {
            bookmark = index - 1
            startTTS("즐겨찾기 카드로 등록되었습니다. 이전 화면으로 돌아갑니다.")
            Handler(Looper.getMainLooper()).postDelayed({
                finish()
            }, 4000)
            return@setOnLongClickListener(true)
        }


    }

    private fun startTTS(txt: String) {
        tts!!.speak(txt, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    fun refreshUI() {
        data_name.text = cardList[index-1].card_name
        data_number.text = cardList[index-1].card_number
        data_validity.text = cardList[index-1].card_validity
    }
}