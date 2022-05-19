package com.example.clusterization.kmeans

import android.os.Parcel
import android.os.Parcelable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

interface BasicPoint {
    var x: Float
    var y: Float
    val offset: Offset
    var color: Color
}

data class StaticPoint(
    override val offset: Offset,
    override var color: Color = Color.Black
) : BasicPoint {
    override var x
        get() = offset.x
        set(oth) { }
    override var y
        get() = offset.y
        set(oth) { }
}


data class MovablePoint(
    private val initOffset: Offset,
    override var color: Color = Color.Black
) : BasicPoint {
    override var x = initOffset.x
    override var y = initOffset.y
    override val offset: Offset
        get() = Offset(x, y)
}
