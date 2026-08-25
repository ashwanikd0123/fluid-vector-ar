package com.example.fluidvectorar.ui.home.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.fluidvectorar.R
import com.example.fluidvectorar.data.local.entity.ProjectEntity

@Composable
fun HomeScreenView(
    projectList: List<ProjectEntity>,
    onProjectClicked: (String) -> Unit,
    onAddProjectClicked: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(onClick = onAddProjectClicked) {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = "Add Project"
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(8.dp)
        ) {
            LazyColumn {
                items(projectList) { item ->
                    HomeRowView(item) { projectId ->
                        onProjectClicked(projectId)
                    }
                }
            }
        }
    }
}