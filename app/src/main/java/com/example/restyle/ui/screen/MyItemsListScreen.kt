package com.example.restyle.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.restyle.data.model.Photo
import androidx.compose.material.icons.filled.Refresh

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyItemsListScreen(
    onNavigateBack: () -> Unit,
    onItemClick: (Photo) -> Unit = {},
    userId: String = "default_user",
    viewModel: HomeViewModel = viewModel()
) {
    val myResellItems by viewModel.myResellItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Items",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshItems() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                // Loading State
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF6FCF97)
                    )
                }
            } else if (myResellItems.isEmpty()) {
                // Empty State
                EmptyItemsState()
            } else {
                // Items List
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    // Summary Card
                    ItemsSummaryCard(items = myResellItems)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "All Items (${myResellItems.size})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D2D2D),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    // Items Grid
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(myResellItems) { photo ->
                            ItemCard(
                                photo = photo,
                                onClick = { onItemClick(photo) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemsSummaryCard(items: List<Photo>) {
    val totalValue = items.sumOf { it.price }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF6FCF97)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Total Items",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Text(
                    text = "${items.size}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Total Value",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Text(
                    text = "Rp ${formatPrice(totalValue)}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun ItemCard(
    photo: Photo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Photo Preview
            Card(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(photo.imageUrl),
                    contentDescription = photo.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Item Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = photo.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D2D2D),
                    maxLines = 1
                )

                if (photo.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = photo.description,
                        fontSize = 13.sp,
                        color = Color(0xFF9E9E9E),
                        maxLines = 2
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF6FCF97).copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "Rp ${formatPrice(photo.price)}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6FCF97),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF5F5F5)
                    ) {
                        Text(
                            text = photo.status,
                            fontSize = 11.sp,
                            color = Color(0xFF2D2D2D),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyItemsState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "📦",
                fontSize = 80.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "No Items Yet",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D2D2D)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Start adding items to your store\nby tapping the Resell button",
                fontSize = 14.sp,
                color = Color(0xFF9E9E9E),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}