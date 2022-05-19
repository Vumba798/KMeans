package com.example.clusterization.neuralNetwork

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt


fun euclideanDist(left: BasicPoint, right: BasicPoint) : Float {
    return sqrt((left.x - right.x).toDouble().pow(2) + (left.y - right.y).toDouble().pow(2)).toFloat()
}

fun chebyshevDist(left: BasicPoint, right: BasicPoint) : Float {
    return max(abs(left.x - right.x), abs(left.y - right.y))
}
