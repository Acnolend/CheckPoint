package com.example.checkpoint.core.backend.api.appwrite
import android.content.Context
import io.appwrite.Client
import io.appwrite.Query
import io.appwrite.exceptions.AppwriteException
import io.appwrite.services.Account
import io.appwrite.services.Databases
import io.appwrite.services.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import io.appwrite.enums.OAuthProvider
import androidx.activity.ComponentActivity

class AuthService(context: Context) {
    private val client = Client(context)
        .setEndpoint("https://cloud.appwrite.io/v1")
        .setProject("67f11f87002b613f4e14")
        .setSelfSigned(true)

    private val account = Account(client)
    private val database = Databases(client)
    private val storage = Storage(client)

    suspend fun isUserLoggedIn(): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                account.get()
            }
            true
        } catch (e: AppwriteException) {
            false
        }
    }

    suspend fun signUp(email: String, password: String, userName: String): Result<String> {
        val userId = generateRandomId()
        println(userName)
        println(userId)
        return try {
            account.create(userId, email, password, userName)

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

            session.userId

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

    suspend fun editUser(newUserName: String, newEmail: String?, newPassword: String?, currentPassword: String): Result<Boolean> {
        return try {
            val currentUserNameResult = getUserName()
            val currentUserEmailResult = getUserEmail()

            if (currentUserNameResult.isFailure || currentUserEmailResult.isFailure) {
                return Result.failure(Exception("Error retrieving current user data"))
            }

            val currentUserName = currentUserNameResult.getOrNull() ?: ""
            val currentUserEmail = currentUserEmailResult.getOrNull() ?: ""

            val userId = getUserIdActual().toString().substringAfter("(").substringBefore(")")
            println("Updating user with documentId: $userId")

            if (newUserName.isNotEmpty() && newUserName != currentUserName) {
                val database = Databases(client)
                database.getDocument(
                    databaseId = "67f16b4800153970e87a",
                    collectionId = "67f19662003ce0c8e7d5",
                    documentId = userId
                )
                database.updateDocument(
                    databaseId = "67f16b4800153970e87a",
                    collectionId = "67f19662003ce0c8e7d5",
                    documentId = userId,
                    data = mapOf("userName" to newUserName)
                )
                account.updateName(newUserName)
            }
            newEmail?.let {
                if (it != currentUserEmail) {
                    account.updateEmail(it, currentPassword)
                }
            }
            newPassword?.let {
                account.updatePassword(it, currentPassword)
            }
            Result.success(true)
        } catch (e: AppwriteException) {
            println("Error updating user: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun deleteUserAndSubscriptions(userId: String): Result<Boolean> {
        return try {
            val query = listOf(Query.equal("userId", userId))
            val queryStrings = query.map { it }
            val documents = database.listDocuments(
                databaseId = "67f16b4800153970e87a",
                collectionId = "67f1730c001c5348bb6a",
                queries = queryStrings
            )
            for (document in documents.documents) {
                println("🗑️ Procesando suscripción con ID: ${document.id}")
                val imageUrl = document.data["image"] as String?
                val fileId = extractFileIdFromUrl(imageUrl)
                println(fileId)
                if (fileId != null) {
                    storage.deleteFile("67f4196f003826072308", fileId)
                }
                database.deleteDocument(
                    databaseId = "67f16b4800153970e87a",
                    collectionId = "67f1730c001c5348bb6a",
                    documentId = document.id
                )
            }
            Result.success(true)
        } catch (e: AppwriteException) {
            println("❌ Error al eliminar usuario o suscripciones: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getUserEmail(): Result<String> {
        return try {
            val user = withContext(Dispatchers.IO) {
                account.get()
            }
            val email = user.email
            Result.success(email)
        } catch (e: AppwriteException) {
            Result.failure(e)
        }
    }

    suspend fun getUserName(): Result<String> {
        return try {
            val user = withContext(Dispatchers.IO) {
                account.get()
            }
            val name = user.name
            Result.success(name)
        } catch (e: AppwriteException) {
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(activity: ComponentActivity) {
        val successUrl = "https://fra.cloud.appwrite.io/v1/account/sessions/oauth2/callback/google/67f11f87002b613f4e14"
        val failureUrl = "https://fra.cloud.appwrite.io/v1/account/sessions/oauth2/callback/google/67f11f87002b613f4e14"
        account.createOAuth2Session(
            provider = OAuthProvider.GOOGLE,
            success = successUrl,
            failure = failureUrl,
            activity = activity
        )
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

    private fun extractFileIdFromUrl(url: String?): String? {
        return url?.substringAfter("/files/")?.substringBefore("/")
    }
}