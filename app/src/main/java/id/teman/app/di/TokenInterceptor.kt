package id.teman.app.di

import id.teman.app.preference.Preference
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor @Inject constructor(
    private val preference: Preference
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val idToken = runBlocking { preference.getBearerToken.first() }
        val deviceId = runBlocking { preference.getDeviceId.first() }
        val deviceName = runBlocking { preference.getDeviceName.first() }
        val authorisedRequestBuilder = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer $idToken")
            .addHeader("x-device-id", deviceId)
            .addHeader("x-device-name", deviceName)
        return chain.proceed(authorisedRequestBuilder.build())

    }
}