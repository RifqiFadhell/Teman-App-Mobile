package id.teman.app.domain.model.camera

import kotlinx.serialization.Serializable

@Serializable
data class CameraSpec(
    val title: String,
    val largeCamera: Boolean = false,
    val cameraType: CameraType
)

@Serializable
enum class CameraType {
    PROFILE
}

@Serializable
data class CameraResult(
    val cameraType: CameraType,
    val uri: String
)