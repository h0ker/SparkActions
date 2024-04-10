package com.vivokey.sparkactions.presentation.edit_action.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.vivokey.sparkactions.domain.models.ActionTarget
import com.vivokey.sparkactions.domain.models.SMSActionTarget
import com.vivokey.sparkactions.domain.utilities.StringUtils.validateAndParsePhoneNumber
import com.vivokey.sparkactions.presentation.components.SparkOutlinedTextField

@Composable
fun SMSActionInfo(
    existingSMSInfo: SMSActionTarget? = null,
    onActionTargetChange: (ActionTarget) -> Unit
) {
    var recipientNumber by remember { mutableStateOf(existingSMSInfo?.recipientNumber ?: "") }
    var message by remember { mutableStateOf(existingSMSInfo?.message ?: "")}
    val allowedChars = "0123456789.(-#*"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SparkOutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = recipientNumber,
            label = "Recipient Number",
            keyboardType = KeyboardType.Phone,
            onValueChanged = { newValue ->
                if (newValue.length <= 30 && newValue.all { char -> char in allowedChars }) {
                    recipientNumber = newValue
                }
            }
        )
        SparkOutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = message,
            label = "Message",
            onValueChanged = { newValue ->
                message = newValue
                val actionTarget = SMSActionTarget(
                    recipientNumber,
                    message
                )
                onActionTargetChange(actionTarget)
            }
        )
    }
}