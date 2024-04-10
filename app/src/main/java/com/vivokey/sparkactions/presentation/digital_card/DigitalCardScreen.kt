package com.vivokey.sparkactions.presentation.digital_card

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vivokey.sparkactions.R
import com.vivokey.sparkactions.domain.models.Action
import com.vivokey.sparkactions.domain.models.DigitalCardActionTarget
import com.vivokey.sparkactions.domain.models.VCardDataType
import com.vivokey.sparkactions.domain.models.VCardEmail
import com.vivokey.sparkactions.domain.models.VCardPhoneNumber
import com.vivokey.sparkactions.domain.models.VCardUrl
import com.vivokey.sparkactions.presentation.components.ScanDialog
import com.vivokey.sparkactions.presentation.digital_card.components.ChooseFieldDialog
import com.vivokey.sparkactions.presentation.digital_card.components.VCardDataField
import com.vivokey.sparkactions.presentation.digital_card.components.VCardManagementCoreFields
import com.vivokey.sparkactions.presentation.theme.SparkActionsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DigitalCardScreen(
    viewModel: DigitalCardViewModel,
    saveOnExit: Boolean = true,
    selectedAction: Action?,
    navigateToImageCropper: () -> Unit,
    onBackPressed: () -> Unit,
    popBackFromWrite: (Action) -> Unit
) {
    val context = LocalContext.current
    val contentResolver = LocalContext.current.contentResolver
    val scrollState = rememberScrollState()

    BackHandler {
        if (viewModel.isEditingField || viewModel.isEditingCoreField) {
            viewModel.isEditingField = false
            viewModel.isEditingCoreField = false
        } else {
            if (saveOnExit) {
                viewModel.onSave()
            }
            onBackPressed()
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            viewModel.parseAndSetBitmap(it, contentResolver)
            navigateToImageCropper()
        }
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
        if (viewModel.action == null) {
            selectedAction?.let {
                viewModel.action = it
                viewModel.initialId = it.id
                (it.target as? DigitalCardActionTarget)?.let { card ->
                    viewModel.digitalCard = card
                }
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
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding()
                        .verticalScroll(scrollState)
                ) {
                    if (!viewModel.isEditingCoreField) {
                        Column {
                            Text(
                                modifier = Modifier.padding(
                                    top = 16.dp,
                                    start = 64.dp,
                                    end = 64.dp,
                                    bottom = 16.dp
                                ),
                                text = stringResource(id = R.string.nfc_sharing_desc),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    VCardManagementCoreFields(
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth(),
                        firstName = viewModel.digitalCard.firstName,
                        lastName = viewModel.digitalCard.lastName,
                        title = viewModel.digitalCard.title ?: "",
                        org = viewModel.digitalCard.org ?: "",
                        note = viewModel.digitalCard.note ?: "",
                        imageBitmap = viewModel.digitalCard.bitmap?.asImageBitmap(),
                        onFirstNameChange = { value ->
                            viewModel.digitalCard =
                                viewModel.digitalCard.copy(firstName = value)
                        },
                        onLastNameChange = { value ->
                            viewModel.digitalCard = viewModel.digitalCard.copy(lastName = value)
                        },
                        onTitleChange = { value ->
                            viewModel.digitalCard = viewModel.digitalCard.copy(title = value)
                        },
                        onOrgChange = { value ->
                            viewModel.digitalCard = viewModel.digitalCard.copy(org = value)
                        },
                        onNoteChange = { value ->
                            viewModel.digitalCard = viewModel.digitalCard.copy(note = value)
                        },
                        onImageSelected = {
                            pickImageLauncher.launch("image/*")
                        }
                    )

                    viewModel.digitalCard.vCardDataList.forEachIndexed { index, data ->

                        if (viewModel.digitalCard.vCardDataList.isNotEmpty()) {

                            lateinit var editValue: MutableState<String>
                            lateinit var icon: ImageVector
                            var keyboardType: KeyboardType = KeyboardType.Text

                            when (data.vCardDataType) {

                                VCardDataType.PHONE_NUMBER -> {
                                    val phoneData = data as? VCardPhoneNumber
                                    editValue = remember {
                                        mutableStateOf(
                                            phoneData?.phoneNumber ?: ""
                                        )
                                    }
                                    keyboardType = KeyboardType.Phone
                                    icon = Icons.Default.Phone
                                }

                                VCardDataType.EMAIL -> {
                                    val emailData = data as? VCardEmail
                                    editValue = remember {
                                        mutableStateOf(
                                            emailData?.emailAddress ?: ""
                                        )
                                    }
                                    keyboardType = KeyboardType.Email
                                    icon = Icons.Default.Email
                                }

                                VCardDataType.URL -> {
                                    val urlData = data as? VCardUrl
                                    editValue = remember { mutableStateOf(urlData?.url ?: "") }
                                    keyboardType = KeyboardType.Uri
                                    icon = Icons.Default.Share
                                }

                                else -> {}
                            }
                            VCardDataField(
                                value = editValue.value,
                                icon = icon,
                                onValueChange = { value ->
                                    editValue.value = value
                                    viewModel.editedIndex?.let { index ->
                                        when (data.vCardDataType) {
                                            VCardDataType.EMAIL -> {
                                                val newList = ArrayList(viewModel.digitalCard.vCardDataList)
                                                newList[index] = VCardEmail(value)
                                                viewModel.digitalCard = viewModel.digitalCard.copy(vCardDataList = newList)
                                            }

                                            VCardDataType.PHONE_NUMBER -> {
                                                val newList = ArrayList(viewModel.digitalCard.vCardDataList)
                                                newList[index] = VCardPhoneNumber(value)
                                                viewModel.digitalCard = viewModel.digitalCard.copy(vCardDataList = newList)
                                            }

                                            VCardDataType.URL -> {
                                                val newList = ArrayList(viewModel.digitalCard.vCardDataList)
                                                newList[index] = VCardUrl(value)
                                                viewModel.digitalCard = viewModel.digitalCard.copy(vCardDataList = newList)
                                            }

                                            else -> {}
                                        }
                                    }
                                },
                                onDeleteField = {
                                    viewModel.removeVCardData(index)
                                },
                                isSelected = index == viewModel.editedIndex,
                                keyboardType = keyboardType,
                                onFocusChange = {
                                    if (it) {
                                        viewModel.editedIndex = index
                                    } else {
                                        viewModel.editedIndex = null
                                    }
                                }
                            )
                        }
                    }

                    Card(
                        modifier = Modifier
                            .clickable {
                                viewModel.isEditingCoreField = false
                                viewModel.showVCardDataTypeDialog = true
                                viewModel.editedIndex = viewModel.digitalCard.vCardDataList.size - 1
                            }
                            .padding(
                                vertical = 8.dp,
                                horizontal = 16.dp
                            ),
                        colors = CardDefaults.cardColors(
                            contentColor = MaterialTheme.colorScheme.secondary
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                modifier = Modifier.size(48.dp),
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    if (viewModel.digitalCard.vCardDataList.isEmpty() && !viewModel.isEditingCoreField) {
                        Text(
                            modifier = Modifier.padding(vertical = 16.dp, horizontal = 64.dp),
                            text = stringResource(id = R.string.add_field_desc),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                ChooseFieldDialog(
                    showDialog = viewModel.showVCardDataTypeDialog,
                    onDismiss = {
                        viewModel.showVCardDataTypeDialog = false
                    },
                    onWebsiteSelected = {
                        viewModel.addVCardData(VCardUrl())
                    },
                    onEmailSelected = {
                        viewModel.addVCardData(VCardEmail())
                    },
                    onPhoneNumberSelected = {
                        viewModel.addVCardData(VCardPhoneNumber())
                    }
                )
                ScanDialog(
                    isVisible = viewModel.readyForWrite,
                    isLoading = viewModel.isLoading,
                    onDismiss = {
                        viewModel.readyForWrite = false
                    }
                )
            }
        }
    }
}