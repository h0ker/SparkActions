package com.vivokey.sparkactions.presentation.edit_action.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vivokey.sparkactions.domain.models.ActionTarget
import com.vivokey.sparkactions.domain.models.ActionType
import com.vivokey.sparkactions.domain.models.EmailActionTarget
import com.vivokey.sparkactions.domain.models.PhoneActionTarget
import com.vivokey.sparkactions.domain.models.SMSActionTarget
import com.vivokey.sparkactions.domain.models.UrlActionTarget

@Composable
fun ActionInfo(
    selectedActionType: ActionType,
    existingActionTarget: ActionTarget?,
    onActionTypeSelected: (ActionType) -> Unit,
    onActionTargetChange: (ActionTarget?) -> Unit
) {

    Card(
        modifier = Modifier.padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            contentColor = MaterialTheme.colorScheme.secondary
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MultiSelector(
                modifier = Modifier.height(38.dp),
                options = ActionType.values().toList().map { it.value },
                selectedOption = selectedActionType.value,
                onOptionSelect = { newValue ->
                    ActionType.fromString(newValue)?.let {
                        onActionTypeSelected(it)
                        onActionTargetChange(null)
                    }
                }
            )

            when (selectedActionType) {
                ActionType.URL -> {
                    val existingUrl = (existingActionTarget as? UrlActionTarget)?.url
                    UrlActionInfo(
                        existingUrl
                    ) { actionTarget ->
                        onActionTargetChange(actionTarget)
                    }
                }
                ActionType.PHONE -> {
                    val existingPhoneNumber = (existingActionTarget as? PhoneActionTarget)?.phoneNumber
                    PhoneActionInfo(
                        existingPhoneNumber
                    ) { actionTarget ->
                        onActionTargetChange(actionTarget)
                    }
                }
                ActionType.EMAIL -> {
                    EmailActionInfo(
                        existingActionTarget as? EmailActionTarget
                    ) { actionTarget ->
                        onActionTargetChange(actionTarget)
                    }
                }
                ActionType.SMS -> {
                    SMSActionInfo(
                        existingActionTarget as? SMSActionTarget
                    ) { actionTarget ->
                        onActionTargetChange(actionTarget)
                    }
                }
            }
        }
    }
}