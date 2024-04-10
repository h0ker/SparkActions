package com.vivokey.sparkactions.presentation.edit_action.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.vivokey.sparkactions.domain.models.ActionTarget
import com.vivokey.sparkactions.domain.models.PhoneActionTarget
import com.vivokey.sparkactions.domain.models.SMSActionTarget
import com.vivokey.sparkactions.domain.utilities.StringUtils.validateAndParsePhoneNumber
import com.vivokey.sparkactions.presentation.components.SparkOutlinedTextField

@Composable
fun PhoneActionInfo(
    existingPhoneNumber: String? = null,
    onActionTargetChange: (ActionTarget?) -> Unit
) {

    var phoneNumber by remember { mutableStateOf("") }
    val allowedChars = "0123456789.(-#*"

    LaunchedEffect(existingPhoneNumber) {
        phoneNumber = existingPhoneNumber ?: ""
    }

    SparkOutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        value = phoneNumber,
        label = "Phone Number",
        keyboardType = KeyboardType.Phone,
        onValueChanged = { newValue ->
            phoneNumber = newValue
            if (newValue.isEmpty()) {
                onActionTargetChange(null)
            } else {
                val filteredText = newValue.filter { char -> char in allowedChars }
                if (filteredText.length <= 30) {
                    phoneNumber = filteredText
                    val actionTarget = PhoneActionTarget(phoneNumber)
                    onActionTargetChange(actionTarget)
                }
            }
        }
    )
}