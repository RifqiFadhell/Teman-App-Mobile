@file:OptIn(ExperimentalTextApi::class)

package id.teman.app.ui.food.resto

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.Lifecycle
import com.google.android.gms.maps.model.LatLng
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.R
import id.teman.app.common.ButtonContinue
import id.teman.app.common.ButtonType
import id.teman.app.common.CustomDivider
import id.teman.app.common.CustomImageIcon
import id.teman.app.common.CustomLoading
import id.teman.app.common.DisposableEffectOnLifecycleEvent
import id.teman.app.common.ErrorOrderUI
import id.teman.app.common.QuantitySelector
import id.teman.app.common.TopBar
import id.teman.app.common.convertToDp
import id.teman.app.common.convertToRupiah
import id.teman.app.common.isNotNullOrEmpty
import id.teman.app.common.orZero
import id.teman.app.domain.model.restaurant.DetailRestaurantModel
import id.teman.app.domain.model.restaurant.MenuRestaurant
import id.teman.app.ui.bill.list.DetailItemRow
import id.teman.app.ui.destinations.SummaryFoodScreenDestination
import id.teman.app.ui.food.FoodMainViewModel
import id.teman.app.ui.food.summary.SummaryFoodSpec
import id.teman.app.ui.theme.Theme
import id.teman.app.ui.theme.buttons.TemanCircleButton
import id.teman.app.ui.theme.buttons.TemanSecondaryButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterialApi::class)
@Composable
@Destination
fun RestaurantScreen(
    navigator: DestinationsNavigator,
    restaurantId: String,
    latLng: LatLng,
    foodViewModel: FoodMainViewModel
) {
    val uiState = foodViewModel.foodUiState
    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_START) {
        foodViewModel.getDetailRestaurant(restaurantId, latLng)
    }
    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_CREATE) {
        foodViewModel.getDetailRestaurant(restaurantId, latLng)
    }
    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_RESUME) {
        foodViewModel.getDetailRestaurant(restaurantId, latLng)
    }
    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_DESTROY) {
        foodViewModel.resetDataBack()
    }

    val headerHeight = Theme.dimension.size_250dp
    val headerHeightPx = with(LocalDensity.current) { headerHeight.toPx() }
    val configuration = LocalConfiguration.current

    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
        confirmStateChange = { false }
    )
    val coroutineScope = rememberCoroutineScope()
    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = RoundedCornerShape(
            topStart = Theme.dimension.size_32dp,
            topEnd = Theme.dimension.size_32dp
        ),
        sheetElevation = Theme.dimension.size_8dp,
        modifier = Modifier.fillMaxSize(),
        sheetContent = {
            OpenHoursPage(spec = uiState.detailRestaurant) {
                coroutineScope.launch {
                    modalSheetState.hide()
                }
            }
        }, content = {
            Scaffold(
                topBar = {
                    ToolbarRestaurant(uiState.detailRestaurant?.name.orEmpty(), {
                        coroutineScope.launch {
                            modalSheetState.show()
                        }
                    }) {
                        navigator.popBackStack()
                    }
                }, content = {
                    val scroll = rememberScrollState()
                    Box(modifier = Modifier.fillMaxSize()) {
                        HeaderTopBar(
                            image = uiState.detailRestaurant?.photoRestaurant.orEmpty(),
                            scroll = scroll,
                            headerHeightPx = headerHeightPx,
                            headerHeight = headerHeight
                        )
                        Column(
                            modifier = Modifier
                                .verticalScroll(scroll)
                        ) {
                            Spacer(Modifier.height(headerHeight))
                            uiState.detailRestaurant?.let { HeaderRestaurant(it) }
                            Spacer(modifier = Modifier.height(Theme.dimension.size_12dp))
                            val menuBySection =
                                uiState.listMenu?.groupBy { it.titleMenu }?.entries?.sortedBy { it.key.order }
                                LazyColumn(
                                    modifier = Modifier
                                        .heightIn(
                                            min = (216 * menuBySection?.size.orZero()).convertToDp(),
                                            max = configuration.screenHeightDp.convertToDp()
                                        )
                                        .padding(bottom = Theme.dimension.size_72dp)
                                ) {
                                    menuBySection?.forEach { (title, items) ->
                                        item {
                                            Text(
                                                title.title,
                                                style = UiFont.poppinsSubHSemiBold,
                                                modifier = Modifier.padding(
                                                    start = Theme.dimension.size_16dp,
                                                    top = Theme.dimension.size_24dp
                                                )
                                            )
                                        }
                                        items(items.count()) {
                                            ItemMenuMainRestaurant(
                                                modifier = Modifier.padding(
                                                    horizontal = Theme.dimension.size_16dp,
                                                    vertical = Theme.dimension.size_8dp
                                                ),
                                                foodViewModel = foodViewModel,
                                                items[it]
                                            )
                                        }
                                    }
                                }
                        }
                    }
                    if (uiState.loading) {
                        Dialog(
                            onDismissRequest = { },
                            DialogProperties(
                                dismissOnBackPress = false,
                                dismissOnClickOutside = false
                            )
                        ) {
                            CustomLoading(modifier = Modifier.fillMaxSize())
                        }
                    }
                    if (uiState.error.isNotNullOrEmpty()) {
                        Column {
                            ErrorOrderUI(message = uiState.error.orEmpty()) {
                                foodViewModel.getDetailRestaurant(restaurantId, latLng)
                            }
                        }
                    }
                }, bottomBar = {
                    if (uiState.totalPrice > 0.0) {
                        ButtonContinue(
                            totalPrice = uiState.totalPrice,
                            discount = uiState.totalDiscount,
                            title = "Order T-Food"
                        ) {
                            navigator.navigate(
                                SummaryFoodScreenDestination(
                                    spec = SummaryFoodSpec(
                                        idRestaurant = restaurantId,
                                        nameRestaurant = uiState.detailRestaurant?.name.orEmpty()
                                    )
                                )
                            )
                        }
                    }
                })
        })
}

