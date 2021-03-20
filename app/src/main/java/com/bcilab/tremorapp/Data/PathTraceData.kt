package com.bcilab.tremorapp.Data

class PathTraceData(val x: Float, val y: Float, val t: Int) {

    val joinToString = { del: String -> "${this.x}$del${this.y}$del${this.t}"}

    fun isSame(otherData: PathTraceData): Boolean {
        var flag = false
        if ((this.x == otherData.x) && (this.y == otherData.y) && (this.t == otherData.t))
            flag = true
        return flag
    }

    fun isSamePosition(otherData: PathTraceData): Boolean {
        var flag = false
        if ((this.x == otherData.x) && (this.y == otherData.y))
            flag = true
        return flag
    }

}