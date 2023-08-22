package id.teman.app.di

import id.teman.app.data.dto.user.RefreshTokenRequestDto
import id.teman.app.data.remote.RefreshTokenInterface
import id.teman.app.manager.UserManager
import id.teman.app.manager.UserState
import id.teman.app.preference.Preference
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator @Inject constructor(
    private val apiClient: RefreshTokenInterface,
    private val userManager: UserManager,
    private val preference: Preference
) : Authenticator {
    @Throws(IOException::class)
    override fun authenticate(route: Route?, response: Response): Request {
        var request: Request = response.request

        try {
            runBlocking(Dispatchers.Default) {
                val refreshToken = preference.getRefreshToken.first()
                val deviceId = preference.getDeviceId.first()
                val deviceName = preference.getDeviceName.first()
                if (refreshToken.isBlank()) {
                    userManager.changeUserState(UserState.Revoked)
                } else {
                    val result = apiClient.refreshToken(
                        RefreshTokenRequestDto(refreshToken)
                    )
                    if (result.accessToken == null) {
                        userManager.changeUserState(UserState.Revoked)
                    } else {
                        preference.setBearerToken(result.accessToken)
                        preference.setRefreshToken(result.refreshToken.orEmpty())

                        request = response.request.newBuilder()
                            .header("Authorization", "Bearer ${result.accessToken}")
                            .addHeader("x-device-id", deviceId.orEmpty())
                            .addHeader("x-device-name", deviceName.orEmpty())
                            .build()
                    }
                }
            }
        } catch (e: Exception) {
            runBlocking {
                preference.clearLoginValues()
                userManager.changeUserState(UserState.Revoked)
            }
        }

        return request
    }
}