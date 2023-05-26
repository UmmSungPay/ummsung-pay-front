package com.project.ummsungpay

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_card_info.button_confirm
import kotlinx.android.synthetic.main.activity_card_info.numValue
import kotlinx.android.synthetic.main.activity_card_info.validityValue

class CardInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_info)

        val txt = intent.getStringExtra("recognized text").toString()

        val regexCardnum = Regex("""\d{4}\s\d{4}\s\d{4}\s\d{4}""")
        val regexValidity = Regex("""\d{2}/\d{2}""")
        //val regexCvc

        val matchCardnum: MatchResult? = regexCardnum.find(txt)
        val matchValidity: MatchResult?  = regexValidity.find(txt)
        //val matchCvc: MatchResult? = regexCvc.find(txt)

        val valueCardnum: String = matchCardnum!!.value
        val valueValidity: String = matchValidity!!.value
        //val valueCvc: String = matchCvc!!.value

        numValue.text = valueCardnum
        validityValue.text = valueValidity
        //cvcValue.text = valueCvc


        button_confirm.setOnClickListener{
            val intent = Intent(this, CardAddCompleteActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        }
    }
}