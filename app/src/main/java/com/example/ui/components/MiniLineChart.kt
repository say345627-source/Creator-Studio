package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyMetric
import com.example.ui.theme.StudioRed
import com.example.ui.theme.StudioRedLight

@Composable
fun MiniLineChart(
    data: List<DailyMetric>,
    title: String = "المشاهدات اليومية",
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return

    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    val maxViews = remember(data) { data.maxOfOrNull { it.views }?.coerceAtLeast(100L) ?: 100L }
    val minViews = remember(data) { data.minOfOrNull { it.views } ?: 0L }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                val activeMetric = selectedIndex?.let { data.getOrNull(it) } ?: data.lastOrNull()
                if (activeMetric != null) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${activeMetric.views} مشاهدة",
                            color = StudioRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = activeMetric.dateLabel,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .matchParentSize()
                        .pointerInput(data) {
                            detectTapGestures { offset ->
                                val pointSpacing = size.width / (data.size - 1).coerceAtLeast(1)
                                val index = (offset.x / pointSpacing).toInt().coerceIn(0, data.size - 1)
                                selectedIndex = index
                            }
                        }
                ) {
                    val width = size.width
                    val height = size.height
                    val spacing = width / (data.size - 1).coerceAtLeast(1)
                    val valueRange = (maxViews - minViews).coerceAtLeast(1L).toFloat()

                    // Horizontal helper grid lines
                    val gridColor = Color.White.copy(alpha = 0.08f)
                    drawLine(gridColor, Offset(0f, 0f), Offset(width, 0f), strokeWidth = 1f)
                    drawLine(gridColor, Offset(0f, height * 0.5f), Offset(width, height * 0.5f), strokeWidth = 1f)
                    drawLine(gridColor, Offset(0f, height), Offset(width, height), strokeWidth = 1f)

                    val linePath = Path()
                    val fillPath = Path()

                    val points = data.mapIndexed { index, metric ->
                        val x = index * spacing
                        val normalized = (metric.views - minViews).toFloat() / valueRange
                        val y = height - (normalized * (height - 20f)) - 10f
                        Offset(x, y)
                    }

                    if (points.isNotEmpty()) {
                        linePath.moveTo(points.first().x, points.first().y)
                        fillPath.moveTo(points.first().x, height)
                        fillPath.lineTo(points.first().x, points.first().y)

                        for (i in 0 until points.size - 1) {
                            val p0 = points[i]
                            val p1 = points[i + 1]
                            val controlPoint1 = Offset(p0.x + (p1.x - p0.x) / 2f, p0.y)
                            val controlPoint2 = Offset(p0.x + (p1.x - p0.x) / 2f, p1.y)
                            linePath.cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, p1.x, p1.y)
                            fillPath.cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, p1.x, p1.y)
                        }

                        fillPath.lineTo(points.last().x, height)
                        fillPath.close()

                        // Draw shaded gradient area
                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    StudioRed.copy(alpha = 0.35f),
                                    StudioRed.copy(alpha = 0.0f)
                                ),
                                startY = 0f,
                                endY = height
                            )
                        )

                        // Draw smooth line
                        drawPath(
                            path = linePath,
                            brush = Brush.horizontalGradient(
                                colors = listOf(StudioRedLight, StudioRed)
                            ),
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Draw selected point indicator
                        selectedIndex?.let { index ->
                            if (index in points.indices) {
                                val pt = points[index]
                                drawLine(
                                    color = StudioRed.copy(alpha = 0.5f),
                                    start = Offset(pt.x, 0f),
                                    end = Offset(pt.x, height),
                                    strokeWidth = 1.5.dp.toPx()
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = 5.dp.toPx(),
                                    center = pt
                                )
                                drawCircle(
                                    color = StudioRed,
                                    radius = 3.dp.toPx(),
                                    center = pt
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Start & End date labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = data.firstOrNull()?.dateLabel ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
                Text(
                    text = "انقر فوق المنحنى لعرض تفاصيل اليوم",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    fontSize = 10.sp
                )
                Text(
                    text = data.lastOrNull()?.dateLabel ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
            }
        }
    }
}
