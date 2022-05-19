package com.example.clusterization

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.clusterization.ui.screens.IntroScreen
import com.example.clusterization.ui.screens.LogScreen
import com.example.clusterization.ui.screens.MainScreen
import com.example.clusterization.ui.theme.ClusterizationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClusterizationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()

                    NavHostInit(navController = navController)
                }
            }
        }
    }
}

object MainDistinctions {
    const val INTRO_SCREEN = "introScreen"
    const val MAIN_SCREEN = "mainScreen"
    const val LOG_SCREEN = "logScreen"
}

@Composable
fun NavHostInit(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = MainDistinctions.INTRO_SCREEN
    ) {
        homeNavGraph(navController)
    }
}

fun NavGraphBuilder.homeNavGraph(navController: NavHostController) {
    composable(MainDistinctions.INTRO_SCREEN) {
        IntroScreen(navController = navController)
    }
    composable(MainDistinctions.MAIN_SCREEN) {
        MainScreen(navController = navController)
    }
    composable(MainDistinctions.LOG_SCREEN) {
        LogScreen()
    }
}
