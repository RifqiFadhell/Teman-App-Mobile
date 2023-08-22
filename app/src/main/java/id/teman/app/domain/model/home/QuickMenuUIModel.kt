package id.teman.app.domain.model.home

import id.teman.app.R as rAppModule
import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import id.teman.app.data.dto.MenusItem

enum class QuickMenuUIModel(
    val title: String,
    val key: String,
    @DrawableRes val icon: Int
) {
    Teman_Bike("T-Bike", "bike", rAppModule.drawable.ic_teman_bike),
    Teman_Car("T-Car","car", rAppModule.drawable.ic_teman_car),
    Teman_Food("T-Food", "food", rAppModule.drawable.ic_teman_food),
    Teman_Bills("T-Bills", "bill", rAppModule.drawable.ic_teman_bills),
    Teman_Send("T-Send", "send", rAppModule.drawable.ic_teman_send),
    Teman_Other("Lainnya", "other", rAppModule.drawable.ic_teman_other)
}

@Keep
data class QuickMenuModel(
    val title: String,
    val key: String,
    @DrawableRes val icon: Int = 0,
    val iconServer: String? = ""
)

fun List<MenusItem>.quickHomeMenus(): List<QuickMenuModel> {
    return if (this.isEmpty()) {
        QuickMenuUIModel.values().map {
            QuickMenuModel(it.title, it.key, it.icon)
        }
    } else {
        this.map {
            QuickMenuModel(title = it.title, key = it.key, iconServer = it.icon.trim())
        }
    }
}

fun List<MenusItem>.quickFoodMenus(): List<QuickMenuModel> {
    return if (this.isEmpty()) {
        QuickMenuFoodUIModel.values().map {
            QuickMenuModel(it.title, it.key, it.icon)
        }
    } else {
        this.map {
            QuickMenuModel(title = it.title, key = it.key, iconServer = it.icon)
        }
    }
}

enum class QuickMenuFoodUIModel(
    val title: String,
    val key: String,
    @DrawableRes val icon: Int
) {
    Food_Nearby("Terdekat","nearby", rAppModule.drawable.ic_food_nearby),
    Food_Best_Seller("Terlaris", "best_selling",rAppModule.drawable.ic_food_best_seller),
    Food_Promo("Lagi Promo","promo", rAppModule.drawable.ic_food_promo)
}