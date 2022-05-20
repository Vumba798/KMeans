package com.example.clusterization.ui.screens

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.clusterization.kmeans.MovablePoint
import com.example.clusterization.kmeans.StaticPoint
import com.example.clusterization.ui.theme.Red800
import com.example.clusterization.ui.theme.Red800Dark

object Logger {
    data class Log(
        val action: String,
        val details: String
    )
    val logs = mutableListOf<Log>()

    fun append(action: String, details: String = "") {
        logs.add(Log(action, details))
    }
}

fun logAll(points: List<StaticPoint>, clusters: List<MovablePoint>) = buildString {
    append(getPointLogs(points))
    append(getClusterLogs(clusters, points))
}
fun getPointLogs(points: List<StaticPoint>) = buildString {
    if (points.isNotEmpty()) {
        points.forEachIndexed { index, point ->
            append("Point[$index] = (${point.x}, ${point.y}),\n\tcolor = ${point.color.value}\n")
        }
    } else {
        append("There are no points")
    }
    append("\n")
}


fun getClusterLogs(clusters: List<MovablePoint>, points: List<StaticPoint>) = buildString {
    if (clusters.isNotEmpty()) {
        clusters.forEachIndexed { index, cluster ->
            append("Cluster[$index] = (${cluster.x}, ${cluster.y}), color = ${cluster.color.value}\n")
            points.filter { point ->
                point.color == cluster.color
            }
                .takeIf {
                    it.isNotEmpty()
                }
                ?.forEach { point ->
                    append("\tPoint[$index] = (${point.x}, ${point.y}), color = ${point.color.value}\n")
                } ?: append("Empty")
        }
    } else {
        append("There are no clusters")
    }
    append("\n")
}

@Composable
fun LogScreen(onShowLogsChange: () -> Unit) {
    val bh = BackHandler(enabled = true) {
        onShowLogsChange()
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Red800)
    ) {
        items(Logger.logs) { log ->
            Box(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(5))
                    .background(Red800)
            ) {
                var expanded by remember { mutableStateOf(false) }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                        .background(Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20))
                            .background(Color.White)
                    ) {
                        Text(
                            text = log.action,
                            color = Color.Black,
                            modifier = Modifier
                                .padding(5.dp)
                                .fillMaxWidth()
                                .weight(1f),
                        )
                        IconButton(
                            onClick = {
                                expanded = !expanded
                            },
                        ) {
                            Icon(
                                imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                contentDescription = null,
                                tint = Red800
                            )
                        }
                    }
                    if (expanded) {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(color = Red800Dark.copy(alpha = 0.5f))
                                .height(1.dp)
                        )
                        Text(
                            text = log.details,
                            color = Color.Black,
                        )
                    }
                }
            }
        }
    }
}



class MyViewModel : ViewModel() {
    var points = mutableListOf<StaticPoint>()
    var onUpdate = mutableStateOf(0)

    private fun updateUi() {
        onUpdate.value = onUpdate.value + 1
    }

    fun update(index: Int, point: StaticPoint) {
        points[index] = point
        updateUi()
    }
}

@Composable
fun MyComposableHandler() {
    val viewModel = MyViewModel()
    viewModel.onUpdate.value
}
