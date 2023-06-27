package com.project.ummsungpay

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.project.ummsungpay.ui.theme.UmmsungpayTheme
import kotlinx.android.synthetic.main.activity_main.button_left
import kotlinx.android.synthetic.main.activity_main.button_right
import java.util.Locale

class CardPayActivity : ComponentActivity() {

    private var tts: TextToSpeech? = null
    private val REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cardpay)

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

        Handler(Looper.getMainLooper()).postDelayed({
            startTTS("음성 결제 안내를 시작합니다. 즐겨찾기 카드를 사용하시겠습니까? 맞으실 경우 화면 왼쪽을, 아닌 경우 화면 오른쪽을 클릭해주세요.")
        }, 1000)

        button_left.setOnClickListener{
            startTTS("즐겨찾기 카드 결제를 선택하셨습니다. 길게 클릭하실 경우, 즐겨찾기 카드로 결제가 진행됩니다.")
        }
        button_left.setOnLongClickListener {
            val intent = Intent(this, CardPayFavoriteActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            return@setOnLongClickListener (true)
        }
        button_right.setOnClickListener {
            startTTS("즐겨찾기 카드를 사용하지 않습니다. 길게 클릭하실 경우, 다음 안내로 넘어갑니다.")
        }

        button_right.setOnLongClickListener {
            val intent = Intent(this, CardPayActivity1::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            return@setOnLongClickListener (true)
        }





    }

    private fun startTTS(txt: String) {
        tts!!.speak(txt, TextToSpeech.QUEUE_FLUSH, null, "")
    }
}

