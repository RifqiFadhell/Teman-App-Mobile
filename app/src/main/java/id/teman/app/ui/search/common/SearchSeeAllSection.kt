package id.teman.app.ui.search.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import id.teman.app.ui.theme.Theme
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Composable
fun SearchSeeAllSection(text: String) {
    Text(
        text,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Theme.dimension.size_20dp),
        style = UiFont.poppinsH5SemiBold.copy(color = UiColor.tertiaryBlue500)
    )
}