package com.sankalp.marketplace.data.api

import com.sankalp.marketplace.data.models.CategoryResponse
import com.sankalp.marketplace.data.models.CityResponse
import com.sankalp.marketplace.data.models.CountryResponse
import com.sankalp.marketplace.data.models.ErrorResponse
import com.sankalp.marketplace.data.models.ForgotPasswordRequest
import com.sankalp.marketplace.data.models.ForgotPasswordResponse
import com.sankalp.marketplace.data.models.LoginRequest
import com.sankalp.marketplace.data.models.LoginResponse
import com.sankalp.marketplace.data.models.MultipartRequest
import com.sankalp.marketplace.data.models.PasswordResetRequest
import com.sankalp.marketplace.data.models.PasswordResetResponse
import com.sankalp.marketplace.data.models.ProductListResponse
import com.sankalp.marketplace.data.models.RegisterResponse
import com.sankalp.marketplace.data.models.StatesResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json

class MarketPlaceApi(private val client: HttpClient) {
    suspend fun login(
        request: LoginRequest
    ): NetworkResult<LoginResponse> = safeApiCall {
        client.post("normal/login") { setBody(request) }
    }

    suspend fun requestForgetPassword(
        request: ForgotPasswordRequest
    ): NetworkResult<ForgotPasswordResponse> = safeApiCall {
        client.post("normal/request-password-reset") { setBody(request) }
    }

    suspend fun requestPasswordReset(
        request: PasswordResetRequest
    ): NetworkResult<PasswordResetResponse> = safeApiCall {
        client.post("normal/reset-password") { setBody(request) }
    }

    suspend fun requestCountriesList(): NetworkResult<List<CountryResponse>> = safeApiCall {
        client.get("normal/countries")
    }

    suspend fun requestStatesList(countryCode: String): NetworkResult<List<StatesResponse>> =
        safeApiCall {
            client.get("normal/states/$countryCode")
        }

    suspend fun requestCitiesList(stateCode: String): NetworkResult<List<CityResponse>> =
        safeApiCall {
            client.get("normal/cities/$stateCode")
        }

    suspend fun registerUser(
        request : MultipartRequest
    ) : NetworkResult<RegisterResponse> = safeApiCall {
        client.post("normal/register") {
            setBody(MultipartHelper.build(request))
        }
    }

    suspend fun getCategories() : NetworkResult<List<CategoryResponse>> = safeApiCall {
        client.get("normal/get_categories")
    }

    suspend fun getProductsList() : NetworkResult<List<ProductListResponse>> = safeApiCall {
        client.get("protected/product-list")
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
                code = response.status.value
            )
        }
    } catch (e: Exception) {
        NetworkResult.Error.NetworkError(
            message = e.message ?: "Network error occurred"
        )
    }
}