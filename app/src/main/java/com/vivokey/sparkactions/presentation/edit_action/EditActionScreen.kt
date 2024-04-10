package com.vivokey.sparkactions.presentation.edit_action

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.PopupProperties
import androidx.constraintlayout.compose.ConstraintLayout
import com.vivokey.sparkactions.domain.models.Action
import com.vivokey.sparkactions.domain.models.ActionType
import com.vivokey.sparkactions.domain.models.EmailActionTarget
import com.vivokey.sparkactions.domain.models.PhoneActionTarget
import com.vivokey.sparkactions.domain.models.SMSActionTarget
import com.vivokey.sparkactions.domain.models.UrlActionTarget
import com.vivokey.sparkactions.presentation.components.AdditionalActionOptions
import com.vivokey.sparkactions.presentation.components.ScanDialog
import com.vivokey.sparkactions.presentation.components.SparkOutlinedTextField
import com.vivokey.sparkactions.presentation.edit_action.components.ActionInfo
import com.vivokey.sparkactions.presentation.edit_action.components.CurrentActionTarget
import com.vivokey.sparkactions.presentation.theme.SparkActionsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditActionScreen(
    viewModel: EditActionViewModel,
    saveOnExit: Boolean = true,
    selectedAction: Action?,
    onBackPressed: () -> Unit,
    popBackFromWrite: (Action) -> Unit
) {

    val context = LocalContext.current

    BackHandler {
        if (saveOnExit) {
            viewModel.saveAction()
        }
        onBackPressed()
    }

    LaunchedEffect(viewModel.messageChannel) {
        for (message in viewModel.messageChannel) {
            if (message == -1) {
                viewModel.action?.let {
                    popBackFromWrite(it)
                }
            } else {
                Toast.makeText(context, context.getString(message), Toast.LENGTH_LONG).show()
            }
        }
    }

    LaunchedEffect(selectedAction) {
        selectedAction?.let {
            viewModel.initialId = it.id
            viewModel.action = it
            viewModel.title = selectedAction.title
            viewModel.actionTarget = selectedAction.target
            viewModel.delay = selectedAction.delay
            viewModel.appendJwt = selectedAction.aj
            when (it.target) {
                is UrlActionTarget -> viewModel.selectedActionType = ActionType.URL
                is PhoneActionTarget -> viewModel.selectedActionType = ActionType.PHONE
                is EmailActionTarget -> viewModel.selectedActionType = ActionType.EMAIL
                is SMSActionTarget -> viewModel.selectedActionType = ActionType.SMS
            }
        }
    }

    SparkActionsTheme {
        Scaffold(
            contentColor = MaterialTheme.colorScheme.background,
            floatingActionButton = {
                FloatingActionButton(
                    modifier = Modifier.padding(top = 16.dp),
                    onClick = {
                        viewModel.onSaveSelected()
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Save Changes",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        ) { paddingValues ->

            Box(
                modifier = Modifier.fillMaxSize()
            ) {

                Column(
                    modifier = Modifier.padding(paddingValues)
                ) {

                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = "Edit Action",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 32.sp
                    )

                    SparkOutlinedTextField(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        value = viewModel.title,
                        label = "Action Title",
                        onValueChanged = { value: String ->
                            viewModel.title = value
                        }
                    )


                    ActionInfo(
                        selectedActionType = viewModel.selectedActionType,
                        existingActionTarget = viewModel.actionTarget,
                        onActionTypeSelected = { actionType ->
                            viewModel.selectedActionType = actionType
                        },
                        onActionTargetChange = { target ->
                            viewModel.actionTarget = target
                        }
                    )

                    AnimatedVisibility(visible = viewModel.selectedActionType == ActionType.URL) {
                        AdditionalActionOptions(
                            appendJwt = viewModel.appendJwt,
                            onAppendJwtChange = {
                                viewModel.appendJwt = it
                            },
                            delayText = viewModel.getDelayText(),
                            onDelayChange = {
                                viewModel.delay = it
                            }
                        )
                    }

                    CurrentActionTarget(
                        modifier = Modifier.padding(16.dp),
                        actionTarget = viewModel.actionTarget
                    )
                }

                ScanDialog(
                    isVisible = viewModel.readyToWrite,
                    isLoading = viewModel.isLoading,
                    onDismiss = {
                        viewModel.readyToWrite = false
                    }
                )
            }
        }
    }
}