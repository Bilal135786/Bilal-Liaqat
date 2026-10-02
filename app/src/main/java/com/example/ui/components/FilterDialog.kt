package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.FilterCriteria
import com.example.ui.SortOption
import com.example.ui.theme.MandiOrangePrimary
import com.example.ui.theme.MaveshiInputTextStyle
import com.example.ui.theme.maveshiTextFieldColors

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterDialog(
    initialCriteria: FilterCriteria,
    onApply: (FilterCriteria) -> Unit,
    onDismiss: () -> Unit
) {
    var minPriceText by remember { mutableStateOf(initialCriteria.minPrice?.toString() ?: "") }
    var maxPriceText by remember { mutableStateOf(initialCriteria.maxPrice?.toString() ?: "") }
    var selectedCity by remember { mutableStateOf(initialCriteria.city ?: "All") }
    var vaccinatedOnly by remember { mutableStateOf(initialCriteria.vaccinatedOnly) }
    var selectedSort by remember { mutableStateOf(initialCriteria.sortOption) }

    val cities = listOf("All", "Lahore", "Karachi", "Islamabad", "Rawalpindi", "Multan", "Faisalabad", "Nankana Sahib", "Gujar Khan")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("filter_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filter Livestock",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Sort By
                Text(
                    text = "Sort By",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF475569)
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SortOption.values().forEach { option ->
                        val label = when (option) {
                            SortOption.NEWEST -> "Newest"
                            SortOption.PRICE_LOW_TO_HIGH -> "Price: Low to High"
                            SortOption.PRICE_HIGH_TO_LOW -> "Price: High to Low"
                            SortOption.MOST_VIEWED -> "Most Viewed"
                        }
                        FilterChip(
                            selected = selectedSort == option,
                            onClick = { selectedSort = option },
                            label = { Text(label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MandiOrangePrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Price Range
                Text(
                    text = "Price Range (PKR)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF475569)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = minPriceText,
                        onValueChange = { minPriceText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Min Rs") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = MaveshiInputTextStyle,
                        colors = maveshiTextFieldColors()
                    )
                    OutlinedTextField(
                        value = maxPriceText,
                        onValueChange = { maxPriceText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Max Rs") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = MaveshiInputTextStyle,
                        colors = maveshiTextFieldColors()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // City
                Text(
                    text = "City / Market Location",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF475569)
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    cities.forEach { city ->
                        FilterChip(
                            selected = selectedCity == city,
                            onClick = { selectedCity = city },
                            label = { Text(city, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MandiOrangePrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Vaccinated Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Vaccinated Animals Only",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF334155)
                    )
                    Switch(
                        checked = vaccinatedOnly,
                        onCheckedChange = { vaccinatedOnly = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = MandiOrangePrimary)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            minPriceText = ""
                            maxPriceText = ""
                            selectedCity = "All"
                            vaccinatedOnly = false
                            selectedSort = SortOption.NEWEST
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reset")
                    }

                    Button(
                        onClick = {
                            val min = minPriceText.toLongOrNull()
                            val max = maxPriceText.toLongOrNull()
                            onApply(
                                FilterCriteria(
                                    minPrice = min,
                                    maxPrice = max,
                                    city = if (selectedCity == "All") null else selectedCity,
                                    vaccinatedOnly = vaccinatedOnly,
                                    sortOption = selectedSort
                                )
                            )
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MandiOrangePrimary),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("apply_filter_button")
                    ) {
                        Text("Apply", color = Color.White)
                    }
                }
            }
        }
    }
}
