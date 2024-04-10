package com.vivokey.sparkactions.domain.models

data class VCardTitle(val title: String?): VCardData {

    override var vCardDataType = VCardDataType.TITLE

    override fun getVCardEntry(): String {
        return "TITLE;CHARSET=UTF-8:$title"
    }

    override fun describeContents(): String {
        return title ?: ""
    }
}