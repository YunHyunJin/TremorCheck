package com.bcilab.tremorapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MotionEvent
import android.view.View
import com.bcilab.tremorapp.Data.PathTraceData
import com.bcilab.tremorapp.Function.fitting
import com.bcilab.tremorapp.functions.Drawable
import kotlinx.android.synthetic.main.activity_spiral.*

class SpiralActivity : AppCompatActivity() {
    private var currentX: Float = 0.toFloat()
    private var currentY: Float = 0.toFloat()
    private var isdraw : Boolean = false
    private val pathTrace: MutableList<PathTraceData> = mutableListOf()
    private val timer = object : CountDownTimer(Long.MAX_VALUE, 1000 / 60) {
        override fun onTick(millisUntilFinished: Long) {
            pathTrace.add(PathTraceData(currentX, currentY, (Long.MAX_VALUE - millisUntilFinished).toInt()))
        }

        override fun onFinish() {}
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spiral)

        val layout = writingcanvasLayout
        val view = DrawView(this)
        val baseLine = baseView(this)
        layout.addView(view)
        layout.addView(baseLine)

    }
    inner class DrawView(context: Context) : Drawable(context) {
        private var flag = false

        override fun onTouchEvent(event: MotionEvent): Boolean {
            currentX = event.x
            currentY = event.y
            isdraw = true
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (!flag) {
                        flag = true
                        timer.start()
                    }
                }
            }
            return super.onTouchEvent(event)
        }

        override fun clearLayout() {
            super.clearLayout()
            pathTrace.clear()
            timer.cancel()
        }
    }

    inner class baseView(context: Context) : View(context) {
        private val startX = this.resources.displayMetrics.widthPixels / 2
        private val startY = this.resources.displayMetrics.heightPixels / 2

        private val theta = FloatArray(720) { (((it * (Math.PI / 180)) / 3) * 2).toFloat() }
        private val basePath = Path()
        private val basePaint = Paint()

        init {
            basePaint.style = Paint.Style.STROKE
            basePaint.strokeWidth = 2f
            basePaint.alpha = 50
            basePaint.isAntiAlias = true
            fitting.startX = startX
            fitting.startY = startY
        }

        override fun onDraw(canvas: Canvas) {
            basePath.moveTo(startX.toFloat(), startY.toFloat())
            for (t in theta)
                basePath.lineTo((t * Math.cos(2.5 * t) * 50 + startX).toFloat(), (t * Math.sin(2.5 * t) * 50 + startY).toFloat())

            canvas.drawPath(basePath, basePaint)
        }

    }
}
