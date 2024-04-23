package com.vraj.spendwise.ui.base

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vraj.spendwise.ui.theme.BaseGreen

@Composable
fun BaseButton(
    modifier: Modifier = Modifier,
    backgroundColor: Color = BaseGreen,
    cornerRadius: Dp = 10.dp,
    textColor: Color = Color.White,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
    text: String,
    onButtonClicked: @Composable () -> Unit
) {
    val triggerButtonClick = remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(52.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { triggerButtonClick.value = true }
    ) {
        Text(
            text = text,
            style = textStyle,
            lineHeight = 20.sp,
            color = textColor
        )
    }

    if (triggerButtonClick.value) {
        onButtonClicked()
        triggerButtonClick.value = false
    }
}