package com.project.ummsungpay

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import androidx.core.app.ActivityCompat
import androidx.core.os.postDelayed
import kotlinx.android.synthetic.main.activity_card_delete.data_name
import kotlinx.android.synthetic.main.activity_card_delete.data_number
import kotlinx.android.synthetic.main.activity_card_delete.data_validity
import kotlinx.android.synthetic.main.activity_card_delete.button_left
import kotlinx.android.synthetic.main.activity_card_delete.button_right
import java.util.Locale

class CardDeleteActivity : AppCompatActivity() {

    private var tts: TextToSpeech? = null
    private val REQUEST_CODE = 1
    var index : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_delete)

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
            startTTS("""화면의 왼쪽이나 오른쪽을 터치해 카드를 선택하세요.
                |길게 터치하면 선택하신 카드가 삭제됩니다.""".trimMargin())
        }, 1000)

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
            if(bookmark == index - 1) {
                bookmark = -1
            }
            startTTS("선택하신 ${cardList[index-1].card_name}를 삭제했습니다.")
            cardList.removeAt(index-1)
            refreshUI()
            return@setOnLongClickListener(true)
        }

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

    fun refreshUI() {
        data_name.text = cardList[index-1].card_name
        data_number.text = cardList[index-1].card_number
        data_validity.text = cardList[index-1].card_validity
    }
}