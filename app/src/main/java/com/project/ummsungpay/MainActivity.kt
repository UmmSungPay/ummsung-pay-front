package com.project.ummsungpay

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_cardlist.card_view

class CardData(
    private var card_name: String? = null,
    private var card_number: String? = null,
    private var card_validity: String? = null
){
    fun getName(): String? {
        return card_name
    }
    fun setName(): String? {
        this.card_name = card_name
        return null
    }
    fun getNumber(): String? {
        return card_number
    }
    fun setNumber(): String? {
        this.card_number = card_number
        return null
    }
    fun getValidity(): String? {
        return card_validity
    }
    fun setValidity(): String? {
        this.card_validity = card_validity
        return null
    }
}

class MainActivity : AppCompatActivity() {

    var cardList: ArrayList<CardData> = arrayListOf(
        CardData("신한S20", "1234-5678-1234-5678", "05/28"),
        CardData("카카오", "0000-0000-0000-0000", "12/23"),
        CardData("KB국민", "9999-9999-9999-9999", "09/25")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //fragment로 데이터 전달
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(
            R.id.fragment_container,
            CardlistFragment()
        )
        transaction.commit()
        intent.putExtra("CardList", cardList)

        //하단 네비게이션 바
        var main_navibar = findViewById<BottomNavigationView>(R.id.navi_bar)

        main_navibar.run {
            setOnNavigationItemReselectedListener {
                when (it.itemId) {
                    R.id.navi_cardlist -> {
                        val cardlistFragment = CardlistFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, cardlistFragment).commit()
                    }

                    R.id.navi_cardadd -> {
                        val cardaddFragment = CardaddFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, cardaddFragment).commit()
                    }

                    R.id.navi_cardpay -> {
                        val cardpayFragment = CardpayFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, cardpayFragment).commit()
                    }

                    R.id.navi_mypage -> {
                        val mypageFragment = MypageFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, mypageFragment).commit()
                    }
                }
                true
            }
            selectedItemId = R.id.navi_cardlist
        }

    }
}