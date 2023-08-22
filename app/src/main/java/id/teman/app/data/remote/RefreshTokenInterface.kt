package id.teman.app.data.remote

import id.teman.app.data.dto.user.RefreshTokenRequestDto
import id.teman.app.data.dto.user.RefreshTokenResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface RefreshTokenInterface {

    @POST("auth/refresh_token")
    suspend fun refreshToken(
        @Body token: RefreshTokenRequestDto
    ): RefreshTokenResponseDto
}