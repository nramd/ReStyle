package com.example.restyle.ui.screen

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
// --- IMPORT LENGKAP AGAR TIDAK ERROR ---
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.InputStream
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// --- COLORS ---
val DarkGreen = Color(0xFF2D5F3F)
val MediumGreen = Color(0xFF3D7A52)
val LightGreen = Color(0xFF6FCF97)
val CreamWhite = Color(0xFFFFF8E7)

enum class Action { CAMERA, GALLERY }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadPhotoScreen(
    featureType: String = "Resell",
    onBackClick: () -> Unit = {},
    onSubmit: (Uri, String, String, String) -> Unit = { _, _, _, _ -> }
) {
    val context = LocalContext.current

    // State Tampilan & Data
    var showCameraPreview by remember { mutableStateOf(false) }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // State Form Input
    var title by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // State Permission
    var pendingAction by remember { mutableStateOf<Action?>(null) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    // --- LAUNCHERS ---
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedUri = uri
            val stream = try {
                context.contentResolver.openInputStream(uri)
            } catch (_: Exception) { null }

            stream?.use {
                selectedBitmap = BitmapFactory.decodeStream(it)
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            when (pendingAction) {
                Action.CAMERA -> showCameraPreview = true
                Action.GALLERY -> galleryLauncher.launch("image/*")
                else -> {}
            }
        } else {
            Toast.makeText(context, "Izin diperlukan", Toast.LENGTH_SHORT).show()
        }
        pendingAction = null
    }

    fun checkAndRequestPermission(action: Action) {
        pendingAction = action
        val permission = if (action == Action.CAMERA) Manifest.permission.CAMERA else {
            if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE
        }
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            when (action) {
                Action.CAMERA -> showCameraPreview = true
                Action.GALLERY -> galleryLauncher.launch("image/*")
            }
        } else {
            permissionLauncher.launch(permission)
        }
    }

    // --- LOGIKA UI UTAMA ---
    if (showCameraPreview) {
        // TAMPILAN 1: KAMERA FULL SCREEN
        CameraCaptureScreen(
            executor = cameraExecutor,
            onImageCaptured = { uri ->
                showCameraPreview = false
                selectedUri = uri
                val stream = try {
                    context.contentResolver.openInputStream(uri)
                } catch (_: Exception) { null }
                stream?.use { selectedBitmap = BitmapFactory.decodeStream(it) }
            },
            onError = { showCameraPreview = false },
            onClose = { showCameraPreview = false }
        )
    } else if (selectedUri != null && selectedBitmap != null) {
        // TAMPILAN 2: FORMULIR INPUT (Muncul setelah foto dipilih)
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Lengkapi Detail", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = {
                            selectedUri = null
                            selectedBitmap = null
                        }) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkGreen)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Preview Foto
                Image(
                    bitmap = selectedBitmap!!.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(12.dp))
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Input Nama Barang
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nama Barang") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Input Harga
                OutlinedTextField(
                    value = price,
                    onValueChange = { if (it.all { char -> char.isDigit() }) price = it },
                    label = { Text("Harga (Rp)") },
                    leadingIcon = { Icon(Icons.Default.AttachMoney, null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Input Deskripsi + MIC
                Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Deskripsi") },
                        placeholder = { Text("Kondisi, ukuran, minus...") },
                        modifier = Modifier.weight(1f).height(120.dp),
                        maxLines = 5,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    // Tombol Mic
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        SpeechToTextButton(
                            onResult = { text ->
                                val spasi = if (description.isNotEmpty()) " " else ""
                                description += "$spasi$text"
                            }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Bicara", fontSize = 10.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Tombol Submit Final
                Button(
                    onClick = {
                        if (title.isNotEmpty() && price.isNotEmpty() && description.isNotEmpty()) {
                            onSubmit(selectedUri!!, title, price, description)
                        } else {
                            Toast.makeText(context, "Mohon lengkapi semua data", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MediumGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Upload Barang", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    } else {
        // TAMPILAN 3: MENU PILIH FOTO (Awal)
        Scaffold(
            topBar = {
                // Di sini diubah "Item" jadi "Barang" agar full Indo
                TopAppBar(
                    title = { Text(text = "$featureType Barang", fontWeight = FontWeight.Bold, color = Color.White) },
                    navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) } },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkGreen)
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().background(DarkGreen).padding(paddingValues)) {
                // Dekorasi Background
                Box(Modifier.size(250.dp).offset((-50).dp, (-50).dp).background(MediumGreen.copy(0.3f), CircleShape))
                Box(Modifier.size(180.dp).offset(280.dp, 150.dp).background(LightGreen.copy(0.15f), CircleShape))

                Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(40.dp))

                    Box(
                        modifier = Modifier.size(220.dp).background(MediumGreen.copy(0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "📸", fontSize = 80.sp)
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                    Text("Ambil foto barang untuk mulai", color = CreamWhite, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.weight(1f))

                    ActionButton("Kamera", Icons.Default.PhotoCamera, CreamWhite, DarkGreen) { checkAndRequestPermission(Action.CAMERA) }
                    Spacer(modifier = Modifier.height(12.dp))
                    ActionButton("Galeri", Icons.Default.Upload, LightGreen, Color.White) { checkAndRequestPermission(Action.GALLERY) }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

// --- HELPER COMPOSABLES ---

@Composable
fun SpeechToTextButton(onResult: (String) -> Unit) {
    val context = LocalContext.current
    val speechLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val text = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (text != null) onResult(text)
        }
    }
    IconButton(
        onClick = {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                // --- UPDATE: PAKSA BAHASA INDONESIA ---
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "id-ID")
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "id-ID")
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Silakan bicara deskripsi barang...")
            }
            try { speechLauncher.launch(intent) }
            catch (_: Exception) { // Fix warning 'e' unused
                Toast.makeText(context, "Fitur suara tidak tersedia", Toast.LENGTH_SHORT).show()
            }
        },
        modifier = Modifier.background(Color(0xFFE8F5E9), CircleShape)
    ) {
        Icon(Icons.Default.Mic, null, tint = Color(0xFF2D5F3F))
    }
}

@Composable
fun ActionButton(text: String, icon: ImageVector, bgColor: Color, txtColor: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = bgColor),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(4.dp)
    ) {
        Icon(icon, null, tint = txtColor)
        Spacer(Modifier.width(12.dp))
        Text(text, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = txtColor)
    }
}

@Composable
fun CameraCaptureScreen(
    executor: ExecutorService,
    onImageCaptured: (Uri) -> Unit,
    onError: () -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    var flashMode by remember { mutableStateOf(ImageCapture.FLASH_MODE_OFF) }

    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }

    LaunchedEffect(lensFacing) {
        val provider = ProcessCameraProvider.getInstance(context).get()
        provider.unbindAll()
        try {
            provider.bindToLifecycle(lifecycleOwner, CameraSelector.Builder().requireLensFacing(lensFacing).build(), preview, imageCapture)
            preview.setSurfaceProvider(previewView.surfaceProvider)
        } catch (e: Exception) {
            Log.e("CameraX", "Binding failed", e)
        }
    }

    LaunchedEffect(flashMode) { imageCapture.flashMode = flashMode }

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView({ previewView }, Modifier.fillMaxSize())
        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            IconButton(onClick = onClose) { Icon(Icons.Default.Close, null, tint = Color.White) }
            IconButton(onClick = {
                flashMode = if (flashMode == ImageCapture.FLASH_MODE_OFF) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF
            }) {
                Icon(if (flashMode == ImageCapture.FLASH_MODE_ON) Icons.Default.FlashOn else Icons.Default.FlashOff, null, tint = if(flashMode == ImageCapture.FLASH_MODE_ON) Color.Yellow else Color.White)
            }
        }
        Row(Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(bottom = 50.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(50.dp))
            Box(Modifier.size(80.dp).border(4.dp, Color.White, CircleShape).padding(6.dp).clip(CircleShape).background(Color.White).clickable {
                val file = File(context.cacheDir, "img_${System.currentTimeMillis()}.jpg")
                val opts = ImageCapture.OutputFileOptions.Builder(file).build()
                imageCapture.takePicture(opts, executor, object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                        ContextCompat.getMainExecutor(context).execute { onImageCaptured(uri) }
                    }
                    override fun onError(exc: ImageCaptureException) { onError() }
                })
            })
            IconButton(onClick = { lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK }) {
                Icon(Icons.Default.Cameraswitch, null, tint = Color.White, modifier = Modifier.size(30.dp))
            }
        }
    }
}