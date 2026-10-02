package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.AnimalPhotoPickerSection
import com.example.ui.components.MultiAnimalPhotoPickerSection
import com.example.ui.theme.MandiOrangePrimary
import com.example.ui.theme.MaveshiInputTextStyle
import com.example.ui.theme.maveshiTextFieldColors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddListingScreen(
    initialImageUri: String? = null,
    initialCategory: String? = null,
    isMaintenanceMode: Boolean = false,
    isPhoneBanned: (String) -> Boolean = { false },
    onBack: () -> Unit,
    onSubmitListing: (
        title: String,
        category: String,
        breed: String,
        price: Long,
        city: String,
        description: String,
        imageResName: String,
        teeth: String,
        age: String,
        weight: String,
        milk: String,
        vaccinated: Boolean,
        phone: String,
        imageUri: String?,
        additionalPhotos: String
    ) -> Unit
) {
    val categories = listOf("Cow", "Goat", "Lamb", "Chicken", "Birds", "Camel", "Buffalo")
    val cities = listOf("Lahore", "Karachi", "Islamabad", "Rawalpindi", "Multan", "Faisalabad", "Nankana Sahib", "Gujar Khan")

    var selectedCategory by remember { mutableStateOf(initialCategory ?: "Goat") }
    var title by remember { mutableStateOf(if (initialImageUri != null) "$selectedCategory for Sale" else "") }
    var breed by remember { mutableStateOf("Makhi Cheeni") }
    var priceText by remember { mutableStateOf("") }
    var selectedCity by remember { mutableStateOf("Lahore") }
    var description by remember { mutableStateOf("") }
    var teeth by remember { mutableStateOf("2 Danda") }
    var age by remember { mutableStateOf("1.5 Years") }
    var weight by remember { mutableStateOf("45 kg") }
    var milk by remember { mutableStateOf("2-3 Litres/day") }
    var isVaccinated by remember { mutableStateOf(true) }
    var phone by remember { mutableStateOf("03038692236") }
    var selectedImageRes by remember { mutableStateOf("goat_cheeni") }
    var photosList by remember {
        mutableStateOf(
            if (!initialImageUri.isNullOrBlank()) {
                listOf(initialImageUri)
            } else {
                emptyList<String>()
            }
        )
    }
    var validationError by remember { mutableStateOf<String?>(null) }

    val photoOptions = listOf(
        Pair("goat_cheeni", R.drawable.goat_cheeni),
        Pair("sahiwal_cow", R.drawable.sahiwal_cow),
        Pair("nili_buffalo", R.drawable.nili_buffalo),
        Pair("chickens", R.drawable.chickens),
        Pair("irani_teeter", R.drawable.irani_teeter)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "New Livestock Listing",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1E293B)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                modifier = Modifier.statusBarsPadding()
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .navigationBarsPadding()
                .testTag("add_listing_screen")
        ) {
            if (isMaintenanceMode) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA)),
                    modifier = Modifier.fillMaxWidth().testTag("add_listing_maintenance_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Marketplace Maintenance Mode Active",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF991B1B)
                            )
                            Text(
                                text = "The app owner has temporarily paused new listings for security reviews.",
                                fontSize = 11.sp,
                                color = Color(0xFFB91C1C)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Category Selection
            Text(
                text = "Select Animal Category",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                categories.forEach { cat ->
                    val isSel = selectedCategory == cat
                    FilterChip(
                        selected = isSel,
                        onClick = {
                            selectedCategory = cat
                            selectedImageRes = when (cat) {
                                "Cow" -> "sahiwal_cow"
                                "Goat" -> "goat_cheeni"
                                "Buffalo" -> "nili_buffalo"
                                "Chicken" -> "chickens"
                                "Birds" -> "irani_teeter"
                                else -> "maveshi_splash"
                            }
                        },
                        label = { Text(cat) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MandiOrangePrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Multi-photo selection and camera capture (at least 3 photos required)
            MultiAnimalPhotoPickerSection(
                photos = photosList,
                onPhotosChange = {
                    photosList = it
                    validationError = null
                },
                currentPresetRes = selectedImageRes,
                onPresetChange = { selectedImageRes = it }
            )

            if (validationError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = validationError!!,
                        color = Color(0xFFB91C1C),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title & Breed
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title (e.g. Makhi Cheeni Goat with 2 Kids)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                textStyle = MaveshiInputTextStyle,
                colors = maveshiTextFieldColors()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = breed,
                onValueChange = { breed = it },
                label = { Text("Breed (e.g. Makhi Cheeni / Sahiwal / Beetal)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                textStyle = MaveshiInputTextStyle,
                colors = maveshiTextFieldColors()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Price & City
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Price (Rs PKR)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = MaveshiInputTextStyle,
                    colors = maveshiTextFieldColors()
                )

                OutlinedTextField(
                    value = selectedCity,
                    onValueChange = { selectedCity = it },
                    label = { Text("City (e.g. Lahore)") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = MaveshiInputTextStyle,
                    colors = maveshiTextFieldColors()
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Teeth & Age & Weight
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = teeth,
                    onValueChange = { teeth = it },
                    label = { Text("Teeth (Danda)") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = MaveshiInputTextStyle,
                    colors = maveshiTextFieldColors()
                )

                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Age") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = MaveshiInputTextStyle,
                    colors = maveshiTextFieldColors()
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Contact Phone / WhatsApp") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                textStyle = MaveshiInputTextStyle,
                colors = maveshiTextFieldColors()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Detailed Description") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                textStyle = MaveshiInputTextStyle,
                colors = maveshiTextFieldColors()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Vaccinated checkbox
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = isVaccinated,
                    onCheckedChange = { isVaccinated = it },
                    colors = CheckboxDefaults.colors(checkedColor = MandiOrangePrimary)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Animal is fully vaccinated and certified disease-free",
                    fontSize = 13.sp,
                    color = Color(0xFF334155)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val finalPhotos = if (photosList.isNotEmpty()) {
                        photosList
                    } else {
                        listOf("res:$selectedImageRes", "res:$selectedImageRes", "res:$selectedImageRes")
                    }

                    if (finalPhotos.size < 3) {
                        validationError = "Please add at least 3 photos (Face, Body, and Teeth) to post a listing."
                        return@Button
                    }

                    if (isMaintenanceMode) {
                        validationError = "Marketplace submissions are currently paused by the app owner in Emergency Maintenance Mode."
                        return@Button
                    }

                    val cleanPhone = phone.trim()
                    if (isPhoneBanned(cleanPhone)) {
                        validationError = "This phone number ($cleanPhone) has been blacklisted by the app owner. Contact bilalkhichi.156@gmail.com."
                        return@Button
                    }

                    if (cleanPhone.length < 10) {
                        validationError = "Please enter a valid seller phone number (e.g. 03038692236)."
                        return@Button
                    }

                    val mainPhoto = finalPhotos.firstOrNull { !it.startsWith("res:") } ?: finalPhotos.firstOrNull()
                    val extraPhotos = if (finalPhotos.size > 1) finalPhotos.drop(1).joinToString("||") else ""

                    val finalPrice = priceText.toLongOrNull() ?: 150000L
                    val finalTitle = title.ifBlank { "$selectedCategory ($breed)" }
                    val finalDesc = description.ifBlank { "$finalTitle for sale in $selectedCity. Excellent health condition." }
                    onSubmitListing(
                        finalTitle,
                        selectedCategory,
                        breed,
                        finalPrice,
                        selectedCity,
                        finalDesc,
                        selectedImageRes,
                        teeth,
                        age,
                        weight,
                        milk,
                        isVaccinated,
                        phone,
                        mainPhoto,
                        extraPhotos
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = MandiOrangePrimary),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_new_listing_button")
            ) {
                Text(
                    text = "Post Livestock Listing",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
