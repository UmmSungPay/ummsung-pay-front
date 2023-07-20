package com.project.ummsungpay

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.os.postDelayed
import kotlinx.android.synthetic.main.activity_card_info.button_confirm
import kotlinx.android.synthetic.main.activity_card_info.num
import kotlinx.android.synthetic.main.activity_card_info.numValue
import kotlinx.android.synthetic.main.activity_card_info.validity
import kotlinx.android.synthetic.main.activity_card_info.validityValue
import java.util.Locale

class CardInfoActivity : AppCompatActivity() {

    private var resultCardnum: String = ""
    private var resultDelete1: String = ""
    private var resultDelete2: String = ""
    private var resultReplace: String = ""
    private var resultValidity: String = ""

    private var tts: TextToSpeech? = null
    private val REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_info)

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

        resultCardnum = intent.getStringExtra("recognized cardnum").toString()
        resultValidity = intent.getStringExtra("recognized validity").toString()

        if (resultCardnum != null && resultValidity != null) {
            Handler(Looper.getMainLooper()).postDelayed({
                startTTS("카드정보가 인식되었습니다. 카드를 추가하시려면 화면을 터치해주세요.")
            }, 1000)

            //카드번호와 유효기간의 맨 처음 공백 제거
            val regexDelete = Regex("""\s""")
            resultDelete1 = regexDelete.replaceFirst(resultCardnum, "")
            resultDelete2 = regexDelete.replaceFirst(resultValidity, "")

            //16자리 숫자 내 줄바꿈을 공백으로 교체
            val regexReplace = Regex("""\n""")
            resultReplace = regexReplace.replace(resultDelete1, " ")

            numValue.text = resultReplace
            validityValue.text = resultDelete2
        }
        else {
            val intentRetry = Intent(this, CardRecognitionActivity::class.java)
            intentRetry.putExtra("isFail", 1)
            startActivity(intentRetry)
            finish()
        }


        button_confirm.setOnClickListener{
            val intentComplete = Intent(this, CardAddCompleteActivity::class.java)
            intentComplete.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            cardList.add(CardData("", resultReplace, resultValidity)) //MainActivity의 cardList에 새 카드정보 추가(카드이름 제외)
            startActivity(intentComplete)
            finish()
        }


    }

    private fun startTTS(txt: String) {
        tts!!.speak(txt, TextToSpeech.QUEUE_FLUSH, null, "")
    }
}