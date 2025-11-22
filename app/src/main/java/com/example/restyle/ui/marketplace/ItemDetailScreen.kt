package com.example.restyle.ui.marketplace

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.restyle.data.model.Photo
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    photo: Photo,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Item Detail",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Image
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                shape = RoundedCornerShape(0.dp),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(photo.imageUrl),
                    contentDescription = photo.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Title
                Text(
                    text = photo.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D2D2D)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Price
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF6FCF97).copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "Rp ${formatPrice(photo.price)}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6FCF97),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Description Section
                Text(
                    text = "Description",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D2D2D)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F5F5)
                    )
                ) {
                    Text(
                        text = photo.description.ifBlank { "No description provided" },
                        fontSize = 14.sp,
                        color = Color(0xFF2D2D2D),
                        modifier = Modifier.padding(16.dp),
                        lineHeight = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Seller Info Section
                Text(
                    text = "Seller Information",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D2D2D)
                )

                Spacer(modifier = Modifier.height(8.dp))

                var sellerName by remember { mutableStateOf<String?>(null) }
                LaunchedEffect(photo.userId) {
                    FirebaseAuth.getInstance().currentUser?.let { currentUser ->
                        if (currentUser.uid == photo.userId) {
                            sellerName = currentUser.displayName ?: "Unknown User"
                        } else {
                            sellerName = "Seller ${photo.userId.take(8)}"
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F5F5)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF6FCF97)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Seller",
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = sellerName ?: "Loading...",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF2D2D2D)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Posted date
                            photo.timestamp?.let { timestamp ->
                                Text(
                                    text = "Posted ${formatDate(timestamp)}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF9E9E9E)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Item Details
                Text(
                    text = "Item Details",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D2D2D)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F5F5)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        DetailRow(label = "Category", value = photo.category)
                        Divider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = Color(0xFFE0E0E0)
                        )
                        DetailRow(label = "Status", value = photo.status)
                        Divider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = Color(0xFFE0E0E0)
                        )
                        DetailRow(
                            label = "Item ID",
                            value = photo.id.take(8) + "..."
                        )
                    }
                }

                Spacer(modifier = Modifier.height(80.dp)) // Space for bottom button
            }

            // Bottom Button (Fixed at bottom - outside scroll)
        }

        // Contact Seller Button - Fixed at bottom
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = { /* TODO: Implement contact seller */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6FCF97)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Contact Seller",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color(0xFF9E9E9E)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2D2D2D)
        )
    }
}

fun formatDate(date: Date): String {
    val now = Date()
    val diff = now.time - date.time
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        days > 30 -> SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)
        days > 0 -> "$days day${if (days > 1) "s" else ""} ago"
        hours > 0 -> "$hours hour${if (hours > 1) "s" else ""} ago"
        minutes > 0 -> "$minutes minute${if (minutes > 1) "s" else ""} ago"
        else -> "Just now"
    }
}