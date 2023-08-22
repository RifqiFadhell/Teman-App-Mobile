package id.teman.app.data.remote.user

import id.teman.app.data.dto.LoginDto
import id.teman.app.data.dto.LoginRequest
import id.teman.app.data.dto.RegisterRequest
import id.teman.app.data.dto.RequestOtpDto
import id.teman.app.data.dto.RequestOtpResetDto
import id.teman.app.data.dto.UserDataDto
import id.teman.app.data.dto.UserDto
import id.teman.app.data.dto.VerifyOtpRequest
import id.teman.app.data.remote.ApiServiceInterface
import id.teman.app.data.remote.handleRequestOnFlow
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface UserRemoteDataSource {
    suspend fun login(request: LoginRequest): Flow<LoginDto>
    suspend fun register(request: RegisterRequest): Flow<UserDto>
    suspend fun verifyOtp(request: VerifyOtpRequest): Flow<UserDto>
    suspend fun sendOtp(): Flow<RequestOtpDto>
    suspend fun verifyOtpReset(request: VerifyOtpRequest): Flow<String>
    suspend fun sendOtpReset(): Flow<RequestOtpDto>
    suspend fun getProfile(): Flow<UserDataDto>
    suspend fun updateProfile(partMap: MutableMap<String, RequestBody>?,
                              profileImageFile: MultipartBody.Part? = null): Flow<UserDataDto>
}

class DefaultUserRemoteDataSource(
    private val httpClient: ApiServiceInterface
) : UserRemoteDataSource {

    override suspend fun login(request: LoginRequest): Flow<LoginDto> =
        handleRequestOnFlow {
            httpClient.login(request)
        }

    override suspend fun register(request: RegisterRequest): Flow<UserDto> =
        handleRequestOnFlow {
            httpClient.register(request)
        }

    override suspend fun verifyOtp(request: VerifyOtpRequest): Flow<UserDto> =
        handleRequestOnFlow {
            httpClient.verifyOtp(request)
        }

    override suspend fun sendOtpReset(): Flow<RequestOtpDto> =
        handleRequestOnFlow {
            httpClient.sendOtpReset()
        }

    override suspend fun verifyOtpReset(request: VerifyOtpRequest): Flow<String> =
        handleRequestOnFlow {
            httpClient.verifyOtpReset(request).token.orEmpty()
        }

    override suspend fun sendOtp(): Flow<RequestOtpDto> =
        handleRequestOnFlow {
            httpClient.sendOtp()
        }

    override suspend fun getProfile(): Flow<UserDataDto> =
        handleRequestOnFlow { httpClient.getProfile() }

    override suspend fun updateProfile(
        partMap: MutableMap<String, RequestBody>?,
        profileImageFile: MultipartBody.Part?): Flow<UserDataDto> =
        handleRequestOnFlow {
            httpClient.updateProfile(partMap, profileImageFile)
        }
}