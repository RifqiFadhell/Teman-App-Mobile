package id.teman.app.ui.myaccount.reward

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.R
import id.teman.app.common.DisposableEffectOnLifecycleEvent
import id.teman.app.common.TopBar
import id.teman.app.domain.model.reward.ItemRewardTransaction
import id.teman.app.ui.theme.Theme
import id.teman.app.ui.theme.buttons.TemanCircleButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
@Destination
fun ListHistoryRewardScreen(
    navigator: DestinationsNavigator,
    viewModel: RewardViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_RESUME) {
        viewModel.getHistoryPoint()
    }
    Scaffold(
        topBar = {
            TopBar(title = "Riwayat Poin") {
                navigator.popBackStack()
            }
        }, content = {
            LazyColumn(modifier = Modifier.padding(Theme.dimension.size_16dp)) {
                items(uiState.historyPoint) { item ->
                    TransactionPointHistory(item = item)
                }
            }
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = UiColor.primaryRed500
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun TransactionPointHistory(
    item: ItemRewardTransaction
) {
    Card(
        modifier = Modifier
            .padding(Theme.dimension.size_8dp)
            .clickable {

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
                    .padding(start = Theme.dimension.size_8dp, end = Theme.dimension.size_12dp)
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
                Row(
                    modifier = Modifier.padding(horizontal = Theme.dimension.size_12dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlideImage(
                        imageModel = R.drawable.ic_calendar,
                        modifier = Modifier.size(Theme.dimension.size_16dp)
                    )
                    Spacer(modifier = Modifier.width(Theme.dimension.size_8dp))
                    Text(
                        item.date,
                        style = UiFont.poppinsCaptionMedium.copy(color = UiColor.neutral300)
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    item.amount.toString(),
                    style = UiFont.poppinsCaptionSemiBold.copy(
                        color = if (item.key == "customer_point" || item.key == "register_point" || item.key == "referral_code") {
                            UiColor.success500
                        } else {
                            UiColor.primaryRed500
                        }
                    ),
                    modifier = Modifier.padding(start = Theme.dimension.size_4dp)
                )
            }
        }
    }
}