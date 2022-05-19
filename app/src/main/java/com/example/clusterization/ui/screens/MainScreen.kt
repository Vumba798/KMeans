package com.example.clusterization.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.PointMode.Companion.Points
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.clusterization.R
import com.example.clusterization.neuralNetwork.KMeans
import com.example.clusterization.neuralNetwork.MovablePoint
import com.example.clusterization.neuralNetwork.StaticPoint
import com.example.clusterization.neuralNetwork.euclideanDist
import com.example.clusterization.ui.theme.Red800
import com.example.clusterization.ui.theme.Red800Dark
import com.example.clusterization.ui.theme.Red800Light

enum class PointType {
    COMMON_POINT,
    CLUSTER
}

val clusterColors = listOf(
    Color.Blue,
    Color.Green,
    Color.Cyan,
    Color.Gray,
    Color.Magenta,
    Color.Red,
    Color.Yellow,
)


@Composable
fun MainScreen() {
    val context = LocalContext.current
    var pointType by remember { mutableStateOf(PointType.COMMON_POINT) }
    val stack by remember { mutableStateOf(ArrayDeque<PointType>())}
    val points = remember { mutableStateListOf<StaticPoint>() }
    val clusters = remember { mutableStateListOf<MovablePoint>()}

    val obj by remember {
        mutableStateOf(
            KMeans(
                points = points,
                clusters = clusters,
                dist = ::euclideanDist
            )
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Red800)
                .padding(15.dp)
        ) {
            Row {
                Button(
                    onClick = {
                        pointType = if (pointType == PointType.COMMON_POINT) {
                            PointType.CLUSTER
                        } else {
                            PointType.COMMON_POINT
                        }
                    },
                    colors = ButtonDefaults.buttonColors(Red800Dark)
                ) {
                    if (pointType == PointType.COMMON_POINT) {
                        Text("Add a point")
                    } else {
                        Text("Add a cluster")
                    }
                }
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                )
                IconButton(
                    onClick = {
                        with(stack) {
                            if (removeLastOrNull() == PointType.COMMON_POINT) {
                                points.removeLastOrNull()
                            } else {
                                clusters.removeLastOrNull()
                            }
                        }
                    },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_baseline_undo_24),
                        tint = Color.White,
                        contentDescription = null
                    )
                }
            }

        }
        Box(
            modifier = Modifier
//                .fillMaxSize()
                .fillMaxWidth()
                .size(550.dp)
                .background(Color.White),
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                when (pointType) {
                                    PointType.COMMON_POINT -> {
                                        points.add(StaticPoint(it))
                                    }
                                    PointType.CLUSTER -> {
                                        if (clusters.size < clusterColors.size) {
                                            clusters.add(MovablePoint(it, clusterColors[clusters.size]))
                                        } else {
                                            Toast.makeText(context, "Too much clusters!", Toast.LENGTH_SHORT)
                                        }
                                    }

                                }
                                stack.addLast(pointType)
                                Toast
                                    .makeText(
                                        context,
                                        "x = ${it.x}, y = ${it.y}, size = ${points.size}",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                            },
                        )
                    },
            ) {
                clusters.forEach { cluster ->
                    drawCircle(
                        color = Color.Black,
                        radius = 28f,
                        center = cluster.offset,
                    )
                    drawCircle(
                        color = cluster.color,
                        radius = 25f,
                        center = cluster.offset,
                    )
                }
                points.groupBy {
                        it.color
                    }
                    .forEach { (color, oneColorPoints) ->
                        drawPoints(
                            points = oneColorPoints.map { it.offset },
                            pointMode = Points,
                            color = Color.Black,
                            strokeWidth = 18f
                        )
                        drawPoints(
                            points = oneColorPoints.map { it.offset },
                            pointMode = Points,
                            color = color,
                            strokeWidth = 15f
                        )
                    }
            }

        }
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Red800Light)
        ) {
            OutlinedButton(
                onClick = {
                    val tmp = KMeans(points, clusters, ::euclideanDist).run()

                    points.clear()
                    tmp
                        .map {it.key}
                        .forEach {
                            points.add(it as StaticPoint)
                        }
                    clusters.clear()
                    tmp.map { it.value }
                        .forEach {
                            clusters.add(it as MovablePoint)
                        }
                },
                colors = ButtonDefaults.buttonColors(Red800Light),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier
                    .weight(1f)
            ) {
                Text("Run")
            }
            OutlinedButton(
                onClick = {
                    obj.step()
                },
                colors = ButtonDefaults.buttonColors(Red800Light),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier
                    .weight(1f)
            ) {
                Text("Step")
            }
            OutlinedButton(
                onClick = {

                },
                colors = ButtonDefaults.buttonColors(Red800Light),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier
                    .weight(1f)
            ) {
                Text("Reset")
            }
            OutlinedButton(
                onClick = {

                },
                colors = ButtonDefaults.buttonColors(Red800Light),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier
                    .weight(1f)
            ) {
                Text("Clear")
            }
        }
    }
}


@Composable
fun ControlBar(obj: KMeans, points: SnapshotStateList<StaticPoint>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Red800)
    ) {
        Button(
            onClick = {
                obj.run()
            },
            colors = ButtonDefaults.buttonColors(Red800Dark),
            modifier = Modifier
                .weight(1f)
        ) {
            Text("Run")
        }
        Button(
            onClick = {
                obj.step()
            },
            colors = ButtonDefaults.buttonColors(Red800Dark),
            modifier = Modifier
                .weight(1f)
        ) {
            Text("Step")
        }
        Button(
            onClick = {

            },
            colors = ButtonDefaults.buttonColors(Red800Dark),
            modifier = Modifier
                .weight(1f)
        ) {
            Text("Reset")
        }
        Button(
            onClick = {

            },
            colors = ButtonDefaults.buttonColors(Red800Dark),
            modifier = Modifier
                .weight(1f)
        ) {
            Text("Clear")
        }
    }
}