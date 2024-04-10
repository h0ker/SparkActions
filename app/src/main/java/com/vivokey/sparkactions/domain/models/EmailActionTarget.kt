package com.vivokey.sparkactions.domain.models

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import java.net.URLEncoder

data class EmailActionTarget(
    val recipient: String,
    val subject: String,
    val body: String
) : ActionTarget {

    override fun toString(): String {
        val encodedSubject = URLEncoder.encode(this.subject, "UTF-8")
        val encodedBody = URLEncoder.encode(this.body, "UTF-8")
        return "mailto:${this.recipient}?subject=$encodedSubject&body=$encodedBody"
    }

    override fun describeContents(): AnnotatedString {
        return buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("Recipient: ")
            }
            appendLine(recipient)
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("Subject: ")
            }
            appendLine(subject)

            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("Body: ")
            }
            append(body)
        }
    }
}
