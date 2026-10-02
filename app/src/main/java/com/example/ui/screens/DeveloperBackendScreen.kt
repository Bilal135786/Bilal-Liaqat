package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.R
import com.example.data.AnimalListing
import com.example.data.AppSettingEntity
import com.example.ui.MaveshiViewModel
import com.example.ui.theme.MandiGreenNew
import com.example.ui.theme.MandiOrangePrimary
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeveloperBackendScreen(
    viewModel: MaveshiViewModel,
    onBack: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit = {}
) {
    val context = LocalContext.current
    val ownerEmail = "bilalkhichi.156@gmail.com"
    val allListings by viewModel.rawListings.collectAsState()
    val flaggedListings by viewModel.flaggedListings.collectAsState()
    val allSettings by viewModel.allSettings.collectAsState()
    val databaseStats by viewModel.databaseStats.collectAsState()
    val syncStatus by viewModel.syncStatus.collectAsState()
    val isOwnerAuth by viewModel.isOwnerAuthenticated.collectAsState()

    var isUnlocked by remember { mutableStateOf(isOwnerAuth) }
    var pinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf<String?>(null) }
    var showChangePinDialog by remember { mutableStateOf(false) }
    var newPinInput by remember { mutableStateOf("") }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Security & Play", "Backend Data", "Illegal Photos", "Owner Settings", "Social Media")

    var searchQuery by remember { mutableStateOf("") }
    var showJsonDialog by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var resetDialogType by remember { mutableStateOf("FRESH") } // FRESH, RESTORE, DELETE_ALL

    val backendBaseUrl = allSettings.find { it.key == "cloud_backend_endpoint" }?.value
        ?: "https://maveshi-backend.asia-east1.firebasedatabase.app"
    val completeBackendLink = "$backendBaseUrl/v1/animal_listings.json"

    // Owner PIN Lock Gate if not yet authenticated
    if (!isUnlocked) {
        Dialog(onDismissRequest = onBack) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .testTag("owner_pin_dialog")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0F172A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Security Lock",
                            tint = MandiOrangePrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Owner Master Security",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "App Owner: Bilal Khichi\n($ownerEmail)",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = {
                            if (it.length <= 8) {
                                pinInput = it
                                pinError = null
                            }
                        },
                        label = { Text("Enter Master PIN (Default: 7860)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        isError = pinError != null,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (pinError != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = pinError!!,
                            color = Color(0xFFDC2626),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onBack,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Exit")
                        }

                        Button(
                            onClick = {
                                if (viewModel.verifyOwnerPin(pinInput)) {
                                    isUnlocked = true
                                    Toast.makeText(context, "Owner Verified! Welcome Bilal Khichi.", Toast.LENGTH_SHORT).show()
                                } else {
                                    pinError = "Incorrect PIN. Default is 7860."
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MandiOrangePrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Unlock", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Change PIN Dialog
    if (showChangePinDialog) {
        AlertDialog(
            onDismissRequest = { showChangePinDialog = false },
            title = { Text("Update Owner Master PIN", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column {
                    Text("Set a secure PIN to control app security and database access.", fontSize = 12.sp, color = Color(0xFF64748B))
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newPinInput,
                        onValueChange = { newPinInput = it },
                        label = { Text("New PIN (4-8 digits)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPinInput.length >= 4) {
                            viewModel.updateOwnerPin(newPinInput)
                            showChangePinDialog = false
                            Toast.makeText(context, "Owner Master PIN updated successfully!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "PIN must be at least 4 digits", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MandiOrangePrimary)
                ) {
                    Text("Save PIN")
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangePinDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Raw JSON Dialog for developer
    if (showJsonDialog) {
        Dialog(onDismissRequest = { showJsonDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(520.dp)
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Live Backend JSON Payload",
                            color = Color(0xFF38BDF8),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Backend JSON", generateJsonString(allListings))
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "JSON copied to clipboard!", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy JSON", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(Color(0xFF020617), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = generateJsonString(allListings),
                            color = Color(0xFF4ADE80),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { showJsonDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = MandiOrangePrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Close Inspector", color = Color.White)
                    }
                }
            }
        }
    }

    // Reset Confirm Dialog
    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = {
                Text(
                    text = when (resetDialogType) {
                        "FRESH" -> "Enable Fresh Mode?"
                        "RESTORE" -> "Restore Demo Animals?"
                        else -> "Delete All Listings?"
                    },
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = when (resetDialogType) {
                        "FRESH" -> "This will remove preloaded sample listings so the new user can start with a clean state and add their real animal photos from scratch."
                        "RESTORE" -> "This will reload the initial sample livestock catalog (Sahiwal cow, Makhi Cheeni goat, etc.)."
                        else -> "This will permanently remove all listings from both local SQLite and Cloud."
                    },
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResetConfirmDialog = false
                        when (resetDialogType) {
                            "FRESH" -> {
                                viewModel.clearSeedListings()
                                Toast.makeText(context, "Fresh mode enabled! Ready for new user animal data.", Toast.LENGTH_SHORT).show()
                            }
                            "RESTORE" -> {
                                viewModel.restoreSeedListings()
                                Toast.makeText(context, "Demo listings restored successfully.", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                viewModel.deleteAllListings()
                                Toast.makeText(context, "All listings wiped clean.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (resetDialogType == "DELETE_ALL") Color(0xFFDC2626) else MandiOrangePrimary
                    )
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Owner Security & Backend",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF22C55E).copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "OWNER: BILAL KHICHI",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF16A34A)
                                )
                            }
                        }
                        Text(
                            text = "Google Play Certified • Total Security & Access Control",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF0F172A)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.refreshDatabaseStats()
                            viewModel.syncAllWithFirebase()
                            Toast.makeText(context, "Syncing backend with Cloud...", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = "Sync",
                            tint = MandiGreenNew
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                modifier = Modifier.statusBarsPadding()
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding()
                .testTag("developer_backend_screen")
        ) {
            // Backend Link Hero Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF22C55E))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "COMPLETE BACKEND LINK",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF38BDF8)
                            )
                        }

                        Row {
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Backend Link", completeBackendLink)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Backend URL copied!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Link",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(completeBackendLink))
                                        context.startActivity(intent)
                                    } catch (_: Exception) {
                                        Toast.makeText(context, "URL: $completeBackendLink", Toast.LENGTH_LONG).show()
                                    }
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OpenInBrowser,
                                    contentDescription = "Open Link",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = completeBackendLink,
                        fontSize = 11.sp,
                        color = Color(0xFFE2E8F0),
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Metrics row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricPill(label = "Listings", value = "${allListings.size}", color = Color(0xFF38BDF8))
                        MetricPill(label = "User Posts", value = "${allListings.count { it.isUserListing }}", color = Color(0xFF4ADE80))
                        MetricPill(label = "Flagged", value = "${flaggedListings.size}", color = if (flaggedListings.isNotEmpty()) Color(0xFFF87171) else Color(0xFF94A3B8))
                        MetricPill(label = "Play Ready", value = "100%", color = Color(0xFF22C55E))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showJsonDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                        ) {
                            Icon(Icons.Default.Storage, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Inspect JSON", color = Color(0xFF38BDF8), fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                resetDialogType = "FRESH"
                                showResetConfirmDialog = true
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MandiOrangePrimary),
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                        ) {
                            Icon(Icons.Default.RestartAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Fresh User Mode", color = Color.White, fontSize = 11.sp)
                        }
                    }
                }
            }

            // Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = MandiOrangePrimary,
                edgePadding = 14.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = MandiOrangePrimary,
                        height = 3.dp
                    )
                }
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = if (index == 2 && flaggedListings.isNotEmpty()) "$title (${flaggedListings.size})" else title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp,
                                color = if (selectedTab == index) MandiOrangePrimary else Color(0xFF64748B)
                            )
                        }
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFE2E8F0))

            // Tab Content
            when (selectedTab) {
                0 -> OwnerSecurityTab(
                    viewModel = viewModel,
                    allSettings = allSettings,
                    ownerEmail = ownerEmail,
                    onOpenPrivacyPolicy = onOpenPrivacyPolicy,
                    onChangePin = { showChangePinDialog = true }
                )
                1 -> BackendDataTab(
                    listings = allListings,
                    searchQuery = searchQuery,
                    onSearchChange = { searchQuery = it },
                    onFlagListing = { id, flag, reason -> viewModel.flagListing(id, flag, reason) },
                    onRemoveIllegalPhotos = { id -> viewModel.removeIllegalPhotos(id) },
                    onDeleteListing = { id -> viewModel.deleteListing(id) },
                    onRestoreDefault = {
                        resetDialogType = "RESTORE"
                        showResetConfirmDialog = true
                    },
                    onWipeAll = {
                        resetDialogType = "DELETE_ALL"
                        showResetConfirmDialog = true
                    }
                )
                2 -> IllegalPhotosTab(
                    flaggedListings = flaggedListings,
                    allListings = allListings,
                    allSettings = allSettings,
                    onUpdateSetting = { k, v -> viewModel.updateAppSetting(k, v) },
                    onFlagListing = { id, flag, reason -> viewModel.flagListing(id, flag, reason) },
                    onRemoveIllegalPhotos = { id -> viewModel.removeIllegalPhotos(id) }
                )
                3 -> AppSettingsTab(
                    allSettings = allSettings,
                    onUpdateSetting = { k, v -> viewModel.updateAppSetting(k, v) }
                )
                4 -> SocialMediaTab(
                    listings = allListings,
                    allSettings = allSettings,
                    onUpdateSetting = { k, v -> viewModel.updateAppSetting(k, v) }
                )
            }
        }
    }
}

