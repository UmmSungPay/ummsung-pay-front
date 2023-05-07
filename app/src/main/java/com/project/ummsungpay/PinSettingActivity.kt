package com.project.ummsungpay

import android.os.Bundle
import android.text.InputFilter
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import kotlinx.android.synthetic.main.activity_pin_setting.button_popup
import kotlinx.android.synthetic.main.activity_pin_setting.input_box

class PinSettingActivity : ComponentActivity() {

    //var imm : InputMethodManager? = null //키보드 InputMethodManager 변수 선언

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_setting)

        //키보드 InputMethodManager 세팅
        //imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager?



        var message: String = "" //팝업 메시지를 담을 변수

        button_popup.setOnClickListener{
            message = input_box.text.toString()
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }


}