package id.teman.app.di

import android.app.Application
import android.os.Build
import android.provider.Settings
import android.util.Size
import android.view.Surface
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.teman.app.BuildConfig
import id.teman.app.R
import id.teman.app.data.device.DeviceInformation
import id.teman.app.manager.DefaultUserManager
import id.teman.app.manager.UserManager
import id.teman.app.preference.DefaultPreference
import id.teman.app.preference.Preference
import id.teman.app.ui.camera.CustomCamera
import id.teman.app.ui.camera.DefaultCustomCamera
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

private const val USER_PREFERENCES = "user_preferences"

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCameraSelector(): CameraSelector {
        return CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
    }

    @Provides
    @Singleton
    fun provideCameraProvider(application: Application): ProcessCameraProvider {
        return ProcessCameraProvider.getInstance(application).get()
    }

    @Provides
    @Singleton
    fun provideLocationRequest() = LocationRequest
        .Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).apply {
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setWaitForAccurateLocation(true)
        }.build()

    @Provides
    @Singleton
    fun provideCameraPreview(): Preview {
        return Preview.Builder()
            .setTargetResolution(Size(325, 205))
            .setTargetRotation(Surface.ROTATION_0)
            .build()
    }

    @Provides
    @Singleton
    fun provideImageCapture(): ImageCapture {
        return ImageCapture.Builder()
            .setTargetResolution(Size(325, 205))
            .setTargetRotation(Surface.ROTATION_0)
            .build()
    }

    @Provides
    @Singleton
    fun provideCamera(
        cameraProvider: ProcessCameraProvider,
        selector: CameraSelector,
        imageCapture: ImageCapture,
        preview: Preview
    ): CustomCamera {
        return DefaultCustomCamera(cameraProvider, selector, preview, imageCapture)
    }

    @Singleton
    @Provides
    fun provideDeviceInformation(application: Application): DeviceInformation {
        return DeviceInformation(
            deviceId = Settings.Secure.getString(
                application.contentResolver,
                Settings.Secure.ANDROID_ID
            ),
            deviceName = Build.BRAND
        )
    }

    @Singleton
    @Provides
    fun provideUserManager(): UserManager = DefaultUserManager()

    @Singleton
    @Provides
    fun provideRemoteConfig(): FirebaseRemoteConfig {
        return FirebaseRemoteConfig.getInstance().apply {
            val setting = remoteConfigSettings {
                minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) {
                    0
                } else {
                    3600
                }
            }
            setConfigSettingsAsync(setting)
            setDefaultsAsync(R.xml.remote_config_defaults)
        }
    }

    @Provides
    @Singleton
    fun providePreference(dataStore: DataStore<Preferences>): Preference {
        return DefaultPreference(dataStore)
    }

    @Singleton
    @Provides
    fun providePreferencesDataStore(application: Application): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { application.preferencesDataStoreFile(USER_PREFERENCES) }
        )
    }
}