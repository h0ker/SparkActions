package com.vivokey.sparkactions.domain.models

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString

data class UrlActionTarget(
    val url: String
) : ActionTarget {

    override fun toString(): String {
        return url
    }

    override fun describeContents(): AnnotatedString {
        return buildAnnotatedString { append(url) }
    }
}
