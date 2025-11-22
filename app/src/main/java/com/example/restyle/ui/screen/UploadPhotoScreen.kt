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
import com.example.restyle.ui.theme.DarkGreen
import com.example.restyle.ui.theme.MediumGreen
import com.example.restyle.ui.theme.LightGreen
import com.example.restyle.ui.theme.CreamWhite
import java.io.InputStream

/**
 * Enum Action untuk menentukan tipe aksi yang akan dilakukan
 * (Camera atau Gallery) saat request permission.
 */
enum class Action {
    CAMERA, GALLERY
}

/**
 * UploadPhotoScreen adalah layar untuk mengunggah foto item
 * yang akan di-resell, donate, atau recycle.
 * 
 * Layar ini menangani:
 * - Permission request untuk camera dan storage
 * - Capture foto dari camera
 * - Pilih foto dari gallery
 * - Preview foto yang dipilih
 * 
 * @param featureType Tipe fitur: "Resell", "Donate", atau "Recycle"
 * @param onBackClick Callback saat tombol back ditekan
 * @param onResult Callback saat foto berhasil diambil/dipilih dengan Bitmap dan Uri
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadPhotoScreen(
    featureType: String = "Resell",
    onBackClick: () -> Unit = {},
    onResult: (bitmap: Bitmap?, uri: Uri?) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current

    // State untuk preview image
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }

    // Pending action (dipakai saat permission diminta)
    var pendingAction by remember { mutableStateOf<Action?>(null) }

    // Launcher untuk mengambil foto: TakePicturePreview -> mengembalikan Bitmap langsung
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            selectedBitmap = bitmap
            selectedUri = null
            onResult(bitmap, null)
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
            onResult(selectedBitmap, uri)
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
                } else if (selectedUri != null) {
                    // fallback - jika kita hanya punya uri, kita sudah set bitmap saat load, tapi tetap safe
                    Text(
                        text = "Preview tersedia",
                        color = CreamWhite,
                        modifier = Modifier.padding(8.dp)
                    )
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

                Spacer(modifier = Modifier.height(20.dp))

                // Opsional: tombol "Lanjut" untuk submit / ke halaman detail
                Button(
                    onClick = {
                        // panggil callback onResult jika ada foto
                        if (selectedBitmap != null || selectedUri != null) {
                            onResult(selectedBitmap, selectedUri)
                            Toast
                                .makeText(context, "Foto siap di proses", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast
                                .makeText(context, "Pilih atau ambil foto terlebih dahulu", Toast.LENGTH_SHORT)
                                .show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MediumGreen),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(text = "Lanjutkan", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * ActionButton adalah komponen button yang digunakan untuk
 * action seperti Camera dan Upload Photo.
 * 
 * @param text Label button
 * @param icon Icon yang ditampilkan di button
 * @param backgroundColor Warna background button
 * @param textColor Warna text dan icon
 * @param onClick Callback saat button diklik
 */
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