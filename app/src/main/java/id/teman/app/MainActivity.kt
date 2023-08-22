@file:Suppress("DEPRECATION")

package id.teman.app

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.plusAssign
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.SettingsClient
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.navigation.popUpTo
import com.ramcosta.composedestinations.spec.Route
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import dagger.hilt.android.AndroidEntryPoint
import id.teman.app.common.UpdatePage
import id.teman.app.common.isNotNullOrEmpty
import id.teman.app.domain.model.user.UserInfo
import id.teman.app.preference.Preference
import id.teman.app.ui.NavGraphs
import id.teman.app.ui.appCurrentDestinationAsState
import id.teman.app.ui.bottomnav.BottomBar
import id.teman.app.ui.destinations.Destination
import id.teman.app.ui.destinations.HomeScreenDestination
import id.teman.app.ui.destinations.LoginScreenDestination
import id.teman.app.ui.destinations.NotificationScreenDestination
import id.teman.app.ui.destinations.OnBoardingScreenDestination
import id.teman.app.ui.destinations.OrdersListScreenDestination
import id.teman.app.ui.destinations.PromoScreenDestination
import id.teman.app.ui.destinations.RegisterScreenDestination
import id.teman.app.ui.sharedviewmodel.LocationPermissionState
import id.teman.app.ui.sharedviewmodel.MainViewModel
import id.teman.app.ui.startAppDestination
import id.teman.app.ui.theme.TemanUlaUserAppTheme
import id.teman.app.ui.theme.Theme
import id.teman.coreui.typography.UiColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    private var fusedLocationClient: FusedLocationProviderClient? = null

    private lateinit var locationCallback: LocationCallback

    @Inject
    lateinit var preference: Preference

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allPermissionGranted = permissions.values.all { it }
        if (allPermissionGranted) {
            getCurrentLocationSettings()
        } else {
            mainViewModel.changeLocationStatusState(LocationPermissionState.LocationDenied)
            mainViewModel.changeLoadingState(false)
        }
    }

    private val gpsLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            startLocationUpdates()
        } else {
            mainViewModel.changeLocationStatusState(LocationPermissionState.LocationDenied)
            mainViewModel.changeLoadingState(false)
        }
    }

    private fun startLocationUpdates() {
        mainViewModel.changeLocationStatusState(LocationPermissionState.LocationActive)
        if (fusedLocationClient != null) {
            fusedLocationClient?.requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper()
            )
        } else {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient?.requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper()
            )
        }
    }

    private val locationRequest = LocationRequest.create().apply {
        interval = 10000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
    }

    private fun getCurrentLocationSettings() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        client.checkLocationSettings(builder.build())
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        val intent = IntentSenderRequest.Builder(exception.resolution).build()
                        gpsLauncher.launch(intent)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Ignore Error
                    }
                }
            }
            .addOnSuccessListener {
                startLocationUpdates()
            }
    }

    fun startLocationPermissionChecker() {
        mainViewModel.changeLoadingState(true)
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun stopLocationUpdates() {
        fusedLocationClient?.removeLocationUpdates(locationCallback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel.changeLoadingState(true)
        startLocationPermissionChecker()
        askNotificationPermission()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                lifecycleScope.launch {
                    val location = locationResult.lastLocation
                    location?.let {
                        mainViewModel.updateUserLocation(it.latitude, it.longitude)
                    }
                    stopLocationUpdates()
                }
            }
        }
        mainViewModel.checkFromDeeplink(intent)
        setContent {
            TemanUlaUserAppTheme {
                // A surface container using the 'background' color from the theme
                ScaffoldNavHost(this, mainViewModel, preference)
            }
            lifecycleScope.launch {
                preference.setDeviceName(Build.MODEL)
            }
        }
    }

    // [START ask_post_notifications]
    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    // [END ask_post_notifications]
}

@Composable
fun SplashScreen() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(UiColor.white),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GlideImage(
            imageModel = R.drawable.ic_splash,
            modifier = Modifier
                .padding()
                .size(280.dp),
            imageOptions = ImageOptions(contentScale = ContentScale.Fit)
        )
    }
}


