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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.enriqueajin.pomidorki.R
import com.enriqueajin.pomidorki.presentation.ui.theme.darkPink
import com.enriqueajin.pomidorki.presentation.ui.theme.pinkSecondary

@Composable
fun TimerPicker() {

    val items = rememberSaveable {
        listOf("Pomodoro", "Short Break", "Long Break")
    }
    var selected by rememberSaveable { mutableStateOf(0) }

    TabRow(
        modifier = Modifier
            .clip(RoundedCornerShape(100)),
        selectedTabIndex = selected,
        containerColor = pinkSecondary,
        indicator = { tabPositions ->
            TabRowDefaults.apply {
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selected])
                        .fillMaxHeight()
                        .padding(7.dp)
                        .background(
                            color = darkPink,
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
                    .height(55.dp),
                selected = selected == index ,
                onClick = { selected = index },
            ) {
                val font = if (selected == index) {
                    R.font.montserrat_medium
                } else {
                    R.font.montserrat_regular
                }

                Text(
                    text = tab,
                    color = Color.White,
                    fontSize = 17.sp,
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
        TimerPicker()
}