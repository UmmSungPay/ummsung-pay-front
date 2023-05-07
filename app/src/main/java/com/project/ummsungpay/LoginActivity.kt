package com.project.ummsungpay

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import kotlinx.android.synthetic.main.activity_login.google_login
import kotlinx.android.synthetic.main.activity_login.kakao_login
import kotlinx.android.synthetic.main.activity_onboarding1.button_next

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        /*
        google_login.setOnClickListener{
            val intent = Intent(this, PinSettingActivity::class.java)
            startActivity(intent)
            finish()
        }

        kakao_login.setOnClickListener{
            val intent = Intent(this, PinSettingActivity::class.java)
            startActivity(intent)
            finish()
        }
        */
    }


}