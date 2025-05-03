package com.andyc.core.data.auth

import com.andyc.core.domain.user.User
import com.google.firebase.auth.FirebaseUser

fun FirebaseUser.toUser(): User {
    return User(
        id = this.uid,
        name = this.displayName,
        email = this.email,
        photoUrl = this.photoUrl.toString()
    )
}