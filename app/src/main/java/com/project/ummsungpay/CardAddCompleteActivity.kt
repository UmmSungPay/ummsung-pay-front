package com.project.ummsungpay

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import kotlinx.android.synthetic.main.activity_card_add_complete.button_toFirst

class CardAddCompleteActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_add_complete)

        button_toFirst.setOnClickListener{
            finish()
        }
    }
}