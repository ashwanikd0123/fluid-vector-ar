package com.example.fluidvectorar.ui.home.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.fluidvectorar.R
import com.example.fluidvectorar.data.local.entity.ProjectEntity
import com.example.fluidvectorar.ui.theme.FluidVectorARTheme

@Composable
fun ProjectImage(modifier: Modifier, thumbnailPath: String? = null) {
    AsyncImage(
        model = thumbnailPath ?: R.drawable.ic_project_placeholder,
        contentDescription = "Project Thumbnail",
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}

@Composable
fun HomeRowView(project: ProjectEntity, onClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable(
                onClick = {
                    onClick(project.id)
                }
            ),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProjectImage(
            modifier = Modifier
            .height(100.dp)
            .width(100.dp),
            project.thumbnailPath
        )

        Text(
            project.title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeRowPreview() {
    val project = ProjectEntity(
        "abc",
        "Project title",
        169,
        140,
        null,
        1L,
        2L
    )
    
    FluidVectorARTheme { 
        HomeRowView(
            project = project,
            onClick = {}
        )
    }
}