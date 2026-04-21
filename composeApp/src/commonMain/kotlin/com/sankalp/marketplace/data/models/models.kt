package com.sankalp.marketplace.data.models

import kotlinx.serialization.Serializable

// region ApiError
@Serializable
data class ErrorResponse(
    val error: String
)
//endregion

// region Token Refresh request
@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
)
// endregion

// region Token Refresh response
@Serializable
data class RefreshTokenResponse(
    val accessToken: String,
    val refreshToken: String
)
// endregion

// region Login Request
@Serializable
data class LoginRequest(
    val email : String,
    val password : String
)
// endregion

// region Login response
@Serializable
data class LoginResponse(
    val accessToken : String,
    val refreshToken : String
)
// endregion

// region Forgot Password Request
@Serializable
data class ForgotPasswordRequest(
    val email : String
)
// endregion

// region Forgot Password Response
@Serializable
data class ForgotPasswordResponse(
    val message : String
)
// endregion

// region Password Reset Request
@Serializable
data class PasswordResetRequest(
    val email: String,
    val otp: String,
    val newPassword: String
)
// endregion

// region Password Reset Response
@Serializable
data class PasswordResetResponse(
    val message: String
)
// endregion

//region country response
@Serializable
data class CountryResponse(
    val countryId : String,
    val countryName : String
)
//endregion

// region state response
@Serializable
data class StatesResponse(
    val stateId : String,
    val stateName : String
)
//endregion

// region city response
@Serializable
data class CityResponse(
    val cityId : String,
    val cityName : String
)
//endregion

// region Multipart Request
data class MultipartRequest(
    val formFields: Map<String, String> = emptyMap(),
    val files: List<MultipartFile> = emptyList()
)

data class MultipartFile(
    val key: String,
    val filePath: String,
    val fileName: String,
    val mimeType: String = "image/jpeg"
)
//endregion

// region Register Response
@Serializable
data class RegisterResponse(
    val message: String
)
//endregion