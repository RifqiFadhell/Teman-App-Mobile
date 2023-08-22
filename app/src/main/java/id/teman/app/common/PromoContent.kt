package id.teman.app.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.R
import id.teman.app.domain.model.home.BannerHomeSpec
import id.teman.app.ui.theme.Theme
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PromoContent(banners: List<BannerHomeSpec>, onClick: (String, String) -> Unit) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val pagerState = rememberPagerState(initialPage = 0)
    val coroutineScope = rememberCoroutineScope()

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.padding(top = Theme.dimension.size_32dp),
        count = banners.size,
        contentPadding =
        PaddingValues(horizontal = Theme.dimension.size_28dp),
        itemSpacing = Theme.dimension.size_16dp
    ) { index ->
        GlideImage(
            modifier = Modifier
                .aspectRatio(2/1f)
                .width(screenWidth - (Theme.dimension.size_16dp * 2))
                .height(Theme.dimension.size_144dp)
                .clickable {
                           onClick(banners[index].url, banners[index].title)
                }
            ,
            imageOptions = ImageOptions(contentScale = ContentScale.Fit),
            imageModel = banners[index].image,
            failure = {
                R.drawable.ic_no_image
            }
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = Theme.dimension.size_6dp)
    ) {
        PagerIndicator(
            modifier = Modifier.align(Alignment.Center),
            indicatorSize = Theme.dimension.size_6dp,
            pagerState = pagerState
        ) {
            coroutineScope.launch {
                pagerState.scrollToPage(it)
            }
        }
    }
}