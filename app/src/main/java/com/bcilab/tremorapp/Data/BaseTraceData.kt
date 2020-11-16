package com.bcilab.tremorapp.Data

data class BaseTraceData(val x: Float, val y: Float) {
    val joinToString = { del: String -> "${this.x}$del${this.y}"}


}