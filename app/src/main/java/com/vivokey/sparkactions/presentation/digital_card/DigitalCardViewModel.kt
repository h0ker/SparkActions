package com.vivokey.sparkactions.presentation.digital_card

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.hoker.intra.domain.NfcAdapterController
import com.hoker.intra.domain.NfcController
import com.hoker.intra.domain.NfcViewModel
import com.hoker.intra.domain.OperationResult
import com.vivokey.sparkactions.data.ActionDatabase
import com.vivokey.sparkactions.domain.models.Action
import com.vivokey.sparkactions.domain.models.DigitalCardActionTarget
import com.vivokey.sparkactions.domain.models.RedirectApiService
import com.vivokey.sparkactions.domain.models.VCardData
import com.vivokey.sparkactions.domain.models.request.SetRedirectRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.io.InputStream
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class DigitalCardViewModel @Inject constructor(
    nfcAdapterController: NfcAdapterController,
    private val redirectApiService: RedirectApiService,
    private val actionDatabase: ActionDatabase
): NfcViewModel(nfcAdapterController) {

    private val _digitalCard = mutableStateOf(
        DigitalCardActionTarget()
    )
    var digitalCard: DigitalCardActionTarget
        get() { return _digitalCard.value }
        set(value) { _digitalCard.value = value }

    private val _initialId: MutableState<String?> = mutableStateOf(null)
    var initialId: String?
        get() { return _initialId.value }
        set(value) { _initialId.value = value }

    private val _action: MutableState<Action?> = mutableStateOf(null)
    var action: Action?
        get() { return _action.value }
        set(value) { _action.value = value }

    private val _isEditingCoreField = mutableStateOf(false)
    var isEditingCoreField: Boolean
        get() { return _isEditingCoreField.value }
        set(value) { _isEditingCoreField.value = value }

    private val _isEditingField = mutableStateOf(false)
    var isEditingField: Boolean
        get() { return _isEditingField.value }
        set(value) { _isEditingField.value = value }

    private val _editedIndex: MutableState<Int?> = mutableStateOf(null)
    var editedIndex: Int?
        get() { return _editedIndex.value }
        set(value) { _editedIndex.value = value }

    private val _showVCardDataTypeDialog = mutableStateOf(false)
    var showVCardDataTypeDialog: Boolean
        get() { return _showVCardDataTypeDialog.value }
        set(value) { _showVCardDataTypeDialog.value = value }

    private val _readyForWrite = mutableStateOf(false)
    var readyForWrite: Boolean
        get() { return _readyForWrite.value }
        set(value) { _readyForWrite.value = value }

    private val _isLoading = mutableStateOf(false)
    var isLoading: Boolean
        get() { return _isLoading.value }
        set(value) { _isLoading.value = value }

    val messageChannel = Channel<Int>(Channel.CONFLATED)

    fun parseAndSetBitmap(
        uri: Uri,
        contentResolver: ContentResolver,
        maxSize: Int = 1000
    ) {

        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, options)
        }

        val aspectRatio: Float = options.outWidth.toFloat() / options.outHeight.toFloat()
        val (newWidth, newHeight) = if (aspectRatio > 1) {
            maxSize to (maxSize / aspectRatio).roundToInt()
        } else {
            (maxSize * aspectRatio).roundToInt() to maxSize
        }

        options.apply {
            inSampleSize = calculateInSampleSize(this, newWidth, newHeight)
            inJustDecodeBounds = false
            inPreferredConfig = Bitmap.Config.RGB_565
        }

        val bitmap = contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, options)
        }

        bitmap?.let { originalBitmap ->
            val inputStreamForExif: InputStream? = contentResolver.openInputStream(uri)
            inputStreamForExif?.let { stream ->
                val exif = ExifInterface(stream)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                val matrix = Matrix()

                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                    else -> {}
                }

                _digitalCard.value = _digitalCard.value.copy(
                    bitmap = Bitmap.createBitmap(
                        originalBitmap,
                        0,
                        0,
                        originalBitmap.width,
                        originalBitmap.height,
                        matrix,
                        true
                    )
                )
            }
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    fun cropImage(
        topLeft: Offset,
        bottomRight: Offset,
        displayWidth: Float,
        displayHeight: Float,
        paddingX: Float,
        paddingY: Float
    ): Boolean {

        _digitalCard.value.bitmap?.let { bitmap ->

            val widthScalingFactor = bitmap.width.toFloat() / displayWidth
            val heightScalingFactor = bitmap.height.toFloat() / displayHeight

            val originalTopLeftX = (topLeft.x - paddingX) * widthScalingFactor
            val originalTopLeftY = (topLeft.y - paddingY) * heightScalingFactor
            val originalBottomRightX = (bottomRight.x - paddingX) * widthScalingFactor
            val originalBottomRightY = (bottomRight.y - paddingY) * heightScalingFactor
            val originalWidth = (originalBottomRightX - originalTopLeftX).toInt()
            val originalHeight = (originalBottomRightY - originalTopLeftY).toInt()

            return try {
                _digitalCard.value = _digitalCard.value.copy(
                    bitmap = Bitmap.createBitmap(
                        bitmap,
                        originalTopLeftX.toInt(),
                        originalTopLeftY.toInt(),
                        originalWidth,
                        originalHeight
                    )
                )
                true
            } catch (e: Exception) {
                false
            }
        }
        return false
    }

    fun calculateDisplayDimensions(
        containerWidth: Float,
        containerHeight: Float
    ): Pair<Float, Float> {
        _digitalCard.value.bitmap?.let { bitmap ->
            val imageAspect = bitmap.width / bitmap.height.toFloat()
            val containerAspect = containerWidth / containerHeight

            return if (imageAspect > containerAspect) {
                val displayHeight = containerWidth / imageAspect
                Pair(containerWidth, displayHeight)
            } else {
                val displayWidth = containerHeight * imageAspect
                Pair(displayWidth, containerHeight)
            }
        }
        return Pair(0f, 0f)
    }

    fun addVCardData(data: VCardData) {
        val newList = _digitalCard.value.vCardDataList + data
        _digitalCard.value = _digitalCard.value.copy(
            vCardDataList = newList
        )
        _editedIndex.value = newList.size - 1
    }

    fun removeVCardData(index: Int) {
        val newList = ArrayList(_digitalCard.value.vCardDataList)
        newList.removeAt(index)
        _digitalCard.value = _digitalCard.value.copy(
            vCardDataList = newList
        )
    }

    fun sendMessage(message: Int) {
        viewModelScope.launch {
            messageChannel.send(message)
        }
    }

    fun onSaveSelected() {
        viewModelScope.launch(Dispatchers.IO) {
            _digitalCard.value.firstName?.let { first ->
                _digitalCard.value.lastName?.let { last ->
                    _readyForWrite.value = true
                    _action.value = Action(
                        title = "$first $last",
                        target = _digitalCard.value
                    )
                }
            }
        }
    }

    fun onSave() {
        viewModelScope.launch(Dispatchers.IO) {
            _digitalCard.value.firstName?.let { first ->
                _digitalCard.value.lastName?.let { last ->
                    _action.value = Action(
                        title = "$first $last",
                        target = _digitalCard.value
                    )
                    _action.value?.let { validAction ->
                        _initialId.value?.let { id ->
                            if (id == validAction.id) {
                                return@launch
                            }
                            actionDatabase.actionDao().deleteById(id)
                        }
                        actionDatabase.actionDao().insertAction(validAction)
                    }
                }
            }
        }
    }

    override fun onNfcTagDiscovered(tag: Tag, nfcController: NfcController) {
        if (_readyForWrite.value) {
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
                                val targetString = validAction.target.toString()
                                println(targetString)
                                val setRedirectRequest = SetRedirectRequest(
                                    jwt = result.data,
                                    title = validAction.title,
                                    target = validAction.target.toString(),
                                    delay = 0,
                                    aj = false,
                                    url = url
                                )

                                val setRedirectRequestJson = Gson().toJson(setRedirectRequest)

                                val setResult =
                                    redirectApiService.setRedirect(setRedirectRequest)

                                Log.i(
                                    this@DigitalCardViewModel::class.toString(),
                                    setResult.toString()
                                )
                                Log.i(
                                    this@DigitalCardViewModel::class.toString(),
                                    setRedirectRequestJson
                                )
                                Log.i(
                                    this@DigitalCardViewModel::class.toString(),
                                    setRedirectRequest.toString()
                                )

                                _action.value = Action(
                                    title = validAction.title,
                                    target = validAction.target,
                                    delay = 0,
                                    aj = false
                                )

                                _action.value?.let { finalAction ->
                                    _initialId.value?.let { id ->
                                        actionDatabase.actionDao().deleteById(id)
                                    }
                                    actionDatabase.actionDao().insertAction(finalAction)
                                }

                                _isLoading.value = false
                                _readyForWrite.value = false

                                messageChannel.send(-1)
                            }

                            is OperationResult.Failure -> {
                                _isLoading.value = false
                            }
                        }
                    }
                }

                } catch (e: Exception) {
                    _isLoading.value = false
                }
            }
        }
    }
}