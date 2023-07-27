package com.project.ummsungpay

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_card_list.data_name
import kotlinx.android.synthetic.main.activity_card_list.data_number
import kotlinx.android.synthetic.main.activity_card_list.data_validity
import kotlinx.android.synthetic.main.activity_main.button_left
import kotlinx.android.synthetic.main.activity_main.button_right
import java.util.Locale
import java.util.concurrent.Executor

class CardChooseActivity : AppCompatActivity() {

    //tts
    private var tts: TextToSpeech? = null
    //카드 선택
    var index: Int = 0
    var tempCardList = cardList
    var tempBookmark = CardData("", "", "")
    //지문인식
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: androidx.biometric.BiometricPrompt
    private lateinit var promptInfo: androidx.biometric.BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_choose)

        //결제완료 후 액티비티 이동
        val intent = Intent(this, PayCompleteActivity::class.java)

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

        //지문인식 사용 가능 여부 확인
        val biometricManager = androidx.biometric.BiometricManager.from(this)
        when (biometricManager.canAuthenticate(androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG or androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
            }
        }

        //지문인식 결과에 따른 동작 설정
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = androidx.biometric.BiometricPrompt(this, executor, object: androidx.biometric.BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
            }

            override fun onAuthenticationSucceeded(result: androidx.biometric.BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                
                //지문인식 성공시 PayCompleteActivity로 이동
                startActivity(intent)
                finish()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                startTTS("지문이 일치하지 않습니다. 다시 시도해주세요.")
            }
        })

        //지문인식 창 설정
        promptInfo = androidx.biometric.BiometricPrompt.PromptInfo.Builder()
            .setTitle("음성페이")
            .setSubtitle(" ")
            .setNegativeButtonText(" ")
            .build()
        
        if (bookmark != -1){ //북마크로 설정된 카드가 있으면
            tempBookmark = tempCardList[bookmark] //북마크 카드정보 임시로 저장
            tempCardList.removeAt(bookmark) //카드리스트에서 북마크 카드 삭제
            tempCardList.add(0, tempBookmark) //카드리스트의 맨 처음에 북마크 카드 삽입
        }

        //첫 화면에 띄울 카드정보 설정
        data_name.text = tempCardList[0].card_name
        data_number.text = tempCardList[0].card_number
        data_validity.text = tempCardList[0].card_validity

        var cardHowMany = tempCardList.size //카드 개수

        //안내멘트
        Handler(Looper.getMainLooper()).postDelayed({
            startTTS("카드를 선택한 뒤 길게 터치하면 결제가 시작됩니다.")
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
            startTTS(tempCardList[index-1].card_name)
            refreshUI()
        }

        //카드 이동
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

        //카드 선택
        button_left.setOnLongClickListener {
            var selectedCard: String = tempCardList[index-1].card_name
            startTTS("$selectedCard 를 선택하셨습니다. 지문을 인식해주세요.")
            biometricPrompt.authenticate(promptInfo)
            return@setOnLongClickListener (true)
        }

        //카드 선택
        button_right.setOnLongClickListener {
            var selectedCard: String = tempCardList[index-1].card_name
            startTTS("$selectedCard 를 선택하셨습니다. 지문을 인식해주세요.")
            biometricPrompt.authenticate(promptInfo)
            return@setOnLongClickListener (true)
        }
    }

    fun refreshUI() { //카드 이동 시 화면상의 카드정보 변경
        data_name.text = tempCardList[index-1].card_name
        data_number.text = tempCardList[index-1].card_number
        data_validity.text = tempCardList[index-1].card_validity
    }

    private fun startTTS(txt: String) { //tts 실행 함수
        tts!!.speak(txt, TextToSpeech.QUEUE_FLUSH, null, "")
    }
}