@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class)
@Composable
private fun ScaffoldNavHost(
    activity: MainActivity,
    mainViewModel: MainViewModel,
    preference: Preference
) {
    var isShow by remember { mutableStateOf(true) }
    val uiState = mainViewModel.locationUiState
    LaunchedEffect(Unit) {
        delay(3.seconds)
        isShow = false
        mainViewModel.checkIsAppUpToDate()
    }

    val engine = rememberAnimatedNavHostEngine(
        rootDefaultAnimations = RootNavGraphDefaultAnimations(
            enterTransition = { fadeIn(animationSpec = tween(700)) },
            exitTransition = { fadeOut(animationSpec = tween(700)) }
        )

    )
    val navController = engine.rememberNavController()
    val packageName = BuildConfig.APPLICATION_ID

    // ATTENTION: This was auto-generated to handle app links.
    val appLinkIntent: Intent = activity.intent
    val appLinkAction: String? = appLinkIntent.action
    val appLinkData: Uri? = appLinkIntent.data

    LaunchedEffect(key1 = uiState.logoutEvent, block = {
        uiState.logoutEvent?.consumeOnce {
            navController.navigate(LoginScreenDestination) {
                popUpTo(NavGraphs.root)
                launchSingleTop
            }
        }
    })
    /*    LaunchedEffect(key1 = uiState.successGetReferral, block = {
            uiState.successGetReferral?.consumeOnce {
                navController.navigate(RegisterScreenDestination(code = it)) {
                    popUpTo(NavGraphs.root)
                    launchSingleTop
                }
            }
        })*/

    val isOnBoardingFinish = runBlocking { preference.getIsOnBoardingFinish.first() }
    val userInfoJson = runBlocking { preference.getUserInfo.first() }
    val startRoute = if (!isOnBoardingFinish) {
        if (mainViewModel.locationUiState.successGetReferral.isNotNullOrEmpty()) {
            RegisterScreenDestination
        } else {
            OnBoardingScreenDestination
        }
    } else if (userInfoJson.isNotBlank()) {
        val userInfo = Json.decodeFromString<UserInfo>(userInfoJson)
        with(FirebaseCrashlytics.getInstance()) {
            setUserId(userInfo.id)
            setCrashlyticsCollectionEnabled(true)
        }
        NavGraphs.root.startRoute
    } else {
        if (mainViewModel.locationUiState.successGetReferral.isNotNullOrEmpty()) {
            RegisterScreenDestination
        } else {
            LoginScreenDestination
        }
    }

    if (uiState.isNeedUpdate) {
        UpdatePage {
            try {
                activity.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$packageName")
                    )
                )
            } catch (e: ActivityNotFoundException) {
                activity.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                    )
                )
            }
        }
    } else {
        if (isShow) {
            SplashScreen()
        } else {
            ScaffoldWrapper(
                startRoute = startRoute,
                navController = navController,
                topBar = { destination, backStackEntry ->

                },
                bottomBar = { destination ->
                    if (destination.shouldShowScaffoldElements) {
                        BottomBar(navController = navController, destination)
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
                            // declare all shared view model that is tight to activity in here
                            dependency(hiltViewModel<MainViewModel>(activity))
                        }
                    )
                }
            )
        }
    }
}

val SupportedNavigationDestination = listOf(
    HomeScreenDestination, PromoScreenDestination, NotificationScreenDestination,
    OrdersListScreenDestination
)
private val Destination.shouldShowScaffoldElements
    get() = SupportedNavigationDestination.contains(this)

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
private fun ScaffoldWrapper(
    startRoute: Route,
    navController: NavHostController,
    topBar: @Composable (Destination, NavBackStackEntry?) -> Unit,
    bottomBar: @Composable (Destination) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val destination =
        navController.appCurrentDestinationAsState().value ?: startRoute.startAppDestination
    val navBackStackEntry = navController.currentBackStackEntry

    val bottomSheetNavigator = rememberBottomSheetNavigator()
    navController.navigatorProvider += bottomSheetNavigator

    ModalBottomSheetLayout(
        bottomSheetNavigator = bottomSheetNavigator,
        sheetShape = RoundedCornerShape(Theme.dimension.size_16dp)
    ) {
        Scaffold(
            topBar = { topBar(destination, navBackStackEntry) },
            bottomBar = { bottomBar(destination) },
            content = content
        )
    }

}