package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Composable
fun QrCodeView(
    data: String,
    size: Dp = 160.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val hash = data.hashCode()
            val random = Random(hash)
            val gridSize = 19
            val cellSize = this.size.width / gridSize

            // Draw Finder Patterns (Top-Left, Top-Right, Bottom-Left)
            fun drawFinder(x: Int, y: Int) {
                drawRect(Color.Black, Offset(x * cellSize, y * cellSize), Size(7 * cellSize, 7 * cellSize))
                drawRect(Color.White, Offset((x + 1) * cellSize, (y + 1) * cellSize), Size(5 * cellSize, 5 * cellSize))
                drawRect(Color.Black, Offset((x + 2) * cellSize, (y + 2) * cellSize), Size(3 * cellSize, 3 * cellSize))
            }

            drawFinder(1, 1)
            drawFinder(gridSize - 8, 1)
            drawFinder(1, gridSize - 8)

            // Fill body modules
            for (i in 0 until gridSize) {
                for (j in 0 until gridSize) {
                    val inTopLeft = i in 0..8 && j in 0..8
                    val inTopRight = i in (gridSize - 9) until gridSize && j in 0..8
                    val inBottomLeft = i in 0..8 && j in (gridSize - 9) until gridSize
                    if (!inTopLeft && !inTopRight && !inBottomLeft) {
                        if (random.nextBoolean()) {
                            drawRect(
                                color = Color.Black,
                                topLeft = Offset(i * cellSize, j * cellSize),
                                size = Size(cellSize * 0.92f, cellSize * 0.92f)
                            )
                        }
                    }
                }
            }
        }
    }
}
