package com.example.checkpoint.core.backend.api.appwrite
import android.content.Context
import android.os.Build
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
import androidx.annotation.RequiresApi
import androidx.work.WorkManager
import com.example.checkpoint.application.services.isDefaultImageUrl
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import com.example.checkpoint.BuildConfig

class AuthService(context: Context) {
    private val client = Client(context)
        .setEndpoint(BuildConfig.ENDPOINT_APPWRITE)
        .setProject(BuildConfig.PROJECT_ID)
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

    suspend fun isUserLoggedInWithGoogle(): Boolean {
        return try {
            val session = withContext(Dispatchers.IO) {
                account.getSession("current")
            }

            session.provider == "google"
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
                databaseId = BuildConfig.DATABASE_ID,
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
                    databaseId = BuildConfig.DATABASE_ID,
                    collectionId = BuildConfig.USER_COLLECTION,
                    documentId = userId
                )
                database.updateDocument(
                    databaseId = BuildConfig.DATABASE_ID,
                    collectionId = BuildConfig.USER_COLLECTION,
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

    suspend fun deleteUserAndSubscriptions(userId: String, context: Context): Result<Boolean> {
        return try {
            val query = listOf(Query.equal("userId", userId))
            val queryStrings = query.map { it }
            val documents = database.listDocuments(
                databaseId = BuildConfig.DATABASE_ID,
                collectionId = BuildConfig.SUBSCRIPTION_COLLECTION,
                queries = queryStrings
            )
            for (document in documents.documents) {
                println("🗑️ Procesando suscripción con ID: ${document.id}")
                val imageUrl = document.data["image"] as String?
                val fileId = extractFileIdFromUrl(imageUrl)
                println(fileId)
                if (fileId != null && imageUrl != null && !isDefaultImageUrl(imageUrl)) {
                    storage.deleteFile(BuildConfig.BUCKET_ID, fileId)
                }
                database.deleteDocument(
                    databaseId = BuildConfig.DATABASE_ID,
                    collectionId = BuildConfig.SUBSCRIPTION_COLLECTION,
                    documentId = document.id
                )
            }
            val workManager = WorkManager.getInstance(context)
            workManager.cancelAllWork()
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

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun signInWithGoogle(context: Context, activity: ComponentActivity) {
        account.createOAuth2Session(
            provider = OAuthProvider.GOOGLE,
            activity = activity,
            scopes = listOf(
                "https://www.googleapis.com/auth/gmail.readonly"
            ),
        )
        saveToken(context)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getValidAccessToken(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("auth_token", null)
        val expiryTime = sharedPreferences.getLong("auth_token_expiry", 0)
        if (accessToken == null || System.currentTimeMillis() >= expiryTime) {
            println("Token expirado o nulo. Refrescando...")
            return manualRefreshGoogleToken(context)
        }
        return accessToken
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun saveToken(context: Context) {
        val session = account.getSession("current")
        val googleToken = session.providerAccessToken
        val expiryMillis = System.currentTimeMillis() + (58 * 60 * 1000)
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("auth_token", googleToken)
            putLong("auth_token_expiry", expiryMillis)
            apply()
        }

        println("Token y expiration guardados correctamente. Expira en: $expiryMillis")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun manualRefreshGoogleToken(context: Context): String? = withContext(Dispatchers.IO) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val refreshToken = sharedPreferences.getString("refresh_token", null)

        if (refreshToken == null) {
            println("No hay refresh_token guardado.")
            return@withContext null
        }

        val clientId = "591711405548-cnq5eu6k5sv4dkgmc2lernr60dfnstom.apps.googleusercontent.com"
        val clientSecret = "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"
        val url = URL("https://oauth2.googleapis.com/token")

        val postData = "client_id=$clientId&client_secret=$clientSecret&refresh_token=$refreshToken&grant_type=refresh_token"

        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.doOutput = true
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

        try {
            conn.outputStream.use { os ->
                val input = postData.toByteArray(Charsets.UTF_8)
                os.write(input, 0, input.size)
            }

            val response = conn.inputStream.bufferedReader().use { it.readText() }
            println("Respuesta de refresh: $response")

            val json = JSONObject(response)
            val newAccessToken = json.getString("access_token")
            val expiresIn = json.getLong("expires_in") * 1000 // en milisegundos
            val expiryMillis = System.currentTimeMillis() + expiresIn

            with(sharedPreferences.edit()) {
                putString("auth_token", newAccessToken)
                putLong("auth_token_expiry", expiryMillis)
                apply()
            }

            newAccessToken
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            conn.disconnect()
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

    private fun extractFileIdFromUrl(url: String?): String? {
        return url?.substringAfter("/files/")?.substringBefore("/")
    }
}