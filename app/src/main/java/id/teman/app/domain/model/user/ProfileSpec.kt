package id.teman.app.domain.model.user

import kotlinx.serialization.Serializable

@Serializable
data class ProfileSpec(
    val name: String,
    val number: String,
    val image: String
)
