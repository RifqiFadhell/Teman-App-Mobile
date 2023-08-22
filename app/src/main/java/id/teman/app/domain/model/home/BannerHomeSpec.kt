package id.teman.app.domain.model.home

import android.os.Parcelable
import id.teman.app.common.orZero
import id.teman.app.data.dto.HomeBannerItem
import kotlinx.parcelize.Parcelize

@Parcelize
data class BannerHomeSpec(
    val id: String,
    val image: String,
    val title: String,
    val scale: Int,
    val url: String
) : Parcelable

fun List<HomeBannerItem>?.toBannerHomeSpec(): List<BannerHomeSpec> {
    return this?.map {
        BannerHomeSpec(
            it.id.orEmpty(),
            it.image?.url.orEmpty().trim(),
            it.title.orEmpty(),
            it.scale.orZero(),
            it.url.orEmpty()
        )
    }.orEmpty()
}
