package com.enriqueajin.pomidorki.presentation.tasks.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enriqueajin.pomidorki.domain.model.Task
import com.enriqueajin.pomidorki.presentation.tasks.getTasks

@Composable
fun TaskItem(
    modifier: Modifier = Modifier,
    task: Task,
    onTaskClick: () -> Unit,
) {
    Card(
        modifier =
            modifier
                .padding(bottom = 10.dp)
                .fillMaxWidth()
                .height(75.dp)
                .clickable { onTaskClick() },
        shape = RoundedCornerShape(14),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 4.dp,
            ),
    ) {
        Row(
            modifier =
                modifier
                    .fillMaxSize()
                    .padding(end = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier =
                        modifier
                            .width(20.dp)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp))
                            .background(Color(task.priority.hexColor)),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = task.title,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    )
                    Text(
                        text = "Pomodoros 3/5",
                        color = Color.DarkGray,
                    )
                }
            }
            Column(
                horizontalAlignment = Alignment.End,
            ) {
                Box(
                    modifier =
                        modifier
                            .height(20.dp)
                            .clip(RoundedCornerShape(50))
                            .background(task.category.color),
                ) {
                    Text(
                        modifier =
                            modifier
                                .align(Alignment.Center)
                                .padding(horizontal = 5.dp),
                        text = task.category.name,
                        color = MaterialTheme.colorScheme.inverseOnSurface,
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        modifier = Modifier.size(18.dp),
                        imageVector = Icons.Default.DateRange,
                        tint = Color.DarkGray,
                        contentDescription = "Calendar icon",
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        modifier = modifier,
                        text = task.dueDate,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TaskItemPreview(modifier: Modifier = Modifier) {
    TaskItem(
        task = getTasks().first(),
        onTaskClick = {},
    )
}
