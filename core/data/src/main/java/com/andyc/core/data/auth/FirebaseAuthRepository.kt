package com.andyc.core.data.auth

import com.andyc.core.domain.auth.AuthRepository
import com.andyc.core.domain.user.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FirebaseAuthRepository: AuthRepository {
    private val auth = Firebase.auth

    override fun getCurrentUser(): Flow<User?> {
        return flow {
            while (true) {
                emit(auth.currentUser?.toUser())
                delay(1000)
            }
        }
    }

    override fun signOut() {
        auth.signOut()
    }

    override fun deleteUser() {
        auth.currentUser!!.delete()
    }
}