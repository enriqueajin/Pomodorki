package com.enriqueajin.pomidorki.presentation.home.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.enriqueajin.pomidorki.R

@Composable
fun CountdownView(
    modifier: Modifier = Modifier,
    formattedText: String,
    textColor: Color,
    fontSize: TextUnit = 82.sp,
    fontFamily: FontFamily = FontFamily(
        Font(resId = R.font.bebasneue_regular)
    ),
) {
    Text(
        modifier = modifier,
        text = formattedText,
        fontSize = fontSize,
        color = textColor,
        fontFamily = fontFamily
    )
}

@Preview(showBackground = true)
@Composable
fun CountdownTimerPreview() {
    CountdownView(
        formattedText = "25:00",
        textColor = Color.Black
    )
}