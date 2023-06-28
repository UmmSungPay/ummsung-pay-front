package com.project.ummsungpay

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_card_list.data_name
import kotlinx.android.synthetic.main.activity_card_list.data_number
import kotlinx.android.synthetic.main.activity_card_list.data_validity
import kotlinx.android.synthetic.main.activity_main.button_left
import kotlinx.android.synthetic.main.activity_main.button_right
import java.util.Locale

class CardChooseActivity : ComponentActivity() {

    private var tts: TextToSpeech? = null
    private val REQUEST_CODE = 1
    var index: Int = 0
    var tempCardList = cardList
    var tempBookmark = CardData("", "", "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_choose)

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

        if(bookmark != -1){
            tempBookmark = tempCardList[bookmark]
            tempCardList.removeAt(bookmark)
            tempCardList.add(0, tempBookmark)
        }

        data_name.text = tempCardList[0].card_name
        data_number.text = tempCardList[0].card_number
        data_validity.text = tempCardList[0].card_validity

        var cardHowMany = tempCardList.size

        Handler(Looper.getMainLooper()).postDelayed({
            startTTS("화면의 왼쪽이나 오른쪽을 터치하여 카드를 선택하세요. 길게 터치하면 결제가 시작됩니다.")
        }, 1000)

        button_left.setOnClickListener{
            if (index == 0) {
                index = 1
            } else if (index == 1) {
                index = cardHowMany
            } else {
                index -= 1
            }
            startTTS(tempCardList[index-1].card_name)
            refreshUI()
        }

        button_right.setOnClickListener {
            if (index == 0) {
                index = 1
            } else if (index == cardHowMany) {
                index = 1
            } else {
                index += 1
            }
            startTTS(tempCardList[index-1].card_name)
            refreshUI()
        }

        button_left.setOnLongClickListener {
            var selectedCard: String = tempCardList[index-1].card_name
            startTTS("$selectedCard 를 선택하셨습니다.")
            return@setOnLongClickListener (true)
        }

        button_right.setOnLongClickListener {
            var selectedCard: String = tempCardList[index-1].card_name
            startTTS("$selectedCard 를 선택하셨습니다.")
            return@setOnLongClickListener (true)
        }
    }

    fun refreshUI() {
        data_name.text = tempCardList[index-1].card_name
        data_number.text = tempCardList[index-1].card_number
        data_validity.text = tempCardList[index-1].card_validity
    }

    private fun startTTS(txt: String) {
        tts!!.speak(txt, TextToSpeech.QUEUE_FLUSH, null, "")
    }
}

