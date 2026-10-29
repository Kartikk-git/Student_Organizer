package com.example.studentorganizer.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

data class AttachmentResult(
    val filePath: String,
    val fileName: String,
    val fileType: String // "IMAGE" or "PDF"
)

object AttachmentUtils {

    fun saveAttachmentFromUri(context: Context, sourceUri: Uri): AttachmentResult? {
        return try {
            val contentResolver = context.contentResolver
            var fileName = "attachment_${System.currentTimeMillis()}"

            // Query original filename
            contentResolver.query(sourceUri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1 && cursor.moveToFirst()) {
                    val name = cursor.getString(nameIndex)
                    if (!name.isNullOrBlank()) {
                        fileName = name
                    }
                }
            }

            val mimeType = contentResolver.getType(sourceUri) ?: ""
            val isPdf = mimeType.contains("pdf", ignoreCase = true) || fileName.endsWith(".pdf", ignoreCase = true)
            val fileType = if (isPdf) "PDF" else "IMAGE"

            // Target directory in app private files
            val attachmentsDir = File(context.filesDir, "note_attachments")
            if (!attachmentsDir.exists()) {
                attachmentsDir.mkdirs()
            }

            val safeFileName = "${System.currentTimeMillis()}_${fileName.replace("[^a-zA-Z0-9._-]".toRegex(), "_")}"
            val destFile = File(attachmentsDir, safeFileName)

            contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }

            AttachmentResult(
                filePath = destFile.absolutePath,
                fileName = fileName,
                fileType = fileType
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun openAttachment(context: Context, filePath: String, fileType: String) {
        try {
            val file = File(filePath)
            if (!file.exists()) {
                Toast.makeText(context, "Attachment file not found", Toast.LENGTH_SHORT).show()
                return
            }

            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            val mime = if (fileType.equals("PDF", ignoreCase = true) || file.name.endsWith(".pdf", ignoreCase = true)) {
                "application/pdf"
            } else {
                "image/*"
            }

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(contentUri, mime)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(Intent.createChooser(intent, "Open with").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "No app available to open this file", Toast.LENGTH_SHORT).show()
        }
    }
}
