package id.teman.app.ui.bottomnav

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec
import id.teman.app.ui.destinations.FoodHomeScreenDestination
import id.teman.app.ui.destinations.SearchFoodScreenDestination
import id.teman.app.R as RApp
import id.teman.app.ui.destinations.FoodOrdersListScreenDestination
import id.teman.app.ui.destinations.FoodPromoScreenDestination

enum class FoodBottomBarDestination(
    val direction: DirectionDestinationSpec,
    @DrawableRes val icon: Int,
    @StringRes val label: Int
) {
    Explore(FoodHomeScreenDestination, RApp.drawable.ic_explore, RApp.string.nav_food_explore),
    Search(SearchFoodScreenDestination, RApp.drawable.ic_search, RApp.string.nav_food_search),
    Promo(
        FoodPromoScreenDestination,
        RApp.drawable.ic_nav_bottom_promo,
        RApp.string.nav_bottom_promo
    ),
    OrderHistory(
        FoodOrdersListScreenDestination,
        RApp.drawable.ic_nav_bottom_order,
        RApp.string.nav_food_history
    )
}