package com.example.checkpoint.core.backend.api.appwrite

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import io.appwrite.Client
import io.appwrite.services.Databases
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.appwrite.extensions.gson
import io.appwrite.models.Document
import io.appwrite.models.InputFile
import io.appwrite.services.Storage
import java.io.ByteArrayOutputStream
import java.util.UUID


class AppwriteService(context: Context) {

    private val client = Client(context)
        .setEndpoint("https://fra.cloud.appwrite.io/v1")
        .setProject("67f11f87002b613f4e14")
        .setSelfSigned(true)

    private val database = Databases(client)
    private val storage = Storage(client)

    suspend fun <T> save(collectionId: String, documentId: String, data: T) {
        val dataMap = data?.toMap() ?: emptyMap()

        database.createDocument(
            databaseId = "67f16b4800153970e87a",
            collectionId = collectionId,
            documentId = documentId,
            data = dataMap as Any
        )
    }

    suspend fun <T> edit(databaseId: String, collectionId: String, documentId: String, data: T) {
        val dataMap = data?.toMap() ?: emptyMap()

        database.updateDocument(
            databaseId = databaseId,
            collectionId = collectionId,
            documentId = documentId,
            data = dataMap as Any
        )
    }

    suspend fun delete(databaseId: String, collectionId: String, documentId: String) {
        database.deleteDocument(
            databaseId = databaseId,
            collectionId = collectionId,
            documentId = documentId
        )
    }

    suspend fun deleteStorage(file: String) {
        storage.deleteFile("67f4196f003826072308", file.substringAfter("/files/").substringBefore("/"))
    }

    suspend fun <T> get(databaseId: String, collectionId: String, documentId: String, clazz: Class<T>): T? {
        val document: Document<Map<String, Any>> = database.getDocument(
            databaseId = databaseId,
            collectionId = collectionId,
            documentId = documentId
        )
        val dataMap = document.data
        return gson.fromJson(gson.toJson(dataMap), clazz)
    }

    suspend fun getAll(databaseId: String, collectionId: String, filters: List<String>): List<Map<String, Any>> {
        val documentsList = database.listDocuments(
            databaseId = databaseId,
            collectionId = collectionId,
            queries = filters
        ).documents
        return documentsList.map { doc -> doc.data }
    }

    private fun Any.toMap(): Map<String, Any> {
        val gson = Gson()
        val json = gson.toJson(this)
        val type = object : TypeToken<Map<String, Any>>() {}.type
        return gson.fromJson(json, type)
    }

    suspend fun uploadImageToAppwrite(context: Context, imageUri: Uri): String {
        return try {
            val fileId = UUID.randomUUID().toString().replace("-", "")
            val contentResolver = context.contentResolver
            val originalInputStream = contentResolver.openInputStream(imageUri)
                ?: throw Exception("InputStream is null")
            val bitmap = BitmapFactory.decodeStream(originalInputStream)
            originalInputStream.close()

            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)

            val byteArray = outputStream.toByteArray()
            outputStream.close()

            val mimeType = "image/jpeg"
            val fileName = "image_${System.currentTimeMillis()}.jpeg"
            val inputFile = InputFile.fromBytes(byteArray, fileName, mimeType)

            val response = storage.createFile(
                bucketId = "67f4196f003826072308",
                fileId = fileId,
                file = inputFile
            )
            val fileUrl = "https://cloud.appwrite.io/v1/storage/buckets/67f4196f003826072308/files/${response.id}/view?project=67f11f87002b613f4e14"
            fileUrl

        } catch (e: Exception) {
            println("Error uploading file: ${e.message}")
            e.printStackTrace()
            "Error al subir el archivo: ${e.message}"
        }
    }


}
