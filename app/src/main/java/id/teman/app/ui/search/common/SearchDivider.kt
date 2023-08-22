package id.teman.app.ui.search.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import id.teman.app.ui.theme.Theme
import id.teman.coreui.typography.UiColor

@Composable
fun SearchDivider() {
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .height(Theme.dimension.size_8dp)
            .background(color = UiColor.neutralGray0)
    )
}