package com.project.ummsungpay

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login_complete.button_toFirst

class LoginCompleteActivity : AppCompatActivity() {

    //파이어베이스 데이터베이스
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    //파이어베이스 숫자 아이디
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_complete)

        firebaseAuth = FirebaseAuth.getInstance() //firebase auth 객체

        val firebaseId = firebaseAuth.currentUser?.uid.toString() //파이어베이스 숫자 아이디

        //파이어베이스 데이터베이스
        database = Firebase.database
        databaseReference = database.getReference("users")

        databaseReference.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }
            override fun onDataChange(snapshot: DataSnapshot) {
                val exist = snapshot.child(firebaseId).child("password")
                if (exist.value == null) { //기존에 설정된 pin번호가 없을 때
                    val toPin = Intent(this@LoginCompleteActivity, PinSettingActivity::class.java)
                    startActivity(toPin)
                    finish()
                } else { //기존에 설정된 pin번호가 있을 때
                    val toMain = Intent(this@LoginCompleteActivity, MainActivity::class.java)
                    startActivity(toMain)
                    finish()
                }
            }
        })
    }
}