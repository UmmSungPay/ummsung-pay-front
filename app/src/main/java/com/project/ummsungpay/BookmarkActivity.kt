package com.project.ummsungpay

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_bookmark.data_name
import kotlinx.android.synthetic.main.activity_bookmark.data_number
import kotlinx.android.synthetic.main.activity_bookmark.data_validity
import kotlinx.android.synthetic.main.activity_card_list.button_left
import kotlinx.android.synthetic.main.activity_card_list.button_right
import kotlinx.android.synthetic.main.activity_card_list.data_name
import kotlinx.android.synthetic.main.activity_card_list.data_number
import kotlinx.android.synthetic.main.activity_card_list.data_validity
import java.util.Locale

class BookmarkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark)
    }
}