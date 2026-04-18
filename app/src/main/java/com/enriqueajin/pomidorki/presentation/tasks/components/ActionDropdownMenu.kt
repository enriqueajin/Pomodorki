package com.enriqueajin.pomidorki.presentation.tasks.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ActionDropdownMenu(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    icon: ImageVector,
    onExpandedChange: (Boolean) -> Unit,
    dropdownItemList: Array<String>,
    onDropdownItemClick: (String) -> Unit,
) {
    Column {
        Box(modifier = modifier) {
            IconButton(
                modifier = modifier,
                onClick = { onExpandedChange(!expanded) },
            ) {
                Icon(
                    modifier = modifier,
                    imageVector = icon,
                    contentDescription = "Three dot dropdown menu",
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) },
            ) {
                dropdownItemList.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = { onDropdownItemClick(item) },
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ActionDropdownMenuPreview(modifier: Modifier = Modifier) {
    val itemList = arrayOf("Group by category", "Sort by priority")
    ActionDropdownMenu(
        expanded = false,
        icon = Icons.Default.MoreVert,
        onExpandedChange = {},
        dropdownItemList = itemList,
        onDropdownItemClick = {},
    )
}
