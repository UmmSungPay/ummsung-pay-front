package com.project.ummsungpay

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import androidx.biometric.BiometricManager
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_card_delete.data_name
import kotlinx.android.synthetic.main.activity_card_delete.data_number
import kotlinx.android.synthetic.main.activity_card_delete.data_validity
import kotlinx.android.synthetic.main.activity_card_delete.button_left
import kotlinx.android.synthetic.main.activity_card_delete.button_right
import java.util.Locale
import java.util.concurrent.Executor

class CardDeleteActivity : AppCompatActivity() {

    private var tts: TextToSpeech? = null //tts 관련 변수
    var index : Int = 0 //카드 이동용 인덱스
    //지문인식
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: androidx.biometric.BiometricPrompt
    private lateinit var promptInfo: androidx.biometric.BiometricPrompt.PromptInfo
    //파이어베이스 데이터베이스
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    //파이어베이스 숫자 아이디
    private lateinit var firebaseAuth: FirebaseAuth
    var cardList = emptyList<CardData>() //카드목록 저장용 list
    var bookmark : Any? = "" //북마크 유무 확인용 변수

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

        firebaseAuth = FirebaseAuth.getInstance() //firebase auth 객체

        val firebaseId = firebaseAuth.currentUser?.uid.toString() //파이어베이스 숫자 아이디

        //파이어베이스 데이터베이스
        database = Firebase.database
        databaseReference = database.getReference("users")

        var cardHowMany: Int = 0 //카드 개수

        //데이터베이스로부터 카드 목록 가져오기
        databaseReference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }
            override fun onDataChange(snapshot: DataSnapshot) {

                //즐겨찾기 카드가 설정되었는지 확인하는 변수
                bookmark = snapshot.child(firebaseId).child("bookmark").value
                //선택한 카드 이름을 저장하는 변수
                var selectedCard : String = ""

                var cards = snapshot.child(firebaseId).child("cardlist")
                for (card in cards.children) {

                    var name = card.key.toString()
                    var number = card.child("number").value.toString()
                    var validity = card.child("validity").value.toString()

                    var eachCard = CardData(name, number, validity)
                    cardList += eachCard
                    cardHowMany += 1
                }

                if (cardList.size != 0) { //카드가 있을 때

                    Handler(Looper.getMainLooper()).postDelayed({
                        startTTS("""카드를 선택한 뒤 길게 터치하면 선택한 카드가 삭제됩니다.""")
                    }, 500)

                    data_name.text = cardList[0].card_name
                    data_number.text = cardList[0].card_number
                    data_validity.text = cardList[0].card_validity

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

                    button_right.setOnClickListener {
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

                    //삭제할 카드 선택
                    button_left.setOnLongClickListener {

                        if (index == 0) {
                            selectedCard = cardList[0].card_name
                        } else {
                            selectedCard = cardList[index-1].card_name
                        }
                        startTTS("$selectedCard 카드를 선택하셨습니다. 삭제하시려면 지문을 인식해주세요.")
                        authFingerprint(firebaseId)

                        return@setOnLongClickListener (true)
                    }

                    button_right.setOnLongClickListener {

                        if (index == 0) {
                            selectedCard = cardList[0].card_name
                        } else {
                            selectedCard = cardList[index-1].card_name
                        }
                        startTTS("$selectedCard 카드를 선택하셨습니다. 삭제하시려면 지문을 인식해주세요.")
                        authFingerprint(firebaseId)

                        return@setOnLongClickListener (true)
                    }
                } else { //카드가 없을 때
                    data_number.text = "카드 없음"
                    Handler(Looper.getMainLooper()).postDelayed({
                        startTTS("등록된 카드가 없습니다.")
                    }, 500)

                }
            }
        })
    }

    fun authFingerprint(id : String) {
        //지문인식 결과에 따른 동작 설정
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = androidx.biometric.BiometricPrompt(this, executor, object: androidx.biometric.BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
            }

            override fun onAuthenticationSucceeded(result: androidx.biometric.BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)

                //지문인식 성공시
                //선택한 카드가 즐겨찾기 카드라면 즐겨찾기 값 삭제
                if (index != 0 && bookmark == cardList[index-1].card_name) { //선택한 카드가 즐겨찾기 카드라면
                    //데이터베이스에서 bookmark 삭제
                    databaseReference.child(id).child("bookmark").removeValue()
                } else if (index == 0 && bookmark == cardList[0].card_name) {
                    databaseReference.child(id).child("bookmark").removeValue()
                }

                //카드 삭제
                if (index == 0) {
                    startTTS("${cardList[0].card_name} 카드를 삭제했습니다. 이전 메뉴로 돌아갑니다.")
                    //선택한 카드 삭제
                    databaseReference.child(id).child("cardlist").child(cardList[0].card_name).removeValue()
                    Handler(Looper.getMainLooper()).postDelayed({
                        finish()
                    }, 4000)
                } else {
                    startTTS("${cardList[index-1].card_name} 카드를 삭제했습니다. 이전 메뉴로 돌아갑니다.")
                    //선택한 카드 삭제
                    databaseReference.child(id).child("cardlist").child(cardList[index-1].card_name).removeValue()
                    Handler(Looper.getMainLooper()).postDelayed({
                        finish()
                    }, 4000)
                }
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

        biometricPrompt.authenticate(promptInfo)
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