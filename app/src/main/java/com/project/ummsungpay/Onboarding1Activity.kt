package com.project.ummsungpay

import android.os.Bundle
import androidx.activity.ComponentActivity
import android.content.Intent
import kotlinx.android.synthetic.main.activity_onboarding1.button_next


class Onboarding1Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding1)

        button_next.setOnClickListener{
            val intent = Intent(this, Onboarding2Activity::class.java)
            startActivity(intent)
            finish()
        }
    }
}