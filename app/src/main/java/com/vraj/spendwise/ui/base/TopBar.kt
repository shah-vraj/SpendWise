package com.vraj.spendwise.ui.base

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vraj.spendwise.R

@Composable
fun TopBar(
    onBackButtonClicked: () -> Unit,
    centerText: String? = null,
    rightImage: Pair<Int, () -> Unit>? = null
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 20.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(45.dp)
                .clip(CircleShape)
                .align(Alignment.CenterStart)
                .background(MaterialTheme.colorScheme.background)
                .clickable { onBackButtonClicked() }
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_back_arrow),
                contentDescription = "backButton",
            )
        }

        centerText?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.onPrimary,
                lineHeight = 40.sp,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        rightImage?.let {
            Icon(
                painter = painterResource(id = it.first),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .size(26.dp)
                    .align(Alignment.CenterEnd)
                    .clickable(interactionSource, null) { it.second.invoke() }
            )
        }
    }
}