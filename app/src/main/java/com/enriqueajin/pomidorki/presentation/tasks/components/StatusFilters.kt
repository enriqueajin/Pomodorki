package com.enriqueajin.pomidorki.presentation.tasks.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.enriqueajin.pomidorki.domain.model.Status

data class Filter(
    val label: String,
    val icon: ImageVector,
    val isSelected: Boolean,
)

fun getFilters(): List<Filter> =
    listOf(
        Filter(
            label = Status.TODO.label,
            icon = Icons.Default.DateRange,
            isSelected = true,
        ),
        Filter(
            label = Status.IN_PROGRESS.label,
            icon = Icons.AutoMirrored.Default.List,
            isSelected = false,
        ),
        Filter(
            label = Status.DONE.label,
            icon = Icons.Default.Check,
            isSelected = false,
        ),
    )

@Composable
fun StatusFilters(
    modifier: Modifier = Modifier,
    selected: String,
    onSelectedChange: (String) -> Unit,
    onChipClick: (String) -> Unit,
) {
    val options = getFilters()

    Row(
        modifier = modifier,
    ) {
        LazyRow {
            items(options) { filter ->
                FilterItem(
                    label = filter.label,
                    isSelected = selected == filter.label,
                    onSelectedChange = { onSelectedChange(filter.label) },
                    onChipClick = onChipClick,
                )
            }
        }
    }
}

@Composable
fun FilterItem(
    modifier: Modifier = Modifier,
    label: String,
    isSelected: Boolean,
    onSelectedChange: (String) -> Unit,
    onChipClick: (String) -> Unit,
) {
    FilterChip(
        modifier = modifier.padding(end = 8.dp),
        selected = isSelected,
        onClick = {
            onChipClick(label)
            onSelectedChange(label)
        },
        label = { Text(text = label) },
        colors =
            FilterChipDefaults.filterChipColors(
                selectedContainerColor = Color.Black,
                selectedLabelColor = MaterialTheme.colorScheme.surface,
            ),
    )
}

@Preview(showBackground = true)
@Composable
fun PriorityFiltersPreview(modifier: Modifier = Modifier) {
    StatusFilters(
        selected = Status.IN_PROGRESS.label,
        onSelectedChange = {},
        onChipClick = {},
    )
}
