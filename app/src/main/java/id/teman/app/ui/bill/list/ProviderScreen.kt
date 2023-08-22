@file:OptIn(ExperimentalTextApi::class)

package id.teman.app.ui.bill.list

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.R
import id.teman.app.common.ButtonType
import id.teman.app.common.CustomLoading
import id.teman.app.common.InputNumberWidget
import id.teman.app.common.TopBar
import id.teman.app.common.isNotNullOrEmpty
import id.teman.app.domain.model.bill.ProviderSpec
import id.teman.app.ui.destinations.PinWalletScreenDestination
import id.teman.app.ui.destinations.TopUpWalletScreenDestination
import id.teman.app.ui.theme.Theme
import id.teman.app.ui.theme.buttons.TemanCircleButton
import id.teman.app.ui.theme.buttons.TemanCircleButtonString
import id.teman.app.ui.theme.buttons.TemanFilledButton
import id.teman.app.ui.wallet.pin.PinScreenSpec
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterialApi::class)
@Composable
@Destination
fun ProviderScreen(
    navigator: DestinationsNavigator,
    viewModel: ProviderViewModel = hiltViewModel(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    providerSpec: ProviderSpec
) {
    val uiState = viewModel.providerUiState
    var codeSelected by remember { mutableStateOf("") }
    var nameSelected by remember { mutableStateOf("") }
    var priceSelected by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var number by remember { mutableStateOf("") }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    if (providerSpec.categoryKey == "voucher_game_menu") {
                        viewModel.getListBill(category = providerSpec.key, "")
                    }
                }
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

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
            ContentDetailPaymentSection(
                navigator,
                icon = providerSpec.icon,
                title = providerSpec.titleBar,
                provider = nameSelected,
                price = priceSelected,
                number = number, onContinue = {
                    navigator.navigate(
                        PinWalletScreenDestination(
                            PinScreenSpec(
                                useFor = if (providerSpec.key == "pulsa") "pulsa" else "bill",
                                number = number,
                                category = providerSpec.key,
                                code = codeSelected,
                                title = providerSpec.titleBar,
                                icon = providerSpec.icon,
                                price = priceSelected
                            )
                        )
                    )
                    coroutineScope.launch {
                        modalSheetState.hide()
                        viewModel.resetListData()
                    }
                }, onHide = {
                    coroutineScope.launch {
                        modalSheetState.hide()
                    }
                })
        },
        content = {
            Scaffold(
                topBar = {
                    TopBar(title = providerSpec.titleBar) {
                        navigator.popBackStack()
                    }
                }, content = {
                    val listState = rememberLazyGridState()
                    var selectedIndex by remember { mutableStateOf(-1) }
                    Column {
                        if (providerSpec.categoryKey == "voucher_game_menu") {
                            GlideImage(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(horizontal = Theme.dimension.size_32dp)
                                    .height(Theme.dimension.size_144dp)
                                    .clip(RoundedCornerShape(Theme.dimension.size_8dp)),
                                imageOptions = ImageOptions(alignment = Center),
                                imageModel = providerSpec.icon,
                                failure = {
                                    R.drawable.ic_no_image
                                }
                            )
                            Spacer(modifier = Modifier.height(Theme.dimension.size_12dp))
                            Text(modifier = Modifier.fillMaxWidth(),
                                text = providerSpec.titleBar,
                                style = UiFont.poppinsP2SemiBold,
                                textAlign = TextAlign.Center
                            )
                        } else {
                            InputNumberWidget(
                                title = providerSpec.titleInput,
                                placeholders = providerSpec.placeHolder
                            ) {
                                number = it
                                if (it.length > 7) viewModel.searchDebounced(
                                    number,
                                    providerSpec.key
                                )
                            }
                        }
                        if (uiState.listBillPrice.isNotEmpty()) {
                            Text(
                                text = providerSpec.caption,
                                modifier = Modifier
                                    .padding(
                                        top = Theme.dimension.size_16dp,
                                        start = Theme.dimension.size_18dp,
                                        bottom = Theme.dimension.size_22dp
                                    )
                                    .align(Alignment.Start),
                                style = UiFont.poppinsSubHSemiBold,
                                color = UiColor.black
                            )
                        }
                        LazyVerticalGrid(
                            modifier = Modifier.padding(horizontal = Theme.dimension.size_12dp),
                            columns = GridCells.Fixed(2),
                            state = listState
                        ) {
                            items(uiState.listBillPrice.size) { index ->
                                with(uiState.listBillPrice[index]) {
                                    CardPrice(
                                        selected = selectedIndex,
                                        price = price,
                                        position = index,
                                        amount = name,
                                        code = code
                                    ) { selected, value ->
                                        codeSelected = value
                                        selectedIndex = selected
                                        priceSelected = price
                                        nameSelected = name
                                    }
                                }
                            }
                        }
                        if (providerSpec.key == "PLN" && uiState.listBillPrice.isNotEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Theme.dimension.size_16dp)
                                    .background(
                                        UiColor.tertiaryBlue50,
                                        shape = RoundedCornerShape(Theme.dimension.size_8dp)
                                    )
                            ) {
                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(Theme.dimension.size_16dp),
                                    text = providerSpec.information,
                                    style = UiFont.poppinsP1Medium
                                )
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
                    }
                },
                bottomBar = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        if (priceSelected.isNotEmpty()) ButtonBillContinue(
                            priceSelected = priceSelected,
                            codeSelected = codeSelected
                        ) {
                            coroutineScope.launch {
                                modalSheetState.show()
                            }
                        }
                    }
                }
            )
        }
    )
}

