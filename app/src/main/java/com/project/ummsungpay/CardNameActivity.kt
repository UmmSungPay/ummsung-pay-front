package com.project.ummsungpay

import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.postDelayed
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_card_name.button
import java.util.Locale

class CardNameActivity : AppCompatActivity() {

    //tts
    private var tts: TextToSpeech? = null

    //stt
    private lateinit var speechRecognizer: SpeechRecognizer
    var cardname: String = "" //카드이름을 저장할 변수

    //전달받은 정보
    private var cardnum: String = ""
    private var validity: String = ""

    //효과음
    var mediaPlayer : MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_name)

        //전달받은 정보
        cardnum = intent.getStringExtra("recognized cardnum").toString()
        validity = intent.getStringExtra("recognized validity").toString()

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

        //stt 권한 요청
        requestPermission()

        //RecognizerIntent 생성
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko_KR")

        //효과음
        mediaPlayer = MediaPlayer.create(this, R.raw.ding_sound_effect)

        Handler(Looper.getMainLooper()).postDelayed({
            startTTS("카드번호와 유효기간을 인식했습니다. 화면을 터치한 뒤 카드 이름을 말씀해주세요.")
        }, 500)

        //음성인식 시작
        button.setOnClickListener{
            //효과음 재생
            mediaPlayer?.start()

            cardname = ""
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this@CardNameActivity)
            speechRecognizer.setRecognitionListener(recognitionListener)
            speechRecognizer.startListening(intent)
        }

        //카드이름 저장
        button.setOnLongClickListener {
            val intent = Intent(this@CardNameActivity, CardInfoActivity::class.java)
            intent.putExtra("recognized cardname", cardname)
            intent.putExtra("recognized cardnum", cardnum)
            intent.putExtra("recognized validity", validity)
            startActivity(intent)
            finish()
            return@setOnLongClickListener(true)
        }

    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= 23 &&
            ContextCompat.checkSelfPermission(this@CardNameActivity, android.Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@CardNameActivity, arrayOf(android.Manifest.permission.RECORD_AUDIO), 0)
        }
    }

    private val recognitionListener: RecognitionListener = object : RecognitionListener {

        override fun onReadyForSpeech(params: Bundle) {}

        override fun onBeginningOfSpeech() {}

        override fun onRmsChanged(rmsdB: Float) {}

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {}

        override fun onError(error: Int) {
            val message = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "오디오 에러"
                SpeechRecognizer.ERROR_CLIENT -> "클라이언트 에러"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "퍼미션 없음"
                SpeechRecognizer.ERROR_NETWORK -> "네트워크 에러"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "네트워크 타임아웃"
                SpeechRecognizer.ERROR_NO_MATCH -> "찾을 수 없음"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RECOGNIZER가 바쁨"
                SpeechRecognizer.ERROR_SERVER -> "서버가 이상함"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "말하는 시간 초과"
                else -> "알 수 없는 오류"
            }
        }

        override fun onResults(results: Bundle) {
            val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            for (i in matches!!.indices) {
                cardname += matches[i]
                Handler(Looper.getMainLooper()).postDelayed({
                    startTTS("인식된 카드 이름은" + cardname +
                            "입니다. 맞으시면 길게, 아니라면 짧게 터치한 뒤 다시 말씀해 주세요.")
                }, 500)
            }
        }

        override fun onPartialResults(partialResults: Bundle) {}

        override fun onEvent(eventType: Int, params: Bundle) {}
    }

    private fun startTTS(txt: String) {
        tts!!.speak(txt, TextToSpeech.QUEUE_FLUSH, null, "")
    }
}