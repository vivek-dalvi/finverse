package com.finverse.network

import com.finverse.data.model.AIRequest
import com.finverse.data.model.AIResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AIApiService {
    @POST("api/v1/generate")
    suspend fun generateAdvice(@Body request: AIRequest): Response<AIResponse>
}
