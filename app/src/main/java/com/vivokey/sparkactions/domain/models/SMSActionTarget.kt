package com.vivokey.sparkactions.domain.models

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import java.net.URLEncoder

data class SMSActionTarget(
    val recipientNumber: String,
    val message: String
): ActionTarget {

    override fun toString(): String {
        val encodedMessage = URLEncoder.encode(message, "UTF-8")
        return "sms:$recipientNumber?body=$encodedMessage"
    }

    override fun describeContents(): AnnotatedString {
        return buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("Recipient: ")
            }
            appendLine(recipientNumber)
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("Message: ")
            }
            append(message)
        }
    }
}