@Composable
fun OpenHoursPage(spec: DetailRestaurantModel?, onHide: () -> Unit) {
    TopBar(title = "Detail Restoran", icon = R.drawable.ic_round_close) {
        onHide()
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.dimension.size_16dp)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = spec?.name.orEmpty(),
            style = UiFont.poppinsH4sSemiBold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(Theme.dimension.size_12dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = spec?.description.orEmpty(),
            style = UiFont.poppinsP2Medium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(Theme.dimension.size_36dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Alamat Restoran",
            style = UiFont.poppinsP2SemiBold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(Theme.dimension.size_6dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = spec?.address.orEmpty(),
            style = UiFont.poppinsP2Medium,
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "${spec?.timeEstimation} • ${spec?.distance}",
            style = UiFont.poppinsP2SemiBold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(Theme.dimension.size_16dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = CenterVertically) {
                GlideImage(
                    imageModel = R.drawable.ic_star,
                    modifier = Modifier.size(Theme.dimension.size_36dp)
                )
                Spacer(modifier = Modifier.width(Theme.dimension.size_6dp))
                Text(
                    spec?.rating.orEmpty(),
                    style = UiFont.poppinsH4sBold.copy(
                        color = Color.Black,
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        ),
                    )
                )
            }
            Text(
                spec?.finalRating.orEmpty(),
                style = UiFont.poppinsH5Medium.copy(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                )
            )
        }
        LazyColumn(modifier = Modifier.padding(top = Theme.dimension.size_24dp)) {
            items(spec?.openHour?.size.orZero()) { index ->
                val item = spec?.openHour?.get(index)
                DetailItemRow(title = item?.day.orEmpty(), value = "${item?.start} - ${item?.end}")
            }
        }
    }
}

@Composable
fun ToolbarRestaurant(title: String, onShow: () -> Unit, backPressed: () -> Unit) {
    TopAppBar(
        elevation = Theme.dimension.size_0dp,
        backgroundColor = Color.Transparent,
        contentPadding = PaddingValues(horizontal = Theme.dimension.size_16dp),
        modifier = Modifier.height(Theme.dimension.size_56dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Theme.dimension.size_16dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TemanCircleButton(
                icon = R.drawable.ic_arrow_back,
                circleModifier = Modifier
                    .size(Theme.dimension.size_48dp)
                    .clickable {
                        backPressed()
                    },
                iconModifier = Modifier.size(Theme.dimension.size_24dp),
                circleBackgroundColor = Color.White
            )
            Text(
                title,
                style = UiFont.poppinsH5SemiBold,
                modifier = Modifier.align(CenterVertically)
            )
            Row {
                TemanCircleButton(
                    icon = R.drawable.ic_info,
                    circleModifier = Modifier
                        .size(Theme.dimension.size_48dp)
                        .clickable {
                            onShow()
                        },
                    iconModifier = Modifier.size(Theme.dimension.size_24dp),
                    circleBackgroundColor = Color.White
                )
            }
        }
    }
}

