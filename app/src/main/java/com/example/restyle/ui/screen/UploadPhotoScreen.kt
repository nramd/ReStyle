//UploadPhotoScreen.kt
package com.example.restyle.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

// Warna Hijau Gelap Theme
val DarkGreen = Color(0xFF2D5F3F)
val MediumGreen = Color(0xFF3D7A52)
val LightGreen = Color(0xFF6FCF97)
val CreamWhite = Color(0xFFFFF8E7)

/**
 * Enum Action harus dideklarasikan di luar fungsi composable (top-level),
 * karena Kotlin tidak mengizinkan enum local di dalam fungsi.
 */
enum class Action {
    CAMERA, GALLERY
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadPhotoScreen(
    featureType: String = "Resell", // "Resell", "Donate", atau "Recycle"
    onBackClick: () -> Unit = {},
    onPhotoSelected: (Uri) -> Unit = {} // BARU: callback untuk navigate ke PhotoDetail
) {
    val context = LocalContext.current

    // State untuk preview image
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }

    // Pending action (dipakai saat permission diminta)
    var pendingAction by remember { mutableStateOf<Action?>(null) }

    // Helper function: convert Bitmap to Uri
    fun bitmapToUri(bitmap: Bitmap): Uri? {
        return try {
            val file = File(context.cacheDir, "temp_photo_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Launcher untuk mengambil foto: TakePicturePreview -> mengembalikan Bitmap langsung
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            selectedBitmap = bitmap
            // Convert bitmap to URI
            val uri = bitmapToUri(bitmap)
            if (uri != null) {
                selectedUri = uri
                // Langsung navigate ke PhotoDetail
                onPhotoSelected(uri)
            } else {
                Toast.makeText(context, "Gagal menyimpan foto", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Foto tidak diambil", Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher untuk memilih gambar dari gallery (GetContent)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedUri = uri
            // convert URI -> Bitmap for preview
            val stream: InputStream? = try {
                context.contentResolver.openInputStream(uri)
            } catch (e: Exception) {
                null
            }
            stream?.use {
                val bitmap = BitmapFactory.decodeStream(it)
                selectedBitmap = bitmap
            }
            // Langsung navigate ke PhotoDetail
            onPhotoSelected(uri)
        } else {
            Toast.makeText(context, "Tidak ada foto yang dipilih", Toast.LENGTH_SHORT).show()
        }
    }

    // Request permission launcher (single permission)
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted: Boolean ->
        if (granted) {
            when (pendingAction) {
                Action.CAMERA -> cameraLauncher.launch(null)
                Action.GALLERY -> galleryLauncher.launch("image/*")
                else -> { /* nothing */ }
            }
        } else {
            Toast.makeText(context, "Izin diperlukan untuk melanjutkan", Toast.LENGTH_SHORT).show()
        }
        pendingAction = null
    }

    // Helper: cek permission sudah granted?
    fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    // Helper: request permission sesuai API
    fun requestPermissionFor(action: Action) {
        pendingAction = action
        val permission = when (action) {
            Action.CAMERA -> Manifest.permission.CAMERA
            Action.GALLERY -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Android 13+
                Manifest.permission.READ_MEDIA_IMAGES
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
        }
        // Jika sudah granted, jalankan action langsung
        if (isPermissionGranted(permission)) {
            when (action) {
                Action.CAMERA -> cameraLauncher.launch(null)
                Action.GALLERY -> galleryLauncher.launch("image/*")
            }
        } else {
            // request permission
            permissionLauncher.launch(permission)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (featureType) {
                            "Resell" -> "Resell Item"
                            "Donate" -> "Donate Item"
                            "Recycle" -> "Recycle Item"
                            else -> "Upload Photo"
                        },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkGreen
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkGreen)
                .padding(paddingValues)
        ) {
            // Decoration
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .offset(x = (-50).dp, y = (-50).dp)
                    .background(
                        color = MediumGreen.copy(alpha = 0.3f),
                        shape = CircleShape
                    )
            )
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .offset(x = 280.dp, y = 150.dp)
                    .background(
                        color = LightGreen.copy(alpha = 0.15f),
                        shape = CircleShape
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // Preview area: tampilkan foto jika ada
                if (selectedBitmap != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CreamWhite,
                        tonalElevation = 4.dp,
                        modifier = Modifier.size(220.dp)
                    ) {
                        Image(
                            bitmap = selectedBitmap!!.asImageBitmap(),
                            contentDescription = "Preview",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        )
                    }
                } else {
                    // Placeholder illustration area
                    Box(
                        modifier = Modifier
                            .size(220.dp)
                            .background(color = MediumGreen.copy(alpha = 0.4f), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (featureType) {
                                "Resell" -> "💰"
                                "Donate" -> "🎁"
                                "Recycle" -> "♻️"
                                else -> "📸"
                            },
                            fontSize = 56.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = when (featureType) {
                        "Resell" -> "Upload foto barang yang ingin dijual"
                        "Donate" -> "Upload foto barang yang ingin didonasikan"
                        "Recycle" -> "Upload foto limbah baju untuk didaur ulang"
                        else -> "Upload foto item Anda"
                    },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CreamWhite,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Analisis kondisi dan prediksi\nberbasis AI",
                    fontSize = 14.sp,
                    color = CreamWhite.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Kamera button (request permission CAMERA jika perlu)
                ActionButton(
                    text = "Kamera",
                    icon = Icons.Default.PhotoCamera,
                    backgroundColor = CreamWhite,
                    textColor = DarkGreen,
                    onClick = {
                        requestPermissionFor(Action.CAMERA)
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Upload button (gallery) - request READ permission kalau perlu
                ActionButton(
                    text = "Upload Foto",
                    icon = Icons.Default.Upload,
                    backgroundColor = LightGreen,
                    textColor = Color.White,
                    onClick = {
                        requestPermissionFor(Action.GALLERY)
                    }
                )
            }
        }
    }
}

@Composable
fun ActionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}