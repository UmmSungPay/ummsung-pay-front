package com.project.ummsungpay

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_delete_account.button
import java.util.Locale
import java.util.concurrent.Executor
import kotlin.system.exitProcess

class DeleteAccountActivity : AppCompatActivity() {

    private var tts: TextToSpeech? = null //tts 관련 변수
    //로그아웃 관련 변수
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    //지문인식
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: androidx.biometric.BiometricPrompt
    private lateinit var promptInfo: androidx.biometric.BiometricPrompt.PromptInfo
    //파이어베이스 데이터베이스
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    //타 액티비티 종료
    val mainActivity = MainActivity.mainActivity
    val mypageActivity = MypageActivity.mypageActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_account)

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

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        firebaseAuth = FirebaseAuth.getInstance() //firebase auth 객체

        val firebaseId = firebaseAuth.currentUser?.uid.toString() //파이어베이스 숫자 아이디

        //파이어베이스 데이터베이스
        database = Firebase.database
        databaseReference = database.getReference("users").child(firebaseId)

        Handler(Looper.getMainLooper()).postDelayed({
            startTTS("탈퇴 시 모든 계정 정보가 사라집니다. 탈퇴하시려면 화면을 터치한 뒤 지문을 인식해주세요.")
        }, 500)

        button.setOnClickListener {
            authFingerprint()
        }
    }

    private fun signOut() {
        //firebaseAuth.signOut()
        FirebaseAuth.getInstance().signOut()
        googleSignInClient?.signOut()
    }

    fun authFingerprint () {
        //지문인식 결과에 따른 동작 설정
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = androidx.biometric.BiometricPrompt(this, executor, object: androidx.biometric.BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
            }

            override fun onAuthenticationSucceeded(result: androidx.biometric.BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)

                signOut()
                //지문인식 성공시 계정 정보 삭제
                databaseReference.removeValue()

                mainActivity?.finish()
                mypageActivity?.finish()
                
                Handler(Looper.getMainLooper()).postDelayed({
                    startTTS("탈퇴되었습니다. 로그인 화면으로 이동합니다.")
                }, 500)

                val intent = Intent(this@DeleteAccountActivity, LoginActivity::class.java)
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

        biometricPrompt.authenticate(promptInfo)
    }


    private fun startTTS(txt: String) {
        tts!!.speak(txt, TextToSpeech.QUEUE_FLUSH, null, "")
    }
}