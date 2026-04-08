package com.sankalp.marketplace.data.api

import com.sankalp.marketplace.data.models.ErrorResponse
import com.sankalp.marketplace.data.models.LoginRequest
import com.sankalp.marketplace.data.models.LoginResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json

class MarketPlaceApi(private val client: HttpClient){
    suspend fun login(
        request: LoginRequest
    ) : NetworkResult<LoginResponse> = safeApiCall {
        client.post("normal/login") { setBody(request) }
    }
}

suspend inline fun <reified T> safeApiCall(
    call: () -> HttpResponse
): NetworkResult<T> {
    return try {
        val response = call()

        if (response.status.isSuccess()) {
            NetworkResult.Success(response.body<T>())
        } else {
            val errorBodyString = response.bodyAsText()
            val errorResponse = try {
                Json.decodeFromString<ErrorResponse>(errorBodyString)
            } catch (e: Exception) {
                null
            }
            NetworkResult.Error.GeneralError(
                message = errorResponse?.error ?: response.status.description,
                code    = response.status.value
            )
        }
    } catch (e: Exception) {
        NetworkResult.Error.NetworkError(
            message = e.message ?: "Network error occurred"
        )
    }
}