package com.example.clusterization.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.PointMode.Companion.Points
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import com.example.clusterization.MainDistinctions
import com.example.clusterization.R
import com.example.clusterization.kmeans.*
import com.example.clusterization.ui.theme.Red800
import com.example.clusterization.ui.theme.Red800Dark
import com.example.clusterization.ui.theme.Red800Light
import java.lang.IllegalArgumentException

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
fun MainScreen(navController: NavHostController) {
    var showLogs by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val pointType = rememberSaveable { mutableStateOf(PointType.COMMON_POINT) }
    val distanceFunction =
        rememberSaveable { mutableStateOf<(BasicPoint, BasicPoint) -> Float>(::euclideanDist) }
    val stack by remember { mutableStateOf(ArrayDeque<PointType>()) }

//    val points = rememberSaveable { mutableStateListOf<StaticPoint>() }
    val points = rememberMutableStateListOf<StaticPoint>()
    val clusters = remember { mutableStateListOf<MovablePoint>() }

    if (showLogs) {
        LogScreen() {
            showLogs = false
        }
    } else {
        Scaffold(
            topBar = {
                TopBar(
                    context = context,
                    points = points,
                    clusters = clusters,
                    stack = stack,
                    pointType = pointType.value,
                    updatePointType = { newType ->
                        pointType.value = newType
                    },
                    function = distanceFunction.value,
                    updateFunction = { newFunction ->
                        distanceFunction.value = newFunction
                    },
                    navController = navController,
                    onShowLogsChange = { showLogs = true }
                )
            },
            bottomBar = {
                BottomBar(
                    points = points,
                    clusters = clusters,
                    stack = stack,
                    distanceFunction = distanceFunction.value
                )
            },
            modifier = Modifier
                .fillMaxSize(),

            ) { padding ->
            DrawingArea(
                context = context,
                points = points,
                clusters = clusters,
                stack = stack,
                pointType = pointType,
                modifier = Modifier.padding(padding)
            )
        }
    }
}




@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TopBar(
    context: Context,
    points: SnapshotStateList<StaticPoint>,
    clusters: SnapshotStateList<MovablePoint>,
    stack: ArrayDeque<PointType>,
    pointType: PointType,
    updatePointType: (PointType) -> Unit,
    function: (BasicPoint, BasicPoint) -> Float,
    updateFunction: ((BasicPoint, BasicPoint) -> Float) -> Unit,
    navController: NavHostController,
    onShowLogsChange: () -> Unit
) {
    var expandedFunction by rememberSaveable { mutableStateOf(false) }
    var selectedFunctionText by rememberSaveable { mutableStateOf("Euclidean") }
    var expandedPointType by rememberSaveable { mutableStateOf(false) }
    var selectedPointTypeText by rememberSaveable { mutableStateOf("Point") }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Red800)
            .padding(5.dp)
    ) {
        val functionOptions = listOf<Pair<String, (BasicPoint, BasicPoint) -> Float>>(
            "Euclidean" to ::euclideanDist,
            "Chebyshev" to ::chebyshevDist
        )
        ExposedDropdownMenuBox(
            expanded = expandedFunction,
            onExpandedChange = {
                expandedFunction = !expandedFunction
            },
            modifier = Modifier
                .weight(3f)
                .background(Red800Dark)
        ) {
            TextField(
                readOnly = true,
                value = selectedFunctionText,
                onValueChange = { },
                label = { Text("Distance function")},
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFunction)
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                    textColor = Color.White,
                    disabledLabelColor = Color.White.copy(alpha = 0.9f),
                    focusedLabelColor = Color.White.copy(alpha = 0.8f),
                    focusedTrailingIconColor = Color.White.copy(alpha = 0.8f),
                    focusedBorderColor = Color.White.copy(alpha = 0.9f),
                    unfocusedLabelColor = Color.White.copy(alpha = 0.9f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.9f),
                )
            )
            ExposedDropdownMenu(
                expanded = expandedFunction,
                onDismissRequest = {
                    expandedFunction = false
                }
            ) {
                functionOptions.forEach { (selectionText, selectionFunction) ->
                    DropdownMenuItem(
                        onClick = {
                            updateFunction(selectionFunction)
                            selectedFunctionText = selectionText
                            expandedFunction = false
                        }
                    ) {
                        Text(text = selectionText)
                    }
                }
            }
        }
        Spacer(
            modifier = Modifier.weight(0.5f)
        )
        val pointTypeOptions = listOf(
            "Point" to PointType.COMMON_POINT,
            "Cluster" to PointType.CLUSTER
        )
        ExposedDropdownMenuBox(
            expanded = expandedPointType,
            onExpandedChange = {
                expandedPointType = !expandedPointType
            },
            modifier = Modifier
                .weight(2.5f)
                .background(Red800Dark)
        ) {
            TextField(
                readOnly = true,
                value = selectedPointTypeText,
                onValueChange = { },
                label = { Text("Add an element")},
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPointType)
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                    textColor = Color.White,
                    disabledLabelColor = Color.White.copy(alpha = 0.9f),
                    focusedLabelColor = Color.White.copy(alpha = 0.8f),
                    focusedTrailingIconColor = Color.White.copy(alpha = 0.8f),
                    focusedBorderColor = Color.White.copy(alpha = 0.9f),
                    unfocusedLabelColor = Color.White.copy(alpha = 0.9f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.9f),
                )
            )
            ExposedDropdownMenu(
                expanded = expandedPointType,
                onDismissRequest = {
                    expandedPointType = false
                }
            ) {
                pointTypeOptions.forEach { (selectionPointText, selectionPointType) ->
                    DropdownMenuItem(
                        onClick = {
                            updatePointType(selectionPointType)
                            selectedPointTypeText = selectionPointText
                            expandedPointType = false
                        }
                    ) {
                        Text(text = selectionPointText)
                    }
                }
            }
        }


        Spacer(
            modifier = Modifier.weight(1f)
        )

        IconButton(
            onClick = {
                if (Logger.logs.isEmpty()) {
                    Toast.makeText(
                        context,
                        "Logs are empty!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
//                    navController.navigate(MainDistinctions.LOG_SCREEN)
                    onShowLogsChange()
                }
            },
            modifier = Modifier
                .clip(shape = RoundedCornerShape(20))
                .background(color = Red800Dark)
        ) {
            Icon(
                imageVector = Icons.Rounded.Menu,
                tint = Color.White,
                contentDescription = null
            )
        }
    }
}

