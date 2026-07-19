package com.example.habitly.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun HabitlyCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = HabitlyCardPadding,
    content: @Composable () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val glassBorderColor = Color(0xFF5D7186)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 18.dp,
                shape = MaterialTheme.shapes.large,
                ambientColor = Color.Black.copy(alpha = 0.34f),
                spotColor = primaryColor.copy(alpha = 0.14f),
                clip = false
            ),
        shape = MaterialTheme.shapes.large,
        color = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 1.dp,
        shadowElevation = 0.dp,
        border = BorderStroke(
            width = 1.dp,
            color = glassBorderColor.copy(alpha = 0.70f)
        )
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF34404E),
                            Color(0xFF283443),
                            Color(0xFF202D3A)
                        )
                    ),
                    shape = MaterialTheme.shapes.large
                )
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val cornerRadius = 24.dp.toPx()
                val inset = 1.2.dp.toPx()
                val cardSize = Size(size.width - inset * 2, size.height - inset * 2)
                val cardTopLeft = Offset(inset, inset)

                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.14f),
                            Color.White.copy(alpha = 0.075f),
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = 96.dp.toPx()
                    ),
                    topLeft = Offset(2.dp.toPx(), 2.dp.toPx()),
                    size = Size(size.width - 4.dp.toPx(), size.height - 4.dp.toPx()),
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                )

                drawRoundRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.28f),
                            glassBorderColor.copy(alpha = 0.58f),
                            primaryColor.copy(alpha = 0.26f)
                        ),
                        start = Offset.Zero,
                        end = Offset(size.width, size.height)
                    ),
                    topLeft = cardTopLeft,
                    size = cardSize,
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                    style = Stroke(width = 2.2.dp.toPx())
                )

                drawRoundRect(
                    color = Color.White.copy(alpha = 0.16f),
                    topLeft = cardTopLeft,
                    size = cardSize,
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                    style = Stroke(width = 1.dp.toPx())
                )

                drawRoundRect(
                    color = primaryColor.copy(alpha = 0.11f),
                    topLeft = Offset(4.dp.toPx(), 4.dp.toPx()),
                    size = Size(size.width - 8.dp.toPx(), size.height - 8.dp.toPx()),
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                    style = Stroke(width = 6.dp.toPx())
                )
            }

            Column(
                modifier = Modifier.padding(contentPadding),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector? = null,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    HabitlyCard(
        modifier = modifier
    ) {
        if (icon != null) {
            Surface(
                shape = CircleShape,
                color = accentColor.copy(alpha = 0.13f),
                contentColor = accentColor
            ) {
                Box(
                    modifier = Modifier
                        .padding(9.dp)
                        .size(22.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineLarge,
            color = accentColor,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
