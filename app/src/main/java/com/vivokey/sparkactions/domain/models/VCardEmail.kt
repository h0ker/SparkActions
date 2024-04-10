package com.vivokey.sparkactions.domain.models

data class VCardEmail(val emailAddress: String? = null) : VCardData {

    override var vCardDataType: VCardDataType = VCardDataType.EMAIL

    override fun getVCardEntry(): String {
        return "EMAIL;CHARSET=UTF-8:$emailAddress"
    }

    override fun describeContents(): String {
        return emailAddress ?: ""
    }
}
