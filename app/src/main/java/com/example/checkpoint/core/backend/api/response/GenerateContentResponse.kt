package com.example.checkpoint.core.backend.api.response

data class GenerateContentResponse(
    val candidates: List<Candidate>
)

data class Candidate(
    val content: ContentResponse
)

data class ContentResponse(
    val parts: List<PartResponse>
)

data class PartResponse(
    val text: String
)

