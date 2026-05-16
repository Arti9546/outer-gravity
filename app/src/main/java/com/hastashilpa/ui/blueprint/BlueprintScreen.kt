package com.hastashilpa.ui.blueprint

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hastashilpa.R
import com.hastashilpa.viewmodel.BlueprintViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlueprintScreen(
    blueprintId: String? = null,
    onBackClick: () -> Unit,
    viewModel: BlueprintViewModel = viewModel()
) {
    val blueprints by viewModel.blueprints.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val blueprint = blueprints.find { it.id == blueprintId } ?: blueprints.firstOrNull()

    var scale by remember { mutableStateOf(1.2f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Visual Guide", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading || blueprint == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(24.dp)
                ) {
                    Column {
                        Text(
                            text = blueprint.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Category: ${blueprint.category}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Interactive Blueprint Section
                Text(
                    text = "Technical Sketch (Zoomable)",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 24.dp).align(Alignment.Start),
                    color = MaterialTheme.colorScheme.primary
                )
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF5F5F5))
                            .pointerInput(Unit) {
                                detectTransformGestures { _, pan, zoom, _ ->
                                    scale = (scale * zoom).coerceIn(1f, 8f)
                                    offset += pan
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer(
                                    scaleX = scale,
                                    scaleY = scale,
                                    translationX = offset.x,
                                    translationY = offset.y
                                )
                                .background(Color.White)
                        ) {
                            val canvasWidth = size.width
                            val canvasHeight = size.height
                            
                            // Professional technical grid
                            val gridSize = 40f
                            for (i in 0..(canvasWidth / gridSize).toInt()) {
                                drawLine(Color.LightGray.copy(alpha = 0.5f), Offset(i * gridSize, 0f), Offset(i * gridSize, canvasHeight), strokeWidth = 1f)
                            }
                            for (i in 0..(canvasHeight / gridSize).toInt()) {
                                drawLine(Color.LightGray.copy(alpha = 0.5f), Offset(0f, i * gridSize), Offset(canvasWidth, i * gridSize), strokeWidth = 1f)
                            }

                            // Sketch drawing
                            val mainColor = Color(0xFF1976D2)
                            val stroke = Stroke(width = 4f)
                            
                            drawRect(
                                color = mainColor,
                                topLeft = Offset(canvasWidth * 0.25f, canvasHeight * 0.35f),
                                size = Size(canvasWidth * 0.5f, canvasHeight * 0.3f),
                                style = stroke
                            )
                            
                            val path = Path().apply {
                                moveTo(canvasWidth * 0.25f, canvasHeight * 0.35f)
                                lineTo(canvasWidth * 0.35f, canvasHeight * 0.25f)
                                lineTo(canvasWidth * 0.85f, canvasHeight * 0.25f)
                                lineTo(canvasWidth * 0.75f, canvasHeight * 0.35f)
                            }
                            drawPath(path, color = mainColor, style = stroke)
                            
                            drawLine(mainColor, Offset(canvasWidth * 0.25f, canvasHeight * 0.7f), Offset(canvasWidth * 0.75f, canvasHeight * 0.7f), strokeWidth = 2f)
                        }
                        
                        Text(
                            text = blueprint.dimensions,
                            color = Color(0xFF1976D2),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 180.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Detail Section: Materials
                DetailSection(
                    title = "Required Materials",
                    icon = Icons.Default.List,
                    items = blueprint.materials,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Detail Section: Instructions
                DetailSection(
                    title = "Assembly Steps",
                    icon = Icons.Default.Info,
                    items = blueprint.instructions,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    isNumbered = true
                )

                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = { viewModel.downloadBlueprint(blueprint) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(stringResource(R.string.download_blueprint))
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun DetailSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    items: List<String>,
    containerColor: Color,
    isNumbered: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(12.dp))
                Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            items.forEachIndexed { index, item ->
                Text(
                    text = if (isNumbered) "${index + 1}. $item" else "• $item",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 4.dp),
                    lineHeight = 24.sp
                )
            }
        }
    }
}
