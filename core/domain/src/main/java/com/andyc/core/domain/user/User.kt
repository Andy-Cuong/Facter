package com.andyc.core.domain.user

data class User(
    val id: String,
    val name: String?,
    val email: String?,
    val photoUrl: String?
)