@Composable
fun DrawingArea(
    context: Context,
    points: SnapshotStateList<StaticPoint>,
    clusters: SnapshotStateList<MovablePoint>,
    stack: ArrayDeque<PointType>,
    pointType: MutableState<PointType>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            when (pointType.value) {
                                PointType.COMMON_POINT -> {
                                    points.add(StaticPoint(it))
                                    Logger.append(
                                        action = "Added a point\n(${it.x}, ${it.y})\ncolor = ${points.last().color.value}",
                                        details = logAll(points, clusters)
                                    )
                                }
                                PointType.CLUSTER -> {
                                    if (clusters.size < clusterColors.size) {
                                        clusters.add(MovablePoint(it, clusterColors[clusters.size]))
                                        Logger.append(
                                            action = "Added a cluster\n(${it.x}, ${it.y})\ncolor = ${clusters.last().color.value}",
                                            details = logAll(points, clusters)
                                        )
                                    } else {
                                        Toast
                                            .makeText(
                                                context,
                                                "Too much clusters!",
                                                Toast.LENGTH_SHORT
                                            )
                                            .show()
                                        Logger.append(
                                            action = "Tried to add a cluster, limit is exhausted",
                                            details = logAll(points, clusters)
                                        )
                                    }
                                }
                            }
                            stack.addLast(pointType.value)
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
}

@Composable
fun BottomBar(
    points: SnapshotStateList<StaticPoint>,
    clusters: SnapshotStateList<MovablePoint>,
    stack: ArrayDeque<PointType>,
    distanceFunction: (BasicPoint, BasicPoint) -> Float
) {
    Row(
        modifier = Modifier
            .background(color = Red800Light)
            .height(50.dp)
    ) {
        OutlinedButton(
            onClick = {
                KMeans(points, clusters, distanceFunction).run()
                Logger.append(
                    action = "Run",
                    details = logAll(points, clusters)
                )
            },
            colors = ButtonDefaults.buttonColors(Red800Light),
            border = BorderStroke(1.dp, Red800),
            shape = RectangleShape,
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
        ) {
            Text("Run")
        }
        OutlinedButton(
            onClick = {
                KMeans(points, clusters, distanceFunction).step()
                Logger.append(
                    action = "Step",
                    details = logAll(points, clusters)
                )
            },
            colors = ButtonDefaults.buttonColors(Red800Light),
            border = BorderStroke(1.dp, Red800),
            shape = RectangleShape,
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
        ) {
            Text("Step")
        }
        OutlinedButton(
            onClick = {
                points.clear()
                clusters.clear()
            },
            colors = ButtonDefaults.buttonColors(Red800Light),
            border = BorderStroke(1.dp, Red800),
            shape = RectangleShape,
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
        ) {
            Text("Clear")
        }
        OutlinedButton(
            onClick = {
                with(stack) {
                    if (removeLastOrNull() == PointType.COMMON_POINT) {
                        points.removeLastOrNull()
                    } else {
                        clusters.removeLastOrNull()
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(Red800Light),
            border = BorderStroke(1.dp, Red800),
            shape = RectangleShape,
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
        ) {
            Text("Undo")
        }
    }
}


@Composable
fun <T: Any> rememberMutableStateListOf(vararg elements: T,): SnapshotStateList<T> {
    return rememberSaveable(
        saver = listSaver(
            save = { stateList ->
                if (stateList.isNotEmpty()) {
                    val first = stateList.first()
                    if (!canBeSaved(first)) {
                        throw IllegalArgumentException("${first::class} cannot be saved")
                    }
                }
                stateList.toList()
            },
            restore = { it.toMutableStateList() }
        )
    ) {
        elements.toList().toMutableStateList()
    }
}

val StaticPointSaver = run {
    val xKey = "x"
    val yKey = "y"
    val offsetKey = "offset"
    val colorKey = "color"

    mapSaver(
        save = { mapOf(xKey to it.x, yKey to it.y, colorKey to it.color.value )},
        restore = { StaticPoint(Offset(it[xKey] as Float, it[yKey] as Float ), Color(it[colorKey] as ULong) )}
    )
}