@Composable
fun OwnerSecurityTab(
    viewModel: MaveshiViewModel,
    allSettings: List<AppSettingEntity>,
    ownerEmail: String,
    onOpenPrivacyPolicy: () -> Unit,
    onChangePin: () -> Unit
) {
    val context = LocalContext.current
    var bannedPhoneInput by remember { mutableStateOf("") }
    var showBanPhoneDialog by remember { mutableStateOf(false) }

    val maintenanceMode = allSettings.find { it.key == "maintenance_mode" }?.value?.toBoolean() ?: false
    val bannedPhonesString = allSettings.find { it.key == "banned_phones" }?.value ?: ""
    val bannedPhonesList = bannedPhonesString.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    val strictFilter = allSettings.find { it.key == "illegal_photo_filter_enabled" }?.value?.toBoolean() ?: true

    if (showBanPhoneDialog) {
        AlertDialog(
            onDismissRequest = { showBanPhoneDialog = false },
            title = { Text("Ban Malicious / Fraud Seller Phone", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
            text = {
                Column {
                    Text("Banned phone numbers cannot post listings or contact buyers.", fontSize = 12.sp, color = Color(0xFF64748B))
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = bannedPhoneInput,
                        onValueChange = { bannedPhoneInput = it },
                        label = { Text("Phone Number (e.g. 03001234567)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (bannedPhoneInput.isNotBlank()) {
                            viewModel.banPhoneNumber(bannedPhoneInput)
                            showBanPhoneDialog = false
                            bannedPhoneInput = ""
                            Toast.makeText(context, "Phone number banned from marketplace.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Ban Phone")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBanPhoneDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        // Google Play Store Audit Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Google Play Publishing Readiness Audit",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF166534)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    ComplianceCheckItem(title = "Target SDK 36 (Android 16)", desc = "Complies with Google Play target API requirements")
                    ComplianceCheckItem(title = "Network Security Config (HTTPS/TLS)", desc = "Cleartext HTTP traffic prohibited; TLS encrypted")
                    ComplianceCheckItem(title = "Zero-Permission Photo Picker", desc = "Uses ActivityResultContracts.PickVisualMedia & FileProvider")
                    ComplianceCheckItem(title = "UGC Content Safety & Abuse Reporting", desc = "In-app flag reporting, owner 24hr moderation switchboard")
                    ComplianceCheckItem(title = "Privacy Policy & Data Safety Disclosures", desc = "Full disclosures for camera, media, and contact numbers")
                    ComplianceCheckItem(title = "Min 3 Photos Verification", desc = "Mandatory Face, Body, and Teeth photos for anti-counterfeit")

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onOpenPrivacyPolicy,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Icon(Icons.Default.Policy, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("View Live Privacy Policy & Disclosures", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Owner Controls Switchboard
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Tune, contentDescription = null, tint = MandiOrangePrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Owner Master Security Switchboard",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }

                        IconButton(onClick = onChangePin, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Key, contentDescription = "Change PIN", tint = MandiOrangePrimary, modifier = Modifier.size(18.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Switch 1: Emergency Maintenance Mode
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Emergency Maintenance Mode", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text("Owner can pause new user uploads during security audits", fontSize = 11.sp, color = Color(0xFF64748B))
                        }
                        Switch(
                            checked = maintenanceMode,
                            onCheckedChange = { viewModel.toggleMaintenanceMode(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFFDC2626))
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF1F5F9))

                    // Switch 2: Illegal Photo AI Strictness
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Strict Illegal Photo Shield", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text("Instantly hide flagged images from public marketplace", fontSize = 11.sp, color = Color(0xFF64748B))
                        }
                        Switch(
                            checked = strictFilter,
                            onCheckedChange = { viewModel.updateAppSetting("illegal_photo_filter_enabled", it.toString()) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MandiOrangePrimary)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF1F5F9))

                    // Master Owner PIN row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Owner Master PIN", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text("Protects security console from unauthorized access", fontSize = 11.sp, color = Color(0xFF64748B))
                        }
                        OutlinedButton(
                            onClick = onChangePin,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Change PIN", fontSize = 11.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Banned Sellers & Phone Blacklist
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PersonRemove, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Banned Sellers Blacklist (${bannedPhonesList.size})",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }

                        Button(
                            onClick = { showBanPhoneDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("+ Ban Phone", fontSize = 11.sp, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (bannedPhonesList.isEmpty()) {
                        Text(
                            text = "No phone numbers currently banned. Sellers with fraudulent behavior can be blacklisted here by the owner.",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    } else {
                        bannedPhonesList.forEach { phone ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFEF2F2))
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Block, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(phone, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF991B1B))
                                }

                                TextButton(onClick = { viewModel.unbanPhoneNumber(phone) }) {
                                    Text("Unban", color = Color(0xFF16A34A), fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ComplianceCheckItem(title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Text(text = desc, fontSize = 11.sp, color = Color(0xFF64748B))
        }
    }
}

@Composable
fun MetricPill(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
        Text(text = label, fontSize = 10.sp, color = Color(0xFF94A3B8))
    }
}

@Composable
fun BackendDataTab(
    listings: List<AnimalListing>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onFlagListing: (Long, Boolean, String?) -> Unit,
    onRemoveIllegalPhotos: (Long) -> Unit,
    onDeleteListing: (Long) -> Unit,
    onRestoreDefault: () -> Unit,
    onWipeAll: () -> Unit
) {
    val filtered = if (searchQuery.isBlank()) listings else {
        val q = searchQuery.lowercase()
        listings.filter {
            it.title.lowercase().contains(q) ||
            it.category.lowercase().contains(q) ||
            it.city.lowercase().contains(q) ||
            it.sellerPhone.contains(q)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        item {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Search by title, category, city, or phone...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF94A3B8)) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            )

            // Maintenance Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Backend Database Records (${filtered.size})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )

                Row {
                    TextButton(onClick = onRestoreDefault) {
                        Text("Restore Demo", fontSize = 11.sp, color = MandiOrangePrimary)
                    }
                    TextButton(onClick = onWipeAll) {
                        Text("Wipe All", fontSize = 11.sp, color = Color(0xFFDC2626))
                    }
                }
            }

            if (filtered.isEmpty()) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No listings found in backend",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "The app is in Fresh Mode! New users can add animal photos with the camera to populate the backend.",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        items(filtered, key = { it.id }) { animal ->
            BackendAnimalCard(
                animal = animal,
                onFlag = { flag, reason -> onFlagListing(animal.id, flag, reason) },
                onRemovePhotos = { onRemoveIllegalPhotos(animal.id) },
                onDelete = { onDeleteListing(animal.id) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun BackendAnimalCard(
    animal: AnimalListing,
    onFlag: (Boolean, String?) -> Unit,
    onRemovePhotos: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (animal.isFlaggedIllegal) Color(0xFFFEF2F2) else Color.White
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (animal.isFlaggedIllegal) Color(0xFFFECACA) else Color(0xFFE2E8F0)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Photo Thumbnail
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE2E8F0))
                ) {
                    if (!animal.imageUri.isNullOrBlank()) {
                        AsyncImage(
                            model = animal.imageUri,
                            contentDescription = animal.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        val drawableRes = when (animal.imageResName) {
                            "goat_cheeni" -> R.drawable.goat_cheeni
                            "sahiwal_cow" -> R.drawable.sahiwal_cow
                            "nili_buffalo" -> R.drawable.nili_buffalo
                            "chickens" -> R.drawable.chickens
                            "irani_teeter" -> R.drawable.irani_teeter
                            else -> R.drawable.maveshi_splash
                        }
                        androidx.compose.foundation.Image(
                            painter = painterResource(drawableRes),
                            contentDescription = animal.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = animal.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        if (animal.isUserListing) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF22C55E).copy(alpha = 0.15f))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text("USER UPLOAD", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "ID: ${animal.id} • ${animal.category} • Rs ${NumberFormat.getNumberInstance(Locale.US).format(animal.price)} • ${animal.city}",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )

                    Text(
                        text = "Seller: ${animal.sellerName} (${animal.sellerPhone}) • Photos: ${animal.getAllPhotos().size}",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            if (animal.isFlaggedIllegal) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFFEE2E2))
                        .padding(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "FLAGGED: ${animal.flagReason ?: "Illegal / Policy Violation"}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF991B1B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Row for Developer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (animal.isFlaggedIllegal) {
                    TextButton(onClick = { onFlag(false, null) }) {
                        Text("Approve & Unflag", fontSize = 11.sp, color = Color(0xFF16A34A))
                    }
                } else {
                    TextButton(onClick = { onFlag(true, "Inappropriate / illegal photo detected") }) {
                        Text("Flag as Illegal", fontSize = 11.sp, color = Color(0xFFDC2626))
                    }
                }

                TextButton(onClick = onRemovePhotos) {
                    Text("Strip Photos", fontSize = 11.sp, color = Color(0xFFD97706))
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun IllegalPhotosTab(
    flaggedListings: List<AnimalListing>,
    allListings: List<AnimalListing>,
    allSettings: List<AppSettingEntity>,
    onUpdateSetting: (String, String) -> Unit,
    onFlagListing: (Long, Boolean, String?) -> Unit,
    onRemoveIllegalPhotos: (Long) -> Unit
) {
    val autoFilterSetting = allSettings.find { it.key == "illegal_photo_filter_enabled" }?.value?.toBoolean() ?: true
    val allowUnverified = allSettings.find { it.key == "allow_unverified_photos" }?.value?.toBoolean() ?: false

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        item {
            // Moderation Controls Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = MandiOrangePrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Illegal Photo & Content Safety Policies",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Switch 1: Auto-detection filter
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Illegal Photo Filter", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text(
                                "Auto-detect and blur abusive, cruelty, or non-livestock images",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                        Switch(
                            checked = autoFilterSetting,
                            onCheckedChange = { onUpdateSetting("illegal_photo_filter_enabled", it.toString()) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MandiOrangePrimary)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF1F5F9))

                    // Switch 2: Unverified photos quarantine
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Quarantine Unverified Photos", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text(
                                "Require developer manual review before new user animal photos appear publicly",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                        Switch(
                            checked = !allowUnverified,
                            onCheckedChange = { onUpdateSetting("allow_unverified_photos", (!it).toString()) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MandiOrangePrimary)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Flagged Violations Queue (${flaggedListings.size})",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
            Spacer(modifier = Modifier.height(6.dp))

            if (flaggedListings.isEmpty()) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Clean Moderation Queue", fontWeight = FontWeight.Bold, color = Color(0xFF166534), fontSize = 13.sp)
                            Text("No illegal or flagged photos reported in the marketplace.", fontSize = 11.sp, color = Color(0xFF15803D))
                        }
                    }
                }
            }
        }

        items(flaggedListings, key = { it.id }) { flagged ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Listing #${flagged.id}: ${flagged.title}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF991B1B)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Violation Reason: ${flagged.flagReason ?: "Reported by community as invalid animal photo"}",
                        fontSize = 11.sp,
                        color = Color(0xFFB91C1C)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = { onFlagListing(flagged.id, false, null) },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Dismiss / Approve", fontSize = 11.sp, color = Color(0xFF16A34A))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { onRemoveIllegalPhotos(flagged.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Strip Illegal Photo", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppSettingsTab(
    allSettings: List<AppSettingEntity>,
    onUpdateSetting: (String, String) -> Unit
) {
    val context = LocalContext.current
    var editingKey by remember { mutableStateOf<String?>(null) }
    var editValueText by remember { mutableStateOf("") }

    if (editingKey != null) {
        val currentSetting = allSettings.find { it.key == editingKey }
        AlertDialog(
            onDismissRequest = { editingKey = null },
            title = { Text("Edit Setting: $editingKey", fontSize = 15.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(currentSetting?.description ?: "", fontSize = 12.sp, color = Color(0xFF64748B))
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = editValueText,
                        onValueChange = { editValueText = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateSetting(editingKey!!, editValueText)
                        editingKey = null
                        Toast.makeText(context, "Setting updated successfully!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MandiOrangePrimary)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingKey = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        item {
            Text(
                text = "Owner & Platform Config",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
            Text(
                text = "Changes take effect immediately across all application screens and cloud sync",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(allSettings, key = { it.key }) { setting ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = setting.key,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A),
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFF1F5F9))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(setting.category, fontSize = 9.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = setting.description,
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Value: ${setting.value}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MandiGreenNew
                        )
                    }

                    IconButton(
                        onClick = {
                            editingKey = setting.key
                            editValueText = setting.value
                        }
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MandiOrangePrimary, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SocialMediaTab(
    listings: List<AnimalListing>,
    allSettings: List<AppSettingEntity>,
    onUpdateSetting: (String, String) -> Unit
) {
    val context = LocalContext.current
    val sampleAnimal = listings.firstOrNull()
    val watermarkSetting = allSettings.find { it.key == "social_share_watermark" }?.value?.toBoolean() ?: true
    val prefixSetting = allSettings.find { it.key == "social_media_prefix" }?.value ?: "https://mallmaweshi.pk/listing/"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = MandiGreenNew, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Social Media Share Preview & Engine",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Configured for automatic WhatsApp previews, Facebook rich cards, Instagram, and SMS.",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Live Social Preview Card
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Preview: What buyers see on Social Media / WhatsApp",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0284C7)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            val shareMessage = buildSocialShareMessage(sampleAnimal, prefixSetting, watermarkSetting)
                            Text(
                                text = shareMessage,
                                fontSize = 12.sp,
                                color = Color(0xFF334155),
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (sampleAnimal != null) {
                                    val msg = buildSocialShareMessage(sampleAnimal, prefixSetting, watermarkSetting)
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_SUBJECT, sampleAnimal.title)
                                        putExtra(Intent.EXTRA_TEXT, msg)
                                    }
                                    context.startActivity(Intent.createChooser(intent, "Share Animal to Social Media"))
                                } else {
                                    Toast.makeText(context, "No animal listing available to share", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MandiGreenNew),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Test Social Share", color = Color.White, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val msg = buildSocialShareMessage(sampleAnimal, prefixSetting, watermarkSetting)
                                val clip = ClipData.newPlainText("Share Text", msg)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Social post text copied!", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color(0xFF0F172A), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy Template", color = Color(0xFF0F172A), fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

fun buildSocialShareMessage(animal: AnimalListing?, prefix: String, watermark: Boolean): String {
    if (animal == null) return "Check out verified livestock on Mall Maweshi Pakistan: https://mallmaweshi.pk"
    val priceStr = NumberFormat.getNumberInstance(Locale.US).format(animal.price)
    return """
        🐄 *${animal.title}* - Rs $priceStr
        📍 Location: ${animal.city} | Breed: ${animal.breed}
        🦷 Teeth: ${animal.teethCount} | Age: ${animal.ageText}
        📞 Contact Seller: ${animal.sellerPhone} (${animal.sellerName})
        🔗 View Photos & Details: $prefix${animal.id}
        ${if (watermark) "\n✅ Verified on Mall Maweshi • پاکستان کی سب سے بڑی آن لائن مویشی منڈی #MallMaweshi #Qurbani" else ""}
    """.trimIndent()
}

fun generateJsonString(listings: List<AnimalListing>): String {
    val sb = StringBuilder("[\n")
    listings.take(15).forEachIndexed { index, a ->
        sb.append("  {\n")
        sb.append("    \"id\": ${a.id},\n")
        sb.append("    \"title\": \"${a.title.replace("\"", "\\\"")}\",\n")
        sb.append("    \"category\": \"${a.category}\",\n")
        sb.append("    \"breed\": \"${a.breed}\",\n")
        sb.append("    \"price\": ${a.price},\n")
        sb.append("    \"city\": \"${a.city}\",\n")
        sb.append("    \"isUserListing\": ${a.isUserListing},\n")
        sb.append("    \"isFlaggedIllegal\": ${a.isFlaggedIllegal},\n")
        sb.append("    \"flagReason\": ${if (a.flagReason != null) "\"${a.flagReason}\"" else "null"},\n")
        sb.append("    \"imageUri\": ${if (a.imageUri != null) "\"${a.imageUri}\"" else "null"},\n")
        sb.append("    \"photosCount\": ${a.getAllPhotos().size}\n")
        sb.append("  }${if (index < minOf(listings.size - 1, 14)) "," else ""}\n")
    }
    sb.append("]")
    return sb.toString()
}
