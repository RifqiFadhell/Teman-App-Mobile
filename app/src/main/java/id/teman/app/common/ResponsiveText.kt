package id.teman.app.common

import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun ResponsiveText(
    text: String,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
    textAlign: TextAlign? = null,
) {
    var multiplier by remember { mutableStateOf(1f) }

    Text(
        text,
        modifier = modifier,
        maxLines = maxLines, // modify to fit your need
        overflow = TextOverflow.Visible,
        style = textStyle.copy(
            fontSize = LocalTextStyle.current.fontSize * multiplier
        ),
        textAlign = textAlign,
        onTextLayout = {
            if (it.hasVisualOverflow) {
                multiplier *= 0.99f // you can tune this constant
            }
        }
    )
}