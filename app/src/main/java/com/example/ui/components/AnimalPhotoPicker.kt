package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.theme.MandiGreenNew
import com.example.ui.theme.MandiOrangePrimary
import java.io.File

/**
 * Creates a unique temporary file inside cacheDir and wraps it in a FileProvider content URI.
 */
fun createTempAnimalPhotoUri(context: Context): Uri {
    val photoDir = File(context.cacheDir, "camera_photos").apply { mkdirs() }
    val photoFile = File(photoDir, "animal_photo_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        photoFile
    )
}

enum class AnimalPhotoSource {
    CAMERA,
    GALLERY,
    PRESET
}

/**
 * Comprehensive Animal Photo Picker section with Camera Permission handling,
 * direct camera capture, gallery selection, preset options, and live preview.
 */
@Composable
fun AnimalPhotoPickerSection(
    currentImageUri: String?,
    currentImageResName: String,
    onPhotoSelected: (uri: String?, resName: String, source: AnimalPhotoSource) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showPermissionRationale by remember { mutableStateOf(false) }
    var permissionDeniedMessage by remember { mutableStateOf<String?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    var currentSource by remember {
        mutableStateOf(
            if (!currentImageUri.isNullOrBlank()) AnimalPhotoSource.GALLERY else AnimalPhotoSource.PRESET
        )
    }

    val photoOptions = listOf(
        Pair("goat_cheeni", R.drawable.goat_cheeni),
        Pair("sahiwal_cow", R.drawable.sahiwal_cow),
        Pair("nili_buffalo", R.drawable.nili_buffalo),
        Pair("chickens", R.drawable.chickens),
        Pair("irani_teeter", R.drawable.irani_teeter)
    )

    // Camera Capture Launcher
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            currentSource = AnimalPhotoSource.CAMERA
            permissionDeniedMessage = null
            onPhotoSelected(tempCameraUri.toString(), currentImageResName, AnimalPhotoSource.CAMERA)
        }
    }

    // Camera Permission Launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            permissionDeniedMessage = null
            try {
                val uri = createTempAnimalPhotoUri(context)
                tempCameraUri = uri
                takePictureLauncher.launch(uri)
            } catch (_: Exception) {
                permissionDeniedMessage = "Unable to initialize camera storage."
            }
        } else {
            permissionDeniedMessage = context.getString(R.string.camera_permission_denied)
        }
    }

    // Photo Picker (Gallery) Launcher
    val pickVisualMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            currentSource = AnimalPhotoSource.GALLERY
            permissionDeniedMessage = null
            onPhotoSelected(uri.toString(), currentImageResName, AnimalPhotoSource.GALLERY)
        }
    }

    // Action function to trigger Camera with permission check
    fun launchCameraFlow() {
        val permissionState = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionState == PackageManager.PERMISSION_GRANTED) {
            val uri = createTempAnimalPhotoUri(context)
            tempCameraUri = uri
            takePictureLauncher.launch(uri)
        } else {
            showPermissionRationale = true
        }
    }

    // Permission Explanation Dialog
    if (showPermissionRationale) {
        AlertDialog(
            onDismissRequest = { showPermissionRationale = false },
            icon = {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(MandiOrangePrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Camera Permission",
                        tint = MandiOrangePrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            },
            title = {
                Text(
                    text = stringResource(R.string.camera_permission_title),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.camera_permission_rationale),
                        fontSize = 14.sp,
                        color = Color(0xFF334155),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.camera_permission_rationale_urdu),
                        fontSize = 13.sp,
                        color = Color(0xFF0F766E),
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPermissionRationale = false
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MandiOrangePrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("grant_camera_permission_button")
                ) {
                    Text(stringResource(R.string.grant_permission), color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showPermissionRationale = false
                        pickVisualMediaLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("choose_gallery_from_dialog_button")
                ) {
                    Text(stringResource(R.string.choose_from_gallery))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Animal Photo / تصویر",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )

            if (!currentImageUri.isNullOrBlank()) {
                TextButton(
                    onClick = {
                        onPhotoSelected(null, currentImageResName, AnimalPhotoSource.PRESET)
                    }
                ) {
                    Text(
                        text = stringResource(R.string.remove_photo),
                        fontSize = 12.sp,
                        color = Color(0xFFDC2626)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Main Photo Preview or Placeholder
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.5.dp,
                color = if (!currentImageUri.isNullOrBlank()) MandiOrangePrimary else Color(0xFFE2E8F0)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (!currentImageUri.isNullOrBlank()) {
                    AsyncImage(
                        model = currentImageUri,
                        contentDescription = "Selected Animal Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                    )

                    // Source Badge overlay
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (currentSource == AnimalPhotoSource.CAMERA) MandiGreenNew else MandiOrangePrimary
                            )
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (currentSource == AnimalPhotoSource.CAMERA) Icons.Default.CameraAlt else Icons.Default.Collections,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (currentSource == AnimalPhotoSource.CAMERA) "Camera Photo Captured" else "Gallery Photo Selected",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Retake / Change Button overlay
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black.copy(alpha = 0.7f))
                            .clickable { launchCameraFlow() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Retake",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.retake_photo),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    // Fallback to Preset Image with indicator
                    val presetDrawable = when (currentImageResName) {
                        "goat_cheeni" -> R.drawable.goat_cheeni
                        "sahiwal_cow" -> R.drawable.sahiwal_cow
                        "nili_buffalo" -> R.drawable.nili_buffalo
                        "chickens" -> R.drawable.chickens
                        "irani_teeter" -> R.drawable.irani_teeter
                        else -> R.drawable.maveshi_splash
                    }

                    Image(
                        painter = painterResource(id = presetDrawable),
                        contentDescription = "Preset Animal Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "Preset Catalog Photo",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Two Big Primary Action Buttons: [ Take Photo with Camera ] and [ Choose from Gallery ]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Camera Button
            Button(
                onClick = { launchCameraFlow() },
                colors = ButtonDefaults.buttonColors(containerColor = MandiOrangePrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("take_animal_photo_camera_button")
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Take Photo",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Take Photo",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Gallery Button
            OutlinedButton(
                onClick = {
                    pickVisualMediaLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF0F766E)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0F766E)),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("select_animal_photo_gallery_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Collections,
                    contentDescription = "Gallery",
                    tint = Color(0xFF0F766E),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "From Gallery",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Permission denied warning alert if user denied camera permission
        if (permissionDeniedMessage != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = permissionDeniedMessage!!,
                        fontSize = 12.sp,
                        color = Color(0xFF991B1B),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { permissionDeniedMessage = null },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = Color(0xFF991B1B),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Preset Livestock Selector
        Text(
            text = "Or Choose from Standard Catalog Breeds",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF64748B)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            photoOptions.forEach { (name, resId) ->
                val isSel = currentImageUri.isNullOrBlank() && currentImageResName == name
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            width = if (isSel) 2.5.dp else 1.dp,
                            color = if (isSel) MandiOrangePrimary else Color(0xFFE2E8F0),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            currentSource = AnimalPhotoSource.PRESET
                            onPhotoSelected(null, name, AnimalPhotoSource.PRESET)
                        }
                ) {
                    Image(
                        painter = painterResource(id = resId),
                        contentDescription = name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    if (isSel) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MandiOrangePrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Selected",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Photography Tips Card for Livestock Sellers
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Tips",
                        tint = Color(0xFF0F766E),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Tips for high-selling livestock photos / مشورے",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F766E)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "• Take full-body side view standing in open daylight.\n• Close-up shot of animal teeth / danda for age confirmation.\n• Show clear udders/milk veins for dairy animals.",
                    fontSize = 11.sp,
                    color = Color(0xFF475569),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

/**
 * Dedicated Interactive Animal Camera & Photo Studio Dialog
 * Accessible from HomeScreen quick actions to capture or select animal photos
 * and directly proceed to create listing.
 */
@Composable
fun AnimalPhotoStudioDialog(
    onDismiss: () -> Unit,
    onProceedWithPhoto: (photoUri: String, suggestedCategory: String) -> Unit
) {
    val context = LocalContext.current
    var selectedPhotoUri by remember { mutableStateOf<String?>(null) }
    var selectedCategory by remember { mutableStateOf("Cow") }
    var showPermissionRationale by remember { mutableStateOf(false) }
    var permissionDeniedMessage by remember { mutableStateOf<String?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    val categories = listOf("Cow", "Goat", "Buffalo", "Lamb", "Chicken", "Birds", "Camel")

    // Camera Capture Launcher
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            selectedPhotoUri = tempCameraUri.toString()
            permissionDeniedMessage = null
        }
    }

    // Camera Permission Launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            permissionDeniedMessage = null
            val uri = createTempAnimalPhotoUri(context)
            tempCameraUri = uri
            takePictureLauncher.launch(uri)
        } else {
            permissionDeniedMessage = context.getString(R.string.camera_permission_denied)
        }
    }

    // Photo Picker (Gallery) Launcher
    val pickVisualMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedPhotoUri = uri.toString()
            permissionDeniedMessage = null
        }
    }

    fun launchCamera() {
        val permissionState = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionState == PackageManager.PERMISSION_GRANTED) {
            val uri = createTempAnimalPhotoUri(context)
            tempCameraUri = uri
            takePictureLauncher.launch(uri)
        } else {
            showPermissionRationale = true
        }
    }

    if (showPermissionRationale) {
        AlertDialog(
            onDismissRequest = { showPermissionRationale = false },
            title = {
                Text(
                    text = stringResource(R.string.camera_permission_title),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.camera_permission_rationale),
                    fontSize = 14.sp,
                    color = Color(0xFF334155)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPermissionRationale = false
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MandiOrangePrimary)
                ) {
                    Text(stringResource(R.string.grant_permission), color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionRationale = false }) {
                    Text("Cancel")
                }
            },
            containerColor = Color.White
        )
    }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("animal_photo_studio_dialog")
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Animal Photo Studio",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Preview Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF1F5F9))
                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedPhotoUri != null) {
                        AsyncImage(
                            model = selectedPhotoUri,
                            contentDescription = "Captured Animal",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f))
                                .clickable { selectedPhotoUri = null }
                                .padding(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = "Add Photo",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Take a photo of your animal or select from gallery",
                                fontSize = 13.sp,
                                color = Color(0xFF64748B),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }

                if (permissionDeniedMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = permissionDeniedMessage!!,
                        fontSize = 11.sp,
                        color = Color(0xFFDC2626),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action buttons: Take Photo & Select Gallery
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { launchCamera() },
                        colors = ButtonDefaults.buttonColors(containerColor = MandiOrangePrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("dialog_take_photo_button")
                    ) {
                        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Take Photo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            pickVisualMediaLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("dialog_gallery_button")
                    ) {
                        Icon(imageVector = Icons.Default.Collections, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Gallery", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Animal Type Detection / Selection
                Text(
                    text = "Select Animal Category",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF334155),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                androidx.compose.foundation.lazy.LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories.size) { idx ->
                        val cat = categories[idx]
                        val isSel = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) MandiOrangePrimary else Color(0xFFF1F5F9))
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cat,
                                fontSize = 12.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSel) Color.White else Color(0xFF334155)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Proceed Button
                Button(
                    onClick = {
                        val photo = selectedPhotoUri ?: "res:goat_cheeni"
                        onProceedWithPhoto(photo, selectedCategory)
                    },
                    enabled = selectedPhotoUri != null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MandiGreenNew,
                        disabledContainerColor = Color(0xFFCBD5E1)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("proceed_with_animal_photo_button")
                ) {
                    Text(
                        text = if (selectedPhotoUri != null) "Post Listing with this Photo" else "Select or Take Photo First",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Multi-photo picker component for livestock listings.
 * Enforces at least 3 photos (1. Face/Front, 2. Side Body, 3. Teeth/Danda)
 * Allows capturing real user photos via Camera with runtime permission
 * or selecting photos from device Gallery.
 */
@Composable
fun MultiAnimalPhotoPickerSection(
    photos: List<String>,
    onPhotosChange: (List<String>) -> Unit,
    currentPresetRes: String,
    onPresetChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showPermissionRationale by remember { mutableStateOf(false) }
    var activeSlotForCamera by remember { mutableStateOf(0) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    var permissionDeniedMessage by remember { mutableStateOf<String?>(null) }

    val slotLabels = listOf(
        "Photo 1: Face & Front View (Cover)",
        "Photo 2: Full Body Side Profile",
        "Photo 3: Teeth / Danda Age Verification",
        "Photo 4: Back / Health & Milking View"
    )

    // Camera Capture Launcher
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            val uriStr = tempCameraUri.toString()
            val currentList = photos.toMutableList()
            if (activeSlotForCamera < currentList.size) {
                currentList[activeSlotForCamera] = uriStr
            } else {
                currentList.add(uriStr)
            }
            onPhotosChange(currentList)
            permissionDeniedMessage = null
        }
    }

    // Camera Permission Launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            permissionDeniedMessage = null
            try {
                val uri = createTempAnimalPhotoUri(context)
                tempCameraUri = uri
                takePictureLauncher.launch(uri)
            } catch (_: Exception) {
                permissionDeniedMessage = "Unable to initialize camera storage."
            }
        } else {
            permissionDeniedMessage = context.getString(R.string.camera_permission_denied)
        }
    }

    fun launchCameraForSlot(slotIndex: Int) {
        activeSlotForCamera = slotIndex
        val permissionState = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionState == PackageManager.PERMISSION_GRANTED) {
            val uri = createTempAnimalPhotoUri(context)
            tempCameraUri = uri
            takePictureLauncher.launch(uri)
        } else {
            showPermissionRationale = true
        }
    }

    // Gallery Picker for single slot
    val pickSingleVisualMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val currentList = photos.toMutableList()
            if (activeSlotForCamera < currentList.size) {
                currentList[activeSlotForCamera] = uri.toString()
            } else {
                currentList.add(uri.toString())
            }
            onPhotosChange(currentList)
        }
    }

    // Batch Gallery Picker (select multiple photos at once)
    val pickMultipleVisualMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris ->
        if (uris.isNotEmpty()) {
            val newUris = uris.map { it.toString() }
            val currentList = photos.toMutableList()
            for (u in newUris) {
                if (!currentList.contains(u)) {
                    currentList.add(u)
                }
            }
            onPhotosChange(currentList)
        }
    }

    if (showPermissionRationale) {
        AlertDialog(
            onDismissRequest = { showPermissionRationale = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = MandiOrangePrimary,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = stringResource(R.string.camera_permission_title),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = "Mall Maweshi needs camera access to capture required real animal photos (Head shot, body side-profile, and teeth/danda check) to ensure buyer trust and authentic marketplace listings.",
                    fontSize = 13.sp,
                    color = Color(0xFF334155),
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPermissionRationale = false
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MandiOrangePrimary)
                ) {
                    Text("Grant Camera Permission", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showPermissionRationale = false
                        pickSingleVisualMediaLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                ) {
                    Text("Select from Gallery")
                }
            },
            containerColor = Color.White
        )
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Section Header with requirement badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Animal Photos (Min. 3 Required)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = "کم از کم 3 تصاویر: چہرہ، پورا جسم، اور دانت",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }

            // Status Badge
            val count = photos.size
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (count >= 3) Color(0xFFDCFCE7) else Color(0xFFFEF3C7))
                    .border(
                        1.dp,
                        if (count >= 3) Color(0xFF86EFAC) else Color(0xFFFDE68A),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (count >= 3) "$count/3 Photos Added ✓" else "$count/3 Added (Need ${3 - count} more)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (count >= 3) Color(0xFF15803D) else Color(0xFFB45309)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Slots Column: Photo 1, Photo 2, Photo 3 (and any extra)
        val totalSlotsToRender = maxOf(3, photos.size + (if (photos.size < 4) 1 else 0))
        for (i in 0 until minOf(4, totalSlotsToRender)) {
            val photoUri = photos.getOrNull(i)
            val slotTitle = slotLabels.getOrElse(i) { "Photo ${i + 1}" }
            val isRequired = i < 3

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (photoUri != null) 1.5.dp else 1.dp,
                    color = if (photoUri != null) MandiOrangePrimary else Color(0xFFCBD5E1)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .testTag("animal_photo_slot_$i")
            ) {
                if (photoUri != null) {
                    // Render Captured / Selected Photo
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(75.dp)
                                .clip(RoundedCornerShape(10.dp))
                        ) {
                            if (photoUri.startsWith("res:")) {
                                val resName = photoUri.removePrefix("res:")
                                val drawableId = when (resName) {
                                    "goat_cheeni" -> R.drawable.goat_cheeni
                                    "sahiwal_cow" -> R.drawable.sahiwal_cow
                                    "nili_buffalo" -> R.drawable.nili_buffalo
                                    "chickens" -> R.drawable.chickens
                                    "irani_teeter" -> R.drawable.irani_teeter
                                    else -> R.drawable.maveshi_splash
                                }
                                Image(
                                    painter = painterResource(id = drawableId),
                                    contentDescription = slotTitle,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                AsyncImage(
                                    model = photoUri,
                                    contentDescription = slotTitle,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = slotTitle,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MandiGreenNew,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (photoUri.startsWith("res:")) "Catalog Photo" else "Real User Photo",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "Retake",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MandiOrangePrimary,
                                    modifier = Modifier
                                        .clickable { launchCameraForSlot(i) }
                                        .padding(vertical = 2.dp)
                                )
                                Text(
                                    text = "•",
                                    fontSize = 12.sp,
                                    color = Color(0xFFCBD5E1)
                                )
                                Text(
                                    text = "Gallery",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF0F766E),
                                    modifier = Modifier
                                        .clickable {
                                            activeSlotForCamera = i
                                            pickSingleVisualMediaLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        }
                                        .padding(vertical = 2.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                val currentList = photos.toMutableList()
                                if (i < currentList.size) {
                                    currentList.removeAt(i)
                                    onPhotosChange(currentList)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Delete Photo",
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                } else {
                    // Empty Slot Upload Prompt
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = slotTitle,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isRequired) Color(0xFF1E293B) else Color(0xFF64748B)
                                )
                                if (isRequired) {
                                    Text(
                                        text = "Required for listing verification",
                                        fontSize = 10.sp,
                                        color = Color(0xFFDC2626)
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { launchCameraForSlot(i) },
                                    colors = ButtonDefaults.buttonColors(containerColor = MandiOrangePrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("slot_${i}_camera_button")
                                ) {
                                    Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Camera", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = {
                                        activeSlotForCamera = i
                                        pickSingleVisualMediaLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("slot_${i}_gallery_button")
                                ) {
                                    Icon(imageVector = Icons.Default.Collections, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Gallery", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Batch Upload Button & Preset Autofill
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = {
                    pickMultipleVisualMediaLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0F766E)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0F766E)),
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .testTag("batch_pick_gallery_button")
            ) {
                Icon(imageVector = Icons.Default.Collections, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Select Multiple (Gallery)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            // Quick Fill Standard 3 Photos button
            OutlinedButton(
                onClick = {
                    val sample3 = listOf("res:$currentPresetRes", "res:$currentPresetRes", "res:$currentPresetRes")
                    onPhotosChange(sample3)
                },
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF94A3B8)),
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .testTag("fill_preset_photos_button")
            ) {
                Text("Auto-Fill 3 Presets", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF475569))
            }
        }

        if (permissionDeniedMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = permissionDeniedMessage!!,
                fontSize = 11.sp,
                color = Color(0xFFDC2626)
            )
        }
    }
}

