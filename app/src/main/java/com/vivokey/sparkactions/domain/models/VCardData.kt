package com.vivokey.sparkactions.domain.models

interface VCardData {

    var vCardDataType: VCardDataType

    fun getVCardEntry(): String

    fun describeContents(): String
}