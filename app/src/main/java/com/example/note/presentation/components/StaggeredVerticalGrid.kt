package com.example.note.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import kotlin.math.ceil

@Composable
fun StaggeredVerticalGrid(
    modifier: Modifier = Modifier,
    children: @Composable () -> Unit
) {
    Layout(
        content = children,
        modifier = modifier
    ) { measurables, constraints ->
        check(constraints.hasBoundedWidth) {
            "Unbounded width not supported"
        }
        val columns = 2
        val columnWidth = constraints.maxWidth / columns
        val itemConstraints = constraints.copy(maxWidth = columnWidth)
        val colHeights = IntArray(columns) { 0 } // track each column's height
        val parcelables = measurables.map { measurable ->
            val column = shortestColumn(colHeights)
            val placeable = measurable.measure(itemConstraints)
            colHeights[column] += placeable.height
            placeable
        }

        val height = colHeights.maxOrNull()?.coerceIn(constraints.minHeight, constraints.maxHeight)
            ?: constraints.minHeight
        layout(
            width = constraints.maxWidth,
            height = height
        ) {
            val colY = IntArray(columns) { 0 }
            parcelables.forEach { placeable ->
                val column = shortestColumn(colY)
                placeable.place(
                    x = columnWidth * column,
                    y = colY[column]
                )
                colY[column] += placeable.height
            }
        }
    }
}

private fun shortestColumn(colHeights: IntArray): Int {
    var minHeight = Int.MAX_VALUE
    var column = 0
    colHeights.forEachIndexed { index, height ->
        if (height < minHeight) {
            minHeight = height
            column = index
        }
    }
    return column
}