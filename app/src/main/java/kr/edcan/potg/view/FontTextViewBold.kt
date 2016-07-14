package kr.edcan.potg.view

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.widget.TextView

/**
 * Created by LNTCS on 2015-09-01.
 */

class FontTextViewBold : TextView {

    internal var mContext: Context? = null

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context)
    }

    fun init(mContext: Context) {
        this.mContext = mContext
        this.setTypeface(Typeface.createFromAsset(mContext.assets, "fonts/koverwatch.ttf"), Typeface.BOLD_ITALIC);
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        //        Toast.makeText(mContext, "선택" +selStart +"/"+selEnd, Toast.LENGTH_SHORT).show();
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        try {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        } catch (e: ArrayIndexOutOfBoundsException) {
            text = text.toString()
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }

    }

    override fun onDraw(canvas: Canvas) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f
        setTextColor(Color.parseColor("#99ff7e00"))
        super.onDraw(canvas)
        paint.style = Paint.Style.FILL
        setTextColor(Color.parseColor("#ffcc00"))
        super.onDraw(canvas)
    }
    override fun setGravity(gravity: Int) {
        try {
            super.setGravity(gravity)
        } catch (e: ArrayIndexOutOfBoundsException) {
            text = text.toString()
            super.setGravity(gravity)
        }

    }
}
