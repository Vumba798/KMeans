package com.example.clusterization.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.clusterization.MainDistinctions
import com.example.clusterization.R
import com.example.clusterization.ui.theme.Red800
import com.example.clusterization.ui.theme.Red800Dark
import com.example.clusterization.ui.theme.Red800Light

@Composable
fun IntroScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Red800),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "Clusterization\nK-Means",
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.Monospace,
                fontSize = 40.sp,
                modifier = Modifier
                    .padding(vertical = 40.dp)
            )
            Icon(
                painter = painterResource(R.drawable.ic_cluster_data),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .padding(vertical = 110.dp)
            )
            Button(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Red800Dark,
                    contentColor = Color.White
                ),
                onClick = { navController.navigate(MainDistinctions.MAIN_SCREEN) },
                modifier = Modifier
                    .padding(vertical = 0.dp)
            ) {
                Text("Continue")
            }
        }
        Text(
            text = "Developed by Vumba798",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(vertical = 5.dp)
        )
    }
}
