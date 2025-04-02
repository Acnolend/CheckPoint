package com.example.checkpoint.core.backend.api.firestore
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreService {
    private val db = FirebaseFirestore.getInstance()

    suspend fun <T> save(collection: String, documentId: String, data: T) {
        db.collection(collection).document(documentId).set(data!!).await()
    }

    suspend fun <T> get(collection: String, documentId: String, clazz: Class<T>): T? {
        val snapshot = db.collection(collection).document(documentId).get().await()
        return snapshot.toObject(clazz)
    }

    suspend fun <T> getAll(collection: String, subCollection: String, clazz: Class<T>): List<T> {
        val snapshot = db.collection(collection)
            .document(subCollection)
            .collection("subscriptions")
            .get()
            .await()

        return snapshot.toObjects(clazz)
    }
}