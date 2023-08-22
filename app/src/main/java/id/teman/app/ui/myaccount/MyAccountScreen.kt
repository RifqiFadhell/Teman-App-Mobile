package id.teman.app.ui.myaccount

import id.teman.app.R as rAppModule
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.BuildConfig
import id.teman.app.common.noRippleClickable
import id.teman.app.dialog.GeneralActionButton
import id.teman.app.dialog.GeneralDialogPrompt
import id.teman.app.domain.model.user.ProfileSpec
import id.teman.app.ui.destinations.AboutUsScreenDestination
import id.teman.app.ui.destinations.EditProfileScreenDestination
import id.teman.app.ui.destinations.LoginScreenDestination
import id.teman.app.ui.destinations.MyAccountScreenDestination
import id.teman.app.ui.destinations.NotificationSettingScreenDestination
import id.teman.app.ui.destinations.PromoScreenDestination
import id.teman.app.ui.destinations.ReferralScreenDestination
import id.teman.app.ui.destinations.RewardScreenDestination
import id.teman.app.ui.destinations.WebViewScreenDestination
import id.teman.app.ui.sharedviewmodel.MainViewModel
import id.teman.app.ui.theme.Theme
import id.teman.app.ui.theme.buttons.TemanCircleButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Destination
@Composable
fun MyAccountScreen(navigator: DestinationsNavigator, viewModel: MainViewModel) {
    val uiState = viewModel.userInfoState
    Scaffold(
        topBar = {
            MyAccountTopBar {
                navigator.popBackStack()
            }
        }
    ) {
        viewModel.updateUserInfo()
        LazyColumn {
            item {
                uiState.profileSpec?.let { spec ->
                    HeaderSection(spec) {
                        navigator.navigate(EditProfileScreenDestination)
                    }
                }
            }
            item {
                AccountSection(navigator)
            }
            item {
                OtherInformationSection(navigator, viewModel)
            }
        }
    }
}

