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