package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MandiOrangePrimary
import com.example.ui.theme.MaveshiInputTextStyle
import com.example.ui.theme.maveshiTextFieldColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReelBottomSheet(
    onDismiss: () -> Unit,
    onConfirmUpload: (caption: String) -> Unit,
    isUrdu: Boolean = true
) {
    val sheetState = rememberModalBottomSheetState()
    var captionText by remember { mutableStateOf("#maveshimandi #livestock #goat #lahore") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        modifier = Modifier.testTag("add_reel_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Phone frame illustration with play button (Image 4)
            Box(
                modifier = Modifier
                    .size(width = 90.dp, height = 110.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, MandiOrangePrimary, RoundedCornerShape(16.dp))
                    .background(Color(0xFFFFF8F1)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp, 4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(MandiOrangePrimary.copy(alpha = 0.5f))
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = MandiOrangePrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Heading: "ریل شامل کریں" / "Add Reel"
            Text(
                text = if (isUrdu) "ریل شامل کریں" else "Add Reel Video",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Description
            Text(
                text = if (isUrdu)
                    "آپ کے پروفائل پر مختصر عمودی کلپ خریداروں کو فیڈ میں آپ کے جانور دکھانے میں مدد کرتی ہے۔"
                else
                    "A short vertical clip on your profile helps buyers discover your livestock directly in the video feed.",
                fontSize = 13.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Bullet points as in Image 4
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BulletItem(
                    text = if (isUrdu) "9:16 عمودی دوسری ریلز کی طرح سکرین بھرتی ہے" else "9:16 vertical full screen format"
                )
                BulletItem(
                    text = if (isUrdu) "زیادہ سے زیادہ 60 سیکنڈ" else "Up to 60 seconds duration"
                )
                BulletItem(
                    text = if (isUrdu) "روشن اور مستحکم شاٹس کو زیادہ دیکھا جاتا ہے" else "Bright and steady shots receive more views"
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = captionText,
                onValueChange = { captionText = it },
                label = { Text(if (isUrdu) "کیپشن / ہیش ٹیگز" else "Caption / Hashtags") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                textStyle = MaveshiInputTextStyle,
                colors = maveshiTextFieldColors()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons: "منسوخ کریں" and "ویڈیو منتخب کریں" (Image 4)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text(
                        text = if (isUrdu) "منسوخ کریں" else "Cancel",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = { onConfirmUpload(captionText) },
                    colors = ButtonDefaults.buttonColors(containerColor = MandiOrangePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("confirm_upload_reel_button")
                ) {
                    Text(
                        text = if (isUrdu) "ویڈیو منتخب کریں" else "Select Video",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun BulletItem(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(MandiOrangePrimary)
        )
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color(0xFF334155),
            fontWeight = FontWeight.Medium
        )
    }
}
