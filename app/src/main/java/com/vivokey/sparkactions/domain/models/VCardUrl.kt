package com.vivokey.sparkactions.domain.models

data class VCardUrl(val url: String? = null) : VCardData {

    override var vCardDataType: VCardDataType = VCardDataType.URL

    //TODO: Clean this up later
    override fun getVCardEntry(): String {
        url?.let {
            val updatedUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
                "https://$url"
            } else {
                url
            }

            return "URL;CHARSET=UTF-8:$updatedUrl"
        }
        return ""
    }

    override fun describeContents(): String {
        return url ?: ""
    }
}
