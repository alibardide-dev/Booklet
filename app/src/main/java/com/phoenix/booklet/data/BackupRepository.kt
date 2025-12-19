package com.phoenix.booklet.data

import android.content.Context
import android.net.Uri
import androidx.room.withTransaction
import com.google.gson.Gson
import com.phoenix.booklet.data.model.BackupData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class BackupRepository(
    private val database: AppDatabase,
    private val context: Context,
    private val gson: Gson
) {

    suspend fun createBackup(outputUri: Uri): Result = withContext(Dispatchers.IO) {
        try {
            val contentResolver = context.contentResolver

            contentResolver.openOutputStream(outputUri)?.use { outputStream ->
                ZipOutputStream(BufferedOutputStream(outputStream)).use { zipOut ->
                    val jsonData = exportDatabaseToJson()
                    val jsonEntry = ZipEntry("database.json")
                    zipOut.putNextEntry(jsonEntry)
                    zipOut.write(jsonData.toByteArray())
                    zipOut.closeEntry()

                    val imagesDir = File(context.filesDir, "")
                    if (imagesDir.exists())
                        addFilesToZip(zipOut, imagesDir, "images")
                }
            }

            Result.Success
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e.message)
        }
    }

    suspend fun restoreBackup(inputUri: Uri): Result = withContext(Dispatchers.IO) {
        try {
            val contentResolver = context.contentResolver
            var backupData: BackupData? = null

            contentResolver.openInputStream(inputUri)?.use { inputStream ->
                ZipInputStream(BufferedInputStream(inputStream)).use { zipIn ->
                    var entry: ZipEntry? = zipIn.nextEntry

                    while(entry != null) {
                        if(entry.name == "database.json") {
                            val jsonString = zipIn.readBytes().toString(Charsets.UTF_8)
                            backupData = gson.fromJson(jsonString, BackupData::class.java)
                        } else if (entry.name.startsWith("images/")) {
                            val imagePath = entry.name.substringAfter("images/")
                            val imageFile = File(context.filesDir, imagePath)

                            FileOutputStream(imageFile).use { output ->
                                zipIn.copyTo(output)
                            }
                        }
                        entry = zipIn.nextEntry
                    }
                }
            }

            backupData?.let {
                restoreDatabase(it)
            } ?: throw Exception("No backup data found")

            Result.Success
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e.message)
        }
     }

    private suspend fun exportDatabaseToJson(): String = withContext(Dispatchers.IO) {
        val books = database.bookDao().getAllBooks()
        val backupData = BackupData(
            timestamp = System.currentTimeMillis(),
            books = books
        )

        gson.toJson(backupData)
    }

    private fun addFilesToZip(zipOut: ZipOutputStream, directory: File, parentPath: String) {
        directory.listFiles()?.forEach { file ->
            if(file.name.endsWith(".png")) {
                val zipEntry = ZipEntry("$parentPath/${file.name}")
                zipOut.putNextEntry(zipEntry)

                file.inputStream().use { inputStream ->
                    inputStream.copyTo(zipOut)
                }

                zipOut.closeEntry()
            }
        }
    }

    private suspend fun restoreDatabase(backupData: BackupData) = withContext(Dispatchers.IO) {
        database.withTransaction {
            val currentVersion = database.openHelper.readableDatabase.version
            if (backupData.version > currentVersion)
                throw Exception("Backup version is newer than app version")

            database.bookDao().insertAll(backupData.books)
        }
    }

}