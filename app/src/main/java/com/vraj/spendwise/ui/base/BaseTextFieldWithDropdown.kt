package com.vraj.spendwise.ui.base

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.window.PopupProperties

@Composable
fun BaseTextFieldWithDropdown(
    modifier: Modifier = Modifier,
    textFieldValue: TextFieldValue = TextFieldValue(),
    onValueChanged: (TextFieldValue) -> Unit = { },
    placeholder: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    keyboardActions: KeyboardActions = KeyboardActions(),
    onDismissRequest: () -> Unit,
    isDropdownExpanded: Boolean,
    list: List<String>
) {
    Box(modifier) {
        BaseTextField(
            textFieldValue = textFieldValue,
            onValueChanged = onValueChanged,
            placeholder = placeholder,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            onFocusChanged = {
                if (!it.isFocused)
                    onDismissRequest()
            }
        )

        DropdownMenu(
            expanded = isDropdownExpanded,
            properties = PopupProperties(
                focusable = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            ),
            onDismissRequest = onDismissRequest
        ) {
            list.forEach { text ->
                DropdownMenuItem(
                    onClick = {
                        onDismissRequest.invoke()
                        onValueChanged(TextFieldValue(text, TextRange(text.length)))
                    },
                    text = {
                        Text(
                            text = text,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                )
            }
        }
    }
}