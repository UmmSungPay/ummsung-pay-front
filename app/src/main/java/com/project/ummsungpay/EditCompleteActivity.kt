package com.project.ummsungpay

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_edit_complete.button
import java.util.Locale

class EditCompleteActivity : AppCompatActivity() {

    //전달받은 정보
    private var cardNow: String = ""
    private var newName: String = ""

    //tts
    private var tts: TextToSpeech? = null
    private val REQUEST_CODE = 1

    //파이어베이스 데이터베이스
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    //파이어베이스 숫자 아이디
    private lateinit var firebaseAuth: FirebaseAuth
    //임시 카드 정보
    private var tempNum: String = ""
    private var tempVal: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_complete)

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

        //전달받은 정보
        cardNow = intent.getStringExtra("CardNow").toString() //변경할 이름
        newName = intent.getStringExtra("NewName").toString() //새로운 이름


        databaseReference.child(firebaseId).child("cardlist").child(newName)

        databaseReference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }
            override fun onDataChange(snapshot: DataSnapshot) {
                var cardList = snapshot.child(firebaseId).child("cardlist")

                for (card in cardList.children) { //모든 카드 중
                    if (card.key.toString() == cardNow) { //이름을 변경할 카드에서
                        tempNum = card.child("number").value.toString() //카드 번호 임시저장
                        tempVal = card.child("validity").value.toString() //유효기간 임시저장
                    }

                    //새로운 이름 + 임시저장한 정보로 이루어진 카드 추가
                    databaseReference.child(firebaseId).child("cardlist").child(newName).child("number").setValue(tempNum)
                    databaseReference.child(firebaseId).child("cardlist").child(newName).child("validity").setValue(tempVal)

                    //원래 카드 삭제
                    databaseReference.child(firebaseId).child("cardlist").child(cardNow).removeValue()
                }

                button.setOnClickListener {
                    finish()
                }
            }
        })

        Handler(Looper.getMainLooper()).postDelayed({
            startTTS("카드 이름을 변경했습니다. 화면을 터치하면 카드 관리 메뉴로 이동합니다.")
        }, 500)
    }

    private fun startTTS(txt: String) {
        tts!!.speak(txt, TextToSpeech.QUEUE_FLUSH, null, "")
    }
}