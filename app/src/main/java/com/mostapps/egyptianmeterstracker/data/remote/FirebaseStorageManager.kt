package com.mostapps.egyptianmeterstracker.data.remote

import android.net.Uri
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await


class FirebaseStorageManager(private val storageReference: StorageReference) :
    FirebaseStorageInterface {


    override suspend fun uploadImageURI(usedId: String, meterId: String, imageUri: Uri) {
        storageReference.storage.reference.child(usedId).child(meterId).putFile(imageUri).await()
    }

    override fun getMeterImageStorageReference(usedId: String, meterId: String): StorageReference {
        return storageReference.storage.reference.child(usedId).child(meterId)
    }
}