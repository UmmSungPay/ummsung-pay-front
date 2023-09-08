package com.project.ummsungpay

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_cam_permission.button_left
import kotlinx.android.synthetic.main.activity_cam_permission.button_right
import java.util.Locale

class CamPermissionActivity : AppCompatActivity() {

    private var tts: TextToSpeech? = null //tts 관련 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cam_permission)

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

        Handler(Looper.getMainLooper()).postDelayed({
            startTTS("화면을 터치하면 카메라가 실행됩니다. 카드의 IC칩이 위로 가도록 위치시켜 주세요.")
        }, 1000)

        button_left.setOnClickListener {
            //카메라 권한 없을 시
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //권한 요청
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 1000)
            } else { //카메라 실행
                startActivity(Intent(this, VisionActivity::class.java))
                finish()
            }
        }

        button_right.setOnClickListener{
            //카메라 권한 없을 시
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //권한 요청
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 1000)
            } else { //카메라 실행
                startActivity(Intent(this, CardRecognitionActivity::class.java))
                finish()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Handler(Looper.getMainLooper()).postDelayed({
                    startTTS("카메라 사용 권한 요구를 거절하셨습니다.")
                }, 500)
            } else {
                startActivity(Intent(this, CardRecognitionActivity::class.java))
                finish()
            }
        }
    }

    private fun startTTS(txt: String) {
        tts!!.speak(txt, TextToSpeech.QUEUE_FLUSH, null, "")
    }
}