@Composable
fun ContentDetailPaymentSection(
    navigator: DestinationsNavigator,
    icon: String,
    title: String,
    provider: String,
    price: String,
    number: String,
    onContinue: () -> Unit,
    onHide: () -> Unit
) {
    TopBar(title = "Pembayaran") {
        onHide()
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize()
            .padding(vertical = Theme.dimension.size_24dp, horizontal = Theme.dimension.size_16dp)
    ) {
        item {
            Row(
                modifier = Modifier.padding(bottom = Theme.dimension.size_12dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TemanCircleButtonString(
                    icon = icon,
                    circleBackgroundColor = UiColor.neutralGray0,
                    circleModifier = Modifier
                        .size(Theme.dimension.size_48dp),
                    iconModifier = Modifier
                        .size(Theme.dimension.size_24dp)
                )
                Text(
                    title,
                    style = UiFont.poppinsP3SemiBold,
                    modifier = Modifier
                        .weight(8f)
                        .padding(start = Theme.dimension.size_16dp)
                )
            }
        }
        item {
            Column(
                modifier = Modifier
                    .background(
                        UiColor.neutralGray0,
                        shape = RoundedCornerShape(Theme.dimension.size_8dp)
                    )
                    .padding(
                        horizontal = Theme.dimension.size_16dp,
                        vertical = Theme.dimension.size_20dp
                    )
            ) {
                DetailItemRow(title = "Jenis Layanan", value = provider)
                if (number.isNotNullOrEmpty()) DetailItemRow(title = "Nomor", value = number)
                DetailItemRow(title = "Harga", value = price)
            }
        }
        item {
            Spacer(Modifier.height(Theme.dimension.size_20dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Metode Pembayaran", style = UiFont.poppinsP2SemiBold)
                Text(
                    "TopUp",
                    style = UiFont.poppinsP2Medium.copy(color = UiColor.tertiaryBlue500),
                    modifier = Modifier.clickable {
                        navigator.navigate(TopUpWalletScreenDestination)
                    }
                )
            }
            Spacer(Modifier.height(Theme.dimension.size_20dp))
        }
        item {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                TemanCircleButton(
                    icon = R.drawable.ic_wallet,
                    circleBackgroundColor = Color.Transparent,
                    circleModifier = Modifier
                        .size(Theme.dimension.size_48dp)
                        .border(
                            BorderStroke(1.dp, color = UiColor.neutral100),
                            shape = CircleShape
                        ),
                    iconModifier = Modifier
                        .size(Theme.dimension.size_24dp)
                )
                Column {
                    Text(
                        "T-Kantong",
                        style = UiFont.poppinsP2SemiBold.copy(color = UiColor.neutral900),
                        maxLines = 1,
                        modifier = Modifier
                            .padding(start = Theme.dimension.size_16dp)
                    )
                    Text(
                        "Bayar secara instan pakai T-Kantong",
                        style = UiFont.poppinsCaptionMedium.copy(color = UiColor.neutral500),
                        maxLines = 1,
                        modifier = Modifier
                            .padding(start = Theme.dimension.size_16dp),
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        item {
            Text(
                modifier = Modifier.padding(top = Theme.dimension.size_16dp),
                text = "Rincian Pesanan",
                style = UiFont.poppinsP2SemiBold.copy(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                )
            )
            Spacer(Modifier.height(Theme.dimension.size_20dp))
            DetailItemRow(
                title = "$title $price", value = price,
                fontTitle = UiFont.poppinsP2Medium,
                fontValue = UiFont.poppinsP2Medium
            )
            Spacer(Modifier.height(Theme.dimension.size_8dp))
            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            Canvas(
                Modifier
                    .fillMaxWidth()
                    .height(Theme.dimension.size_2dp)
            ) {
                drawLine(
                    color = UiColor.neutral200,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    pathEffect = pathEffect
                )
            }
            Spacer(Modifier.height(Theme.dimension.size_20dp))
            DetailItemRow(
                title = "Total yang dibayarkan",
                value = price,
                fontTitle = UiFont.poppinsP2SemiBold,
                fontValue = UiFont.poppinsP2SemiBold
            )
        }
        item {
            TemanFilledButton(
                content = "Konfirmasi Pembayaran",
                buttonType = ButtonType.Large,
                activeColor = UiColor.primaryRed500,
                activeTextColor = Color.White,
                isEnabled = true,
                borderRadius = Theme.dimension.size_30dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                onContinue()
            }
        }
    }
}

@Composable
fun DetailItemRow(
    title: String,
    value: String,
    fontTitle: TextStyle = UiFont.poppinsP2Medium,
    fontValue: TextStyle = UiFont.poppinsP2SemiBold
) {
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        Text(title, style = fontTitle)
        Text(
            value,
            style = fontValue.copy(
                color = UiColor.neutral900
            )
        )
    }
    Spacer(Modifier.height(Theme.dimension.size_12dp))
}

@Composable
fun CardPrice(
    amount: String,
    price: String,
    code: String,
    selected: Int,
    position: Int,
    onClick: (Int, String) -> Unit
) {
    var selectedItem by remember { mutableStateOf(-1) }
    Card(
        modifier = Modifier
            .padding(horizontal = Theme.dimension.size_8dp, vertical = Theme.dimension.size_6dp)
            .selectable(selected = selected == position, onClick = {
                selectedItem = if (selected != position) position else -1
                onClick(selectedItem, code)
            }),
        elevation = Theme.dimension.size_2dp,
        shape = RoundedCornerShape(Theme.dimension.size_12dp),
        border = BorderStroke(
            Theme.dimension.size_1dp,
            if (position == selected) UiColor.tertiaryBlue500 else UiColor.white
        ),
        backgroundColor = if (position == selected) UiColor.tertiaryBlue50 else UiColor.white
    ) {
        Column(
            modifier = Modifier.padding(
                top = Theme.dimension.size_12dp,
                start = Theme.dimension.size_16dp,
                bottom = Theme.dimension.size_16dp,
                end = Theme.dimension.size_16dp
            )
        ) {
            Text(
                amount,
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
                price,
                style = UiFont.poppinsCaptionMedium.copy(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                ),
                color = UiColor.tertiaryBlue500
            )
        }
    }
}

@Composable
fun ButtonBillContinue(priceSelected: String, codeSelected: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Theme.dimension.size_16dp)
            .background(
                UiColor.primaryRed500,
                shape = RoundedCornerShape(Theme.dimension.size_32dp)
            )
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Column(
            modifier = Modifier
                .weight(0.7f)
                .padding(start = Theme.dimension.size_20dp)
        ) {
            Text(
                "Total Bayar",
                modifier = Modifier.padding(
                    top = Theme.dimension.size_8dp
                ),
                style = UiFont.poppinsCaptionMedium.copy(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    ),
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.LastLineBottom
                    )
                ),
                color = UiColor.white
            )
            Text(
                priceSelected,
                modifier = Modifier.padding(
                    top = Theme.dimension.size_2dp,
                    bottom = Theme.dimension.size_8dp,
                ),
                style = UiFont.poppinsH5SemiBold.copy(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                ),
                color = UiColor.white
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(0.3f)) {
            Text(
                "Lanjutkan",
                style = UiFont.poppinsCaptionSemiBold.copy(color = UiColor.white),
                modifier = Modifier
            )
            GlideImage(
                R.drawable.ic_next_button,
                modifier = Modifier
                    .weight(1f)
                    .width(Theme.dimension.size_6dp)
                    .height(Theme.dimension.size_18dp)
                    .size(Theme.dimension.size_18dp),
                imageOptions = ImageOptions(contentScale = ContentScale.Fit)
            )
        }
    }
}