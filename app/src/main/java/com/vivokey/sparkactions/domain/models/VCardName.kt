package com.vivokey.sparkactions.domain.models

data class VCardName(
    val firstName: String?,
    val lastName: String?
): VCardData {

    override var vCardDataType = VCardDataType.NAME

    override fun getVCardEntry(): String {
        return buildString {
            append("N;CHARSET=UTF-8:")
            lastName?.let { value ->
                append("$value;")
            }
            firstName?.let { value ->
                append("$value;")
            }
        }
    }

    override fun describeContents(): String {
        return "$firstName $lastName"
    }
}