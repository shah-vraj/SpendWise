package com.vraj.spendwise.ui.base

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp

@Composable
fun BaseTextField(
    modifier: Modifier = Modifier,
    textFieldValue: String = "",
    onValueChanged: (String) -> Unit = { },
    placeholder: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    keyboardActions: KeyboardActions = KeyboardActions()
) {
    Box(
        modifier = modifier
            .height(52.dp)
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.background)
            .padding(start = 19.dp, end = 21.dp)
    ) {
        BasicTextField(
            value = textFieldValue,
            onValueChange = { onValueChanged(it) },
            textStyle = MaterialTheme.typography.bodyMedium,
            singleLine = true,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            decorationBox = { innerTextField ->
                Box {
                    if (textFieldValue.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .alpha(0.5f)
                        )
                    }
                }
                innerTextField()
            },
            cursorBrush = SolidColor(MaterialTheme.colorScheme.secondary),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        )
    }
}