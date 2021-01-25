package com.al.mond.example

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.al.mond.digitcounter.DigitCounter
import com.al.mond.support.dp2px
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Very simple RecyclerView and Adapter Bind


        val counterView = findViewById<LinearLayout>(R.id.content)
        val meterStyleCounterView = DigitCounter(this)
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 52.dp2px())
        meterStyleCounterView.layoutParams = params
        counterView.addView(meterStyleCounterView)
        meterStyleCounterView.setNum(0, true)
        counterView.postDelayed({
            meterStyleCounterView.focusOn()
        }, 1300)


        findViewById<View>(R.id.roll_btn).setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                meterStyleCounterView.setNum(Random.nextInt(), true)
            }
        })
    }

}