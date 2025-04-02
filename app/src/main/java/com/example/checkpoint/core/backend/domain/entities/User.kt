package com.example.checkpoint.core.backend.domain.entities

import com.example.checkpoint.core.backend.domain.valueobjects.UserName

data class User(
    val name: UserName,
    val subscriptions: List<Subscription>
)
