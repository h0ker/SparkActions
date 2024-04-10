package com.vivokey.sparkactions.domain.models

data class VCardOrg(val org: String?): VCardData {

    override var vCardDataType = VCardDataType.ORG

    override fun getVCardEntry(): String {
        return "ORG;CHARSET=UTF-8:$org"
    }

    override fun describeContents(): String {
        return org ?: ""
    }
}