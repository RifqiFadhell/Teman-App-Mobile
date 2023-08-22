package id.teman.app.ui.orderlist.ui

import id.teman.app.R as RApp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.common.ButtonType
import id.teman.app.ui.orderlist.domain.model.OrderHistorySectionUiModel
import id.teman.app.ui.orderlist.domain.model.fakeOrderHistoryList
import id.teman.app.ui.theme.Theme
import id.teman.app.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Destination
@Composable
fun OrdersHistoryScreen(navigator: DestinationsNavigator) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = Theme.dimension.size_16dp,
                    vertical = Theme.dimension.size_20dp
                )
        ) {
            GlideImage(
                imageModel = RApp.drawable.ic_arrow_back,
                modifier = Modifier
                    .size(Theme.dimension.size_24dp)
                    .clickable {
                        navigator.popBackStack()
                    }
            )
            Text(
                "Riwayat", style = UiFont.poppinsH5SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
            )
        }

        LazyColumn {
            itemsIndexed(fakeOrderHistoryList) { index, item ->
                when (item) {
                    is OrderHistorySectionUiModel.Item -> OrderHistoryRowItem(item)
                    is OrderHistorySectionUiModel.Title -> Text(
                        item.text,
                        style = UiFont.poppinsP2SemiBold,
                        modifier = Modifier.padding(start = Theme.dimension.size_16dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
private fun OrderHistoryRowItem(item: OrderHistorySectionUiModel.Item) {
    Column(
        modifier = Modifier
            .padding(Theme.dimension.size_16dp)
            .border(
                border = BorderStroke(
                    width = Theme.dimension.size_1dp,
                    color = UiColor.neutral50
                ),
                shape = RoundedCornerShape(Theme.dimension.size_16dp)
            )
            .padding(Theme.dimension.size_16dp)
    ) {
        Row {
            GlideImage(
                imageModel = RApp.drawable.ic_no_image,
                modifier = Modifier
                    .width(Theme.dimension.size_100dp)
                    .height(Theme.dimension.size_80dp)
                    .clip(RoundedCornerShape(Theme.dimension.size_12dp))
            )
            Spacer(modifier = Modifier.width(Theme.dimension.size_16dp))
            Column(
                modifier = Modifier
                    .padding(start = Theme.dimension.size_8dp)
                    .weight(1f)
            ) {
                Text(
                    item.title,
                    style = UiFont.poppinsP3SemiBold.copy(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(Theme.dimension.size_4dp))
                Text(
                    item.subtitle,
                    style = UiFont.poppinsCaptionMedium.copy(color = UiColor.neutral500),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(Theme.dimension.size_8dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(Theme.dimension.size_12dp)
                            .background(color = UiColor.blue)
                    )
                    Text(
                        stringResource(item.orderListType.orderStatus),
                        modifier = Modifier.padding(start = Theme.dimension.size_8dp),
                        style = UiFont.poppinsCaptionMedium.copy(
                            color = UiColor.blue,
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            )
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .padding(top = Theme.dimension.size_12dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Total", style = UiFont.poppinsH5SemiBold)
            Text(item.totalOrderPayment, style = UiFont.poppinsH5SemiBold)
        }
        Row(
            modifier = Modifier.padding(top = Theme.dimension.size_16dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            if (item.showOrderButton) {
                TemanFilledButton(
                    modifier = Modifier.weight(1f),
                    content = "Pesan Lagi",
                    buttonType = ButtonType.Small,
                    activeTextColor = Color.White,
                    borderRadius = Theme.dimension.size_30dp,
                    isEnabled = true,
                    activeColor = UiColor.primaryRed500,
                    onClicked = {}
                )
            }
            if (item.showRatingButton) {
                Spacer(modifier = Modifier.width(Theme.dimension.size_16dp))
                OutlinedButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(Theme.dimension.size_32dp),
                    shape = RoundedCornerShape(
                        Theme.dimension.size_30dp,
                    ),
                    border = BorderStroke(
                        width = Theme.dimension.size_1dp,
                        color = UiColor.primaryRed500
                    ),
                    content = {
                        Text(
                            "Penilaian",
                            style = UiFont.poppinsCaptionSemiBold.copy(
                                color = UiColor.primaryRed500,
                                platformStyle = PlatformTextStyle(
                                    includeFontPadding = false
                                )
                            ),
                            textAlign = TextAlign.Center,
                        )
                    },
                    onClick = {}
                )
            }
        }
    }
}