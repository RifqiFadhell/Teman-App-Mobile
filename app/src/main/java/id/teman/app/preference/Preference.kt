package id.teman.app.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import id.teman.app.common.orFalse
import id.teman.app.preference.PreferenceConstant.BEARER_TOKEN
import id.teman.app.preference.PreferenceConstant.DEVICE_ID
import id.teman.app.preference.PreferenceConstant.DEVICE_NAME
import id.teman.app.preference.PreferenceConstant.IS_ON_BOARDING_FINISH
import id.teman.app.preference.PreferenceConstant.IS_USER_LOGIN
import id.teman.app.preference.PreferenceConstant.REFRESH_TOKEN
import id.teman.app.preference.PreferenceConstant.USER_INFO
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface Preference {
    val getIsUserLoggedIn: Flow<Boolean>
    val getRefreshToken: Flow<String>
    val getBearerToken: Flow<String>
    val getUserInfo: Flow<String>
    val getDeviceName: Flow<String>
    val getDeviceId: Flow<String>
    val getIsOnBoardingFinish: Flow<Boolean>

    suspend fun setIsUserLoggedIn(newValue: Boolean)
    suspend fun setRefreshToken(newValue: String)
    suspend fun setBearerToken(newValue: String)
    suspend fun setUserInfo(newValue: String)
    suspend fun setDeviceName(newValue: String)
    suspend fun setDeviceId(newValue: String)
    suspend fun setIsOnBoardingFinish(newValue: Boolean)
    suspend fun clearLoginValues()
}

class DefaultPreference @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : Preference {
    override val getIsUserLoggedIn: Flow<Boolean>
        get() = dataStore.data.map { preference -> preference[IS_USER_LOGIN].orFalse() }
    override val getRefreshToken: Flow<String>
        get() = dataStore.data.map { preference ->
            preference[REFRESH_TOKEN].orEmpty()
        }
    override val getBearerToken: Flow<String>
        get() = dataStore.data.map { preference ->
            preference[BEARER_TOKEN].orEmpty()
        }
    override val getUserInfo: Flow<String>
        get() = dataStore.data.map { preference ->
            preference[USER_INFO].orEmpty()
        }
    override val getDeviceName: Flow<String>
        get() = dataStore.data.map { preference ->
            preference[DEVICE_NAME].orEmpty()
        }
    override val getDeviceId: Flow<String>
        get() = dataStore.data.map { preference ->
            preference[DEVICE_ID].orEmpty()
        }
    override val getIsOnBoardingFinish: Flow<Boolean>
        get() = dataStore.data.map { preference -> preference[IS_ON_BOARDING_FINISH].orFalse() }

    override suspend fun setIsUserLoggedIn(newValue: Boolean) {
        dataStore.edit { preference ->
            preference[IS_USER_LOGIN] = newValue
        }
    }

    override suspend fun setRefreshToken(newValue: String) {
        dataStore.edit { preference ->
            preference[REFRESH_TOKEN] = newValue
        }
    }

    override suspend fun setBearerToken(newValue: String) {
        dataStore.edit { preference ->
            preference[BEARER_TOKEN] = newValue
        }
    }

    override suspend fun setUserInfo(newValue: String) {
        dataStore.edit { preference ->
            preference[USER_INFO] = newValue
        }
    }

    override suspend fun setDeviceName(newValue: String) {
        dataStore.edit { preference ->
            preference[DEVICE_NAME] = newValue
        }
    }

    override suspend fun setDeviceId(newValue: String) {
        dataStore.edit { preference ->
            preference[DEVICE_ID] = newValue
        }
    }

    override suspend fun setIsOnBoardingFinish(newValue: Boolean) {
        dataStore.edit { preference ->
            preference[IS_ON_BOARDING_FINISH] = newValue
        }
    }

    override suspend fun clearLoginValues() {
        dataStore.edit { preference ->
            preference.remove(BEARER_TOKEN)
            preference.remove(REFRESH_TOKEN)
            preference.remove(USER_INFO)
            preference[IS_USER_LOGIN] = false
        }
    }

}