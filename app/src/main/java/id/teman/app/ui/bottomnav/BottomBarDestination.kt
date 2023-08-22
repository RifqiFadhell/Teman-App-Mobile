package id.teman.app.ui.bottomnav

import id.teman.app.R as RApp
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec
import id.teman.app.ui.destinations.HomeScreenDestination
import id.teman.app.ui.destinations.NotificationScreenDestination
import id.teman.app.ui.destinations.OrdersListScreenDestination
import id.teman.app.ui.destinations.PromoScreenDestination

enum class BottomBarDestination(
    val direction: DirectionDestinationSpec,
    @DrawableRes val icon: Int,
    @StringRes val label: Int
) {
    Home(HomeScreenDestination, RApp.drawable.ic_nav_bottom_home, RApp.string.nav_bottom_home),
    Promo(PromoScreenDestination, RApp.drawable.ic_nav_bottom_promo, RApp.string.nav_bottom_promo),
    OrderHistory(
        OrdersListScreenDestination,
        RApp.drawable.ic_nav_bottom_order,
        RApp.string.nav_bottom_order
    ),
    Notification(
        NotificationScreenDestination,
        RApp.drawable.ic_nav_bottom_notification,
        RApp.string.nav_bottom_notification
    )
}