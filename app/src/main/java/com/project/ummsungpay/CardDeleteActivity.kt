package com.project.ummsungpay

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import kotlinx.android.synthetic.main.activity_card_delete.data_name
import kotlinx.android.synthetic.main.activity_card_delete.data_number
import kotlinx.android.synthetic.main.activity_card_delete.data_validity
import kotlinx.android.synthetic.main.activity_card_delete.button_left
import kotlinx.android.synthetic.main.activity_card_delete.button_right
import java.util.Locale

class CardDeleteActivity : AppCompatActivity() {

    private var tts: TextToSpeech? = null //tts 관련 변수
    var index : Int = 0 //카드 이동용 인덱스

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_delete)

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

        //안내 멘트
        Handler(Looper.getMainLooper()).postDelayed({
            startTTS("""카드를 선택한 뒤 길게 터치하면 선택하신 카드가 삭제됩니다.""")
        }, 500)

        //카드 이동
        button_left.setOnClickListener {
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

        //카드 삭제
        button_left.setOnLongClickListener {
            if(bookmark == index - 1) {
                bookmark = -1
            }
            startTTS("선택하신 ${cardList[index-1].card_name}를 삭제했습니다.")
            cardList.removeAt(index-1)
            refreshUI()
            return@setOnLongClickListener(true)
        }

        //카드 삭제
        button_right.setOnLongClickListener {
            if(bookmark == index - 1) {
                bookmark = -1
            }
            startTTS("선택하신 ${cardList[index-1].card_name}를 삭제했습니다.")
            cardList.removeAt(index-1)
            refreshUI()
            return@setOnLongClickListener(true)
        }
    }

    private fun startTTS(txt: String) {
        tts!!.speak(txt, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    fun refreshUI() { //카드 이동 시 화면상의 카드정보 변경
        data_name.text = cardList[index-1].card_name
        data_number.text = cardList[index-1].card_number
        data_validity.text = cardList[index-1].card_validity
    }
}