package com.mostapps.egyptianmeterstracker.data.remote.models

import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class User(val username: String? = null, val email: String? = null)