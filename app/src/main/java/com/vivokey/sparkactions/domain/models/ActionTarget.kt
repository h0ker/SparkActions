package com.vivokey.sparkactions.domain.models

import androidx.compose.ui.text.AnnotatedString

interface ActionTarget {
    override fun toString(): String
    fun describeContents(): AnnotatedString
}
