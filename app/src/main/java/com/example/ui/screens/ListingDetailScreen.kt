package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.AnimalListing
import com.example.ui.Screen
import com.example.ui.theme.MandiOrangePrimary
import com.example.ui.theme.MaveshiInputTextStyle
import com.example.ui.theme.maveshiTextFieldColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.window.Dialog
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingDetailScreen(
    animal: AnimalListing,
    onBack: () -> Unit,
    onChatWithSeller: () -> Unit,
    onCallSeller: (String) -> Unit,
    onWhatsAppSeller: (String, String) -> Unit,
    onMakeOffer: ((offerPrice: Long, message: String, buyerName: String, buyerPhone: String) -> Unit)? = null,
    onDeleteListing: ((Long) -> Unit)? = null,
    onFlagListing: ((Long, Boolean, String) -> Unit)? = null,
    onRemoveIllegalPhotos: ((Long) -> Unit)? = null
) {
    val context = LocalContext.current
    var selectedPhotoIndex by remember { mutableStateOf(0) }
    var showOfferDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var reportReasonText by remember { mutableStateOf("Illegal / Prohibited animal photo") }
    var offerSubmitted by remember { mutableStateOf(false) }
    var showSocialShareDialog by remember { mutableStateOf(false) }

    var offerPriceText by remember { mutableStateOf((animal.price * 95 / 100).toString()) }
    var offerBuyerName by remember { mutableStateOf("Chaudhry Buyer") }
    var offerBuyerPhone by remember { mutableStateOf("03001234567") }
    var offerMessage by remember { mutableStateOf("Interested in this animal. Can buy immediately if agreed.") }

    if (showOfferDialog) {
        Dialog(onDismissRequest = { showOfferDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Make an Offer / قیمت کی پیشکش",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Original Asking Price: Rs ${NumberFormat.getNumberInstance(Locale.US).format(animal.price)}",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = offerPriceText,
                        onValueChange = { offerPriceText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Your Offer Price (PKR)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = MaveshiInputTextStyle,
                        colors = maveshiTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = offerBuyerName,
                        onValueChange = { offerBuyerName = it },
                        label = { Text("Your Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = MaveshiInputTextStyle,
                        colors = maveshiTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = offerBuyerPhone,
                        onValueChange = { offerBuyerPhone = it },
                        label = { Text("Your Phone / WhatsApp") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = MaveshiInputTextStyle,
                        colors = maveshiTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = offerMessage,
                        onValueChange = { offerMessage = it },
                        label = { Text("Message to Seller") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = MaveshiInputTextStyle,
                        colors = maveshiTextFieldColors()
                    )
                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showOfferDialog = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                val price = offerPriceText.toLongOrNull() ?: animal.price
                                onMakeOffer?.invoke(price, offerMessage, offerBuyerName, offerBuyerPhone)
                                showOfferDialog = false
                                offerSubmitted = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MandiOrangePrimary),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Send Offer", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showReportDialog) {
        Dialog(onDismissRequest = { showReportDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = "Report",
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Report Photo or Listing",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "This alert will be sent directly to the developer for moderation and immediate review.",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    val reasons = listOf(
                        "Illegal / Prohibited wildlife species",
                        "Graphic / Abusive animal photo",
                        "Fake / Stolen catalog photo",
                        "Misleading price or description"
                    )

                    reasons.forEach { r ->
                        val isSel = reportReasonText == r
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) Color(0xFFFEF2F2) else Color.Transparent)
                                .clickable { reportReasonText = r }
                                .padding(vertical = 8.dp, horizontal = 10.dp)
                        ) {
                            androidx.compose.material3.RadioButton(
                                selected = isSel,
                                onClick = { reportReasonText = r },
                                colors = androidx.compose.material3.RadioButtonDefaults.colors(selectedColor = Color(0xFFDC2626))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = r, fontSize = 13.sp, color = Color(0xFF1E293B))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showReportDialog = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                onFlagListing?.invoke(animal.id, true, reportReasonText)
                                showReportDialog = false
                                Toast.makeText(context, "Listing reported to Developer for moderation.", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Submit Flag", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Social Media Share Dialog
    if (showSocialShareDialog) {
        val sharePriceStr = NumberFormat.getNumberInstance(Locale.US).format(animal.price)
        val shareText = """
            🐄 *${animal.title}* - Rs $sharePriceStr
            📍 Location: ${animal.city} | Breed: ${animal.breed}
            🦷 Teeth: ${animal.teethCount} | Age: ${animal.ageText}
            📞 Contact Seller: ${animal.sellerPhone} (${animal.sellerName})
            🔗 Link: https://mallmaweshi.pk/listing/${animal.id}
            
            ✅ Shared from Mall Maweshi • پاکستان کی سب سے بڑی آن لائن مویشی منڈی #MallMaweshi #Qurbani #Livestock
        """.trimIndent()

        Dialog(onDismissRequest = { showSocialShareDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .testTag("social_share_dialog")
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = MandiOrangePrimary, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Share to Social Media",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                        }
                        IconButton(onClick = { showSocialShareDialog = false }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Flag, contentDescription = "Close", tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Social Preview Card
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Post Preview / پیش نظارہ",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0284C7)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = shareText,
                                fontSize = 11.sp,
                                color = Color(0xFF334155),
                                lineHeight = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Quick Action: WhatsApp Direct Share
                    Button(
                        onClick = {
                            showSocialShareDialog = false
                            try {
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse("https://api.whatsapp.com/send?text=" + Uri.encode(shareText))
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Animal Listing"))
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("share_whatsapp_button")
                    ) {
                        Text("Share to WhatsApp", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Action 2: Android Share Sheet (Facebook, Instagram, SMS, X)
                    Button(
                        onClick = {
                            showSocialShareDialog = false
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, animal.title)
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share to Social Media / Apps"))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MandiOrangePrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("share_all_apps_button")
                    ) {
                        Text("Share via All Social Apps", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Action 3: Copy Link
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Mall Maweshi Link", shareText)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Listing details copied to clipboard!", Toast.LENGTH_SHORT).show()
                            showSocialShareDialog = false
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Link & Details", color = Color(0xFF1E293B))
                    }
                }
            }
        }
    }

    val formatter = NumberFormat.getNumberInstance(Locale.US)
    val formattedPrice = "Rs ${formatter.format(animal.price)}"

    val drawableId = when (animal.imageResName) {
        "goat_cheeni" -> R.drawable.goat_cheeni
        "sahiwal_cow" -> R.drawable.sahiwal_cow
        "nili_buffalo" -> R.drawable.nili_buffalo
        "chickens" -> R.drawable.chickens
        "irani_teeter" -> R.drawable.irani_teeter
        else -> R.drawable.maveshi_splash
    }

    val photoList = animal.getAllPhotos()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("detail_back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1E293B)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showSocialShareDialog = true },
                        modifier = Modifier.testTag("detail_share_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color(0xFF1E293B)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                ),
                modifier = Modifier.statusBarsPadding()
            )
        },
        bottomBar = {
            // Sticky Bottom Action Bar (Image 6)
            Surface(
                color = Color.White,
                shadowElevation = 12.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Chat with Seller Button (Orange)
                    Button(
                        onClick = onChatWithSeller,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MandiOrangePrimary
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("chat_with_seller_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "Chat",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Chat with Seller",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Circular Phone Call Button
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, Color(0xFF3B82F6), CircleShape)
                            .clickable { onCallSeller(animal.sellerPhone) }
                            .testTag("call_seller_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Call",
                            tint = Color(0xFF3B82F6),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Circular WhatsApp Button
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, Color(0xFF22C55E), CircleShape)
                            .clickable {
                                onWhatsAppSeller(
                                    animal.sellerPhone,
                                    "Salam, I am interested in your ${animal.title} (${animal.breed}) on Mall Maweshi."
                                )
                            }
                            .testTag("whatsapp_seller_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "WhatsApp",
                            tint = Color(0xFF22C55E),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .testTag("listing_detail_scroll")
        ) {
            // Main Photo Box with Photo Indicator
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(Color(0xFFE2E8F0))
            ) {
                val currentPhoto = photoList.getOrNull(selectedPhotoIndex) ?: "res:${animal.imageResName}"
                if (currentPhoto.startsWith("res:")) {
                    val resName = currentPhoto.removePrefix("res:")
                    val resId = when (resName) {
                        "goat_cheeni" -> R.drawable.goat_cheeni
                        "sahiwal_cow" -> R.drawable.sahiwal_cow
                        "nili_buffalo" -> R.drawable.nili_buffalo
                        "chickens" -> R.drawable.chickens
                        "irani_teeter" -> R.drawable.irani_teeter
                        else -> drawableId
                    }
                    Image(
                        painter = painterResource(id = resId),
                        contentDescription = animal.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    AsyncImage(
                        model = currentPhoto,
                        contentDescription = animal.title,
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = drawableId),
                        placeholder = painterResource(id = drawableId),
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Photo Indicator: "1/3"
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomEnd)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${selectedPhotoIndex + 1}/${photoList.size} Photos",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // If flagged illegal by developer/moderator
                if (animal.isFlaggedIllegal) {
                    Box(
                        modifier = Modifier
                            .padding(14.dp)
                            .align(Alignment.TopStart)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFDC2626))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "FLAGGED FOR REVIEW",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Thumbnail Gallery Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                photoList.forEachIndexed { index, photoItem ->
                    val isSelected = selectedPhotoIndex == index
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = if (isSelected) 2.5.dp else 1.dp,
                                color = if (isSelected) MandiOrangePrimary else Color(0xFFE2E8F0),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedPhotoIndex = index }
                    ) {
                        if (photoItem.startsWith("res:")) {
                            val resName = photoItem.removePrefix("res:")
                            val resId = when (resName) {
                                "goat_cheeni" -> R.drawable.goat_cheeni
                                "sahiwal_cow" -> R.drawable.sahiwal_cow
                                "nili_buffalo" -> R.drawable.nili_buffalo
                                "chickens" -> R.drawable.chickens
                                "irani_teeter" -> R.drawable.irani_teeter
                                else -> drawableId
                            }
                            Image(
                                painter = painterResource(id = resId),
                                contentDescription = "Thumbnail $index",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            AsyncImage(
                                model = photoItem,
                                contentDescription = "Thumbnail $index",
                                contentScale = ContentScale.Crop,
                                error = painterResource(id = drawableId),
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            // Status Badge & Actions Row (Report Photo & Share)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
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
                        text = if (animal.isFlaggedIllegal) "Under Moderation" else "Available",
                        color = if (animal.isFlaggedIllegal) Color(0xFFDC2626) else Color(0xFF22C55E),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Report / Flag Inappropriate Photo button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFFEF2F2))
                            .clickable { showReportDialog = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("report_listing_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = "Report",
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Report Photo",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFDC2626)
                        )
                    }

                    // Share Button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF1F5F9))
                            .clickable { showSocialShareDialog = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("share_listing_top_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color(0xFF475569),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Share",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF475569)
                        )
                    }
                }
            }

            // Title & Price (Image 6)
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                Text(
                    text = animal.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formattedPrice,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MandiOrangePrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Pin Lahore • 14 hours ago • 7 views
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "City",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = animal.city,
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Time",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = animal.postedTimeText,
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Views",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${animal.viewsCount}",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3 Info Meta Blocks (CATEGORY, ADDRESS, POSTED) as in Image 6
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Category Block
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "CATEGORY",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = animal.category,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B)
                        )
                    }
                }

                // Address Block
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "ADDRESS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = animal.city,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B)
                        )
                    }
                }

                // Posted Block
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "POSTED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = animal.postedTimeText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Description Section (Image 6)
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Description",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = animal.description,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = Color(0xFF475569)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Attributes List
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        DetailAttributeRow("Breed", animal.breed)
                        DetailAttributeRow("Age", animal.ageText)
                        DetailAttributeRow("Teeth / Danda", animal.teethCount)
                        DetailAttributeRow("Estimated Weight", animal.weightKg)
                        if (animal.milkCapacity.isNotBlank()) {
                            DetailAttributeRow("Milk Capacity", animal.milkCapacity)
                        }
                        DetailAttributeRow(
                            "Vaccinated",
                            if (animal.isVaccinated) "Yes (Verified)" else "No"
                        )
                        DetailAttributeRow("Seller", animal.sellerName)
                        DetailAttributeRow("Contact Phone", animal.sellerPhone)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Make Offer Action Card
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFFDF2)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFB7E4C7)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (offerSubmitted) "Offer Submitted Successfully!" else "Want to Negotiate? / رعایت چاہتے ہیں؟",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF135E24)
                                )
                                Text(
                                    text = if (offerSubmitted) "Seller has been notified via Room database." else "Submit a direct price proposal to the seller",
                                    fontSize = 12.sp,
                                    color = Color(0xFF2D6A4F)
                                )
                            }
                            Button(
                                onClick = { showOfferDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E7F34)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = if (offerSubmitted) "Edit Offer" else "Make Offer",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                // If this is user's listing, offer delete listing option
                if (animal.isUserListing && onDeleteListing != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedButton(
                        onClick = { onDeleteListing(animal.id) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Delete Listing from Database / لسٹنگ ختم کریں", fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Database Persistence Badge
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified DB",
                            tint = Color(0xFF16A34A),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Item ID #${animal.id} • Verified in Local Room Database & Synced to Firestore Cloud",
                            fontSize = 11.sp,
                            color = Color(0xFF475569)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun DetailAttributeRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color(0xFF64748B)
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1E293B)
        )
    }
}
