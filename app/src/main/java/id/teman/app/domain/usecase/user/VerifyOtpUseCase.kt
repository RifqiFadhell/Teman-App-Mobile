package id.teman.app.domain.usecase.user

import id.teman.app.data.dto.VerifyOtpRequest
import id.teman.app.domain.model.user.toUserInfo
import id.teman.app.domain.repository.user.UserRepository
import id.teman.app.domain.usecase.UseCase
import id.teman.app.preference.Preference
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class VerifyOtpUseCase @Inject constructor(
    private val repository: UserRepository,
    private val json: Json,
    private val preference: Preference
) : UseCase<String, Flow<Unit>> {
    override suspend fun execute(params: String): Flow<Unit> {
        return flow {
            repository.verifyOtp(VerifyOtpRequest(params))
                .catch { exception -> throw exception }
                .collect {
                    val userInfo = it.user.toUserInfo()
                    val rawJson = json.encodeToString(userInfo)
                    preference.setUserInfo(rawJson)
                    preference.setBearerToken(it.accessToken.orEmpty())
                    preference.setRefreshToken(it.refreshToken.orEmpty())
                    preference.setIsUserLoggedIn(true)
                    emit(Unit)
                }
        }
    }
}