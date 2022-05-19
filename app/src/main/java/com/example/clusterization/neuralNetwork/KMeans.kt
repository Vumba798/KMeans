package com.example.clusterization.neuralNetwork

import androidx.compose.ui.geometry.Offset

class KMeans(
    private val points: List<StaticPoint>,
    private val clusters: List<MovablePoint>,
    private val dist: (BasicPoint, BasicPoint) -> Float
) {
    val relation: MutableMap<BasicPoint, BasicPoint> = mutableMapOf() // point to cluster
    init {
        recreateRelation()
    }

    fun step() : Map<BasicPoint, BasicPoint> {
        relation.clear()
        recreateRelation()
        recomputeClusters()

        return relation
    }

    fun run() : Map<BasicPoint, BasicPoint> {
        while (true) {
            var changed = false
            changed = recomputeClusters()
            recreateRelation()

            if (!changed) {
                return relation
            }
        }
    }


    private fun recreateRelation() {
        relation.clear()


        points.forEach { point ->
            clusters
                .minByOrNull {
                    dist(it, point)
                }
                ?.let { nearestCluster ->
                    relation[point] = nearestCluster
                    point.color = nearestCluster.color
                }
        }
    }

    private fun recomputeClusters(): Boolean {
        var changed = false
        clusters.forEach { cluster ->
            val (newX: Float, newY: Float) = relation
                .filterValues {
                    // returns all relations with current cluster
                    it == cluster
                }
                .run {
                    var xMean = 0f
                    var yMean = 0f
                    forEach { (point, _) ->
                        xMean += point.x
                        yMean += point.y
                    }
                    // returns recomputed mass center of current cluster
                    Pair(xMean / size, yMean / size)
                }
            cluster.apply {
                if (cluster.x != newX && cluster.y != newY) {
                    changed = true
                    x = newX
                    y = newY
                }
            }
        }
        return changed
    }

}