@Composable
private fun MyAccountTopBar(onClick: () -> Unit) {
    TopAppBar(
        elevation = Theme.dimension.size_0dp,
        backgroundColor = Color.White,
        contentPadding = PaddingValues(horizontal = Theme.dimension.size_16dp),
        modifier = Modifier.height(Theme.dimension.size_60dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            GlideImage(
                imageModel = id.teman.app.R.drawable.ic_arrow_back,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(Theme.dimension.size_24dp)
                    .clickable {
                        onClick()
                    }
            )
            Text(
                "Akun",
                style = UiFont.poppinsH5SemiBold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun HeaderSection(profileSpec: ProfileSpec, onClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier.padding(
                top = Theme.dimension.size_32dp,
                start = Theme.dimension.size_16dp,
                end = Theme.dimension.size_16dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            GlideImage(
                imageModel = profileSpec.image,
                modifier = Modifier
                    .size(Theme.dimension.size_48dp)
                    .clip(CircleShape),
                failure = {
                    Image(
                        painter = painterResource(id = rAppModule.drawable.ic_person_mamoji),
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
                    profileSpec.name,
                    modifier = Modifier.padding(bottom = Theme.dimension.size_4dp),
                    style = UiFont.poppinsH3Bold.copy(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        ),
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Center,
                            trim = LineHeightStyle.Trim.LastLineBottom
                        )
                    )
                )
                Text(profileSpec.number)
            }
            GlideImage(
                modifier = Modifier
                    .size(Theme.dimension.size_20dp)
                    .noRippleClickable {
                        onClick()
                    },
                imageModel = rAppModule.drawable.ic_edit_profile
            )
        }
    }
}

@Composable
fun AccountSection(navigator: DestinationsNavigator) {
    Column(
        modifier = Modifier.padding(
            top = Theme.dimension.size_32dp,
            start = Theme.dimension.size_16dp,
            end = Theme.dimension.size_16dp
        ),
        content = {
            Text("Akun", style = UiFont.poppinsP3SemiBold, modifier = Modifier.fillMaxWidth())
            CardItem(
                modifier = Modifier
                    .padding(top = Theme.dimension.size_20dp), title = "Promo",
                icon = rAppModule.drawable.ic_promo
            ) {
                navigator.navigate(PromoScreenDestination)
            }
            CardItem(
                modifier = Modifier
                    .padding(top = Theme.dimension.size_20dp), title = "Reward",
                icon = rAppModule.drawable.ic_giftcard
            ) {
                navigator.navigate(RewardScreenDestination)
            }
            CardItem(
                modifier = Modifier
                    .padding(top = Theme.dimension.size_16dp),
                title = "Pengaturan Notifikasi",
                icon = rAppModule.drawable.ic_notification
            ) {
                navigator.navigate(NotificationSettingScreenDestination)
            }
        }
    )
}

@Composable
private fun OtherInformationSection(
    navigator: DestinationsNavigator,
    viewModel: MainViewModel = hiltViewModel()
) {
    var showLogoutSheet by remember { mutableStateOf(false) }
    val packageName = BuildConfig.APPLICATION_ID
    val context = LocalContext.current
    if (showLogoutSheet) {
        GeneralDialogPrompt(
            title = "Yakin Mau Logout?",
            subtitle = "Kamu nanti harus masukin nomor handphone lagi untuk masuk",
            actionButtons = {
                GeneralActionButton(
                    text = "Ga jadi",
                    textColor = UiColor.neutral900,
                    isFirstAction = true,
                    onClick = {
                        showLogoutSheet = false
                    }
                )
                GeneralActionButton(
                    text = "Logout",
                    textColor = UiColor.primaryRed500,
                    isFirstAction = false,
                    onClick = {
                        viewModel.removeAllPreference()
                        navigator.navigate(LoginScreenDestination) {
                            popUpTo(MyAccountScreenDestination) {
                                inclusive = false
                            }
                        }
                    }
                )
            },
            onDismissRequest = {
                showLogoutSheet = false
            }
        )
    }
    Column(
        modifier = Modifier.padding(
            top = Theme.dimension.size_32dp,
            start = Theme.dimension.size_16dp,
            end = Theme.dimension.size_16dp
        ),
        content = {
            Text(
                "Informasi Lainnya",
                style = UiFont.poppinsP3SemiBold,
                modifier = Modifier.fillMaxWidth()
            )
            CardItem(
                modifier = Modifier
                    .padding(top = Theme.dimension.size_20dp),
                title = "Referral",
                icon = rAppModule.drawable.referral_icon
            ) {
                navigator.navigate(ReferralScreenDestination)
            }
            CardItem(
                modifier = Modifier
                    .padding(top = Theme.dimension.size_20dp),
                title = "Tentang Kami",
                icon = rAppModule.drawable.information
            ) {
                navigator.navigate(AboutUsScreenDestination)
            }
            CardItem(
                modifier = Modifier.padding(top = Theme.dimension.size_16dp),
                title = "Kebijakan Privasi",
                icon = rAppModule.drawable.privacy
            ) {
                navigator.navigate(WebViewScreenDestination("Kebijakan Privasi", "https://www.temanofficial.co.id/privacy-policy"))
            }
            CardItem(
                modifier = Modifier.padding(top = Theme.dimension.size_16dp),
                title = "Beri Rating",
                icon = rAppModule.drawable.rating
            ) {
                try {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=$packageName")
                        )
                    )
                } catch (e: ActivityNotFoundException) {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                        )
                    )
                }
            }
            CardItem(
                modifier = Modifier
                    .padding(top = Theme.dimension.size_16dp), title = "Logout",
                circleBackgroundColor = UiColor.primaryRed50, icon = rAppModule.drawable.ic_logout
            ) {
                showLogoutSheet = true
            }
        }
    )
}

@Composable
fun CardItem(
    modifier: Modifier = Modifier,
    circleBackgroundColor: Color = UiColor.neutralGray0,
    @DrawableRes icon: Int = rAppModule.drawable.ic_promo,
    title: String,
    onClick: () -> Unit
) {
    Column {
        Row(
            modifier = modifier.noRippleClickable {
                onClick()
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            TemanCircleButton(
                icon = icon,
                circleBackgroundColor = circleBackgroundColor,
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

            GlideImage(
                imageModel = rAppModule.drawable.ic_right_arrow,
                modifier = Modifier
                    .weight(1f)
                    .width(Theme.dimension.size_6dp)
                    .height(Theme.dimension.size_12dp)
            )
        }
        Divider(
            color = UiColor.neutral100, thickness = 1.dp,
            modifier = Modifier.padding(
                start = Theme.dimension.size_64dp,
                top = Theme.dimension.size_16dp
            )
        )
    }
}


@Preview
@Composable
fun MyAccountPagePreview() {
    //MyAccountPage()
}