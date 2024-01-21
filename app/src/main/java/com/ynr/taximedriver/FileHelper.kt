package com.ynr.taximedriver

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import com.google.android.gms.common.util.IOUtils
import java.io.*

object FileHelper {

    fun getFilePathFromURI(context: Context, contentUri: Uri?): String? {
        //copy file and send new file path
        val fileName = getFileName(contentUri)
        if (!TextUtils.isEmpty(fileName)) {
            val copyFile = File(context.cacheDir.path + File.separator.toString() + fileName)
            copy(context, contentUri, copyFile)
            return copyFile.absolutePath
        }
        return null
    }

    fun getFileName(uri: Uri?): String? {
        if (uri == null) return null
        var fileName: String? = null
        val path = uri.path
        val cut = path!!.lastIndexOf('/')
        if (cut != -1) {
            fileName = path.substring(cut + 1)
        }
        return fileName
    }

    fun copy(context: Context, srcUri: Uri?, dstFile: File?) {
        try {
            val inputStream = context.contentResolver.openInputStream(srcUri!!) ?: return
            val outputStream: OutputStream = FileOutputStream(dstFile)
            IOUtils.copyStream(inputStream, outputStream)
            inputStream.close()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}