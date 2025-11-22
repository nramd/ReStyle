package com.example.restyle.ui.screen

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.restyle.ui.photodetail.PhotoDetailScreen
import com.example.restyle.ui.pickup.PickupLocationScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.restyle.ui.screen.HomeViewModel
import com.example.restyle.data.model.Photo
import androidx.compose.foundation.Image
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TextButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.layout.ContentScale
import com.example.restyle.ui.screen.MyItemsListScreen
import androidx.compose.runtime.LaunchedEffect
import com.example.restyle.ui.marketplace.MarketplaceScreen
import com.example.restyle.ui.marketplace.ItemDetailScreen
import com.example.restyle.ui.marketplace.MarketplaceViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button


@Composable
fun HomeScreen() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        // Route: Home
        composable("home") {
            HomeContent(navController = navController)
        }

        // Route: Upload Photo
        composable("upload/{type}") { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: "Resell"

            UploadPhotoScreen(
                featureType = type,
                onBackClick = { navController.popBackStack() },
                onPhotoSelected = { uri ->
                    val encodedUri = Uri.encode(uri.toString())
                    navController.navigate("photo_detail/$encodedUri/$type")
                }
            )
        }

        composable(
            route = "photo_detail/{imageUri}/{category}",
            arguments = listOf(
                navArgument("imageUri") { type = NavType.StringType },
                navArgument("category") {
                    type = NavType.StringType
                    defaultValue = "Resell"
                }
            )
        ) { backStackEntry ->
            val imageUriString = backStackEntry.arguments?.getString("imageUri")
            val category = backStackEntry.arguments?.getString("category") ?: "Resell"
            val imageUri = imageUriString?.let { Uri.parse(it) }

            PhotoDetailScreen(
                imageUri = imageUri,
                category = category,
                onNavigateBack = {
                    navController.popBackStack("home", inclusive = false)
                },
                onNavigateToPickup = { uri, title, desc, cat ->
                    val encodedUri = Uri.encode(uri.toString())
                    val encodedTitle = Uri.encode(title)
                    val encodedDesc = Uri.encode(desc)
                    navController.navigate("pickup_location/$encodedUri/$encodedTitle/$encodedDesc/$cat")
                }
            )
        }

        composable(
            route = "pickup_location/{imageUri}/{title}/{description}/{category}",
            arguments = listOf(
                navArgument("imageUri") { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType },
                navArgument("description") { type = NavType.StringType },
                navArgument("category") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val imageUriString = backStackEntry.arguments?.getString("imageUri")
            val title = backStackEntry.arguments?.getString("title") ?: ""
            val description = backStackEntry.arguments?.getString("description") ?: ""
            val category = backStackEntry.arguments?.getString("category") ?: "Donate"
            val imageUri = imageUriString?.let { Uri.parse(it) }

            PickupLocationScreen(
                imageUri = imageUri,
                title = title,
                description = description,
                category = category,
                onNavigateBack = {
                    navController.popBackStack("home", inclusive = false)
                }
            )
        }
        // Route: My-items
        composable("my_items_list") {
            MyItemsListScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onItemClick = { photo ->
                }
            )
        }
        // Route: Marketplace
        composable("marketplace") {
            MarketplaceScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onItemClick = { photo ->
                    navController.navigate("item_detail/${photo.id}")
                }
            )
        }
        // Route: Item Detail
        composable(
            route = "item_detail/{itemId}",
            arguments = listOf(
                navArgument("itemId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: ""

            val marketplaceViewModel: MarketplaceViewModel = viewModel()
            val marketplaceItems by marketplaceViewModel.marketplaceItems.collectAsState()
            val selectedItem = marketplaceItems.find { it.id == itemId }

            if (selectedItem != null) {
                ItemDetailScreen(
                    photo = selectedItem,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Item not found")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { navController.popBackStack() }) {
                            Text("Go Back")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val myResellItems by viewModel.myResellItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshItems()
    }

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
            MyItemsSection(
                items = myResellItems,
                isLoading = isLoading,
                onViewAllClick = {
                    navController.navigate("my_items_list")
                }
            )

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

@Composable
fun MyItemsSection(
    items: List<Photo>,
    isLoading: Boolean,
    onViewAllClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            if (isLoading) {
                // Loading State
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(40.dp),
                        color = Color(0xFF6FCF97)
                    )
                }
            } else {
                Column {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "My Items",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2D2D2D)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "🏪",
                                fontSize = 20.sp
                            )
                        }

                        if (items.isNotEmpty()) {
                            TextButton(onClick = onViewAllClick) {
                                Text(
                                    text = "View All",
                                    color = Color(0xFF6FCF97),
                                    fontWeight = FontWeight.SemiBold
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowRight,
                                    contentDescription = "View All",
                                    tint = Color(0xFF6FCF97)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (items.isEmpty()) {
                        // Empty State
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(
                                        color = Color(0xFFF5F5F5),
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "📦", fontSize = 30.sp)
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = "No items yet",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF2D2D2D)
                                )
                                Text(
                                    text = "Start selling your items",
                                    fontSize = 13.sp,
                                    color = Color(0xFF9E9E9E)
                                )
                            }
                        }
                    } else {
                        // Items Preview (3 photos + count)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Show first 3 photos
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items.take(3).forEach { photo ->
                                    Card(
                                        modifier = Modifier.size(60.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        elevation = CardDefaults.cardElevation(2.dp)
                                    ) {
                                        Image(
                                            painter = rememberAsyncImagePainter(photo.imageUrl),
                                            contentDescription = photo.title,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Item Count & Info
                            Column {
                                Text(
                                    text = "${items.size} Item${if (items.size > 1) "s" else ""}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2D2D2D)
                                )

                                // Calculate total value
                                val totalValue = items.sumOf { it.price }
                                if (totalValue > 0) {
                                    Text(
                                        text = "Rp ${formatPrice(totalValue)}",
                                        fontSize = 14.sp,
                                        color = Color(0xFF6FCF97),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun formatPrice(price: Long): String {
    return price.toString().reversed().chunked(3).joinToString(".").reversed()
}

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
                onClick = { navController.navigate("marketplace")}
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
        val navController = rememberNavController()
        HomeContent(navController = navController)
    }
}