@Composable
fun HeaderTopBar(image: String, scroll: ScrollState, headerHeightPx: Float, headerHeight: Dp) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(headerHeight)
        .graphicsLayer {
            translationY = -scroll.value.toFloat() / 2f // Parallax effect
            alpha = (-1f / headerHeightPx) * scroll.value + 1
        }
    ) {
        GlideImage(
            imageModel = image,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2 / 1f)
                .heightIn(max = Theme.dimension.size_250dp),
//            imageOptions = ImageOptions(contentScale = ContentScale.FillBounds),
            failure = {
                Image(
                    painter = painterResource(id = R.drawable.ic_no_image),
                    contentDescription = "failed"
                )
            }
        )
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun HeaderRestaurant(spec: DetailRestaurantModel) {
    Column(
        modifier = Modifier
            .padding(
                top = Theme.dimension.size_32dp,
            )
            .background(
                color = UiColor.white,
                RoundedCornerShape(
                    topStart = Theme.dimension.size_16dp,
                    topEnd = Theme.dimension.size_16dp
                )
            )
    ) {
        Text(
            modifier = Modifier
                .padding(
                    top = Theme.dimension.size_16dp,
                    start = Theme.dimension.size_16dp,
                    end = Theme.dimension.size_16dp
                ),
            text = spec.name,
            style = UiFont.poppinsH3SemiBold
        )
        Text(
            modifier = Modifier.padding(
                start = Theme.dimension.size_16dp,
                top = Theme.dimension.size_4dp
            ),
            text = spec.categories,
            style = UiFont.poppinsP2Medium
        )

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    modifier = Modifier,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CustomImageIcon(
                        text = spec.rating,
                        icon = R.drawable.ic_star
                    )
                    Text(
                        spec.finalRating,
                        style = UiFont.poppinsCaptionSemiBold.copy(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            )
                        ),
                        textAlign = TextAlign.Center
                    )
                }
                CustomDivider()
                Column(
                    modifier = Modifier,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        spec.distance,
                        style = UiFont.poppinsCaptionSemiBold.copy(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            )
                        ),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "Jarak",
                        style = UiFont.poppinsCaptionSemiBold.copy(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            )
                        ),
                        textAlign = TextAlign.Center
                    )
                }
                CustomDivider()
                Column(
                    modifier = Modifier,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CustomImageIcon(
                        text = spec.timeEstimation,
                        icon = R.drawable.ic_teman_bike
                    )
                    Text(
                        "Waktu Antar",
                        style = UiFont.poppinsCaptionSemiBold.copy(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            )
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun ItemMenuMainRestaurant(
    modifier: Modifier,
    foodViewModel: FoodMainViewModel,
    spec: MenuRestaurant,
    isFromSummary: Boolean = false
) {
    var note by remember { mutableStateOf(spec.notes) }
    var isSaved by remember { mutableStateOf(false) }
    Card(
        modifier = modifier,
        elevation = Theme.dimension.size_1dp
    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Theme.dimension.size_16dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        spec.name,
                        style = UiFont.poppinsP2SemiBold.copy(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            )
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        spec.description,
                        style = UiFont.poppinsCaptionMedium.copy(color = UiColor.neutral500),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row {
                        Text(
                            if (spec.strikeTrough) spec.promoPrice.convertToRupiah() else spec.price.convertToRupiah(),
                            style = UiFont.poppinsP2SemiBold.copy(color = UiColor.tertiaryBlue500),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (spec.strikeTrough) Text(
                            spec.price.convertToRupiah(),
                            style = UiFont.poppinsP2Medium.copy(color = UiColor.neutral200),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }
                GlideImage(
                    imageModel = spec.productPhoto,
                    modifier = Modifier
                        .size(Theme.dimension.size_64dp)
                        .clip(RoundedCornerShape(Theme.dimension.size_6dp)),
                    failure = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_no_image),
                            contentDescription = "failed"
                        )
                    }
                )
            }
            if (spec.qty > 0) {
                QuantitySelector(
                    count = spec.qty,
                    decreaseItemCount = {
                        foodViewModel.decreaseItem(spec.id)
                    },
                    increaseItemCount = {
                        foodViewModel.increaseItem(spec.id)
                    }, isFromSummary = isFromSummary
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            shape = RoundedCornerShape(Theme.dimension.size_4dp),
                            color = Color.Transparent
                        )
                        .focusable(isSaved),
                    value = note,
                    placeholder = {
                        Text(
                            "Catatan Makanan",
                            style = UiFont.poppinsP2Medium.copy(color = UiColor.neutral400)
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = UiColor.neutral100,
                        cursorColor = UiColor.black,
                        unfocusedBorderColor = UiColor.neutral100
                    ),
                    onValueChange = {
                        note = it
                        isSaved = false
                    },
                    trailingIcon = {
                        Box(
                            modifier = Modifier
                                .size(TextFieldDefaults.MinHeight)
                                .background(color = if (isSaved) UiColor.neutral100 else UiColor.primaryRed500)
                                .clickable {
                                    foodViewModel.updateNotes(spec.id, note)
                                    isSaved = true
                                }
                        ) {
                            Text(
                                "Simpan", style = UiFont.poppinsCaptionSemiBold,
                                modifier = Modifier.align(Alignment.Center),
                                textAlign = TextAlign.Center,
                                color = UiColor.white
                            )
                        }
                    }
                )
            } else {
                TemanSecondaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = Theme.dimension.size_16dp,
                            end = Theme.dimension.size_16dp,
                            bottom = Theme.dimension.size_16dp
                        ),
                    content = "+Tambah",
                    buttonType = ButtonType.Medium,
                    borderRadius = Theme.dimension.size_20dp
                ) {
                    foodViewModel.increaseItem(spec.id)
                }
            }
        }
    }
}