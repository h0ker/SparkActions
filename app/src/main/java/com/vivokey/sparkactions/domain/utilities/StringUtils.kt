package com.vivokey.sparkactions.domain.utilities

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.vivokey.sparkactions.domain.models.DigitalCardActionTarget
import com.vivokey.sparkactions.domain.models.EmailActionTarget
import java.net.URLDecoder

object StringUtils {



    fun String.validateAndParsePhoneNumber(): Pair<Boolean, String> {
        val phoneUtil = PhoneNumberUtil.getInstance()
        var parsedNumber = this.trim()
        val isValid: Boolean

        if (!this.startsWith("+")) {
            parsedNumber = "+$parsedNumber"
        }

        return try {
            val numberProto = phoneUtil.parse(parsedNumber, "US")
            isValid = phoneUtil.isValidNumber(numberProto)
            Pair(isValid, if (isValid) phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.E164) else parsedNumber)
        } catch (e: Exception) {
            Pair(false, parsedNumber)
        }
    }
}