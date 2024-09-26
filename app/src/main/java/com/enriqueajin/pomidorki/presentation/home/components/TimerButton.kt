package com.enriqueajin.pomidorki.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.enriqueajin.pomidorki.R
import com.enriqueajin.pomidorki.presentation.ui.theme.greenPomodoro

@Composable
fun TimerButton(
    text: String,
    icon: ImageVector,
    containerColor: Color,
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier
            .width(140.dp)
            .height(45.dp),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = Color.White,
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier
                    .size(35.dp),
                imageVector = icon,
                contentDescription = null
            )
            Text(
                text = text,
                fontSize = 18.sp,
                fontFamily = FontFamily(
                    Font(R.font.montserrat_semibold)
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TimerButtonPreview() {
    TimerButton(
        text = "Start",
        icon = Icons.Default.PlayArrow,
        containerColor = greenPomodoro,
        onClick = {}
    )
}