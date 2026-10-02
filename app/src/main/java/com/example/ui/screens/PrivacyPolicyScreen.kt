package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MandiGreenNew
import com.example.ui.theme.MandiOrangePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val ownerEmail = "bilalkhichi.156@gmail.com"
    val policyUrl = "https://mallmaweshi.pk/privacy-policy"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Privacy Policy & Safety",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "Google Play Compliant • Data Safety Disclosures",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("privacy_back_button")) {
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
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Privacy Policy", policyUrl)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Policy URL copied!", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Policy Link",
                            tint = MandiOrangePrimary
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .navigationBarsPadding()
                .testTag("privacy_policy_screen")
        ) {
            // Verified Owner Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF22C55E).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = Color(0xFF4ADE80),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Mall Maweshi Security & Ownership",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Text(
                                text = "App Owner: Bilal Khichi ($ownerEmail)",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "This application strictly adheres to the Google Play Developer Program Policies, User-Generated Content (UGC) Guidelines, and international Data Safety standards. The app owner maintains active moderation, illegal photo filtering, and anti-fraud systems.",
                        fontSize = 12.sp,
                        color = Color(0xFFCBD5E1),
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 1: Information Collection & Usage
            PolicySectionCard(
                icon = Icons.Default.Security,
                title = "1. Information Collection & Usage",
                content = """
                    • Animal Listings: When sellers post an animal for sale, we collect details including category (Cow, Goat, Buffalo, Lamb, etc.), breed, teeth count (age verification), price, description, and market location.
                    • Seller Contact Information: The seller's contact phone number is collected solely to allow verified prospective buyers to contact the seller via direct phone call or WhatsApp. Phone numbers are never sold or shared with third-party advertisers.
                    • Zero Location Tracking: The app does not track background GPS. Only user-selected city or market region is stored.
                """.trimIndent()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Section 2: Camera & Photo Picker Disclosures
            PolicySectionCard(
                icon = Icons.Default.CameraAlt,
                title = "2. Device Camera & Media Permission Disclosures",
                content = """
                    • Camera Permission (android.permission.CAMERA): The app requests camera access only when the user voluntarily taps to take real animal photos. The camera is used to capture front/face, body profile, and teeth/danda photos for age and health verification.
                    • Zero-Permission Android Photo Picker: For existing gallery photos, the app utilizes the Android Photo Picker (ActivityResultContracts.PickVisualMedia), which accesses only the user-selected images without requiring broad storage permissions (READ_EXTERNAL_STORAGE).
                    • Storage: Captured photos are stored in secure app-private storage via Android FileProvider.
                """.trimIndent()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Section 3: Google Play UGC & Content Moderation Policy
            PolicySectionCard(
                icon = Icons.Default.Shield,
                title = "3. User-Generated Content (UGC) & Safety Policy",
                content = """
                    • Zero Tolerance Policy: Mall Maweshi enforces a strict zero-tolerance policy against objectionable content, animal cruelty, illegal trade of endangered or protected species, stolen cattle, counterfeit offers, harassment, and inappropriate imagery.
                    • In-App Reporting: Every listing includes a prominent 'Report Photo' flag button. Reports are transmitted instantly to the Owner Moderation Console.
                    • Owner Moderation & Banning: The app owner actively monitors listings and takes action within 24 hours. The owner holds full authority to strip illegal photos, permanently delete violating listings, and blacklist fraudulent phone numbers.
                """.trimIndent()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Section 4: Data Security & Encryption
            PolicySectionCard(
                icon = Icons.Default.Lock,
                title = "4. Data Security & Encryption in Transit",
                content = """
                    • All network communication between the app and cloud backend is encrypted using Transport Layer Security (TLS/HTTPS).
                    • Cleartext HTTP traffic is strictly prohibited via Android Network Security Configuration.
                    • Local marketplace data is persisted in encrypted SQLite Room database within the sandboxed application environment.
                """.trimIndent()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Section 5: Data Deletion & User Rights
            PolicySectionCard(
                icon = Icons.Default.CheckCircle,
                title = "5. User Rights & Data Deletion Request",
                content = """
                    • Listing Deletion: Sellers can delete any of their listings directly from the listing details screen at any time.
                    • Account & Data Wipe Request: Users can request complete removal of their profile and uploaded content by emailing the owner at $ownerEmail with the subject 'Data Deletion Request'. All associated records are permanently purged within 48 hours.
                """.trimIndent()
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:$ownerEmail?subject=Mall%20Maweshi%20Inquiry%20or%20Safety%20Report")
                            }
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            Toast.makeText(context, "Owner Email: $ownerEmail", Toast.LENGTH_LONG).show()
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Email, contentDescription = null, tint = MandiOrangePrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Contact Owner", color = MandiOrangePrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = MandiGreenNew),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("I Understand & Agree", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PolicySectionCard(
    icon: ImageVector,
    title: String,
    content: String
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = MandiOrangePrimary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content,
                fontSize = 12.sp,
                color = Color(0xFF475569),
                lineHeight = 17.sp
            )
        }
    }
}
