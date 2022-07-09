package com.mostapps.egyptianmeterstracker.data.remote

import android.net.Uri
import com.google.firebase.storage.StorageReference

interface FirebaseStorageInterface {


    suspend fun uploadImageURI(usedId: String, meterId: String, imageUri: Uri)

    fun getMeterImageStorageReference(usedId: String, meterId: String): StorageReference


}