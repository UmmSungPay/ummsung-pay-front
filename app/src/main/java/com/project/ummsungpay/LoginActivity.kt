package com.project.ummsungpay

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.button_login
import java.util.Locale


class LoginActivity : AppCompatActivity() {

    //tts 관련 변수
    private var tts: TextToSpeech? = null
    //로그인 관련 변수
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 99
    //파이어베이스 데이터베이스
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    public override fun onStart() { //유저가 이미 앱에 구글 로그인을 했는지 확인
        super.onStart()

        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) { //이미 로그인이 되어있을 시
            Handler(Looper.getMainLooper()).postDelayed({
                startTTS("${account.displayName} 계정으로 로그인 되었습니다.")
            }, 500)
            toCompleteActivity(firebaseAuth.currentUser) //메인 액티비티로 이동
        } else {
            //안내 멘트
            Handler(Looper.getMainLooper()).postDelayed({
                startTTS("구글 로그인을 위해 화면을 터치해주세요.")
            }, 500)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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

        database = Firebase.database
        databaseReference = database.getReference("users")

        //버튼 -> 구글 로그인
        button_login.setOnClickListener {
            signIn()
        }

        //구글 로그인 옵션 구성. requestIdToken 등 요청
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestId()
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        firebaseAuth = FirebaseAuth.getInstance() //firebase auth 객체

    }

    fun toCompleteActivity(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                startTTS("로그인에 실패했습니다.")
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        //Toast.makeText(this, acct.id!!, Toast.LENGTH_SHORT).show()

        //Google SignInAccount 객체에서 ID 토큰을 가져와 Firebase Auth로 교환하고 Firebase에 인증
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startTTS("${acct.displayName} 계정으로 로그인 되었습니다. 메인화면으로 이동합니다.")

                    val firebaseId = firebaseAuth.currentUser?.uid.toString()

                    databaseReference.addValueEventListener(object: ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {

                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            val exist = snapshot.child(firebaseId)
                            if (exist.value == null) { //기존에 로그인한 계정이 아닌 경우
                                databaseReference.child(firebaseId).child("name")
                                    .setValue(acct.displayName.toString())
                                databaseReference.child(firebaseId).child("email")
                                    .setValue(acct.email.toString())
                            }
                        }
                    })
                    toCompleteActivity(firebaseAuth?.currentUser)
                } else {
                    startTTS("로그인에 실패했습니다.")
                }
            }
    }

    private fun signIn() { //로그인
        Handler(Looper.getMainLooper()).postDelayed({
            startTTS("계정을 선택해 주세요.")
        }, 500)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun startTTS(txt: String) { //tts 실행 함수
        tts!!.speak(txt, TextToSpeech.QUEUE_FLUSH, null, "")
    }
}