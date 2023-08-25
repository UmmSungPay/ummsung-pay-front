package com.project.ummsungpay

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.renderscript.Sampler.Value
import android.speech.tts.TextToSpeech
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.button_left
import kotlinx.android.synthetic.main.activity_main.button_right
import java.util.Locale

class MainActivity : AppCompatActivity() {

    //tts 관련 변수
    private var tts: TextToSpeech? = null
    //파이어베이스 데이터베이스
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    //파이어베이스 숫자 아이디
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //tts
        tts = TextToSpeech(this) {
            if (it == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.KOREAN)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                } else {
                }
            } else {
            }
        }

        //안내멘트
        Handler(Looper.getMainLooper()).postDelayed({
            startTTS("""메인화면으로 들어왔습니다. 왼쪽 또는 오른쪽 화면을 터치해 메뉴를 선택하고 어떤 메뉴인지 들을 수 있습니다. 
                |길게 터치하면 해당 메뉴로 이동합니다.""".trimMargin())
        }, 500)

        mainActivity = this //탈퇴 후 액티비티 종료를 위함

        var index: Int = 0 //메뉴 이동용 인덱스
        var arrayOfText = arrayOf("결제", "카드관리", "카드추가", "마이페이지") //메뉴 이동용 배열

        //액티비티별 intent
        val intent1 = Intent(this, CardChooseActivity::class.java)
        val intent2 = Intent(this, CardManageActivity::class.java)
        val intent3 = Intent(this, CamPermissionActivity::class.java)
        val intent4 = Intent(this, MypageActivity::class.java)

        val arrayOfIntent = arrayOf(intent1, intent2, intent3, intent4) //intent의 배열

        //메뉴 인덱스 이동
        button_left.setOnClickListener {
            if (index == 0) {
                index = 1
            } else if (index == 1) {
                index = 4
            } else {
                index -= 1
            }
            startTTS(arrayOfText[index - 1])
        }

        //메뉴 인덱스 이동
        button_right.setOnClickListener {
            if (index == 0) {
                index = 1
            } else if (index == 4) {
                index = 1
            } else {
                index += 1
            }
            startTTS(arrayOfText[index - 1])
        }

        //메뉴 선택
        button_left.setOnLongClickListener {
            if (index == 0) {
                startActivity(arrayOfIntent[0])
            } else {
                startActivity(arrayOfIntent[index - 1])
            }
            return@setOnLongClickListener (true)
        }

        //메뉴 선택
        button_right.setOnLongClickListener {
            if (index == 0) {
                startActivity(arrayOfIntent[0])
            } else {
                startActivity(arrayOfIntent[index - 1])
            }
            return@setOnLongClickListener (true)
        }
    }

    private fun startTTS(txt: String) { //tts 실행 함수
        tts!!.speak(txt, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    companion object {
        var mainActivity : MainActivity? = null
    }

}