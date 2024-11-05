package com.vivokey.sparkactions.presentation.edit_action

import android.nfc.Tag
import android.nfc.tech.Ndef
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.hoker.intra.domain.NfcAdapterController
import com.hoker.intra.domain.NfcController
import com.hoker.intra.domain.NfcViewModel
import com.hoker.intra.domain.OperationResult
import com.vivokey.sparkactions.R
import com.vivokey.sparkactions.data.ActionDatabase
import com.vivokey.sparkactions.domain.models.Action
import com.vivokey.sparkactions.domain.models.ActionTarget
import com.vivokey.sparkactions.domain.models.ActionType
import com.vivokey.sparkactions.domain.models.EmailActionTarget
import com.vivokey.sparkactions.domain.models.PhoneActionTarget
import com.vivokey.sparkactions.domain.models.RedirectApiService
import com.vivokey.sparkactions.domain.models.UrlActionTarget
import com.vivokey.sparkactions.domain.models.request.SetRedirectRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditActionViewModel @Inject constructor(
    nfcAdapterController: NfcAdapterController,
    private val redirectApiService: RedirectApiService,
    private val actionDatabase: ActionDatabase
) : NfcViewModel(nfcAdapterController) {

    private val _initialId: MutableState<String?> = mutableStateOf(null)
    var initialId: String?
        get() { return _initialId.value }
        set(value) { _initialId.value = value }

    private val _title = mutableStateOf("")
    var title: String
        get() { return _title.value }
        set(value) { _title.value = value }

    private val _selectedActionType = mutableStateOf(ActionType.URL)
    var selectedActionType: ActionType
        get() { return _selectedActionType.value }
        set(value) { _selectedActionType.value = value }

    private val _action: MutableState<Action?> = mutableStateOf(null)
    var action: Action?
        get() { return _action.value }
        set(value) { _action.value = value }

    private val _actionTarget: MutableState<ActionTarget?> = mutableStateOf(null)
    var actionTarget: ActionTarget?
        get() { return _actionTarget.value }
        set(value) { _actionTarget.value = value }

    private val _appendJwt = mutableStateOf(false)
    var appendJwt: Boolean
        get() { return _appendJwt.value }
        set(value) { _appendJwt.value = value }

    private val _delay = mutableIntStateOf(0)
    var delay: Int
        get() { return _delay.intValue }
        set(value) { _delay.intValue = value }

    private val _readyToWrite = mutableStateOf(false)
    var readyToWrite: Boolean
        get() { return _readyToWrite.value }
        set(value) { _readyToWrite.value = value }

    private val _isLoading = mutableStateOf(false)
    var isLoading: Boolean
        get() { return _isLoading.value }
        set(value) { _isLoading.value = value }

    val messageChannel = Channel<Int>(Channel.CONFLATED)

    fun saveAction() {
        viewModelScope.launch(Dispatchers.IO) {
            if (_actionTarget.value == null) {
                return@launch
            }
            _actionTarget.value?.let { target ->

                val finalTitle = _title.value.ifEmpty {
                    when (target) {
                        is UrlActionTarget -> "Website"
                        is EmailActionTarget -> "Email"
                        is PhoneActionTarget -> "Phone Number"
                        else -> {
                            ""
                        }
                    }
                }

                _action.value = Action(
                    title = finalTitle,
                    target = target,
                    delay = _delay.intValue,
                    aj = _appendJwt.value
                )

                _action.value?.let { validAction ->
                    _action.value = validAction.copy(title = finalTitle)
                    _initialId.value?.let { id ->
                        actionDatabase.actionDao().deleteById(id)
                    }
                    actionDatabase.actionDao().insertAction(validAction)
                }
            }
        }
    }

    override fun onNfcTagDiscovered(tag: Tag, nfcController: NfcController) {
        if (_readyToWrite.value) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    _isLoading.value = true
                    _action.value?.let { validAction ->

                        val ndef = Ndef.get(tag)
                        ndef.connect()
                        val payload = ndef.ndefMessage.records[0].payload
                        val url = String(payload.copyOfRange(1, payload.size))
                        ndef.close()

                        nfcController.withConnection(tag) {
                            when (val result = nfcController.getVivokeyJwt(tag)) {
                                is OperationResult.Success -> {
                                    val setRedirectRequest = SetRedirectRequest(
                                        jwt = result.data,
                                        title = validAction.title,
                                        target = validAction.target.toString(),
                                        delay = _delay.intValue,
                                        aj = _appendJwt.value,
                                        url = url
                                    )

                                    val setRedirectRequestJson = Gson().toJson(setRedirectRequest)

                                    val setResult =
                                        redirectApiService.setRedirect(setRedirectRequest)

                                    Log.i(
                                        this@EditActionViewModel::class.toString(),
                                        setResult.toString()
                                    )
                                    Log.i(
                                        this@EditActionViewModel::class.toString(),
                                        setRedirectRequestJson
                                    )
                                    Log.i(
                                        this@EditActionViewModel::class.toString(),
                                        setRedirectRequest.toString()
                                    )

                                    saveAction()

                                    _isLoading.value = false
                                    _readyToWrite.value = false

                                    messageChannel.send(-1)
                                }

                                is OperationResult.Failure -> {
                                    _isLoading.value = false
                                    _readyToWrite.value = false
                                    messageChannel.send(R.string.action_write_failure)
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    _isLoading.value = false
                    _readyToWrite.value = false
                    messageChannel.send(R.string.action_write_failure)
                }
            }
        }
    }

    fun onSaveSelected() {
        if (_actionTarget.value == null) {
            return
        }
        _actionTarget.value?.let { target ->

            val finalTitle = _title.value.ifEmpty {
                when (_actionTarget.value) {
                    is UrlActionTarget -> "Website"
                    is EmailActionTarget -> "Email"
                    is PhoneActionTarget -> "Phone Number"
                    else -> { "" }
                }
            }

            _action.value = Action(
                title = finalTitle,
                target = target,
                delay = _delay.intValue,
                aj = _appendJwt.value
            )

            _readyToWrite.value = true
        }
    }

    fun getDelayText(): String {
        return if (_delay.intValue == 0) {
            "No delay"
        } else {
            "${_delay.intValue} seconds"
        }
    }
}