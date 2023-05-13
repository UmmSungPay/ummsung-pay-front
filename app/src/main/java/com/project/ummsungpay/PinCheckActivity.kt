package com.project.ummsungpay

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.ComponentActivity
import kotlinx.android.synthetic.main.activity_pin_setting.input_box

class PinCheckActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_check)

        val toNextIntent = Intent(this, PinCompleteActivity::class.java) //다음 페이지로 이동하는 intent
        var pwLength: Int = 0

        input_box.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                pwLength = s.toString().length
                if (pwLength == 8 && s.toString() == intent.getStringExtra("data_pin")) {
                    startActivity(toNextIntent)
                }
            }
        })
    }
}