package com.example.checkpoint.core.backend.api.appwrite
import android.content.Context
import io.appwrite.Client
import io.appwrite.exceptions.AppwriteException
import io.appwrite.services.Account
import io.appwrite.services.Databases
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthService(context: Context) {
    private val client = Client(context)
        .setEndpoint("https://cloud.appwrite.io/v1")
        .setProject("67f11f87002b613f4e14")
        .setSelfSigned(true)

    private val account = Account(client)

    suspend fun isUserLoggedIn(): Boolean {
        return try {
            val user = withContext(Dispatchers.IO) {
                account.get()
            }
            user != null
        } catch (e: AppwriteException) {
            false
        }
    }

    suspend fun signUp(email: String, password: String, userName: String): Result<String> {
        val userId = generateRandomId()
        println(userName)
        println(userId)
        return try {
            val user = account.create(userId, email, password, userName)

            val database = Databases(client)
            database.createDocument(
                databaseId = "67f16b4800153970e87a",
                collectionId = "67f19662003ce0c8e7d5",
                documentId = userId,
                data = mapOf("userName" to userName)
            )

            val session = account.createEmailPasswordSession(email, password)

            Result.success(session.userId)
        } catch (e: AppwriteException) {
            println("Error occurred: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun signIn(email: String, password: String): Result<String> {
        return try {

            val session = withContext(Dispatchers.IO) {
                account.createEmailPasswordSession(email, password)
            }

            val userId = session.userId

            Result.success(session.userId)
        } catch (e: AppwriteException) {
            Result.failure(e)
        }
    }

    suspend fun signOut(): Result<Boolean> {
        return try {
            withContext(Dispatchers.IO) {
                account.deleteSession("current")
            }
            Result.success(true)
        } catch (e: AppwriteException) {
            Result.failure(e)
        }
    }

    suspend fun getUserIdActual(): Result<String> {
        return try {
            val user = withContext(Dispatchers.IO) {
                account.get()
            }

            Result.success(user.id)
        } catch (e: AppwriteException) {
            Result.failure(e)
        }
    }

    private fun generateRandomId(length: Int = 36): String {
        val charset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_"
        var userId = (1..length)
            .map { charset.random() }
            .joinToString("")

        // Asegúrate de que el userId no comience con un carácter especial
        while (userId[0] in "-_." || userId.length > 36) {
            userId = (1..length).map { charset.random() }.joinToString("")
        }

        return userId
    }
}