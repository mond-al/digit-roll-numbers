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
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.AttrRes
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
    private val itemWidth = resources.getDimensionPixelSize(R.dimen.counter_digit_item_width)
    private val itemHeight = resources.getDimensionPixelSize(R.dimen.counter_digit_item_height)
    private val commaWidth = resources.getDimensionPixelSize(R.dimen.counter_digit_comma_width)
    private val textSize = resources.getDimensionPixelSize(R.dimen.odo_meter_font_size)

    private val digit: LinearLayout
    private val mask: View

    private var isAnimated = false


    private val counterHeight = resources.getDimensionPixelSize(R.dimen.counter_height)
    private val digitViews = ArrayList<RecyclerView>()

    private var diff = 0

    init {
        inflate(context, R.layout.digit_counter, this)
        digit = findViewById(R.id.digitCounter)
        mask = findViewById(R.id.digit)
    }

    fun setNum(count: Int, isAnimated: Boolean) {
        diff = 0
        this.isAnimated = isAnimated
        setCounter(count)
    }

    private fun setCounter(afterCount: Int) {
        var afterCount = afterCount
        reset()
        var beforeCount = afterCount - diff
        val afterDigitList = LinkedList<Int>()
        while (afterCount > 0) {
            afterDigitList.push(afterCount % 10)
            afterCount /= 10
        }
        val beforeDigitList = LinkedList<Int>()
        while (beforeCount > 0) {
            beforeDigitList.push(beforeCount % 10)
            beforeCount /= 10
        }
        val beforeDigitCount = beforeDigitList.size
        val afterDigitCount = afterDigitList.size
        if (afterDigitCount > beforeDigitCount) {
            beforeDigitList.add(0, 0)
        }
        mask.alpha = 0f
        for (afterDigit in 0 until afterDigitCount) {
            addCommaIfNeed(afterDigitCount, afterDigit)
            addDigit(afterDigit, beforeDigitList[afterDigit])
        }
    }

    private fun addDigit(
        afterDigit: Int,
        beforeDigit: Int
    ) {
        val recyclerView = RecyclerView(context)
        recyclerView.layoutParams = LinearLayout.LayoutParams(itemWidth, counterHeight)
        recyclerView.adapter = CounterNumberAdapter()
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        digit.addView(recyclerView)
        val targetPosition = getAnimateDistance(afterDigit) + beforeDigit
        recyclerView.tag = targetPosition
        recyclerView.post {
            val movementPos = targetPosition * itemHeight + (counterHeight + itemHeight) / 2
            if (isAnimated) recyclerView.smoothScrollBy(
                0,
                movementPos
            ) else recyclerView.scrollBy(0, movementPos)
        }
        digitViews.add(recyclerView)
    }

    private fun getAnimateDistance(index: Int): Int {
        val max = if (index > 5) 5 else index
        return (max + 1) * 10 - 2
    }

    private fun addCommaIfNeed(afterDigitCount: Int, index: Int) {
        if ((afterDigitCount - index) % 3 == 0 && index != 0 && afterDigitCount != index) {
            val comma = TextView(context)
            val params = LinearLayout.LayoutParams(commaWidth, itemHeight)
            params.gravity = Gravity.CENTER
            params.bottomMargin = 3.dp2px()
            comma.layoutParams = params
            comma.text = ","
            comma.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            comma.includeFontPadding = false
            comma.setTypeface(comma.typeface, Typeface.BOLD)
            comma.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize * 0.8f)
            comma.height = textSize
            digit!!.addView(comma)
            post {
                comma.alpha = 0f
                comma.animate().alpha(1f).setDuration(1200).start()
            }
        }
    }

    fun addOne() {
        val count = digitViews.size
        for (i in 0 until count) {
            val recyclerView = digitViews[i]
            val currentPosition =
                (recyclerView.layoutManager as LinearLayoutManager?)!!.findFirstCompletelyVisibleItemPosition()
            val currentDigit = currentPosition % 10
            val afterDigit = Integer.getInteger(recyclerView.tag.toString())
            val moveDistance =
                if (afterDigit > currentDigit) afterDigit - currentDigit else afterDigit + 10 - currentDigit
            if (currentDigit != afterDigit) {
                recyclerView.smoothScrollBy(0, itemHeight * moveDistance)
            }
        }
    }

    fun focusOn() {
        mask.animate().setDuration(300).alpha(1f).start()
    }

    private fun reset() {
        digit.removeAllViews()
        digitViews.clear()
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