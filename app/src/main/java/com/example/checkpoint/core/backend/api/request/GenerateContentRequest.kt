package com.example.checkpoint.core.backend.api.request

data class GenerateContentRequest(
    val contents: List<Content>
)

data class Content(
    val parts: List<Part>
)

data class Part(
    val text: String
)

