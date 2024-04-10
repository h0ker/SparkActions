package com.vivokey.sparkactions.presentation.edit_action.components

import android.webkit.URLUtil
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vivokey.sparkactions.domain.models.UrlActionTarget
import com.vivokey.sparkactions.presentation.components.SparkOutlinedTextField

@Composable
fun UrlActionInfo(
    existingUrl: String? = null,
    onUrlChange: (UrlActionTarget?) -> Unit
) {

    var url by remember { mutableStateOf("") }
    var finalUrl by remember { mutableStateOf("") }

    LaunchedEffect(existingUrl) {
        url = existingUrl?.removePrefix("https://") ?: ""
    }

    SparkOutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        value = url,
        label = "URL",
        onValueChanged = { newValue ->
            url = newValue
            if (url.isNotEmpty()) {
                val stagedUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    "https://$url"
                } else {
                    url
                }

                if (URLUtil.isValidUrl(stagedUrl)) {
                    finalUrl = stagedUrl
                    val actionTarget = UrlActionTarget(finalUrl)
                    onUrlChange(actionTarget)
                }
            } else {
                onUrlChange(null)
            }
        }
    )
}