package com.example.fluidvectorar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fluidvectorar.ui.editor.canvas.state.CanvasState
import com.example.fluidvectorar.ui.editor.canvas.state.EditorMode
import com.example.fluidvectorar.ui.editor.canvas.view.FluidCanvas
import com.example.fluidvectorar.ui.theme.FluidVectorARTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            NavHost(
                navController = navController,
                startDestination = AppRoute.EditorStudio("abcd123"),
                modifier = Modifier
                    .fillMaxSize()
            ) {
                composable<AppRoute.EditorStudio> {
                    val canvasState = CanvasState()
                    canvasState.activeMode = EditorMode.DRAW
                    FluidCanvas(
                        canvasState = canvasState
                    )
                }

                composable<AppRoute.Home> {

                }
            }
        }
    }
}