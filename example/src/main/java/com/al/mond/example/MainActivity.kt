package com.al.mond.example

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.al.mond.digitcounter.DigitCounter


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val counter = findViewById<DigitCounter>(R.id.counter)

        findViewById<View>(R.id.roll_btn).setOnClickListener {
            counter.set(1000, 3, useComma = false)
        }
    }

}