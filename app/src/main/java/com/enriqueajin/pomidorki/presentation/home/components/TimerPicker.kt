package com.enriqueajin.pomidorki.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.enriqueajin.pomidorki.R
import com.enriqueajin.pomidorki.presentation.ui.theme.darkPink
import com.enriqueajin.pomidorki.presentation.ui.theme.pinkSecondary
import com.enriqueajin.pomidorki.utils.Constants.pomodoroTabItems

@Composable
fun TimerPicker(
    modifier: Modifier,
    selected: Int,
    items: List<String>,
    containerColor: Color,
    indicatorColor: Color,
    onTabSelected: (Int) -> Unit
) {

    val tabRowTestTag = stringResource(id = R.string.timer_picker_tab_row)

    TabRow(
        modifier = modifier
            .clip(RoundedCornerShape(100))
            .semantics {
                contentDescription = tabRowTestTag
            },
        selectedTabIndex = selected,
        containerColor = containerColor,
        indicator = { tabPositions ->
            TabRowDefaults.apply {
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selected])
                        .fillMaxHeight()
                        .padding(7.dp)
                        .background(
                            color = indicatorColor,
                            shape = RoundedCornerShape(100)
                        )
                )
            }
        },
    ) {
        items.forEachIndexed { index, tab ->
            Tab(
                modifier = Modifier
                    .zIndex(1f)
                    .height(55.dp)
                    .semantics {
                        contentDescription = tab
                    },
                selected = selected == index ,
                onClick = {
                    onTabSelected(index)
                },
            ) {
                val font = if (selected == index) {
                    R.font.montserrat_medium
                } else {
                    R.font.montserrat_regular
                }

                Text(
                    text = tab,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(
                        Font(
                            resId = font
                        )
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TimerPickerPreview() {
    var selected by remember {
        mutableIntStateOf(1)
    }
    TimerPicker(
        modifier = Modifier,
        selected = selected,
        items = pomodoroTabItems,
        containerColor = pinkSecondary,
        indicatorColor = darkPink,
        onTabSelected = { selected = it }
    )
}