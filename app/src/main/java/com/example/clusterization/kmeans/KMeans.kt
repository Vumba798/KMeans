package com.example.clusterization.kmeans

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset

class KMeans(
    private val points: SnapshotStateList<StaticPoint>,
    private val clusters: SnapshotStateList<MovablePoint>,
    private val dist: (BasicPoint, BasicPoint) -> Float
) {
//    val relation: MutableMap<BasicPoint, BasicPoint> = mutableMapOf() // point to cluster
    init {
        recolor()
    }

    fun step() : Pair<List<StaticPoint>, List<MovablePoint>> {
 //       relation.clear()
        recolor()
        moveClusters()

        return Pair(points, clusters)
    }

    fun run() : Pair<List<StaticPoint>, List<MovablePoint>> {
        while (true) {
            val changed = moveClusters()
            recolor()

            if (!changed) {
                return Pair(points, clusters)
            }
        }
    }


    private fun recolor() {
  //      relation.clear()

        for (i in 0 until points.size) {
            clusters
                .minByOrNull {
                    dist(it, points[i])
                }
                ?.let { nearestCluster ->
   //                 relation[point] = nearestCluster
                    points[i] = StaticPoint(points[i].offset, nearestCluster.color)
//                    point.color = nearestCluster.color
                }
        }
    }

    private fun moveClusters(): Boolean {
        var changed = false
        for (i in 0 until clusters.size) {
            points
                .filter {
                    // returns all relations with current cluster
                    it.color == clusters[i].color
                }
                .takeIf {
                    it.isNotEmpty()
                }
                ?.run {
                    var xMean = 0f
                    var yMean = 0f
                    forEach { (point, _) ->
                        xMean += point.x
                        yMean += point.y
                    }
                    // returns recomputed mass center of current cluster
                    Pair(xMean / size, yMean / size)
                }
                ?.also { (newX, newY) ->
                    if (clusters[i].x != newX && clusters[i].y != newY) {
                        changed = true
                        clusters[i] = MovablePoint(Offset(newX, newY), clusters[i].color)
                    }
                }
        }
        return changed
    }

}
