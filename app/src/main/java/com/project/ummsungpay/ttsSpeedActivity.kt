package com.project.ummsungpay

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.values
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_tts_speed.button_left
import kotlinx.android.synthetic.main.activity_tts_speed.button_right
import java.util.Locale

class ttsSpeedActivity : AppCompatActivity() {

    private var tts: TextToSpeech? = null //tts 관련 변수
    //파이어베이스 데이터베이스
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    //파이어베이스 숫자 아이디
    private lateinit var firebaseAuth: FirebaseAuth
    var speed : Float = 1.0f //음성 안내 속도

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tts_speed)

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

        firebaseAuth = FirebaseAuth.getInstance() //firebase auth 객체

        val firebaseId = firebaseAuth.currentUser?.uid.toString() //파이어베이스 숫자 아이디

        //파이어베이스 데이터베이스
        database = Firebase.database
        databaseReference = database.getReference("users")

        var index = 0 //메뉴 이동용 인덱스
        var arrayOfText = arrayOf("기본", "약간 빠르게", "빠르게")
        var arrayOfSpeed = arrayOf(1.0f, 1.4f, 1.8f)

        button_left.setOnClickListener {
            if (index == 0) {
                index = 1
            } else if (index == 1) {
                index = 3
            } else {
                index -= 1
            }
            startTTS(arrayOfText[index-1])
        }

        button_right.setOnClickListener {
            if (index == 0) {
                index = 1
            } else if (index == 3) {
                index = 1
            } else {
                index += 1
            }
            startTTS(arrayOfText[index-1])
        }

        button_left.setOnLongClickListener {
            databaseReference.child(firebaseId).child("speed").setValue(arrayOfSpeed[index-1])
            startTTS("음성 속도가 변경되었습니다.")
            return@setOnLongClickListener (true)
        }

        button_right.setOnLongClickListener {
            databaseReference.child(firebaseId).child("speed").setValue(arrayOfSpeed[index-1])
            startTTS("음성 속도가 변경되었습니다.")
            return@setOnLongClickListener (true)
        }

    }

    private fun startTTS(txt: String) { //tts 실행 함수
        tts!!.speak(txt, TextToSpeech.QUEUE_FLUSH, null, "")
    }

}