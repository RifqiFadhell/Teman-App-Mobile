package id.teman.app.ui.bill

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import id.teman.app.R
import id.teman.app.common.CardItemCommon
import id.teman.app.common.CustomLoading
import id.teman.app.common.DisposableEffectOnLifecycleEvent
import id.teman.app.common.TopBar
import id.teman.app.common.orZero
import id.teman.app.domain.model.bill.ProviderSpec
import id.teman.app.ui.destinations.GameScreenDestination
import id.teman.app.ui.destinations.ProviderScreenDestination
import id.teman.app.ui.destinations.WebViewScreenDestination
import id.teman.app.ui.myaccount.CardItem
import id.teman.app.ui.theme.Theme
import id.teman.coreui.typography.UiFont

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
@Destination
fun BillScreen(navigator: DestinationsNavigator, viewModel: BillViewModel = hiltViewModel()) {
    val uiState = viewModel.billUiState
    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_CREATE) {
        viewModel.getListCategoryBill()
    }
    Scaffold(
        topBar = {
            TopBar(title = "Pilih Tagihan") {
                navigator.popBackStack()
            }
        }, content = {
            LazyColumn(modifier = Modifier.padding(Theme.dimension.size_16dp)) {
                item {
                    Text(
                        "Prabayar",
                        style = UiFont.poppinsP3SemiBold,
                        modifier = Modifier.fillMaxWidth()
                    )
                    CardItem(
                        icon = R.drawable.ic_phone,
                        modifier = Modifier
                            .padding(top = Theme.dimension.size_16dp), title = "Pulsa"
                    ) {
                        navigator.navigate(
                            ProviderScreenDestination(
                                ProviderSpec(
                                    titleBar = "Pulsa",
                                    titleInput = "No Handphone",
                                    caption = "Mau beli pulsa berapa?",
                                    placeHolder = "Masukkan no. handphone",
                                    key = "pulsa",
                                    icon = "https://apidev.temanofficial.co.id/file/Pulsa-1673107686642-340356390.png",
                                    information = ""
                                )
                            )
                        )
                    }
                }
                item {
                    Text(
                        "Isi Ulang",
                        style = UiFont.poppinsP3SemiBold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Theme.dimension.size_16dp)
                    )
                }
                items(uiState.listCategoryBill.size.orZero()) { index ->
                    with(uiState.listCategoryBill[index]) {
                        CardItemCommon(
                            title = name, icon = icon,
                            modifier = Modifier
                                .padding(top = Theme.dimension.size_16dp)
                        ) {
                            navigator.navigate(
                                when (key) {
                                    "VOUCHER_GAME" -> GameScreenDestination(
                                        ProviderSpec(
                                            titleBar = name,
                                            placeHolder = placeHolder,
                                            caption = caption,
                                            titleInput = titleInput,
                                            key = key,
                                            icon = icon,
                                            information = information,
                                            categoryKey = categoryKey,
                                        )
                                    )

                                    "TRAVEL" -> {
                                        WebViewScreenDestination(name, information)
                                    }

                                    else -> {
                                        ProviderScreenDestination(
                                            ProviderSpec(
                                                titleBar = name,
                                                placeHolder = placeHolder,
                                                caption = caption,
                                                titleInput = titleInput,
                                                key = key,
                                                icon = icon,
                                                information = information,
                                                categoryKey = categoryKey
                                            )
                                        )
                                    }
                                }
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
        })
}