package id.teman.app.common

import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class LatLngSerializer : KSerializer<LatLng> {
    override fun deserialize(decoder: Decoder): LatLng {
        val latLngString = decoder.decodeString()
        val parts = latLngString.split(",")
        val latitude = parts.getOrNull(0).orEmpty().toDouble()
        val longitude = parts.getOrNull(1).orEmpty().toDouble()
        return LatLng(latitude, longitude)
    }

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("com.example.LatLng", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LatLng) {
        val latLngString = "${value.latitude},${value.longitude}"
        encoder.encodeString(latLngString)
    }
}