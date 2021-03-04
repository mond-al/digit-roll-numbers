package com.al.mond.example

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.al.mond.digitcounter.DigitCounter
import com.al.mond.digitcounter.dp2px
import kotlin.math.absoluteValue
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val counterView = findViewById<LinearLayout>(R.id.content)
        val digitCounter = DigitCounter(this)
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 52.dp2px())

        digitCounter.layoutParams = params
        counterView.addView(digitCounter)
        digitCounter.set(0)

        findViewById<View>(R.id.roll_btn).setOnClickListener {
            digitCounter.set(Random.nextInt().absoluteValue)
        }
    }

}