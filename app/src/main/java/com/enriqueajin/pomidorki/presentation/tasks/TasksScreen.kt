package com.enriqueajin.pomidorki.presentation.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.enriqueajin.pomidorki.presentation.tasks.components.ActionDropdownMenu
import com.enriqueajin.pomidorki.presentation.tasks.components.StatusFilters
import com.enriqueajin.pomidorki.presentation.tasks.components.TaskItem
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val title: String,
    val description: String,
    val targetPomodoros: Int,
    val status: Status,
    val priority: Priority,
    val category: Category,
    val dueDate: String,
)

enum class Status(val label: String) {
    TODO("To-do"),
    IN_PROGRESS("In progress"),
    DONE("Done")
}

@Serializable
data class Category(
    val name: String,
    @Contextual val color: Color
)

enum class Priority(val hexColor: Long, val priorityValue: Int) {
    VERY_HIGH(0xFFE74C3C, 1),
    HIGH(0xFFF5B041, 2),
    MEDIUM(0xFFF7DC6F, 3),
    LOW(0xFFA8D5BA, 4),
}

@Composable
fun TasksScreenRoot(
    tasksViewModel: TasksViewModel = hiltViewModel()
) {
    val state by tasksViewModel.uiState.collectAsStateWithLifecycle()

    TasksScreen(
        state = state,
        event = tasksViewModel::onEvent
    )
}

@Composable
private fun TasksScreen(
    modifier: Modifier = Modifier,
    state: TasksScreenState,
    event: (TasksScreenEvent) -> Unit
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatusFilters(
                selected = state.selectedStatus ?: Status.TODO.label,
                onSelectedChange = { event(TasksScreenEvent.UpdateSelectedStatus(it)) },
                onChipClick = { }
            )
            val sortingOptions = arrayOf("Sort by priority", "Sort by title", "Sort by category")
            val groupingOptions = arrayOf("Group by category", "Group by priority")

            Row {
                var isGroupingMenuExpanded by remember { mutableStateOf(false) }
                var isSortingMenuExpanded by remember { mutableStateOf(false) }
                ActionDropdownMenu(
                    modifier.size(28.dp),
                    expanded = isSortingMenuExpanded,
                    icon = Icons.Default.MoreVert,
                    onExpandedChange = { isSortingMenuExpanded = it },
                    dropdownItemList = sortingOptions,
                    onDropdownItemClick = { item ->
                        isSortingMenuExpanded = false
                        when(item) {
                           "Sort by priority" -> { event(TasksScreenEvent.UpdateCurrentSorting(Sorting.Priority)) }
                           "Sort by title" -> { event(TasksScreenEvent.UpdateCurrentSorting(Sorting.Title)) }
                           "Sort by category" -> { event(TasksScreenEvent.UpdateCurrentSorting(Sorting.Category)) }
                        }
                    }
                )
                Spacer(modifier = Modifier.width(10.dp))
                ActionDropdownMenu(
                    modifier.size(28.dp),
                    expanded = isGroupingMenuExpanded,
                    icon = if(state.groupedTasks != null) Icons.Rounded.Clear else Icons.Rounded.Menu,
                    onExpandedChange = {
                        if(state.groupedTasks != null) {
                            event(TasksScreenEvent.UpdateCurrentGrouping(null))
                        } else {
                            isGroupingMenuExpanded = it
                        }
                   },
                    dropdownItemList = groupingOptions,
                    onDropdownItemClick = { item ->
                        isGroupingMenuExpanded = false
                        when(item) {
                            "Group by category" -> { event(TasksScreenEvent.UpdateCurrentGrouping(Grouping.Category)) }
                            "Group by priority" -> { event(TasksScreenEvent.UpdateCurrentGrouping(Grouping.Priority)) }
                        }
                    }
                )
            }
        }
        if(!state.groupedTasks.isNullOrEmpty()) {
            GroupedTasks(groupedTasks = state.groupedTasks)
        } else {
            state.tasks?.let {
                LazyColumn {
                    items(it) { task ->
                        TaskItem(
                            task = task,
                            onTaskClick = {}
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GroupedTasks(
    modifier: Modifier = Modifier,
    groupedTasks: Map<String, List<Task>>,
) {
    LazyColumn {
        groupedTasks.forEach { (category, tasks) ->
            if(tasks.isNotEmpty()) {
                item {
                    Text(text = category)
                }
                items(tasks) { task ->
                    TaskItem(
                        task = task,
                        onTaskClick = {}
                    )
                }

            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TasksScreenPreview() {
    TasksScreen(
        state = TasksScreenState(tasks = getTasks()),
        event = {}
    )
}