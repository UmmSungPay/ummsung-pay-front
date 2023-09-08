package com.project.ummsungpay

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_card_info.button_confirm
import kotlinx.android.synthetic.main.activity_card_info.nameValue
import kotlinx.android.synthetic.main.activity_card_info.numValue
import kotlinx.android.synthetic.main.activity_card_info.validityValue
import java.util.Locale

class CardInfoActivity : AppCompatActivity() {

    //카드정보 추출
    private var resultCardname: String = ""
    private var resultCardnum: String = ""
    private var resultDelete1: String = ""
    private var resultDelete2: String = ""
    private var resultReplace: String = ""
    private var resultValidity: String = ""

    //tts
    private var tts: TextToSpeech? = null
    private val REQUEST_CODE = 1

    //파이어베이스 데이터베이스
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    //파이어베이스 숫자 아이디
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_info)

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

        firebaseAuth = FirebaseAuth.getInstance() //firebase auth 객체

        val firebaseId = firebaseAuth.currentUser?.uid.toString() //파이어베이스 숫자 아이디

        //파이어베이스 데이터베이스
        database = Firebase.database
        databaseReference = database.getReference("users")
        
        resultCardname = intent.getStringExtra("recognized cardname").toString()
        resultCardnum = intent.getStringExtra("recognized cardnum").toString()
        resultValidity = intent.getStringExtra("recognized validity").toString()

        //카드번호, 유효기간 추출
        //카드번호와 유효기간의 맨 처음 공백 제거
        val regexDelete = Regex("""\s""")
        resultDelete1 = regexDelete.replaceFirst(resultCardnum, "")
        resultDelete2 = regexDelete.replaceFirst(resultValidity, "")

        //16자리 숫자 내 줄바꿈을 공백으로 교체
        val regexReplace = Regex("""\n""")
        resultReplace = regexReplace.replace(resultDelete1, " ")

        //기존에 있는 카드인지 확인
        databaseReference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }
            override fun onDataChange(snapshot: DataSnapshot) {
                var duplicate: Boolean = false //중복 카드가 있는지 확인하는 변수

                var cardList = snapshot.child(firebaseId).child("cardlist")

                var cardHowMany : Int = 0 //카드 갯수를 저장할 변수

                for (card in cardList.children) {
                    cardHowMany += 1
                }

                if (cardHowMany != 0) { //기존에 등록된 카드가 있고
                    for (card in cardList.children) {
                        if (resultReplace == card.child("number").value.toString()
                            && resultDelete2 == card.child("validity").value.toString()) {
                            //카드번호와 유효기간이 동일한 카드가 있다면
                            duplicate = true
                        }
                    }
                }
                duplicateOrNot(duplicate)
            }
        })

        //화면에 값 띄우기
        nameValue.text = resultCardname
        numValue.text = resultReplace
        validityValue.text = resultDelete2

    }

    fun duplicateOrNot(result: Boolean) {

        if (result == false) {
            button_confirm.setOnClickListener{
                //데이터베이스에 새 카드정보 추가
                databaseReference.child(firebaseAuth.currentUser?.uid.toString()).child("cardlist").child(resultCardname).child("number").setValue(resultReplace)
                databaseReference.child(firebaseAuth.currentUser?.uid.toString()).child("cardlist").child(resultCardname).child("validity").setValue(resultDelete2)
                finish()
            }

            Handler(Looper.getMainLooper()).postDelayed({
                startTTS("카드추가가 완료되었습니다. 화면을 터치하면 메인화면으로 돌아갑니다.")
            }, 500)
        } else if (result == true) {
            Handler(Looper.getMainLooper()).postDelayed({
                startTTS("이미 등록된 카드입니다. 카메라로 돌아갑니다.")
            }, 500)

            val intent = Intent(this@CardInfoActivity, CardRecognitionActivity::class.java)

            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(intent)
                finish()
            }, 5000)
        }
    }

    private fun startTTS(txt: String) {
        tts!!.speak(txt, TextToSpeech.QUEUE_FLUSH, null, "")
    }
}