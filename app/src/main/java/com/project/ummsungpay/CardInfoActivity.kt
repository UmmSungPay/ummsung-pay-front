package com.project.ummsungpay

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_card_info.button_confirm
import kotlinx.android.synthetic.main.activity_card_info.numValue
import kotlinx.android.synthetic.main.activity_card_info.validityValue

class CardInfoActivity : AppCompatActivity() {

    private var resultCardnum: String = ""
    private var resultDelete: String = ""
    private var resultReplace: String = ""
    private var resultValidity: String = ""
    private var allText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_info)


        allText = intent.getStringExtra("recognized text").toString()
        if (allText != "") {
            textExtraction(allText)
        }
        else {
            val intentRetry = Intent(this, CardRecognitionActivity::class.java)
            Toast.makeText(this, "카드정보를 인식하지 못했습니다.\n다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            startActivity(intentRetry)
            finish()
        }


        button_confirm.setOnClickListener{
            val intentComplete = Intent(this, CardAddCompleteActivity::class.java)
            intentComplete.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            cardList.add(CardData(null, resultReplace, resultValidity)) //MainActivity의 cardList에 새 카드정보 추가(카드이름 제외)
            startActivity(intentComplete)
            finish()
        }
    }

    private fun textExtraction(txt: String) {
        
        //16자리 숫자 추출
        val regexCardnum = Regex("""\s\d{4}\s\d{4}\s\d{4}\s\d{4}""")
        val matchCardnum: MatchResult? = regexCardnum.find(txt)
        resultCardnum = matchCardnum!!.value

        val regexValidity = Regex("""\d{2}/\d{2}""")
        val matchValidity: MatchResult? = regexValidity.find(txt)
        resultValidity = matchValidity!!.value

        //맨 앞 공백 제거
        val regexDelete = Regex("""\s""")
        resultDelete = regexDelete.replaceFirst(resultCardnum, "")
                
        //카드번호 내 줄바꿈을 공백으로 교체
        val regexReplace = Regex("""\n""")
        resultReplace = regexReplace.replace(resultDelete, " ")
                

        //카드번호 표시
        numValue.text = resultReplace
        //유효기간 표시
        validityValue.text = resultValidity
    }
}