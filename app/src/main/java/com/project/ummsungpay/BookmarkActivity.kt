package com.project.ummsungpay

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import kotlinx.android.synthetic.main.activity_bookmark.button_left
import kotlinx.android.synthetic.main.activity_bookmark.button_right
import kotlinx.android.synthetic.main.activity_bookmark.data_name
import kotlinx.android.synthetic.main.activity_bookmark.data_number
import kotlinx.android.synthetic.main.activity_bookmark.data_validity
import java.util.Locale

class BookmarkActivity : AppCompatActivity() {

    private var tts: TextToSpeech? = null
    var index : Int = 0 //카드 이동용 인덱스

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark)

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

        //첫 화면에 띄울 카드정보 설정
        data_name.text = cardList[0].card_name
        data_number.text = cardList[0].card_number
        data_validity.text = cardList[0].card_validity

        var cardHowMany = cardList.size //카드 개수

        //안내멘트
        Handler(Looper.getMainLooper()).postDelayed({
            if (bookmark == -1) {
                startTTS("""현재 즐겨찾기로 등록된 카드가 없습니다. 
                |카드를 선택한 뒤, 길게 터치하여 등록하세요.""".trimMargin())
            } else {
                var bookmarkNow : String = cardList[bookmark].card_name
                startTTS("""현재 즐겨찾기 카드는 $bookmarkNow 입니다.
                |변경하시려면 카드를 선택한 뒤, 길게 터치하세요.""".trimMargin())
            }
        }, 500)

        //카드 이동
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

        //카드 이동
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

        //북마크 카드 선택
        button_left.setOnLongClickListener {
            bookmark = index - 1
            startTTS("즐겨찾기 카드로 등록되었습니다. 이전 화면으로 돌아갑니다.")
            Handler(Looper.getMainLooper()).postDelayed({
                finish()
            }, 4000)

            return@setOnLongClickListener(true)
        }

        //북마크 카드 선택
        button_right.setOnLongClickListener {
            bookmark = index - 1
            startTTS("즐겨찾기 카드로 등록되었습니다. 이전 화면으로 돌아갑니다.")
            Handler(Looper.getMainLooper()).postDelayed({
                finish()
            }, 4000)
            return@setOnLongClickListener(true)
        }
    }

    private fun startTTS(txt: String) { //tts 실행 함수
        tts!!.speak(txt, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    fun refreshUI() { //카드 이동 시 화면상의 카드정보 변경
        data_name.text = cardList[index-1].card_name
        data_number.text = cardList[index-1].card_number
        data_validity.text = cardList[index-1].card_validity
    }
}