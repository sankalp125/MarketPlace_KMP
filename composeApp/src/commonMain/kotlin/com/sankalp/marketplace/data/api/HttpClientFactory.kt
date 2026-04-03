package com.sankalp.marketplace.data.api

import com.sankalp.marketplace.data.models.RefreshTokenRequest
import com.sankalp.marketplace.data.models.RefreshTokenResponse
import com.sankalp.marketplace.utils.AuthEvent
import com.sankalp.marketplace.utils.AuthEventBus
import com.sankalp.marketplace.utils.TokenStorage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun createHttpClient(
    tokenStorage : TokenStorage,
    engine: HttpClientEngine
) : HttpClient {
    return HttpClient(engine){
        install(ContentNegotiation){
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(Logging){
            logger = Logger.DEFAULT
            level = LogLevel.BODY
        }
        install(HttpTimeout){
            requestTimeoutMillis = 30_000
            connectTimeoutMillis = 30_000
            socketTimeoutMillis = 30_000
        }
        install(DefaultRequest){
            url("http://172.31.42.84:8080/api/v1/")
            contentType(ContentType.Application.Json)
        }
        install(Auth){
            bearer {
                loadTokens {
                    val accessToken = tokenStorage.getAccessToken()
                    if (accessToken != null){
                        BearerTokens(
                            accessToken = accessToken,
                            refreshToken = ""
                        )
                    }else{
                        null
                    }
                }
                refreshTokens {
                    val refreshToken = tokenStorage.getRefreshToken()
                    if (refreshToken == null){
                        tokenStorage.clearTokens()
                        AuthEventBus.sendEvent(AuthEvent.SessionExpired)
                        return@refreshTokens null
                    }
                    try {
                        val response = client.post("refresh-token"){
                            markAsRefreshTokenRequest()
                            setBody(RefreshTokenRequest(refreshToken))
                        }
                        when(response.status){
                            HttpStatusCode.OK -> {
                                val newTokens = response.body<RefreshTokenResponse>()
                                tokenStorage.saveTokens(
                                    accessToken = newTokens.accessToken,
                                    refreshToken = newTokens.refreshToken
                                )
                                BearerTokens(
                                    accessToken = newTokens.accessToken,
                                    refreshToken = ""
                                )
                            }

                            HttpStatusCode.Unauthorized, HttpStatusCode.Forbidden -> {
                                tokenStorage.clearTokens()
                                AuthEventBus.sendEvent(AuthEvent.SessionExpired)
                                null
                            }
                            else -> {
                                null
                            }
                        }
                    }catch (e : Exception){
                        null
                    }
                }
                sendWithoutRequest { request ->
                    request.url.pathSegments.contains("protected")
                }
            }
        }
    }
}