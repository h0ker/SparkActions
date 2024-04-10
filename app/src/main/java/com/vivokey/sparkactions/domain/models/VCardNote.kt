package com.vivokey.sparkactions.domain.models

data class VCardNote(val note: String?) : VCardData {

    override var vCardDataType = VCardDataType.NOTE

    override fun getVCardEntry(): String {
        return "NOTE;CHARSET=UTF-8:$note"
    }

    override fun describeContents(): String {
        return note ?: ""
    }
}