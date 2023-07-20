package com.project.ummsungpay

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import kotlinx.android.synthetic.main.activity_login_complete.button_toFirst

class LoginCompleteActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_complete)

        button_toFirst.setOnClickListener{
            val intent = Intent(this, PinSettingActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}