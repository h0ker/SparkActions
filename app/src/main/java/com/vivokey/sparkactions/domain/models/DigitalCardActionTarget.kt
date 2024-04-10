package com.vivokey.sparkactions.domain.models

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPOutputStream

data class DigitalCardActionTarget(
    val firstName: String? = null,
    val lastName: String? = null,
    val title: String? = null,
    val org: String? = null,
    val note: String? = null,
    val vCardDataList: List<VCardData> = listOf(),
    val bitmap: Bitmap? = null
): ActionTarget {

    companion object {

        private const val TARGET_SIZE = 8192

        fun jpegCompressBitmapToBase64(bitmap: Bitmap, quality: Int): String {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            return Base64.encodeToString(byteArray, Base64.NO_WRAP)
        }

        private fun gzipCompressString(input: String): String {
            val bos = ByteArrayOutputStream()
            val gzip: GZIPOutputStream?

            try {
                gzip = GZIPOutputStream(bos)
                gzip.write(input.toByteArray())
                gzip.close()

                val compressed = bos.toByteArray()
                return Base64.encodeToString(compressed, Base64.NO_WRAP)
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
        }
    }

    override fun toString(): String {

        var quality = 100
        var scaledWidth = bitmap?.width ?: 0
        var scaledHeight = bitmap?.height ?: 0
        var compressedResult: String

        while(true) {
            val vCardString = buildString {
                appendLine("BEGIN:VCARD")
                appendLine("VERSION:2.1")
                appendLine("N;CHARSET=UTF-8:${lastName?.trimEnd(' ')};${firstName?.trimEnd(' ')};")
                if (title != null) appendLine("TITLE;CHARSET=UTF-8:${title.trimEnd(' ')}")
                if (org != null) appendLine("ORG;CHARSET=UTF-8:${org.trimEnd(' ')}")
                if (note != null) appendLine("NOTE;CHARSET=UTF-8:${note.trimEnd(' ')}")
                vCardDataList.forEach {
                    appendLine(it.getVCardEntry())
                }
                bitmap?.let {
                    val tempBitmap = Bitmap.createScaledBitmap(it, scaledWidth, scaledHeight, false)
                    appendLine(
                        "PHOTO;JPEG;ENCODING=BASE64:${
                            jpegCompressBitmapToBase64(
                                tempBitmap,
                                quality
                            )
                        }"
                    )
                }
                appendLine("END:VCARD")
            }
            compressedResult = gzipCompressString(vCardString)

            val size = compressedResult.toByteArray(Charsets.UTF_8).size
            if (size < TARGET_SIZE) {
                break
            }

            quality = (quality * 0.9).toInt()
            scaledWidth = (scaledWidth * 0.9).toInt()
            scaledHeight = (scaledHeight * 0.9).toInt()
        }

        return "https://vivokey.social/#$compressedResult"
    }

    //not needed
    override fun describeContents(): AnnotatedString {
        return buildAnnotatedString {}
    }
}