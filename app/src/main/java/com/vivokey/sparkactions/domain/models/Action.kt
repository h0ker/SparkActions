package com.vivokey.sparkactions.domain.models

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.webkit.URLUtil
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Phone
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.carbidecowboy.intra.domain.OperationResult
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.zip.GZIPInputStream

@Entity(tableName = "actions")
data class Action(
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "action_target")
    val target: ActionTarget,
    @ColumnInfo(name = "delay")
    val delay: Int = 0,
    @ColumnInfo(name = "count")
    val count: Int = 0,
    @ColumnInfo(name = "aj")
    val aj: Boolean = false,
    @ColumnInfo(name = "last_seen")
    val lastSeen: String? = null
) {

    @PrimaryKey
    var id: String = generateHash()

    private fun generateHash(): String {
        val input = "$title$target$delay$aj$"
        return input.toByteArray().toHash()
    }

    companion object {

        private const val TEL_PREFIX = "tel:"
        private const val SMS_PREFIX = "sms:"
        private const val EMAIL_PREFIX = "mailto:"
        private const val DIGITAL_CARD_PREFIX = "https://vivokey.social/#"

        private const val VCARD_NAME_PREFIX = "N;CHARSET=UTF-8:"
        private const val VCARD_TITLE_PREFIX = "TITLE;CHARSET=UTF-8:"
        private const val VCARD_ORG_PREFIX = "ORG;CHARSET=UTF-8:"
        private const val VCARD_NOTE_PREFIX = "NOTE;CHARSET=UTF-8:"
        private const val VCARD_EMAIL_PREFIX = "EMAIL;CHARSET=UTF-8:"
        private const val VCARD_PHONE_PREFIX = "TEL;CHARSET=UTF-8:"
        private const val VCARD_URL_PREFIX = "URL;CHARSET=UTF-8:"
        private const val VCARD_IMAGE_PREFIX = "PHOTO;JPEG;ENCODING=BASE64:"
        private const val NULL = "null"

        fun ByteArray.toHash(): String {
            return MessageDigest.getInstance("SHA-256")
                .digest(this)
                .joinToString("") { "%02x".format(it)}
        }

        fun String.toActionTarget(): ActionTarget? {
            when {
                this.startsWith(TEL_PREFIX) -> {
                    return PhoneActionTarget(this.removePrefix(TEL_PREFIX))
                }
                this.startsWith(SMS_PREFIX) -> {
                    return this.parseSmsHtmlToActionTarget()
                }
                this.startsWith(EMAIL_PREFIX) -> {
                    return this.parseEmailHtmlToActionTarget()
                }
                this.startsWith(DIGITAL_CARD_PREFIX) -> {
                    return this.parseDigitalCardUrlToActionTarget()
                }
                else -> {
                    return if (URLUtil.isValidUrl(this)) {
                        UrlActionTarget(this)
                    } else {
                        null
                    }
                }
            }
        }

        private fun parseName(name: String): Pair<String?, String?> {
            val parts = name.split(";")
            val lastName = parts.getOrNull(0)?.takeIf { it.isNotEmpty() }
            val firstName = parts.getOrNull(1)?.takeIf { it.isNotEmpty() }
            return Pair(firstName, lastName)
        }

        private fun base64ToBitmap(base64: String): Bitmap? {
            val decodedBytes = Base64.decode(base64, Base64.NO_WRAP)
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        }

        private fun String.parseEmailHtmlToActionTarget(): EmailActionTarget? {
            if (!this.startsWith(EMAIL_PREFIX)) {
                return null // Not a valid mailto URL
            }

            val parts = this.removePrefix("mailto:").split("?")
            val recipient = parts[0]

            val queryParams =
                parts.getOrNull(1)?.split("&") ?: return EmailActionTarget(recipient, "", "")

            var subject = ""
            var body = ""

            for (param in queryParams) {
                val keyValue = param.split("=")
                if (keyValue.size == 2) {
                    when (keyValue[0]) {
                        "subject" -> subject = URLDecoder.decode(keyValue[1], "UTF-8")
                        "body" -> body = URLDecoder.decode(keyValue[1], "UTF-8")
                    }
                }
            }

            return EmailActionTarget(recipient, subject, body)
        }

        private fun String.parseSmsHtmlToActionTarget(): SMSActionTarget? {
            if (!this.startsWith(SMS_PREFIX)) {
                return null // Not a valid sms href
            }

            val parts = this.removePrefix(SMS_PREFIX).split("?")
            val recipientNumber = parts[0]

            val queryParams = parts.getOrNull(1)?.split("&") ?: return SMSActionTarget(recipientNumber, "")

            var message = ""

            for (param in queryParams) {
                val keyValue = param.split("=")
                if (keyValue.size == 2 && keyValue[0] == "body") {
                    message = URLDecoder.decode(keyValue[1], "UTF-8")
                    break
                }
            }

            return SMSActionTarget(recipientNumber, message)
        }

        private fun String.toDigitalCard(): OperationResult<DigitalCardActionTarget> {
            var businessCard = DigitalCardActionTarget()

            val lines = this.lines()
            lines.forEach { line ->

                if (line.startsWith(VCARD_NAME_PREFIX)) {
                    val (firstName, lastName) = parseName(line.substring(VCARD_NAME_PREFIX.length))
                    firstName?.let {
                        businessCard = businessCard.copy(firstName = it)
                    }
                    lastName?.let {
                        businessCard = businessCard.copy(lastName = it)
                    }
                }

                if (line.startsWith(VCARD_TITLE_PREFIX)) {
                    val title = line.substring(VCARD_TITLE_PREFIX.length)
                    businessCard = businessCard.copy(title = if (title == NULL) "" else title)
                }

                if (line.startsWith(VCARD_ORG_PREFIX)) {
                    val org = line.substring(VCARD_ORG_PREFIX.length)
                    businessCard = businessCard.copy(org = if (org == NULL) "" else org)
                }

                if (line.startsWith(VCARD_NOTE_PREFIX)) {
                    val note = line.substring(VCARD_NOTE_PREFIX.length)
                    businessCard = businessCard.copy(note = if (note == NULL) "" else note)
                }

                if (line.startsWith(VCARD_EMAIL_PREFIX)) {
                    val newList = businessCard.vCardDataList + VCardEmail(line.substring(VCARD_EMAIL_PREFIX.length))
                    businessCard = businessCard.copy(vCardDataList = newList)
                }

                if (line.startsWith(VCARD_PHONE_PREFIX)) {
                    val newList = businessCard.vCardDataList + VCardPhoneNumber(line.substring(VCARD_PHONE_PREFIX.length))
                    businessCard = businessCard.copy(vCardDataList = newList)
                }

                if (line.startsWith(VCARD_URL_PREFIX)) {
                    val newList = businessCard.vCardDataList + VCardUrl(line.substring(VCARD_URL_PREFIX.length))
                    businessCard = businessCard.copy(vCardDataList = newList)
                }

                if (line.startsWith(VCARD_IMAGE_PREFIX)) {
                    val image = base64ToBitmap(line.substring(VCARD_IMAGE_PREFIX.length))
                    image?.let {
                        businessCard = businessCard.copy(bitmap = it)
                    }
                }
            }
            return OperationResult.Success(businessCard)
        }

        private fun String.parseDigitalCardUrlToActionTarget(): DigitalCardActionTarget? {
            if (!this.startsWith(DIGITAL_CARD_PREFIX)) {
                return null
            }

            try {
                val urlData = this.substring(DIGITAL_CARD_PREFIX.length)
                val decodedCompressedPayload = Base64.decode(urlData, Base64.NO_WRAP)
                val byteArrayInputStream = ByteArrayInputStream(decodedCompressedPayload)
                val gzipInputStream = GZIPInputStream(byteArrayInputStream)
                val byteArrayOutputStream = ByteArrayOutputStream()
                val buffer = ByteArray(32000)
                var len: Int
                while (gzipInputStream.read(buffer).also { len = it } > 0) {
                    byteArrayOutputStream.write(buffer, 0, len)
                }

                val decompressedVCard =
                    String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8)

                gzipInputStream.close()
                byteArrayOutputStream.close()

                return when (val result = decompressedVCard.toDigitalCard()) {
                    is OperationResult.Success -> {
                        result.data
                    }

                    is OperationResult.Failure -> {
                        null
                    }
                }
            } catch (e: Exception) {
                return null
            }
        }
    }

    fun getIcon(): Any? {
        return when (target) {
            is UrlActionTarget -> Icons.Default.Link
            is PhoneActionTarget -> Icons.Default.Phone
            is EmailActionTarget -> Icons.Default.Email
            is SMSActionTarget -> Icons.Default.Message
            is DigitalCardActionTarget -> target.bitmap
            else -> null
        }
    }
}