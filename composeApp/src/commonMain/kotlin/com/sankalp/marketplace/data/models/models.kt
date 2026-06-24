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

//region Category Response
@Serializable
data class CategoryResponse(
    val categoryId: String,
    val categoryName: String
)
//endregion

// region Products Response
@Serializable
data class ProductListResponse(
    val productId: String,
    val productName : String,
    val productDesc : String,
    val productCategory : String,
    val productCity : String,
    val productPrice : String,
    val pictureUrl : String
)
// endregion

// region Profile Details
@Serializable
data class ProfileResponse(
    val id : String,
    val name  : String,
    val email : String,
    val password : String,
    val mobile : String,
    val country : String,
    val state : String,
    val city : String,
    val photoUrl : String,
    val createdAt : String,
    val updatedAt : String
)
//endregion

// region my products
@Serializable
data class MyProductResponse(
    val productId : String,
    val productImage : String,
    val productName : String,
    val productPrice : Double,
    val productDate : String,
    val productStatus : Boolean
)
//endregion

// region Update profile request
@Serializable
data class UpdateProfileRequest(
    val name : String,
    val email : String,
    val mobileNo : String,
    val country : String,
    val state : String,
    val city : String
)
// endregion

// region update Product
@Serializable
data class UpdateProductRequest(
    val productId : String,
    val price : String,
    val date : String,
    val status : String
)
// endregion

// region Delete product picture
@Serializable
data class DeleteProductPictureRequest(
    val url : String
)
// endregion

// region Product Details Response
@Serializable
data class ProductDetailsResponse(
    val name : String,
    val category : String,
    val description : String,
    val price : Double,
    val date : String,
    val status : Boolean,
    val forCountry : String,
    val forState : String,
    val forCity : String,
    val pictureUrl : String,
    val publisher : String,
    val publisherEmail : String,
    val publisherMobileNumber : String,
    val otherPictures : List<String>
)