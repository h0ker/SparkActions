package com.vivokey.sparkactions.presentation.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SparkOutlinedTextField(
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    value: String,
    label: String? = null,
    onValueChanged: (String) -> Unit
) {

    val focusRequester = remember { FocusRequester() }
    var isFieldFocused by remember { mutableStateOf(false) }

    OutlinedTextField(
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged { focusState ->
                isFieldFocused = focusState.isFocused
            },
        label = {
            label?.let {
                Text(
                    text = it,
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
        singleLine = true,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            unfocusedLabelColor = if (value.isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.inverseSurface,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.inverseSurface,
            focusedBorderColor = MaterialTheme.colorScheme.primary
        ),
        textStyle = TextStyle(fontSize = 20.sp),
        shape = RoundedCornerShape(16.dp),
        value = value,
        onValueChange = { newValue ->
            onValueChanged(newValue)
        }
    )
}