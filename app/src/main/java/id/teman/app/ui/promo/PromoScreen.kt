package id.teman.app.ui.promo

import id.teman.app.R as rAppModule
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.common.CenterTopAppBar
import id.teman.app.common.CustomChip
import id.teman.app.common.CustomLoading
import id.teman.app.common.DisposableEffectOnLifecycleEvent
import id.teman.app.common.EmptyState
import id.teman.app.domain.model.promo.PromoFeature
import id.teman.app.ui.NavGraphs
import id.teman.app.ui.destinations.HomeScreenDestination
import id.teman.app.ui.theme.Theme
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

fun onBackPressed(navigator: DestinationsNavigator) {
    navigator.navigate(NavGraphs.root) {
        popUpTo(HomeScreenDestination)
    }
}

@Destination
@Composable
fun PromoScreen(navigator: DestinationsNavigator, viewModel: PromoViewModel = hiltViewModel()) {
    val uiState = viewModel.promoUiState
    BackHandler {
        onBackPressed(navigator)
    }
    DisposableEffectOnLifecycleEvent(lifecycleEvent = Lifecycle.Event.ON_RESUME) {
        viewModel.getPromoList()
    }

    if (uiState.loading) {
        Dialog(
            onDismissRequest = { },
            DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            CustomLoading(modifier = Modifier.fillMaxSize())
        }
    }
    Scaffold(
        topBar = {
            CenterTopAppBar(
                backgroundColor = Color.Transparent,
                elevation = Theme.dimension.size_0dp,
                title = {
                    Text(
                        text = "Promo",
                        style = UiFont.poppinsH5SemiBold,
                        textAlign = TextAlign.Center
                    )
                },
            )
        },
        content = {
            Column(
                modifier = Modifier.padding(
                    top = Theme.dimension.size_32dp,
                    start = Theme.dimension.size_16dp,
                    end = Theme.dimension.size_16dp
                )
            ) {
                var activeCategory by remember { mutableStateOf(PromoFeature.ALL) }
                val promoBySections = if (activeCategory == PromoFeature.ALL) {
                    uiState.promoList?.groupBy { it.sectionTitle }
                } else {
                    uiState.promoList?.filter { it.sectionTitle == activeCategory }
                        ?.groupBy { it.sectionTitle }
                }

                LazyRow {
                    items(PromoFeature.values()) { item ->
                        CustomChip(
                            title = item.title,
                            backgroundColor = (item == activeCategory).chipBackgroundColor,
                            textColor = (item == activeCategory).chipTextColor,
                            modifier = Modifier
                                .wrapContentHeight()
                                .padding(end = Theme.dimension.size_8dp),
                            textModifier = Modifier.padding(
                                vertical = Theme.dimension.size_8dp,
                                horizontal = Theme.dimension.size_12dp
                            )
                        ) {
                            activeCategory = item
                        }
                    }
                }
                LazyColumn(
                    modifier = Modifier.padding(bottom = Theme.dimension.size_24dp)
                ) {
                    promoBySections?.forEach { (title, items) ->
                        item {
                            Text(
                                title.name,
                                style = UiFont.poppinsP2SemiBold,
                                modifier = Modifier.padding(top = Theme.dimension.size_24dp)
                            )
                        }
                        items(items.count()) {
                            Column(
                                modifier = Modifier
                                    .padding(top = if (it == 0) Theme.dimension.size_24dp else Theme.dimension.size_16dp)
                                    .border(
                                        BorderStroke(
                                            Theme.dimension.size_1dp,
                                            color = UiColor.neutral50
                                        ),
                                        shape = RoundedCornerShape(Theme.dimension.size_16dp)
                                    )
                                    .padding(Theme.dimension.size_4dp)
                            ) {
                                GlideImage(
                                    modifier = Modifier
                                        .aspectRatio(3f / 1f)
                                        .clip(RoundedCornerShape(Theme.dimension.size_12dp)),
                                    imageModel = items[it].imageUrl,
                                )
                                Spacer(modifier = Modifier.height(Theme.dimension.size_16dp))
                                Text(
                                    items[it].title,
                                    modifier = Modifier.padding(horizontal = Theme.dimension.size_12dp),
                                    style = UiFont.poppinsSubHSemiBold
                                )
                                Spacer(modifier = Modifier.height(Theme.dimension.size_8dp))
                                Row(modifier = Modifier.padding(horizontal = Theme.dimension.size_12dp)) {
                                    GlideImage(
                                        imageModel = rAppModule.drawable.ic_calendar,
                                        modifier = Modifier.size(Theme.dimension.size_16dp)
                                    )
                                    Spacer(modifier = Modifier.width(Theme.dimension.size_8dp))
                                    Text(
                                        items[it].expired,
                                        style = UiFont.poppinsCaptionMedium.copy(color = UiColor.neutral300)
                                    )
                                }
                                Spacer(modifier = Modifier.height(Theme.dimension.size_16dp))
                            }
                        }
                    }
                }
            }
        }
    )
    if (uiState.promoList.isNullOrEmpty()) EmptyState(
        icon = rAppModule.drawable.ic_no_promo,
        title = "Belum Ada Kupon Promo",
        description = "Tenang aja, kita bakal kabari kamu ketika ada Promo baru."
    )
}

private val Boolean.chipTextColor
    get() = if (this) UiColor.white else UiColor.neutral500

private val Boolean.chipBackgroundColor
    get() = if (this) UiColor.neutral900 else Color.Transparent