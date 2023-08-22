package id.teman.app.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.R
import id.teman.app.ui.destinations.LoginScreenDestination
import id.teman.app.ui.theme.Theme
import id.teman.app.ui.theme.buttons.TemanCircleButtonCLicked
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont
import kotlinx.coroutines.launch

@Destination
@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnBoardingScreen(
    navigator: DestinationsNavigator,
    viewModel: OnBoardingViewModel = hiltViewModel()
) {
    val state = rememberPagerState()
    val scope = rememberCoroutineScope()
    Column {
        GlideImage(
            imageModel = R.drawable.teman_logo,
            modifier = Modifier
                .size(Theme.dimension.size_146dp)
                .align(Alignment.CenterHorizontally),
            imageOptions = ImageOptions(contentScale = ContentScale.Fit)
        )
        HorizontalPager(count = 2, state = state, userScrollEnabled = false) { page ->
            when (page) {
                0 -> OnBoardingUi(
                    onBoardingSpec = OnBoardingSpec(
                        image = R.drawable.onboarding_first,
                        title = stringResource(id = R.string.on_boarding_title_first),
                        description = stringResource(id = R.string.on_boarding_caption_first)
                    )
                )
                1 -> OnBoardingUi(
                    onBoardingSpec = OnBoardingSpec(
                        image = R.drawable.onboarding_second,
                        title = stringResource(id = R.string.on_boarding_title_second),
                        description = stringResource(id = R.string.on_boarding_caption_second)
                    )
                )
            }
        }
        DotsIndicator(
            totalDots = 2,
            selectedIndex = state.currentPage,
            selectedColor = UiColor.black,
            unSelectedColor = UiColor.neutral100
        )
        TemanCircleButtonCLicked(
            icon = R.drawable.ic_right_arrow,
            iconColor = UiColor.white,
            circleBackgroundColor = UiColor.primaryRed500,
            circleModifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = Theme.dimension.size_40dp)
                .size(Theme.dimension.size_56dp),
            iconModifier = Modifier.size(Theme.dimension.size_28dp),
            onClick = {
                scope.launch {
                    if (state.currentPage == 1) {
                        viewModel.saveUserHasFinishedOnBoard()
                        navigator.navigate(LoginScreenDestination)
                    } else {
                        state.scrollToPage(state.currentPage + 1)
                    }
                }
            })
    }

}

@Composable
fun OnBoardingUi(onBoardingSpec: OnBoardingSpec) {
    Column {
        GlideImage(
            imageModel = onBoardingSpec.image,
            modifier = Modifier
                .fillMaxWidth()
                .size(237.dp),
            imageOptions = ImageOptions(
                contentScale = ContentScale.Fit,
                alignment = Alignment.Center
            )
        )
        Text(
            modifier = Modifier.padding(
                top = Theme.dimension.size_40dp,
                start = Theme.dimension.size_36dp,
                end = Theme.dimension.size_36dp
            ), text = onBoardingSpec.title, style = UiFont.poppinsH3Bold
        )
        Text(
            modifier = Modifier.padding(
                horizontal = Theme.dimension.size_36dp,
                vertical = Theme.dimension.size_16dp
            ), text = onBoardingSpec.description, style = UiFont.poppinsP3Medium
        )
    }
}

@Composable
fun DotsIndicator(
    totalDots: Int,
    selectedIndex: Int,
    selectedColor: Color,
    unSelectedColor: Color,
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = Theme.dimension.size_16dp),
        horizontalArrangement = Arrangement.Center
    ) {
        items(totalDots) { index ->
            if (index == selectedIndex) {
                Box(
                    modifier = Modifier
                        .size(Theme.dimension.size_8dp)
                        .clip(CircleShape)
                        .background(selectedColor)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(Theme.dimension.size_8dp)
                        .clip(CircleShape)
                        .background(unSelectedColor)
                )
            }
            if (index != totalDots - 1) {
                Spacer(modifier = Modifier.padding(horizontal = 2.dp))
            }
        }
    }
}