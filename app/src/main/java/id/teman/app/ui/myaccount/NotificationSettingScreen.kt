package id.teman.app.ui.myaccount

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import id.teman.app.R
import id.teman.app.common.CenterTopAppBar
import id.teman.app.common.orFalse
import id.teman.app.ui.myaccount.edit.EditProfileViewModel
import id.teman.app.ui.theme.Theme
import id.teman.app.ui.theme.buttons.TemanCircleButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Destination
@Composable
fun NotificationSettingScreen(
    navigator: DestinationsNavigator,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val uiState = viewModel.editProfileUiState
    var isPromoEnabled by remember { mutableStateOf(uiState.isNotificationShow) }
    val context = LocalContext.current

    LaunchedEffect(key1 = uiState.successUpdate, block = {
        uiState.successUpdate?.consumeOnce {
            Toast.makeText(context, "Sukses Memperbaharui Notifikasi", Toast.LENGTH_SHORT).show()
        }
    })
    Scaffold(
        topBar = {
            CenterTopAppBar(
                elevation = Theme.dimension.size_0dp,
                backgroundColor = Color.Transparent,
                title = {
                    Text(
                        text = "Pengaturan Notifikasi",
                        style = UiFont.poppinsH5SemiBold,
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navigator.popBackStack()
                    }) {
                        Icon(Icons.Filled.ArrowBack, "backIcon")
                    }
                },
            )
        },
        content = {
            Row(
                modifier = Modifier.padding(
                    top = Theme.dimension.size_32dp,
                    start = Theme.dimension.size_16dp,
                    end = Theme.dimension.size_16dp
                )
            ) {
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    TemanCircleButton(
                        icon = R.drawable.ic_promo,
                        iconColor = UiColor.primaryRed900,
                        circleBackgroundColor = UiColor.tertiaryBlue50,
                        circleModifier = Modifier
                            .size(Theme.dimension.size_56dp),
                        iconModifier =
                        Modifier.size(Theme.dimension.size_24dp)
                    )
                    Column(
                        modifier = Modifier
                            .weight(8f)
                            .padding(start = Theme.dimension.size_12dp),
                    ) {
                        Text(
                            "Promo dan lainnya",
                            style = UiFont.poppinsCaptionSemiBold
                        )
                        Spacer(modifier = Modifier.height(Theme.dimension.size_6dp))
                        Text(
                            "Nonaktifkan notifikasi perihal voucher, promo dan rekomendasi." +
                                    " Kamu tetap bisa cek semuanya di halaman Promo.",
                            style = UiFont.poppinsCaptionSmallMedium.copy(color = UiColor.neutral500)
                        )
                    }
                    Switch(
                        checked = isPromoEnabled.orFalse(),
                        onCheckedChange = {
                            isPromoEnabled = it
                            viewModel.updateNotification(isPromoEnabled.orFalse())
                        },
                        modifier = Modifier.weight(1f),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = UiColor.tertiaryBlue900,
                            checkedTrackColor = UiColor.tertiaryBlue50
                        )
                    )
                }
            }
        }
    )
}