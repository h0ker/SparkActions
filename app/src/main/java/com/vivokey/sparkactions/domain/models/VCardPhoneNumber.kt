package com.vivokey.sparkactions.domain.models

data class VCardPhoneNumber(val phoneNumber: String? = null) : VCardData {

    override var vCardDataType: VCardDataType = VCardDataType.PHONE_NUMBER

    override fun getVCardEntry(): String {
        return "TEL;CHARSET=UTF-8:$phoneNumber"
    }

    override fun describeContents(): String {
        return phoneNumber ?: ""
    }
}
