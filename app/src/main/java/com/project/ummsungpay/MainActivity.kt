package com.project.ummsungpay

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var main_navibar = findViewById(R.id.navi_bar) as BottomNavigationView

        main_navibar.run { setOnNavigationItemReselectedListener {
            when(it.itemId) {
                R.id.navi_cardlist -> {
                    val cardlistFragment = CardlistFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.flagment_container, cardlistFragment).commit()
                }

                R.id.navi_cardadd -> {
                    val cardaddFragment = CardaddFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.flagment_container, cardaddFragment).commit()
                }

                R.id.navi_cardpay -> {
                    val cardpayFragment = CardpayFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.flagment_container, cardpayFragment).commit()
                }

                R.id.navi_mypage -> {
                    val mypageFragment = MypageFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.flagment_container, mypageFragment).commit()
                }
            }
            true
        }
        selectedItemId = R.id.navi_cardlist
        }

    }
}