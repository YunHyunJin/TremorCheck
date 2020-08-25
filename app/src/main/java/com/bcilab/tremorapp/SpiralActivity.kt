package com.bcilab.tremorapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class SpiralActivity : AppCompatActivity() {
    private var currentX: Float = 0.toFloat()
    private var currentY: Float = 0.toFloat()
    private var isdraw : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spiral)


    }

}
