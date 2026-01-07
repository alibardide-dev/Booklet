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

    try {
        var bitmap = if (Build.VERSION.SDK_INT < 28) {
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
        /*
        If you're reading this, I have no idea how this works.
        I mean I have an idea to write it but please don't ask me to explain what I've done.
        It's been running on hopes and prayers.
         */
        val fos = context.contentResolver.openOutputStream(path.toUri())!!
        // Crop to fit 2:3 ratio
        bitmap = if (bitmap.width / 2 < bitmap.height / 3) {
            val newHeight = bitmap.width * (3f / 2f)
            val newY = (bitmap.height - newHeight) / 2f

            Bitmap.createBitmap(
                bitmap,
                0,
                newY.toInt(),
                bitmap.width,
                newHeight.toInt()
            )
        } else {
            val newWidth = bitmap.height * (2f / 3f)
            val newX = (bitmap.width - newWidth) / 2f

            Bitmap.createBitmap(
                bitmap,
                newX.toInt(),
                0,
                newWidth.toInt(),
            bitmap.height
            )
        }
        // Change Resolution
        bitmap = Bitmap.createScaledBitmap(bitmap, 300, 450, false)
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, fos)
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

fun deleteFileFromName(context: Context, name: String?) {
    if (name == null)
        return
    val file = File(context.filesDir, name)
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

fun cacheFileUri(context: Context): Uri {
    return File(context.cacheDir, "cached_${System.currentTimeMillis()}.jpg").toUri()
}