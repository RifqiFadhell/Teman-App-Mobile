package id.teman.app.ui.wallet.withdrawal

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import id.teman.app.R
import id.teman.app.common.BankTextFormField
import id.teman.app.common.ButtonType
import id.teman.app.common.DisposableEffectOnLifecycleEvent
import id.teman.app.common.FormTextFields
import id.teman.app.common.amountTransformation
import id.teman.app.common.convertToRupiah
import id.teman.app.ui.destinations.WithdrawalSummaryScreenDestination
import id.teman.app.ui.ordermapscreen.initiate.FormTextField
import id.teman.app.ui.theme.Theme
import id.teman.app.ui.theme.buttons.TemanCircleButton
import id.teman.app.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Destination
@Composable
fun WithdrawalBankInformationScreen(
    navigator: DestinationsNavigator,
    balance: Double,
    viewModel: WalletBankViewModel = hiltViewModel()
) {
    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_CREATE) {
        viewModel.getWalletBankInformation()
        viewModel.getListBank()
    }

    val uiState = viewModel.uiState

    var bankName by rememberSaveable { mutableStateOf("") }
    var accountName by rememberSaveable { mutableStateOf("") }
    var accountNumber by rememberSaveable { mutableStateOf("") }
    var withdrawalAmount by rememberSaveable { mutableStateOf("") }
    var isValid by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(key1 = uiState.successUpdatingBankInformation, block = {
        uiState.successUpdatingBankInformation?.consumeOnce {
            navigator.navigate(WithdrawalSummaryScreenDestination(it))
        }
    })

    LaunchedEffect(key1 = uiState.successGetInfo, block = {
        uiState.successGetInfo?.consumeOnce {
            bankName = it.bankName
            accountName = it.accountName
            accountNumber = it.accountNumber
        }
    })

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenteredTopNavigation(title = "Penarikan Kantong") {
                navigator.popBackStack()
            }
        },
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .padding(
                            start = Theme.dimension.size_16dp,
                            end = Theme.dimension.size_16dp,
                            bottom = Theme.dimension.size_72dp
                        )
                        .verticalScroll(scrollState)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = Theme.dimension.size_16dp,
                                vertical = Theme.dimension.size_20dp
                            )
                            .border(
                                shape = RoundedCornerShape(Theme.dimension.size_16dp),
                                color = UiColor.neutral100,
                                width = Theme.dimension.size_1dp
                            )
                            .padding(Theme.dimension.size_16dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TemanCircleButton(
                            icon = R.drawable.ic_money_withdraw,
                            circleModifier = Modifier.size(Theme.dimension.size_44dp),
                            iconModifier = Modifier.size(Theme.dimension.size_28dp),
                            circleBackgroundColor = UiColor.tertiaryBlue50,
                            iconColor = UiColor.tertiaryBlue500
                        )
                        Spacer(modifier = Modifier.width(Theme.dimension.size_8dp))
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Total Kantong Saat Ini",
                                style = UiFont.poppinsCaptionMedium.copy(
                                    color = UiColor.neutral500
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                balance.convertToRupiah(),
                                style = UiFont.poppinsH5SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    BankTextFormField(value = bankName, listBank = uiState.listBank, onSelected = {
                        bankName = it
                    })
                    FormTextFields(
                        title = "Nomor Rekening",
                        hint = "Masukkan nomor rekening",
                        keyboardType = KeyboardType.Number,
                        textFieldValue = accountNumber,
                        errorMessage = if (!isValid && accountNumber.isEmpty()) "Mohon masukkan nomor rekening" else null
                    ) {
                        accountNumber = it
                    }
                    FormTextFields(
                        title = "Nama Pemilik Buku Rekening",
                        hint = "Masukkan nama pemilik buku rekening",
                        bottomHint = "Mohon sesuaikan dengan nama yang tertera di halaman pertama pada Buku Rekening",
                        textFieldValue = accountName,
                        errorMessage = if (!isValid && accountName.isEmpty()) "Mohon masukkan nama pemilik buku rekening" else null,
                    ) {
                        accountName = it
                    }

                    FormTextFields(
                        title = "Nominal Penarikan Kantong",
                        hint = "Masukkan nominal penarikan",
                        bottomHint = "Minimal penarikan Rp12.500 (sudah termasuk biaya transfer Rp2.500)",
                        bottomHintIsBold = true,
                        textFieldValue = withdrawalAmount,
                        visualTransformation = { amountTransformation(it.text) },
                        keyboardType = KeyboardType.Number,
                        onTextChanged = {
                            withdrawalAmount = it
                        })
                }
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = UiColor.primaryRed500
                    )
                }
            }
        },
        bottomBar = {
            TemanFilledButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(
                        horizontal = Theme.dimension.size_16dp,
                        vertical = Theme.dimension.size_16dp
                    ),
                content = "Lanjut",
                buttonType = ButtonType.Medium,
                activeColor = UiColor.primaryRed500,
                borderRadius = Theme.dimension.size_30dp,
                activeTextColor = UiColor.white,
                isEnabled = getValue(withdrawalAmount) >= 12500
            ) {
                if (uiState.isLoading) return@TemanFilledButton
                if (bankName.isNotEmpty() && accountName.isNotEmpty() && accountNumber.isNotEmpty() && withdrawalAmount.isNotEmpty()) {
                    viewModel.updateBankInformation(
                        accountName,
                        accountNumber,
                        bankName,
                        withdrawalAmount.toDouble()
                    )
                } else {
                    isValid = false
                }
            }
        }
    )
}

fun getValue(value: String): Double {
    return if (value.isEmpty()) {
        0.0
    } else {
        value.toDouble()
    }
}