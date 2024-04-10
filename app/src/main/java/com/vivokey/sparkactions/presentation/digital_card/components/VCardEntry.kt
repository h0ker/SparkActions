package com.vivokey.sparkactions.presentation.digital_card.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VCardEntry(
    modifier: Modifier = Modifier,
    value: String,
    label: String? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    keyboardType: KeyboardType = KeyboardType.Text,
    capitalizeFirst: Boolean = false,
    isMultiline: Boolean = false,
    isFocused: Boolean = false,
    onFocusChange: (Boolean) -> Unit = {},
    onValueChange: (String) -> Unit
) {

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isFocused) {
        if (isFocused) {
            focusRequester.requestFocus()
        }
    }

    OutlinedTextField(
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged { focusState ->
                onFocusChange(focusState.hasFocus)
            }
            .padding(bottom = 8.dp),
        textStyle = TextStyle(
            fontSize = 16.sp,
        ),
        keyboardOptions = KeyboardOptions(
            capitalization = if (capitalizeFirst) KeyboardCapitalization.Sentences else KeyboardCapitalization.None,
            keyboardType = keyboardType
        ),
        label = {
            label?.let {
                Text(
                    text = it,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp
                )
            }
        },
        singleLine = !isMultiline,
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = backgroundColor
        ),
        value = value,
        onValueChange = onValueChange
    )
}