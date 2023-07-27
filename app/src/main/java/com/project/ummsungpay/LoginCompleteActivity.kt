package com.project.ummsungpay

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login_complete.button_toFirst

class LoginCompleteActivity : AppCompatActivity() {
    /*
    로그인한 계정에 PIN이 설정되어 있는지 확인 후,
    설정되어 있으면 MainActivity로
    그렇지 않으면 PinSettingActivity로 이동하도록
    */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_complete)

        //추후 삭제
        button_toFirst.setOnClickListener{
            val intent = Intent(this, PinSettingActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}