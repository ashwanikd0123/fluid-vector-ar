package com.example.fluidvectorar.ui.home.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.fluidvectorar.AppRoute
import com.example.fluidvectorar.R
import com.example.fluidvectorar.data.local.entity.ProjectEntity
import com.example.fluidvectorar.ui.home.state.HomeScreenState
import com.example.fluidvectorar.ui.home.viewmodel.HomeScreenViewModel
import com.example.fluidvectorar.ui.theme.FluidVectorARTheme

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {

    LaunchedEffect(viewModel.homeScreenState.moveToProject) {
        viewModel.homeScreenState.moveToProject?.let {
            navController.navigate(AppRoute.EditorStudio(it))
            viewModel.homeScreenState.reset()
        }
    }

    HomeScreenView(
        homeScreenState = viewModel.homeScreenState,
        { projectId ->
            navController.navigate(AppRoute.EditorStudio(projectId))
        },
        {
            viewModel.homeScreenState.showingNewProjectDialog = true
        },
        { title ->
            viewModel.createNewProject(title)
        }
    )
}

@Composable
fun HomeScreenView(
    homeScreenState: HomeScreenState,
    onProjectClicked: (String) -> Unit,
    onAddProjectClicked: () -> Unit,
    onCreateNewProject: (String) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
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
                val listState = rememberLazyListState()
                LazyColumn(state = listState) {
                    items(homeScreenState.projectList) { item ->
                        HomeRowView(item) { projectId ->
                            onProjectClicked(projectId)
                        }
                    }
                }
            }
        }

        if (homeScreenState.isProjectListLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        }

        if (homeScreenState.showingNewProjectDialog) {
            Dialog(
                onDismissRequest = {
                    homeScreenState.showingNewProjectDialog = false
                }
            ) {
                Card(
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = homeScreenState.currentProjectName,
                            modifier = Modifier
                                .fillMaxWidth(),
                            onValueChange = {
                                homeScreenState.currentProjectName = it
                            },
                            placeholder = {
                                Text("Project name")
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                onCreateNewProject(homeScreenState.currentProjectName)
                                homeScreenState.showingNewProjectDialog = false
                            }
                        ) {
                            Text("Create Project")
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    val projectList = listOf(
        ProjectEntity(
            "abc",
            "Project title",
            169,
            140,
            null,
            1L,
            2L
        ),
        ProjectEntity(
            "abc",
            "Project title",
            169,
            140,
            null,
            1L,
            2L
        ),
        ProjectEntity(
            "abc",
            "Project title",
            169,
            140,
            null,
            1L,
            2L
        )
    )

    val state = HomeScreenState()
    state.projectList = projectList

    FluidVectorARTheme {
        HomeScreenView(
            state,
            {},
            {},
            {}
            )
    }
}