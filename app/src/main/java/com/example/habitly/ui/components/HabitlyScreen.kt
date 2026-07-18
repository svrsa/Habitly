package com.example.habitly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

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
    val lineColor = primary.copy(alpha = 0.065f)
    val accentColor = primary.copy(alpha = 0.10f)

    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        val patternTop = 152.dp.toPx()
        val spacing = 30.dp.toPx()
        val strokeWidth = 1.dp.toPx()
        val patternHeight = size.height - patternTop
        var x = -patternHeight

        while (x < size.width) {
            drawLine(
                color = lineColor,
                start = Offset(x, patternTop),
                end = Offset(x + patternHeight, size.height),
                strokeWidth = strokeWidth
            )
            x += spacing
        }

        drawLine(
            color = accentColor,
            start = Offset(0f, patternTop + patternHeight * 0.18f),
            end = Offset(size.width, patternTop + patternHeight * 0.06f),
            strokeWidth = strokeWidth
        )
        drawLine(
            color = accentColor,
            start = Offset(0f, patternTop + patternHeight * 0.70f),
            end = Offset(size.width, patternTop + patternHeight * 0.58f),
            strokeWidth = strokeWidth
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
        Column(
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
