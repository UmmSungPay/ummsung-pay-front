package com.project.ummsungpay

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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

        //tts
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

        firebaseAuth = FirebaseAuth.getInstance() //firebase auth 객체

        val firebaseId = firebaseAuth.currentUser?.uid.toString() //파이어베이스 숫자 아이디

        //파이어베이스 데이터베이스
        database = Firebase.database
        databaseReference = database.getReference("users")
        
        resultCardname = intent.getStringExtra("recognized cardname").toString()
        resultCardnum = intent.getStringExtra("recognized cardnum").toString()
        resultValidity = intent.getStringExtra("recognized validity").toString()

        //카드번호, 유효기간 추출
        if (resultCardnum != null && resultValidity != null) {
            Handler(Looper.getMainLooper()).postDelayed({
                startTTS("카드정보가 모두 인식되었습니다. 카드를 추가하시려면 화면을 터치해주세요.")
            }, 1000)

            //카드번호와 유효기간의 맨 처음 공백 제거
            val regexDelete = Regex("""\s""")
            resultDelete1 = regexDelete.replaceFirst(resultCardnum, "")
            resultDelete2 = regexDelete.replaceFirst(resultValidity, "")

            //16자리 숫자 내 줄바꿈을 공백으로 교체
            val regexReplace = Regex("""\n""")
            resultReplace = regexReplace.replace(resultDelete1, " ")

            nameValue.text = resultCardname
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

            //데이터베이스에 새 카드정보 추가
            databaseReference.child(firebaseId).child("cardlist").child(resultCardname).child("number").setValue(resultReplace)
            databaseReference.child(firebaseId).child("cardlist").child(resultCardname).child("validity").setValue(resultValidity)

            startActivity(intentComplete)
            finish()
        }


    }

    private fun startTTS(txt: String) {
        tts!!.speak(txt, TextToSpeech.QUEUE_FLUSH, null, "")
    }
}