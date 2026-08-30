package com.example.fluidvectorar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.fluidvectorar.ui.editor.view.EditorScreen
import com.example.fluidvectorar.ui.home.view.HomeScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = AppRoute.Home,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                composable<AppRoute.EditorStudio> {
                    EditorScreen(
                        navController = navController
                    )
                }

                composable<AppRoute.Home> {
                    HomeScreen(
                        navController = navController
                    )
                }
            }
        }
    }
}