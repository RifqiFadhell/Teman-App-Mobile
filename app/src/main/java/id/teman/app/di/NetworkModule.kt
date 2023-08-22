package id.teman.app.di

import android.app.Application
import android.content.Context
import android.location.Geocoder
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.google.firebase.installations.FirebaseInstallations
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.teman.app.BuildConfig
import id.teman.app.data.remote.ApiServiceInterface
import id.teman.app.data.remote.RefreshTokenInterface
import id.teman.app.data.remote.bill.BillRemoteDataSource
import id.teman.app.data.remote.bill.DefaultBillDataSource
import id.teman.app.data.remote.chat.ChatRemoteDataSource
import id.teman.app.data.remote.chat.DefaultChatRemoteDataSource
import id.teman.app.data.remote.food.DefaultFoodRemoteDataSource
import id.teman.app.data.remote.food.FoodRemoteDataSource
import id.teman.app.data.remote.history.DefaultHistoryOrderDataSource
import id.teman.app.data.remote.history.HistoryOrderDataSource
import id.teman.app.data.remote.home.DefaultHomeRemoteDataSource
import id.teman.app.data.remote.home.HomeRemoteDataSource
import id.teman.app.data.remote.location.DefaultLocationDataSource
import id.teman.app.data.remote.location.LocationRemoteDataSource
import id.teman.app.data.remote.notification.DefaultNotificationDataSource
import id.teman.app.data.remote.notification.NotificationDataSource
import id.teman.app.data.remote.order.DefaultOrderRemoteDataSource
import id.teman.app.data.remote.order.OrderRemoteDataSource
import id.teman.app.data.remote.promo.DefaultPromoRemoteDataSource
import id.teman.app.data.remote.promo.PromoRemoteDataSource
import id.teman.app.data.remote.referral.DefaultReferralDataSource
import id.teman.app.data.remote.referral.ReferralRemoteDataSource
import id.teman.app.data.remote.user.DefaultUserRemoteDataSource
import id.teman.app.data.remote.user.UserRemoteDataSource
import id.teman.app.data.remote.wallet.DefaultPinWalletDataSource
import id.teman.app.data.remote.wallet.DefaultWalletDataSource
import id.teman.app.data.remote.wallet.PinWalletRemoteDataSource
import id.teman.app.data.remote.wallet.WalletRemoteDataSource
import id.teman.app.manager.UserManager
import id.teman.app.preference.Preference
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Qualifier
    annotation class RefreshTokenClient

    @Qualifier
    annotation class ApiClient

    @Provides
    @Singleton
    fun provideBaseUrl() = BuildConfig.BASE_URL

    @Provides
    @Singleton
    fun provideTokenAuthenticator(
        apiClient: RefreshTokenInterface,
        userManager: UserManager,
        preference: Preference
    ): TokenAuthenticator {
        return TokenAuthenticator(apiClient, userManager, preference)
    }

    @Singleton
    @Provides
    @ApiClient
    fun provideOkHttpClient(
        chuckerInterceptor: ChuckerInterceptor,
        tokenAuthenticator: TokenAuthenticator,
        tokenInterceptor: TokenInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(10000, TimeUnit.SECONDS)
            .readTimeout(10000, TimeUnit.SECONDS)
            .writeTimeout(10000, TimeUnit.SECONDS)
            .callTimeout(10000, TimeUnit.SECONDS)
            .authenticator(tokenAuthenticator)
            .retryOnConnectionFailure(false)
            .also {
                it.addInterceptor(chuckerInterceptor)
                it.addInterceptor(HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
                })
                it.addInterceptor(tokenInterceptor)
            }
            .build()
    }

    @Singleton
    @Provides
    fun provideTokenInterceptor(preference: Preference): TokenInterceptor {
        return TokenInterceptor(preference)
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Singleton
    @Provides
    @ApiClient
    fun provideRetrofit(
        @ApiClient okHttpClient: OkHttpClient,
        BASE_URL: String,
        json: Json
    ): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory(contentType))
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideKotlinXJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }

    @Provides
    @Singleton
    @RefreshTokenClient
    fun provideOkHttpClientForRefreshToken(
        chuckerInterceptor: ChuckerInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(10000, TimeUnit.SECONDS)
            .readTimeout(10000, TimeUnit.SECONDS)
            .writeTimeout(10000, TimeUnit.SECONDS)
            .callTimeout(10000, TimeUnit.SECONDS)
            .also {
                it.addInterceptor(HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
                })
                it.addInterceptor(chuckerInterceptor)
            }
            .retryOnConnectionFailure(false)
            .build()
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    @RefreshTokenClient
    fun provideRetrofitForRefreshToken(
        @RefreshTokenClient okHttpClient: OkHttpClient,
        json: Json,
        BASE_URL: String
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideRefreshApiService(@RefreshTokenClient retrofit: Retrofit): RefreshTokenInterface {
        return retrofit.create(RefreshTokenInterface::class.java)
    }


    @Provides
    @ApiClient
    @Singleton
    fun provideApiService(@ApiClient retrofit: Retrofit): ApiServiceInterface {
        return retrofit.create(ApiServiceInterface::class.java)
    }

    @Provides
    @Singleton
    fun provideChuckerInterceptor(
        context: Context,
    ): ChuckerInterceptor =
        ChuckerInterceptor.Builder(context = context)
            .collector(
                collector = ChuckerCollector(
                    context = context,
                    showNotification = true,
                    retentionPeriod = RetentionManager.Period.ONE_HOUR,
                )
            )
            .maxContentLength(length = 120000L)
            .redactHeaders(emptySet())
            .alwaysReadResponseBody(
                enable = true
            )
            .build()

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application
    }

    @Provides
    @Singleton
    fun provideFirebaseInstallations(): FirebaseInstallations {
        return FirebaseInstallations.getInstance()
    }

    @Provides
    @Singleton
    fun provideGeocoder(context: Context): Geocoder {
        return Geocoder(context, Locale("in", "ID"))
    }

    /*
    DATA SOURCE
    */
    @Provides
    @Singleton
    fun provideUserDataSource(@ApiClient httpClient: ApiServiceInterface): UserRemoteDataSource {
        return DefaultUserRemoteDataSource(httpClient)
    }

    @Provides
    @Singleton
    fun provideOrderDataSource(@ApiClient httpClient: ApiServiceInterface): OrderRemoteDataSource {
        return DefaultOrderRemoteDataSource(httpClient)
    }

    @Provides
    @Singleton
    fun provideLocationDataSource(@ApiClient httpClient: ApiServiceInterface): LocationRemoteDataSource {
        return DefaultLocationDataSource(httpClient)
    }

    @Provides
    @Singleton
    fun provideHomeDataSource(@ApiClient httpClient: ApiServiceInterface): HomeRemoteDataSource {
        return DefaultHomeRemoteDataSource(httpClient)
    }

    @Provides
    @Singleton
    fun provideChatDataSource(@ApiClient httpClient: ApiServiceInterface): ChatRemoteDataSource {
        return DefaultChatRemoteDataSource(httpClient)
    }

    @Provides
    @Singleton
    fun provideWalletDataSource(@ApiClient httpClient: ApiServiceInterface): WalletRemoteDataSource {
        return DefaultWalletDataSource(httpClient)
    }

    @Provides
    @Singleton
    fun providePinWalletDataSource(@ApiClient httpClient: ApiServiceInterface): PinWalletRemoteDataSource {
        return DefaultPinWalletDataSource(httpClient)
    }

    @Provides
    @Singleton
    fun provideBillDataSource(@ApiClient httpClient: ApiServiceInterface): BillRemoteDataSource {
        return DefaultBillDataSource(httpClient)
    }

    @Provides
    @Singleton
    fun provideNotificationDataSource(@ApiClient httpClient: ApiServiceInterface): NotificationDataSource {
        return DefaultNotificationDataSource(httpClient)
    }

    @Provides
    @Singleton
    fun providePromoDataSource(@ApiClient httpClient: ApiServiceInterface): PromoRemoteDataSource {
        return DefaultPromoRemoteDataSource(httpClient)
    }

    @Provides
    @Singleton
    fun provideHistoryDataSource(@ApiClient httpClient: ApiServiceInterface): HistoryOrderDataSource {
        return DefaultHistoryOrderDataSource(httpClient)
    }

    @Provides
    @Singleton
    fun provideFoodDataSource(@ApiClient httpClient: ApiServiceInterface): FoodRemoteDataSource {
        return DefaultFoodRemoteDataSource(httpClient)
    }

    @Provides
    @Singleton
    fun provideReferralDataSource(@ApiClient httpClient: ApiServiceInterface): ReferralRemoteDataSource {
        return DefaultReferralDataSource(httpClient)
    }
}