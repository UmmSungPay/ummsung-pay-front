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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.values
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_card_name.button
import java.util.Locale

class NameEdit2Activity : AppCompatActivity() {

    //tts
    private var tts: TextToSpeech? = null

    //stt
    private lateinit var speechRecognizer: SpeechRecognizer
    var cardname: String = "" //카드이름을 저장할 변수

    //파이어베이스 데이터베이스
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    //파이어베이스 숫자 아이디
    private lateinit var firebaseAuth: FirebaseAuth

    //전달받은 정보
    private var cardNow: String = ""

    //효과음
    var mediaPlayer : MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_name_edit2)

        //전달받은 정보
        cardNow = intent.getStringExtra("CardNow").toString()

        //다음 액티비티
        val intentNext = Intent(this, EditCompleteActivity::class.java)

        firebaseAuth = FirebaseAuth.getInstance() //firebase auth 객체

        val firebaseId = firebaseAuth.currentUser?.uid.toString() //파이어베이스 숫자 아이디

        //파이어베이스 데이터베이스
        database = Firebase.database
        databaseReference = database.getReference("users")

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
            startTTS("화면을 터치한 후 새로운 카드 이름을 말씀해주세요.")
        }, 500)

        //음성인식 시작
        button.setOnClickListener{
            mediaPlayer?.start()
            cardname = ""
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this@NameEdit2Activity)
            speechRecognizer.setRecognitionListener(recognitionListener)
            speechRecognizer.startListening(intent)
        }

        //카드이름 저장
        button.setOnLongClickListener {
            intentNext.putExtra("CardNow", cardNow)
            intentNext.putExtra("NewName", cardname)
            startActivity(intentNext)
            finish()
            return@setOnLongClickListener(true)
        }

    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= 23 &&
            ContextCompat.checkSelfPermission(this@NameEdit2Activity, android.Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@NameEdit2Activity, arrayOf(android.Manifest.permission.RECORD_AUDIO), 0)
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