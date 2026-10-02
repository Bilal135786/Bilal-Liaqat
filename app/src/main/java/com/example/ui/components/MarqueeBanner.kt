package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MandiOrangePrimary

@Composable
fun MarqueeBanner(
    text: String = "• Welcome to Mall Maweshi - Pakistan's Online Cattle and Livestock Marketplace • مال مویشی میں خوش آمدید • پاکستان کی سب سے بڑی آن لائن مویشی منڈی • خرید و فروخت باآسانی •",
    onClick: () -> Unit = {}
) {
    val transition = rememberInfiniteTransition(label = "marquee")
    val animatedOffset by transition.animateFloat(
        initialValue = 0f,
        targetValue = -1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "marqueeOffset"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MandiOrangePrimary)
            .clickable { onClick() }
            .padding(vertical = 6.dp)
    ) {
        val density = LocalDensity.current
        val offsetPx = (animatedOffset * density.density).toInt()

        Text(
            text = "$text          $text          $text",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            softWrap = false,
            modifier = Modifier.offset { IntOffset(offsetPx % 1200, 0) }
        )
    }
}
