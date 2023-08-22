package id.teman.app.domain.repository.user

import id.teman.app.common.orZero
import id.teman.app.data.dto.LoginRequest
import id.teman.app.data.dto.RegisterRequest
import id.teman.app.data.dto.UserDto
import id.teman.app.data.dto.VerifyOtpRequest
import id.teman.app.data.remote.user.UserRemoteDataSource
import id.teman.app.domain.model.user.UserInfo
import id.teman.app.domain.model.user.toUserInfo
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody

typealias accessToken = String
typealias userInfo = UserInfo

class UserRepository @Inject constructor(
    private val userRemoteDataSource: UserRemoteDataSource
) {

    suspend fun login(request: LoginRequest): Flow<String> =
        userRemoteDataSource.login(request).map { it.accessToken.orEmpty() }

    suspend fun register(request: RegisterRequest): Flow<Pair<accessToken, userInfo>> =
        userRemoteDataSource.register(request)
            .map { Pair(it.accessToken.orEmpty(), it.user.toUserInfo()) }

    suspend fun sendOtp(): Flow<Int> =
        userRemoteDataSource.sendOtp().map { it.attemption.orZero() }

    suspend fun verifyOtp(request: VerifyOtpRequest): Flow<UserDto> =
        userRemoteDataSource.verifyOtp(request)

    suspend fun sendOtpReset(): Flow<Int> =
        userRemoteDataSource.sendOtpReset().map { it.attemption.orZero() }

    suspend fun verifyOtpReset(request: VerifyOtpRequest): Flow<String> =
        userRemoteDataSource.verifyOtpReset(request)

    suspend fun getUserProfile(): Flow<UserInfo> =
        userRemoteDataSource.getProfile().map { it.toUserInfo() }

    suspend fun updateUserProfile(
        partMap: MutableMap<String, RequestBody>,
        profileImageFile: MultipartBody.Part? = null
    ): Flow<UserInfo> =
        userRemoteDataSource.updateProfile(partMap, profileImageFile).map { it.toUserInfo() }
}