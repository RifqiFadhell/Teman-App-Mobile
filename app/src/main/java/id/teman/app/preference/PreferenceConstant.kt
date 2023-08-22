package id.teman.app.preference

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceConstant {

    val IS_ON_BOARDING_FINISH = booleanPreferencesKey("isOnBoardingFinish")
    val BEARER_TOKEN = stringPreferencesKey("bearerToken")
    val REFRESH_TOKEN = stringPreferencesKey("refreshToken")
    val USER_INFO = stringPreferencesKey("userInfo")
    val IS_USER_LOGIN = booleanPreferencesKey("isUserLogin")
    val DEVICE_NAME = stringPreferencesKey("deviceName")
    val DEVICE_ID = stringPreferencesKey("deviceId")
}