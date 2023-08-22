package id.teman.app.data.device

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class DeviceInformation(
    val deviceName: String,
    val deviceId: String
)