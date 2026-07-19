package com.example.habitly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.habitly.R

private val NoOverscrollNestedScrollConnection = object : NestedScrollConnection {}

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun HabitlyScreen(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    scrollable: Boolean = true,
    content: @Composable () -> Unit
) {
    val scrollModifier = if (scrollable) {
        Modifier.verticalScroll(rememberScrollState())
    } else {
        Modifier.nestedScroll(NoOverscrollNestedScrollConnection)
    }

    CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            HabitlyBackgroundPattern()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .then(scrollModifier)
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp, bottom = 124.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                HabitlyScreenHeader(
                    title = title,
                    subtitle = subtitle
                )

                Spacer(modifier = Modifier.height(6.dp))

                content()
            }
        }
    }
}

@Composable
private fun HabitlyBackgroundPattern(
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    val tealGlow = Color(0xFF18D4C2)
    val violetGlow = Color(0xFF7B61FF)

    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF252A31),
                    Color(0xFF151A22),
                    Color(0xFF0D1219)
                ),
                startY = 0f,
                endY = size.height
            )
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.13f),
                    primary.copy(alpha = 0.10f),
                    primary.copy(alpha = 0f)
                ),
                center = Offset(size.width * 0.80f, 100.dp.toPx()),
                radius = 360.dp.toPx()
            ),
            radius = 360.dp.toPx(),
            center = Offset(size.width * 0.80f, 100.dp.toPx())
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    tealGlow.copy(alpha = 0.14f),
                    tealGlow.copy(alpha = 0.045f),
                    tealGlow.copy(alpha = 0f)
                ),
                center = Offset(size.width * 0.02f, size.height * 0.34f),
                radius = 440.dp.toPx()
            ),
            radius = 440.dp.toPx(),
            center = Offset(size.width * 0.02f, size.height * 0.34f)
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    violetGlow.copy(alpha = 0.12f),
                    violetGlow.copy(alpha = 0.035f),
                    violetGlow.copy(alpha = 0f)
                ),
                center = Offset(size.width * 1.04f, size.height * 0.72f),
                radius = 500.dp.toPx()
            ),
            radius = 500.dp.toPx(),
            center = Offset(size.width * 1.04f, size.height * 0.72f)
        )

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.Black.copy(alpha = 0.10f),
                    Color.Transparent
                ),
                startY = size.height * 0.25f,
                endY = size.height
            )
        )
    }
}

@Composable
private fun HabitlyScreenHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(56.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
                                )
                            ),
                            shape = MaterialTheme.shapes.extraLarge
                        )
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                modifier = Modifier.shadow(
                    elevation = 12.dp,
                    shape = MaterialTheme.shapes.extraLarge,
                    ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.20f),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.28f),
                    clip = false
                ),
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f),
                contentColor = MaterialTheme.colorScheme.onSurface,
                border = BorderStroke(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.10f)
                )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_habitly_mark),
                    contentDescription = "Habitly logo",
                    modifier = Modifier
                        .padding(8.dp)
                        .size(36.dp)
                )
            }
        }
    }
}

val HabitlyCardPadding = PaddingValues(18.dp)
