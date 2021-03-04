package com.al.mond.digitcounter

import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView.OnScrollListener.SCROLL_STATE_FLING
import android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * Created by mond on 2017. 6. 29..
 */
class DigitCounter @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {
    private var function: () -> Unit = {}
    private val itemWidth = resources.getDimensionPixelSize(R.dimen.counter_digit_item_width)
    private val itemHeight = resources.getDimensionPixelSize(R.dimen.counter_digit_item_height)
    private val commaWidth = resources.getDimensionPixelSize(R.dimen.counter_digit_comma_width)
    private val textSize = resources.getDimensionPixelSize(R.dimen.odo_meter_font_size)

    private val digits: LinearLayout

    private val counterHeight = resources.getDimensionPixelSize(R.dimen.counter_height)


    init {
        inflate(context, R.layout.digit_counter, this)
        digits = findViewById(R.id.digitCounter)
    }

    fun set(value: Int, tic: Int = 1) {
        removeCallbacks(function)
        reset()
        function = { setCounter(value, tic) }
        post(function)
    }

    private fun setCounter(input: Int, tic: Int) {
        reset()

        var before = input - tic
        var number = input
        
        val afterPresentNumber = LinkedList<Int>()
        val beforePresentNumber = LinkedList<Int>()


        if(number == 0){
            afterPresentNumber.push(0)
            beforePresentNumber.push(0)
        }else{
            while (number > 0) {
                afterPresentNumber.push(number % 10)
                number /= 10
            }
            while (before > 0) {
                beforePresentNumber.push(before % 10)
                before /= 10
            }
        }

        if (afterPresentNumber.size > beforePresentNumber.size) {
            beforePresentNumber.add(0, 0)
        }

        for (index in 0 until afterPresentNumber.size) {
            addComma(index, afterPresentNumber.size)
            addDigit(
                index,
                beforePresentNumber[index],
                afterPresentNumber[index],
                afterPresentNumber.lastIndex == index
            )
        }
    }

    private fun addDigit(
        index: Int,
        before: Int,
        after: Int,
        isLast: Boolean
    ) {
        val digit = RecyclerView(context)
        digit.layoutParams = LinearLayout.LayoutParams(itemWidth, counterHeight)
        digit.adapter = CounterNumberAdapter()
        val layoutManager = LinearLayoutManager(context)
        digit.layoutManager = layoutManager
        digit.tag = after.toString()
        digits.addView(digit)
        val finalDigit = animationRollCount(index) + before  // 자리수에따라 회전카운트가 늘어난다.

        if (isLast) {
            digit.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                var oldState: Int = SCROLL_STATE_IDLE
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (oldState == SCROLL_STATE_FLING && newState == SCROLL_STATE_IDLE) {
                        post { lastRoll() }
                    }else {
                        oldState = newState
                    }
                }
            })
        }

        digit.post {
            val movementPos = finalDigit * itemHeight + (counterHeight + itemHeight) / 2
            digit.smoothScrollBy(0, movementPos)
        }
    }

    private fun animationRollCount(index: Int): Int {
        val max = if (index > 5) 5 else index
        return (max + 1) * 10 - 2
    }

    private fun addComma(index: Int, finalDigitSize: Int) {
        if ((finalDigitSize - index) % 3 == 0 && index != 0 && finalDigitSize != index) {
            val comma = TextView(context)
            val params = LinearLayout.LayoutParams(commaWidth, itemHeight)
            params.gravity = Gravity.CENTER
            comma.layoutParams = params
            comma.text = ","
            comma.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            comma.includeFontPadding = false
            comma.setTypeface(comma.typeface, Typeface.BOLD)
            comma.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize * 0.8f)
            comma.height = textSize
            digits.addView(comma)
        }
    }

    private fun lastRoll() {
        for (digit in digits.children) {
            if ((digit is RecyclerView).not()) continue
            val currentPosition =
                ((digit as RecyclerView).layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
            val from = currentPosition % 10
            val to = Integer.parseInt(digit.tag as String)
            val moveDistance = getDistance(from, to)
            if (from != to) {
                digit.smoothScrollBy(0, itemHeight * moveDistance)
            }
        }
    }

    private fun getDistance(from: Int, to: Int) =
        if (to > from)
            to - from
        else
            to + 10 - from

    private fun reset() {
        digits.removeAllViews()
    }

    private class CounterNumberAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.digitview, parent, false)
            return DigitCounterView(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is DigitCounterView) {
                holder.setNumber(position % 10)
            }
        }

        override fun getItemCount(): Int {
            return 10000000
        }

        override fun getItemId(position: Int): Long {
            return (position % 10).toLong()
        }
    }

    internal class DigitCounterView(view: View) : RecyclerView.ViewHolder(view) {
        var number: TextView = view.findViewById(R.id.digit_number)
        fun setNumber(number: Int) {
            this.number.text = number.toString()
        }
    }
}


fun Number.dp2px() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    Resources.getSystem().displayMetrics
).toInt()