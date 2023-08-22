package id.teman.app.ui.ordermapscreen.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import id.teman.app.ui.theme.Theme
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Composable
fun BottomContentUIWrapper(
    modifier: Modifier = Modifier,
    headingText: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(
            topStart = Theme.dimension.size_32dp,
            topEnd = Theme.dimension.size_32dp
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(
                vertical = Theme.dimension.size_12dp,
                horizontal = Theme.dimension.size_24dp
            ),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(Theme.dimension.size_100dp)
                    .height(Theme.dimension.size_4dp)
                    .background(
                        color = UiColor.neutral50,
                        shape = RoundedCornerShape(Theme.dimension.size_30dp)
                    )
            )
            Spacer(modifier = Modifier.height(Theme.dimension.size_16dp))
            Text(headingText, style = UiFont.poppinsH5Bold)
            Spacer(modifier = Modifier.height(Theme.dimension.size_22dp))
            content()
            Spacer(modifier = Modifier.height(Theme.dimension.size_20dp))
        }
    }
}