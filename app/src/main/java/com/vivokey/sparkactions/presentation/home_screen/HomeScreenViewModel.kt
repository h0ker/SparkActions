package com.vivokey.sparkactions.presentation.home_screen

import android.nfc.Tag
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.carbidecowboy.intra.di.NfcModule
import com.carbidecowboy.intra.domain.NfcAdapterController
import com.carbidecowboy.intra.domain.NfcController
import com.carbidecowboy.intra.domain.NfcViewModel
import com.carbidecowboy.intra.domain.OperationResult
import com.vivokey.sparkactions.R
import com.vivokey.sparkactions.data.ActionDatabase
import com.vivokey.sparkactions.domain.models.Action
import com.vivokey.sparkactions.domain.models.RedirectApiService
import com.vivokey.sparkactions.domain.models.request.GetRedirectRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    nfcAdapterController: NfcAdapterController,
    nfcControllerFactory: NfcModule.NfcControllerFactory,
    private val redirectApiService: RedirectApiService,
    private val actionDatabase: ActionDatabase
): NfcViewModel(nfcAdapterController, nfcControllerFactory) {

    companion object {
        const val NEW = "new"
    }

    private val _isLoading = mutableStateOf(false)
    val isLoading: Boolean
        get() { return _isLoading.value }

    private val _toastChannel = Channel<Int>()
    val toastChannel = _toastChannel.receiveAsFlow()

    private val _getResult: MutableState<String?> = mutableStateOf(null)
    val getResult: String?
        get() { return _getResult.value }

    private val _currentAction: MutableState<Action?> = mutableStateOf(null)
    var currentAction: Action?
        get() { return _currentAction.value }
        set(value) { _currentAction.value = value }

    private val _actions = MutableStateFlow<List<Action>>(emptyList())
    val actions: StateFlow<List<Action>> = _actions.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            actionDatabase.actionDao().getAllActions().collect { actionList ->
                _actions.value = actionList
            }
        }
    }

    fun removeAction(action: Action) {
        viewModelScope.launch(Dispatchers.IO) {
            actionDatabase.actionDao().deleteById(action.id)
        }
    }

    override fun onNfcTagDiscovered(tag: Tag, nfcController: NfcController) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            when (val result = nfcController.getVivokeyJwt(tag)) {
                is OperationResult.Success -> {
                    val getRedirectRequest = GetRedirectRequest(
                        jwt = result.data
                    )
                    val response = redirectApiService.getRedirect(getRedirectRequest)
                    _getResult.value = response.body()?.result
                    if (response.body()?.target != null) {
                        response.body()?.toAction()?.let {
                            _currentAction.value = it
                        }
                    } else {
                        _currentAction.value = null
                    }
                }

                is OperationResult.Failure -> {
                    _toastChannel.send(R.string.error_getting_spark_info)
                }
            }
            _isLoading.value = false
        }
    }
}