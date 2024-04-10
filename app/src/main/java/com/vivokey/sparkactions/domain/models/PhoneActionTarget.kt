package com.vivokey.sparkactions.domain.models

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString

data class PhoneActionTarget(
    val phoneNumber: String
) : ActionTarget {

    override fun toString(): String {
        return "tel:$phoneNumber"
    }

    override fun describeContents(): AnnotatedString {
        return buildAnnotatedString { append(phoneNumber) }
    }
}
