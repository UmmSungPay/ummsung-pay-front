package com.project.ummsungpay

import android.content.Intent
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
import kotlinx.android.synthetic.main.activity_card_choose.button_left
import kotlinx.android.synthetic.main.activity_card_choose.button_right
import kotlinx.android.synthetic.main.activity_card_choose.data_name
import kotlinx.android.synthetic.main.activity_card_choose.data_number
import kotlinx.android.synthetic.main.activity_card_choose.data_validity
import java.util.Locale
import java.util.concurrent.Executor

class NameEditActivity : AppCompatActivity() {

    //tts
    private var tts: TextToSpeech? = null
    //카드 선택
    var index: Int = 0
    var newCardList = emptyList<CardData>()
    var temp = CardData("", "", "")
    //파이어베이스 데이터베이스
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    //파이어베이스 숫자 아이디
    private lateinit var firebaseAuth: FirebaseAuth
    var cardList = emptyList<CardData>() //카드 목록 저장용 list
    var selectedCard: String = "" //선택한 카드

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_name_edit)

        val intent = Intent(this, NameEdit2Activity::class.java)

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

        var cardHowMany: Int = 0 //카드 개수

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {

                //데이터베이스로부터 카드 목록 가져오기
                var cards = snapshot.child(firebaseId).child("cardlist")

                for (card in cards.children) { //카드 갯수 세기
                    cardHowMany += 1
                }

                var name : String = ""
                var number : String = ""
                var validity : String = ""
                var eachCard = CardData("", "", "")

                if (cardHowMany != 0) { //카드가 있을 때

                    //안내멘트
                    Handler(Looper.getMainLooper()).postDelayed({
                        startTTS("총 $cardHowMany 개의 카드가 있습니다. 길게 터치하여 카드를 선택한 후 이름을 수정할 수 있습니다.")
                    }, 500)

                    for (card in cards.children) {
                        name = card.key.toString()
                        number = card.child("number").value.toString()
                        validity = card.child("validity").value.toString()

                        eachCard = CardData(name, number, validity)
                        cardList += eachCard
                    }

                    //첫 화면에 뜰 카드
                    data_name.text = cardList[0].card_name
                    data_number.text = cardList[0].card_number
                    data_validity.text = cardList[0].card_validity

                    //카드 이동
                    button_left.setOnClickListener {
                        if (index == 0) {
                            index = 1
                        } else if (index == 1) {
                            index = cardHowMany
                        } else {
                            index -= 1
                        }
                        startTTS(cardList[index - 1].card_name)
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
                        startTTS(cardList[index - 1].card_name)
                        refreshUI()
                    }

                    //카드 선택
                    button_left.setOnLongClickListener {
                        if (index == 0) {
                            selectedCard = cardList[0].card_name
                        } else {
                            selectedCard = cardList[index-1].card_name
                        }
                        startTTS("$selectedCard 카드의 이름을 수정합니다.")
                        intent.putExtra("CardNow", cardList[index - 1].card_name)
                        startActivity(intent)
                        finish()
                        return@setOnLongClickListener (true)
                    }
                    button_right.setOnLongClickListener {
                        if (index == 0) {
                            selectedCard = cardList[0].card_name
                        } else {
                            selectedCard = cardList[index-1].card_name
                        }
                        startTTS("$selectedCard 카드의 이름을 수정합니다.")
                        intent.putExtra("CardNow", cardList[index - 1].card_name)
                        startActivity(intent)
                        finish()
                        return@setOnLongClickListener (true)
                    }

                } else { //카드가 없을 떄
                    data_number.text = "카드 없음"
                    Handler(Looper.getMainLooper()).postDelayed({
                        startTTS("등록된 카드가 없습니다.")
                    }, 500)
                }

            }
        })
    }

    fun refreshUI() { //카드 이동 시 화면상의 카드정보 변경
        data_name.text = cardList[index-1].card_name
        data_number.text = cardList[index-1].card_number
        data_validity.text = cardList[index-1].card_validity
    }

    private fun startTTS(txt: String) { //tts 실행 함수
        tts!!.speak(txt, TextToSpeech.QUEUE_FLUSH, null, "")
    }
}