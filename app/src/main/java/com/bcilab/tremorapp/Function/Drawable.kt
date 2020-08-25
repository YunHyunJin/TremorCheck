package com.bcilab.tremorapp.functions

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import java.io.File
import java.io.FileOutputStream

abstract class Drawable(context: Context) : View(context) {
    protected val path = Path()
    protected val paint = Paint()
    protected val bitmapPaint = Paint()

    protected lateinit var bitmap: Bitmap
    public lateinit var canvas: Canvas

    init {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f
        bitmapPaint.style = paint.style
        bitmapPaint.strokeWidth = paint.strokeWidth
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, 0.toFloat(), 0.toFloat(), bitmapPaint)
        canvas.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x: Float = event.x
        val y: Float = event.y

        when(event.action) {
            MotionEvent.ACTION_DOWN -> path.moveTo(x, y)
            MotionEvent.ACTION_MOVE -> path.lineTo(x, y)
        }

        invalidate()
        return true
    }

    open fun clearLayout() {
        path.rewind()
        invalidate()
        bitmap.eraseColor(Color.TRANSPARENT)
    }

    fun saveAsJPG(view: View, path: String, filename:String) {
        canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        val realPath = File(path)

        if (!realPath.exists())
            realPath.mkdirs()
        try {
            val outputStream = FileOutputStream(File("$realPath/$filename"))
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        canvas.drawColor(Color.argb(0, 255, 255, 255))
    }
}