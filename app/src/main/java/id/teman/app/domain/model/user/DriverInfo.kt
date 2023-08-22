package id.teman.app.domain.model.user

import id.teman.app.common.orZero
import id.teman.app.data.dto.driver.DriverBasicInfoDto
import kotlinx.serialization.Serializable

@Serializable
data class DriverInfo(
    val latitude: Double,
    val longitude: Double,
    val bearing: Float,
    val id: String
)

fun DriverBasicInfoDto.toDriverInfo(): DriverInfo = DriverInfo(
    latitude = lat.orZero(),
    longitude = lng.orZero(),
    id = id.orEmpty(),
    bearing = orientation ?: 0f
)