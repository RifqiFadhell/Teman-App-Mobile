package id.teman.app.ui.food.summary

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SummaryFoodSpec(
    val idRestaurant: String,
    val nameRestaurant: String
): Parcelable
