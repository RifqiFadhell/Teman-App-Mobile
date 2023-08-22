@file:OptIn(ExperimentalTextApi::class)

package id.teman.app.ui.wallet

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
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
import id.teman.app.common.TemanSectionTitleIcon
import id.teman.app.common.TopBar
import id.teman.app.common.TopBarWallet
import id.teman.app.domain.model.wallet.WalletItemDetailSpec
import id.teman.app.ui.NavGraphs
import id.teman.app.ui.destinations.HomeScreenDestination
import id.teman.app.ui.destinations.TopUpWalletScreenDestination
import id.teman.app.ui.destinations.WalletScreenDestination
import id.teman.app.ui.destinations.WebViewScreenDestination
import id.teman.app.ui.destinations.WithdrawalBankInformationScreenDestination
import id.teman.app.ui.ordermapscreen.done.BillFinished
import id.teman.app.ui.ordermapscreen.done.OrderFinishedSpec
import id.teman.app.ui.theme.Theme
import id.teman.app.ui.theme.buttons.TemanCircleButton
import id.teman.app.utils.getCurrentTimeFormat
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont
import kotlinx.coroutines.launch

fun onBackPressed(navigator: DestinationsNavigator) {
    navigator.navigate(NavGraphs.root) {
        popUpTo(HomeScreenDestination)
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalTextApi::class)
@Composable
@Destination
fun WalletScreen(
    navigator: DestinationsNavigator,
    walletViewModel: WalletViewModel = hiltViewModel()
) {
    BackHandler {
        onBackPressed(navigator)
    }
    val uiState = walletViewModel.walletUiState
    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_RESUME) {
        walletViewModel.getWalletBalance()
    }
    var spec by remember { mutableStateOf(OrderFinishedSpec()) }
    val coroutineScope = rememberCoroutineScope()

    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
        confirmStateChange = { false }
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
            TopBar(title = "") {
                coroutineScope.launch {
                    modalSheetState.hide()
                }
            }
            Box(modifier = Modifier.fillMaxSize()) {
                BillFinished(spec = spec, alignment = Alignment.Center, onButtonClick = {
                    if (spec.serialNumber.isNullOrEmpty()) walletViewModel.updateStatusBill(spec.id) else {
                        coroutineScope.launch {
                            modalSheetState.hide()
                        }
                    }
                })
            }
        },
        content = {
            Column {
                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .background(color = UiColor.neutral900)
                ) {
                    TopBarWallet(title = "Teman Kantong", onBackClick = {
                        navigator.popBackStack()
                    }, onWalletClick = {
                        navigator.navigate(WithdrawalBankInformationScreenDestination(balance = uiState.balanceDouble))
                    })
                    Text(
                        text = "Kantong saat ini",
                        modifier = Modifier
                            .padding(
                                top = Theme.dimension.size_40dp,
                                start = Theme.dimension.size_40dp,
                                end = Theme.dimension.size_16dp
                            )
                            .align(Alignment.Start),
                        style = UiFont.poppinsSubHSemiBold,
                        color = UiColor.white
                    )
                    Text(
                        text = uiState.balance,
                        modifier = Modifier
                            .padding(
                                top = Theme.dimension.size_8dp,
                                start = Theme.dimension.size_40dp,
                                end = Theme.dimension.size_16dp
                            )
                            .align(Alignment.Start),
                        style = UiFont.cabinH1Bold,
                        color = UiColor.white
                    )
                    Card(
                        modifier = Modifier
                            .padding(
                                horizontal = Theme.dimension.size_16dp
                            )
                            .offset(
                                x = Theme.dimension.size_0dp,
                                y = Theme.dimension.size_48dp
                            )
                            .clickable {
                                navigator.navigate(TopUpWalletScreenDestination) {
                                    popUpTo(WalletScreenDestination.route)
                                }
                            },
                        elevation = Theme.dimension.size_2dp,
                        shape = RoundedCornerShape(Theme.dimension.size_12dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(Theme.dimension.size_16dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            GlideImage(
                                modifier = Modifier.size(Theme.dimension.size_48dp),
                                imageModel = R.drawable.teman_wallet_new
                            )
                            Column(
                                modifier = Modifier
                                    .weight(8f)
                                    .padding(start = Theme.dimension.size_20dp)
                            ) {
                                Text(
                                    "Top Up Kantong",
                                    modifier = Modifier.padding(bottom = Theme.dimension.size_4dp),
                                    style = UiFont.poppinsH5SemiBold.copy(
                                        platformStyle = PlatformTextStyle(
                                            includeFontPadding = false
                                        ),
                                        lineHeightStyle = LineHeightStyle(
                                            alignment = LineHeightStyle.Alignment.Center,
                                            trim = LineHeightStyle.Trim.LastLineBottom
                                        )
                                    )
                                )
                                Text(
                                    "Transfer Bank, Ovo, LinkAja,\nShopee, Alfamart, Dll",
                                    style = UiFont.poppinsCaptionMedium.copy(
                                        platformStyle = PlatformTextStyle(
                                            includeFontPadding = false
                                        )
                                    )
                                )
                            }
                            GlideImage(
                                modifier = Modifier
                                    .size(Theme.dimension.size_28dp),
                                imageModel = R.drawable.ic_right_arrow,
                                imageOptions = ImageOptions(colorFilter = ColorFilter.tint(UiColor.black))
                            )
                        }
                    }
                }
                Column {
                    Text(
                        text = "Riwayat Pembayaran",
                        modifier = Modifier
                            .padding(
                                top = Theme.dimension.size_64dp,
                                start = Theme.dimension.size_16dp,
                                end = Theme.dimension.size_16dp
                            )
                            .align(Alignment.Start),
                        style = UiFont.poppinsP3SemiBold,
                        color = UiColor.neutral900
                    )
                    LazyColumn(modifier = Modifier.padding(horizontal = Theme.dimension.size_12dp)) {
                        items(uiState.historyList.orEmpty()) { item ->
                            TransactionWalletHistory(item = item, onClick = {
                                navigator.navigate(WebViewScreenDestination("Payment Page", it))
                            }) {
                                coroutineScope.launch {
                                    modalSheetState.show()
                                }
                                spec = OrderFinishedSpec(
                                    id = it.id,
                                    title = "${it.title} ${it.description}",
                                    caption = it.caption.orEmpty(),
                                    button = it.button.orEmpty(),
                                    serialNumber = it.serialNumber.orEmpty(),
                                    number = it.number,
                                    category = it.category,
                                    icon = "https://apidev.temanofficial.co.id/file/tbills-1673160258196-480502767.png",
                                    price = it.amount,
                                )
                            }
                        }
                    }
                }
                if (uiState.loading) {
                    Dialog(
                        onDismissRequest = { },
                        DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
                    ) {
                        CustomLoading(modifier = Modifier.fillMaxSize())
                    }
                }
            }
        })
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun TransactionWalletHistory(
    item: WalletItemDetailSpec,
    onClick: (String) -> Unit,
    onClickDetail: (WalletItemDetailSpec) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(Theme.dimension.size_8dp)
            .clickable {
                if (item.type == "topup" && item.status == "pending") {
                    onClick(item.url)
                } else if (item.type == "bill") {
                    onClickDetail(item)
                }
            },
        elevation = Theme.dimension.size_2dp,
        shape = RoundedCornerShape(Theme.dimension.size_12dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Theme.dimension.size_16dp)
        ) {
            TemanCircleButton(
                icon = item.icon,
                circleBackgroundColor = UiColor.neutralGray0,
                circleModifier = Modifier
                    .size(Theme.dimension.size_48dp),
                iconModifier = Modifier
                    .size(Theme.dimension.size_24dp)
            )
            Column(
                modifier = Modifier
                    .padding(start = Theme.dimension.size_8dp)
                    .weight(1f)
            ) {
                Text(
                    item.title,
                    style = UiFont.poppinsP2SemiBold.copy(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    ),
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    item.description,
                    style = UiFont.poppinsCaptionMedium.copy(color = UiColor.neutral500),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    getCurrentTimeFormat(item.updatedAt, "HH:mm, dd MMM yyyy"), style = UiFont.poppinsCaptionMedium.copy(
                        color = UiColor.neutral300
                    ), maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    item.amount,
                    style = UiFont.poppinsCaptionSemiBold.copy(
                        color = if (item.type == "topup" && item.status == "success") {
                            UiColor.success500
                        } else if (item.type == "topup" && item.status == "pending") {
                            UiColor.primaryYellow500
                        } else if (item.type == "topup" && item.status == "failed") {
                            UiColor.neutral900
                        } else {
                            UiColor.primaryRed500
                        }
                    ),
                    modifier = Modifier.padding(start = Theme.dimension.size_4dp)
                )
                TemanSectionTitleIcon(
                    title = "Teman",
                    icon = R.drawable.ic_wallet,
                    modifier = Modifier
                        .padding(top = Theme.dimension.size_8dp)
                )
            }
        }
    }
}