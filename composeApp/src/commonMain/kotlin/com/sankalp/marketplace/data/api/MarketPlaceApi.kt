package com.sankalp.marketplace.data.api

import com.sankalp.marketplace.data.models.CategoryResponse
import com.sankalp.marketplace.data.models.CityResponse
import com.sankalp.marketplace.data.models.CountryResponse
import com.sankalp.marketplace.data.models.DeleteProductPictureRequest
import com.sankalp.marketplace.data.models.ErrorResponse
import com.sankalp.marketplace.data.models.ForgotPasswordRequest
import com.sankalp.marketplace.data.models.ForgotPasswordResponse
import com.sankalp.marketplace.data.models.LoginRequest
import com.sankalp.marketplace.data.models.LoginResponse
import com.sankalp.marketplace.data.models.MultipartRequest
import com.sankalp.marketplace.data.models.MyProductResponse
import com.sankalp.marketplace.data.models.PasswordResetRequest
import com.sankalp.marketplace.data.models.PasswordResetResponse
import com.sankalp.marketplace.data.models.ProductDetailsResponse
import com.sankalp.marketplace.data.models.ProductListResponse
import com.sankalp.marketplace.data.models.ProfileResponse
import com.sankalp.marketplace.data.models.RegisterResponse
import com.sankalp.marketplace.data.models.StatesResponse
import com.sankalp.marketplace.data.models.UpdateProductRequest
import com.sankalp.marketplace.data.models.UpdateProfileRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
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

    suspend fun addProduct(
        request: MultipartRequest
    ): NetworkResult<RegisterResponse> = safeApiCall {
        client.post("protected/add-product") {
            setBody(MultipartHelper.build(request))
        }
    }
    suspend fun productDetails(
        productId : String
    ) : NetworkResult<ProductDetailsResponse> = safeApiCall {
        client.get("protected/product-details/$productId")
    }

    suspend fun getProfile() : NetworkResult<ProfileResponse> = safeApiCall {
        client.get("protected/profile")
    }

    suspend fun getMyProducts() : NetworkResult<List<MyProductResponse>> = safeApiCall {
        client.get("protected/my-products")
    }

    suspend fun updateProfile(
        request : UpdateProfileRequest
    ) : NetworkResult<String> = safeApiCall {
        client.put("protected/update-profile-details") { setBody(request) }
    }

    suspend fun updateProfilePicture(
        request : MultipartRequest
    ) : NetworkResult<String> = safeApiCall {
        client.put("protected/update-profile-picture") { setBody(MultipartHelper.build(request)) }
    }
    suspend fun updateProduct(
        request : UpdateProductRequest
    ) : NetworkResult<String> = safeApiCall {
        client.put("protected/update-product") { setBody(request) }
    }
    suspend fun addProductPictures(
        request : MultipartRequest
    ) : NetworkResult<String> = safeApiCall {
        client.put("protected/add-product-picture") { setBody(MultipartHelper.build(request)) }
    }
    suspend fun deleteProductPicture(
        request: DeleteProductPictureRequest
    ) : NetworkResult<String> = safeApiCall {
        client.put("protected/delete-picture") { setBody(request) }
    }
    suspend fun deleteProduct(
        productId : String
    ) : NetworkResult<String> = safeApiCall {
        client.delete("protected/delete-product/${productId}")
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