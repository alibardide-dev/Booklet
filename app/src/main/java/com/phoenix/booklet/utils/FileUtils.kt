package com.phoenix.booklet.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.net.toUri
import com.phoenix.booklet.data.FileResult
import com.phoenix.booklet.data.Result
import java.io.File

fun saveUriAsPhoto(context: Context, uri: Uri?, name: String): FileResult {
    if (uri == null)
        return FileResult.Error("Uri is null the fuck am I gonna save?")
//    val file = File(context.filesDir, "$name.png")
//    val stream = FileOutputStream(file)

//        stream.write(uri.toString().toByteArray())
//        stream.close()
//        return FileResult.Success("${context.filesDir}/$name.png")
    try {
        val bitmap = if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        } else {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.setTargetSampleSize(1) // shrinking by
                decoder.isMutableRequired = true // this resolve the hardware type of bitmap problem
            }
        }
        val displayName = "$name.png" // Set the name of the image file here
        val path = "file://${context.filesDir}/$displayName"
        val fos = context.contentResolver.openOutputStream(path.toUri())!!
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.flush()
        fos.close()
        return FileResult.Success(path.substringAfter("com.phoenix.booklet/files/"))
    } catch (e: Exception) {
        e.printStackTrace()
        return FileResult.Error(e.message)
    }
}

fun getUriFromName(context: Context, name: String?): Uri? {
    if (name == null)
        return null
    return File(context.filesDir, name).toUri()
}

fun deleteFileFromPath(path: String?) {
    if (path == null)
        return
    val file = File(path)
    if (file.exists())
        file.delete()
}

fun deleteAllPictures(context: Context): Result {
    try {
        val directory = File(context.filesDir, "")
        val files = directory.listFiles()
        files?.forEach {
            if (it.exists())
                if (!it.delete())
                    return Result.Error()
        }
        return Result.Success
    } catch (e: Exception) {
        e.printStackTrace()
        return Result.Error(e.message)
    }
}