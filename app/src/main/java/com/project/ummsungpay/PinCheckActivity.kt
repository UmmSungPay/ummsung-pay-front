package com.project.ummsungpay

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import android.util.DisplayMetrics
import android.widget.Button
import android.widget.TableRow
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_pin_setting.key_remove
import kotlinx.android.synthetic.main.activity_pin_setting.pin
import kotlinx.android.synthetic.main.activity_pin_setting.tableLayout
import java.util.Locale

class PinCheckActivity : AppCompatActivity() {

    private var tts: TextToSpeech? = null //tts 관련 변수
    var password = ArrayList<Int>() //PIN 저장 배열

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_check)

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

        //안내멘트
        Handler(Looper.getMainLooper()).postDelayed({
            startTTS("PIN번호를 한 번 더 입력해주세요.")
        }, 500)


        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val btnWidth: Int = getBtnWidth()

        key_remove.setOnClickListener{//'삭제' 음성안내
            startTTS("삭제")
        }

        key_remove.setOnLongClickListener{//삭제 동작
            if (password.size >= 1) { //1개 이상 입력된 상태일 때
                password.removeLast() //마지막 숫자 삭제
            }
            pin.text = "*".repeat(password.size)
            vibrator.vibrate(VibrationEffect.createOneShot(100, 10)) //동작 때마다 진동
            return@setOnLongClickListener (true)
        }
        reOrderKeyboard(btnWidth)
    }

    private fun reOrderKeyboard(btnWidth: Int){

        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        //배열을 하나 만들어 string 0부터 9까지 순서대로 넣기
        val keyNumberArr = ArrayList<Int>()
        for (i in 0..9) {
            keyNumberArr.add(i)
        }

        var tr: TableRow? = null
        var btn: Button? = null
        var randIndx: Int = 0;
        var random = kotlin.random.Random

        for (i in 0..(tableLayout.childCount-1)) {
            tr = tableLayout.getChildAt(i) as TableRow //열 이동

            for (i in 0..(tr.childCount-1)) {
                btn = tr.getChildAt(i) as Button //행 이동

                if(btn.id == -1) {
                    randIndx = random.nextInt(keyNumberArr.size) //0부터 9중에 랜덤으로 인덱스 하나 지정
                    btn.text = keyNumberArr[randIndx].toString() //지정된 인덱스에 해당하는 숫자를 버튼의 글자로 설정
                    btn.width = btnWidth/3
                    var btnNumber = keyNumberArr[randIndx]

                    keyNumberArr.removeIf{x -> x == keyNumberArr[randIndx]} //지정된 숫자를 배열에서 삭제

                    btn.setOnClickListener{//'숫자' 음성안내
                        startTTS(btnNumber.toString())
                    }

                    btn.setOnLongClickListener{//숫자 입력
                        password += btnNumber //배열 마지막에 숫자 추가
                        pin.text = "*".repeat(password.size) //시각요소 수정
                        vibrator.vibrate(VibrationEffect.createOneShot(100, 10)) //동작 때마다 진동
                        if (password.size == 6) { //6자리를 다 입력했을 경우
                            if (password == intent.getIntegerArrayListExtra("pwFirst")) { //PIN 일치할 경우
                                val toNext2 = Intent(this, PinCompleteActivity::class.java)
                                toNext2.putExtra("pwFinal", password) //다음 액티비티로 최종 비밀번호 전달
                                startActivity(toNext2)
                                finish()
                            } else {
                                for(i in 0..password.size - 1) { //PIN 불일치할 경우
                                    password.removeLast() //입력한 숫자 모두 삭제
                                }
                                startTTS("PIN번호가 일치하지 않습니다. 처음부터 다시 입력해 주세요.")
                            }
                        }
                        return@setOnLongClickListener (true)
                    }
                }
            }
        }
    }

    private fun getBtnWidth(): Int {
        val displaymetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displaymetrics)
        return displaymetrics.widthPixels
    }

    private fun startTTS(txt: String) { //tts 실행 함수
        tts!!.speak(txt, TextToSpeech.QUEUE_FLUSH, null, "")
    }
}