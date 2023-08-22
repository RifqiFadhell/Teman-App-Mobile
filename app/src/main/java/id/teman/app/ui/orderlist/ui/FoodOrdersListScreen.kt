package id.teman.app.ui.orderlist.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.R
import id.teman.app.common.CustomLoading
import id.teman.app.common.DisposableEffectOnLifecycleEvent
import id.teman.app.common.EmptyState
import id.teman.app.common.TopBar
import id.teman.app.common.noRippleClickable
import id.teman.app.domain.model.history.HistoryModel
import id.teman.app.domain.model.order.OrderRequestType
import id.teman.app.domain.model.order.TransportRequestType
import id.teman.app.ui.NavGraphs
import id.teman.app.ui.destinations.FoodHomeScreenDestination
import id.teman.app.ui.destinations.OrderProcessScreenDestination
import id.teman.app.ui.food.summary.ItemMenuSummary
import id.teman.app.ui.ordermapscreen.inprogress.bottomsheet.ContentDetailOriginDestinationSectionItem
import id.teman.app.ui.ordermapscreen.inprogress.bottomsheet.ContentDetailPaymentSection
import id.teman.app.ui.ordermapscreen.inprogress.bottomsheet.InfoDetailSend
import id.teman.app.ui.theme.Theme
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalTextApi::class)
@Destination
@Composable
fun FoodOrdersListScreen(
    navigator: DestinationsNavigator,
    viewModel: OrdersHistoryViewModel = hiltViewModel()
) {
    val uiState = viewModel.historyUiState
    var itemSelected by remember { mutableStateOf(HistoryModel()) }
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    var selectedRating by remember { mutableStateOf(itemSelected.rating) }

    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
        confirmStateChange = { false }
    )

    BackHandler {
        navigator.navigate(FoodHomeScreenDestination) {
            popUpTo(NavGraphs.root) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_RESUME) {
        viewModel.getHistoryList()
        coroutineScope.launch {
            modalSheetState.hide()
        }
    }
    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_PAUSE) {
        coroutineScope.launch {
            modalSheetState.hide()
        }
    }

    LaunchedEffect(key1 = viewModel.historyUiState.successRating, block = {
        viewModel.historyUiState.successRating?.let {
            Toast.makeText(context, "Sukses update rating driver", Toast.LENGTH_SHORT).show()
        }
    })

    if (uiState.loading) {
        Dialog(
            onDismissRequest = { },
            DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            CustomLoading(modifier = Modifier.fillMaxSize())
        }
    }
    if (uiState.historyList.isNullOrEmpty()) EmptyState(
        icon = R.drawable.ic_empty_order,
        title = "Belum pernah pakai Teman?",
        description = "Teman akan temenin kamu bepergian, makan, kirim barang, dan bayar ini itu. Cobain, yuk!"
    )
    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = RoundedCornerShape(
            topStart = Theme.dimension.size_32dp,
            topEnd = Theme.dimension.size_32dp
        ),
        sheetElevation = Theme.dimension.size_8dp,
        modifier = Modifier.fillMaxSize(),
        sheetContent = {
            TopBar(title = "Detail Riwayat Pesanan") {
                coroutineScope.launch {
                    modalSheetState.hide()
                }
            }
            LazyColumn(
                modifier = Modifier.padding(Theme.dimension.size_12dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "Nomor Pesanan\n#${itemSelected.numberOrder}",
                            style = UiFont.poppinsP3SemiBold,
                            modifier = Modifier
                                .padding(
                                    bottom = Theme.dimension.size_10dp
                                ),
                            textAlign = TextAlign.Center,
                        )
                        GlideImage(
                            imageModel = R.drawable.ic_copy,
                            modifier = Modifier
                                .padding(Theme.dimension.size_8dp)
                                .size(Theme.dimension.size_18dp)
                                .clickable {
                                    clipboardManager.setText(AnnotatedString(itemSelected.numberOrder))
                                }
                        )
                    }
                    Divider(
                        color = UiColor.neutral100, thickness = 1.dp,
                        modifier = Modifier
                    )
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "Rating ke Driver",
                            style = UiFont.poppinsP3SemiBold,
                            modifier = Modifier
                                .padding(
                                    bottom = Theme.dimension.size_12dp,
                                    top = Theme.dimension.size_10dp
                                ),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (i in 0 until 5) {
                            GlideImage(
                                imageModel = R.drawable.ic_star,
                                imageOptions = ImageOptions(
                                    colorFilter = ColorFilter.tint(
                                        color = if (i >= selectedRating) UiColor.neutral100 else UiColor.primaryYellow500
                                    )
                                ),
                                modifier = Modifier
                                    .size(Theme.dimension.size_30dp)
                                    .clickable {
                                        if (itemSelected.status == "arrived") {
                                            selectedRating = i + 1
                                            viewModel.sendRating(i + 1, "", itemSelected.id)
                                        }
                                    }
                            )
                        }
                    }
                }
                item {
                    Row(
                        modifier = Modifier.padding(
                            top = Theme.dimension.size_16dp,
                            end = Theme.dimension.size_16dp,
                            bottom = Theme.dimension.size_6dp
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        GlideImage(
                            imageModel = itemSelected.driverPhoto,
                            modifier = Modifier
                                .size(Theme.dimension.size_48dp)
                                .clip(CircleShape),
                            failure = {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_person_mamoji),
                                    contentDescription = "failed"
                                )
                            }
                        )
                        Column(
                            modifier = Modifier
                                .weight(8f)
                                .padding(start = Theme.dimension.size_20dp)
                        ) {
                            Text(
                                itemSelected.driverName,
                                modifier = Modifier.padding(bottom = Theme.dimension.size_4dp),
                                style = UiFont.poppinsH4sSemiBold.copy(
                                    platformStyle = PlatformTextStyle(
                                        includeFontPadding = false
                                    ),
                                    lineHeightStyle = LineHeightStyle(
                                        alignment = LineHeightStyle.Alignment.Center,
                                        trim = LineHeightStyle.Trim.LastLineBottom
                                    )
                                )
                            )
                            Text(itemSelected.driverLicence)
                        }
                    }
                    Divider(
                        color = UiColor.neutral100, thickness = 1.dp,
                        modifier = Modifier
                    )
                }
                item {
                    Text(
                        "Detail pesanan",
                        style = UiFont.poppinsP3SemiBold,
                        modifier = Modifier.padding(
                            bottom = Theme.dimension.size_8dp,
                            top = Theme.dimension.size_12dp
                        )
                    )
                    if (itemSelected.packageType.isNotEmpty()) {
                        Row {
                            InfoDetailSend(
                                icon = R.drawable.box_fix,
                                title = itemSelected.packageType
                            )
                        }
                    }
                }
                if (itemSelected.packageType.isNotEmpty()) {
                    item {
                        Column {
                            Text(
                                "Berat Total",
                                style = UiFont.poppinsP2SemiBold,
                                modifier = Modifier.padding(
                                    bottom = Theme.dimension.size_8dp,
                                    top = Theme.dimension.size_8dp
                                )
                            )
                            Text(
                                "Maks. ${itemSelected.packageWeight}Kg",
                                style = UiFont.poppinsCaptionMedium,
                                modifier = Modifier.padding(
                                    bottom = Theme.dimension.size_4dp,
                                    top = Theme.dimension.size_0dp
                                )
                            )
                        }
                        Divider(
                            color = UiColor.neutral50,
                            modifier = Modifier
                                .padding(
                                    top = Theme.dimension.size_8dp,
                                    bottom = Theme.dimension.size_16dp
                                )
                                .height(Theme.dimension.size_1dp)
                                .fillMaxWidth()
                        )
                    }
                }
                if (itemSelected.listMenu.isNotEmpty()) {
                    item {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Daftar Pesanan", style = UiFont.poppinsP2SemiBold)
                        }
                        Spacer(Modifier.height(Theme.dimension.size_8dp))
                    }
                    val list = itemSelected.listMenu
                    items(list) { productItem ->
                        if (productItem.qty > 0) {
                            ItemMenuSummary(
                                modifier = Modifier.padding(
                                    horizontal = Theme.dimension.size_0dp,
                                    vertical = Theme.dimension.size_8dp
                                ),
                                spec = productItem
                            )
                        }
                        Spacer(Modifier.height(Theme.dimension.size_8dp))
                        Divider(
                            color = UiColor.neutral100, thickness = 1.dp,
                            modifier = Modifier
                        )
                        Spacer(Modifier.height(Theme.dimension.size_16dp))
                    }
                }
                item {
                    ContentDetailOriginDestinationSectionItem(
                        itemTitle = "Titik Penjemputan",
                        itemHint = itemSelected.address,
                        notes = itemSelected.notes
                    )
                    Spacer(modifier = Modifier.height(Theme.dimension.size_20dp))
                    ContentDetailOriginDestinationSectionItem(
                        itemTitle = "Titik Tujuan",
                        itemHint = itemSelected.destination
                    )
                    Spacer(modifier = Modifier.height(Theme.dimension.size_20dp))
                }
                item {
                    ContentDetailPaymentSection(
                        itemSelected.paymentBreakdown,
                        itemSelected.paymentMethod
                    ) {

                    }
                }

            }
        }, content = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Theme.dimension.size_16dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Pesanan", style = UiFont.poppinsH3SemiBold)
                    Text(
                        "Riwayat", style = UiFont.poppinsH5SemiBold.copy(color = UiColor.blue),
                        modifier = Modifier.noRippleClickable {
                        })
                }
                val historyBySection =
                    uiState.historyList?.groupBy { it.sectionTitle }.orEmpty().entries
                        .sortedBy { it.key.order }
                LazyColumn(
                    modifier = Modifier.padding(bottom = Theme.dimension.size_24dp)
                ) {
                    historyBySection.forEach { (title, items) ->
                        item {
                            Text(
                                title.title,
                                style = UiFont.poppinsP2SemiBold,
                                modifier = Modifier.padding(
                                    start = Theme.dimension.size_16dp,
                                    top = Theme.dimension.size_24dp
                                )
                            )
                        }
                        items(items) { item ->
                            OrderHistoryRowItem(item) {
                                if (item.status == TransportRequestType.ARRIVED.value || item.status == TransportRequestType.FINISHED.value) {
                                    itemSelected = item
                                    selectedRating = item.rating
                                    coroutineScope.launch {
                                        modalSheetState.show()
                                    }
                                } else {
                                    navigator.navigate(
                                        OrderProcessScreenDestination(
                                            orderRequestType = OrderRequestType.FOOD
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        })
}