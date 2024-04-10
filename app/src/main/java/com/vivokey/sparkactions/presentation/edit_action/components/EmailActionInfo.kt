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
import androidx.compose.ui.unit.dp
import com.vivokey.sparkactions.domain.models.Action
import com.vivokey.sparkactions.domain.models.ActionTarget
import com.vivokey.sparkactions.domain.models.EmailActionTarget
import com.vivokey.sparkactions.presentation.components.SparkOutlinedTextField

@Composable
fun EmailActionInfo(
    existingEmailInfo: EmailActionTarget? = null,
    onActionTargetChange: (ActionTarget) -> Unit
) {
    var recipient by remember { mutableStateOf(existingEmailInfo?.recipient ?: "") }
    var subject by remember { mutableStateOf(existingEmailInfo?.subject ?: "") }
    var body by remember { mutableStateOf(existingEmailInfo?.body ?: "") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SparkOutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = recipient,
            label = "Recipient",
            onValueChanged = { newValue ->
                recipient = newValue
                val actionTarget = EmailActionTarget(
                    recipient,
                    subject,
                    body
                )
                onActionTargetChange(actionTarget)
            }
        )
        SparkOutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = subject,
            label = "Subject",
            onValueChanged = { newValue ->
                subject = newValue
                val actionTarget = EmailActionTarget(
                    recipient,
                    subject,
                    body
                )
                onActionTargetChange(actionTarget)
            }
        )
        SparkOutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = body,
            label = "Body",
            onValueChanged = { newValue ->
                body = newValue
                val actionTarget = EmailActionTarget(
                    recipient,
                    subject,
                    body
                )
                onActionTargetChange(actionTarget)
            }
        )
    }
}