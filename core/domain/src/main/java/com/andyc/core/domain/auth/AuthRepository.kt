package com.andyc.core.domain.auth

import com.andyc.core.domain.user.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getCurrentUser(): Flow<User?>

    fun signOut()

    fun deleteUser()
}