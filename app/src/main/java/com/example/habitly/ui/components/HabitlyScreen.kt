package com.example.habitly.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
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
    val tertiary = MaterialTheme.colorScheme.tertiary
    val dotColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.13f)

    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        val patternTop = 152.dp.toPx()
        val patternHeight = size.height - patternTop
        if (patternHeight <= 0f) return@Canvas

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    primary.copy(alpha = 0.115f),
                    primary.copy(alpha = 0.045f),
                    primary.copy(alpha = 0f)
                ),
                center = Offset(size.width * 0.92f, patternTop + patternHeight * 0.10f),
                radius = 270.dp.toPx()
            ),
            radius = 270.dp.toPx(),
            center = Offset(size.width * 0.92f, patternTop + patternHeight * 0.10f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    tertiary.copy(alpha = 0.085f),
                    tertiary.copy(alpha = 0.035f),
                    tertiary.copy(alpha = 0f)
                ),
                center = Offset(size.width * 0.05f, patternTop + patternHeight * 0.58f),
                radius = 310.dp.toPx()
            ),
            radius = 310.dp.toPx(),
            center = Offset(size.width * 0.05f, patternTop + patternHeight * 0.58f)
        )
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    primary.copy(alpha = 0.045f),
                    primary.copy(alpha = 0f)
                ),
                startY = patternTop,
                endY = patternTop + 220.dp.toPx()
            )
        )

        val dotSpacing = 28.dp.toPx()
        val dotRadius = 1.35.dp.toPx()
        var row = 0
        var y = patternTop + 20.dp.toPx()

        while (y < size.height) {
            val rowOffset = if (row % 2 == 0) 0f else dotSpacing / 2f
            var x = rowOffset

            while (x < size.width) {
                drawCircle(
                    color = dotColor,
                    radius = dotRadius,
                    center = Offset(x, y)
                )
                x += dotSpacing
            }

            y += dotSpacing
            row++
        }
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
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_habitly_mark),
                    contentDescription = "Habitly logo",
                    modifier = Modifier
                        .padding(7.dp)
                        .size(34.dp)
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(56.dp)
                    .height(4.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.extraLarge
                    )
            )
            Box(
                modifier = Modifier
                    .padding(top = 5.dp)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                        shape = MaterialTheme.shapes.extraLarge
                    )
            )
        }
    }
}

val HabitlyCardPadding = PaddingValues(18.dp)
