package id.teman.app.ui.food

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.plusAssign
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.navigation.popUpTo
import com.ramcosta.composedestinations.spec.Route
import id.teman.app.domain.model.order.OrderRequestType
import id.teman.app.ui.NavGraphs
import id.teman.app.ui.appCurrentDestinationAsState
import id.teman.app.ui.bottomnav.FoodBottomBar
import id.teman.app.ui.destinations.Destination
import id.teman.app.ui.destinations.FoodHomeScreenDestination
import id.teman.app.ui.destinations.FoodOrdersListScreenDestination
import id.teman.app.ui.destinations.FoodPromoScreenDestination
import id.teman.app.ui.destinations.HomeScreenDestination
import id.teman.app.ui.destinations.LoginScreenDestination
import id.teman.app.ui.destinations.OrderProcessScreenDestination
import id.teman.app.ui.destinations.SearchFoodScreenDestination
import id.teman.app.ui.sharedviewmodel.MainViewModel
import id.teman.app.ui.startAppDestination
import id.teman.app.ui.theme.Theme

@com.ramcosta.composedestinations.annotation.Destination
@Composable
fun FoodNavScreen(
    navigator: DestinationsNavigator,
    mainViewModel: MainViewModel,
    spec: FoodScreenSpec
) {
    val foodMainViewModel = hiltViewModel<FoodMainViewModel>()
    val uiState = foodMainViewModel.foodUiState
    val foodLocationUiState = foodMainViewModel.foodMainUiState

    LaunchedEffect(key1 = uiState.navigateToMap, block = {
        uiState.navigateToMap?.consumeOnce {
            navigator.popBackStack(HomeScreenDestination.route, false)
            navigator.navigate(
                OrderProcessScreenDestination(
                    orderRequestType = OrderRequestType.FOOD,
                    foodOrders = it
                )
            )
        }
    })

    LaunchedEffect(key1 = foodLocationUiState.FoodChangeEvent, block = {
        foodLocationUiState.FoodChangeEvent?.consumeOnce {
            mainViewModel.changeHomeLocationAddress(it)
        }
    })
    ScaffoldNavHost(mainViewModel, spec, foodMainViewModel)
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class)
@Composable
private fun ScaffoldNavHost(
    mainViewModel: MainViewModel,
    spec: FoodScreenSpec,
    foodMainViewModel: FoodMainViewModel
) {
    val engine = rememberAnimatedNavHostEngine()
    val navController = engine.rememberNavController()


    foodMainViewModel.updateData(mainViewModel.locationUiState, spec)
    val uiState = foodMainViewModel.foodUiState

    LaunchedEffect(key1 = uiState.logoutEvent, block = {
        uiState.logoutEvent?.consumeOnce {
            navController.navigate(LoginScreenDestination) {
                popUpTo(NavGraphs.root)
                launchSingleTop
            }
        }
    })

    val startRoute = FoodHomeScreenDestination
    ScaffoldWrapper(
        startRoute = startRoute,
        navController = navController,
        bottomBar = { destination ->
            if (destination.shouldShowScaffoldElements) {
                FoodBottomBar(navController = navController, destination)
            }
        },
        content = {
            DestinationsNavHost(
                engine = engine,
                navController = navController,
                navGraph = NavGraphs.root,
                modifier = Modifier.padding(it),
                startRoute = startRoute,
                dependenciesContainerBuilder = {
                    dependency(foodMainViewModel)
                }
            )
        }
    )
}

val SupportedNavigationDestination = listOf(
    FoodHomeScreenDestination, SearchFoodScreenDestination, FoodPromoScreenDestination,
    FoodOrdersListScreenDestination
)
private val Destination.shouldShowScaffoldElements
    get() = SupportedNavigationDestination.contains(this)

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
private fun ScaffoldWrapper(
    startRoute: Route,
    navController: NavHostController,
    bottomBar: @Composable (Destination) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val destination =
        navController.appCurrentDestinationAsState().value ?: startRoute.startAppDestination

    val bottomSheetNavigator = rememberBottomSheetNavigator()
    navController.navigatorProvider += bottomSheetNavigator

    ModalBottomSheetLayout(
        bottomSheetNavigator = bottomSheetNavigator,
        sheetShape = RoundedCornerShape(Theme.dimension.size_16dp)
    ) {
        Scaffold(
            bottomBar = { bottomBar(destination) },
            content = content
        )
    }
}