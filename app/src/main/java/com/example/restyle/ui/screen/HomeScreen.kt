package com.example.restyle.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


/**
 * HomeScreen adalah layar utama aplikasi ReStyle yang menampilkan:
 * - Loyalty card dengan informasi koleksi pakaian user
 * - Grid fitur utama (Marketplace, Resell, Donate, Recycle)
 * - Navigation ke layar lain
 */
@Composable
fun HomeScreen() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeContent(navController = navController)
        }

        // upload route with type argument
        composable("upload/{type}") { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: "Resell"
            UploadPhotoScreen(
                featureType = type,
                onBackClick = { navController.popBackStack() },
                onResult = { _, _ ->
                    // default behavior after a photo is selected/taken:
                    // close upload screen and return to previous screen.
                    // Replace this with navigation to detail/pricing screen if needed.
                    navController.popBackStack()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(navController: NavController) {
    Scaffold(
        topBar = {
            TopBar()
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
                .padding(16.dp)
        ) {
            // Loyalty Card
            LoyaltyCard()

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Features",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D2D2D)
            )

            Spacer(modifier = Modifier.height(16.dp))

            FeatureGrid(navController = navController)
        }
    }
}

/**
 * TopBar menampilkan header aplikasi dengan:
 * - Notification icon (kiri)
 * - Logo/judul Restyle (tengah)
 * - Shopping cart icon (kanan)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Restyle",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6FCF97)
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = { /* Handle notification click */ }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color(0xFF2D2D2D)
                )
            }
        },
        actions = {
            IconButton(onClick = { /* Handle cart click */ }) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Shopping Cart",
                    tint = Color(0xFF2D2D2D)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        )
    )
}

/**
 * LoyaltyCard menampilkan card informasi koleksi pakaian user:
 * - Jumlah item yang siap di-recycle
 * - Impact points user
 * - User name
 * - Ilustrasi dekoratif dengan circle shapes
 */
@Composable
fun LoyaltyCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF6FCF97)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .offset(x = 200.dp, y = (-20).dp)
                    .background(
                        color = Color.White.copy(alpha = 0.1f),
                        shape = CircleShape
                    )
            )

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .offset(x = 250.dp, y = 80.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.1f),
                        shape = CircleShape
                    )
            )

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Clothing Collection",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.9f)
                    )

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Details",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "👕",
                            fontSize = 50.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    Column {
                        Text(
                            text = "12 Items",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Ready to recycle",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Your Impact",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "Henry Moore",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.25f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🌱",
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "25,750",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * FeatureGrid menampilkan grid 2x2 dengan 4 fitur utama:
 * - Marketplace: Browse dan beli pakaian second-hand
 * - Resell: Jual pakaian yang tidak dipakai
 * - Donate: Donasikan pakaian untuk yang membutuhkan
 * - Recycle: Recycle limbah pakaian menjadi produk baru
 * 
 * @param navController Navigation controller untuk navigasi ke layar lain
 */
@Composable
fun FeatureGrid(navController: NavController) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FeatureButton(
                title = "Marketplace",
                icon = "🛍️",
                backgroundColor = Color(0xFF6FCF97),
                textColor = Color.White,
                modifier = Modifier.weight(1f),
                onClick = { /* Navigate to Marketplace */ }
            )
            FeatureButton(
                title = "Resell",
                icon = "💰",
                backgroundColor = Color(0xFFFFE8B3),
                textColor = Color(0xFFFF9F43),
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate("upload/Resell") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FeatureButton(
                title = "Donate",
                icon = "🎁",
                backgroundColor = Color(0xFFFFD6E0),
                textColor = Color(0xFFFF6B9D),
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate("upload/Donate") }
            )
            FeatureButton(
                title = "Recycle",
                icon = "♻️",
                backgroundColor = Color(0xFFD4E8FF),
                textColor = Color(0xFF4A90E2),
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate("upload/Recycle") }
            )
        }
    }
}

/**
 * FeatureButton adalah card button untuk setiap fitur di home screen.
 * Card memiliki background warna yang berbeda untuk setiap fitur dan
 * dilengkapi dengan decorative circles.
 * 
 * @param title Nama fitur
 * @param icon Emoji icon untuk fitur
 * @param backgroundColor Warna background card
 * @param textColor Warna text judul
 * @param modifier Modifier untuk customisasi layout
 * @param onClick Callback saat card diklik
 */
@Composable
fun FeatureButton(
    title: String,
    icon: String,
    backgroundColor: Color,
    textColor: Color = Color.White,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(160.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .offset(x = 60.dp, y = (-20).dp)
                    .background(
                        color = textColor.copy(alpha = 0.1f),
                        shape = CircleShape
                    )
            )

            Box(
                modifier = Modifier
                    .size(60.dp)
                    .offset(x = (-10).dp, y = 100.dp)
                    .background(
                        color = textColor.copy(alpha = 0.08f),
                        shape = CircleShape
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = textColor.copy(alpha = 0.15f),
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = icon,
                            fontSize = 30.sp
                        )
                    }
                }
                Column {
                    Text(
                        text = title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )

                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .background(
                                color = textColor.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        // Use HomeContent with a rememberNavController in preview to avoid runtime nav host in preview
        HomeContent(navController = rememberNavController())
    }
}