package id.teman.app.ui.ordermapscreen.done

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.R
import id.teman.app.common.ButtonType
import id.teman.app.common.noRippleClickable
import id.teman.app.ui.theme.Theme
import id.teman.app.ui.theme.buttons.TemanFilledButton
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Composable
fun OrderDetailRatingForm(onRatingClick: (Int, String) -> Unit, enableButton: Boolean) {
    var ratingState by remember { mutableStateOf(0) }
    var feedbackValue by remember { mutableStateOf("") }

    Column {
        Text(
            "Gimana Perjalanannya ?", style = UiFont.poppinsH5Bold, textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            "Feedback kamu bakal membantu kita untuk mengembangakan Experience loh!",
            style = UiFont.poppinsP2Medium.copy(color = UiColor.neutral500),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(Theme.dimension.size_32dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (i in 1..5) {
                GlideImage(
                    imageModel = R.drawable.ic_star,
                    imageOptions = ImageOptions(
                        colorFilter = ColorFilter.tint(
                            color = if (i <= ratingState) UiColor.primaryYellow500 else UiColor.neutral100
                        )
                    ),
                    modifier = Modifier
                        .size(Theme.dimension.size_30dp)
                        .noRippleClickable {
                            ratingState = i
                        }
                )
            }
        }
        Spacer(modifier = Modifier.height(Theme.dimension.size_32dp))
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(Theme.dimension.size_80dp),
            value = feedbackValue,
            shape = RoundedCornerShape(Theme.dimension.size_12dp),
            placeholder = {
                Text(
                    "Masukkan ulasan disini...",
                    style = UiFont.poppinsP2Medium.copy(color = UiColor.neutral500)
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = UiColor.neutral100,
                cursorColor = UiColor.black,
                unfocusedBorderColor = UiColor.neutral100
            ),
            onValueChange = {
                feedbackValue = it
            },
        )
        Spacer(modifier = Modifier.height(Theme.dimension.size_24dp))
        TemanFilledButton(
            content = "Kirim Penilaian Driver",
            buttonType = ButtonType.Large,
            activeColor = UiColor.primaryRed500,
            activeTextColor = Color.White,
            isEnabled = enableButton,
            borderRadius = Theme.dimension.size_30dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            onRatingClick(ratingState, feedbackValue)
        }
    }
}

@Composable
fun OrderDetailRestoRatingForm(onRatingClick: (Int, String) -> Unit, enableButton: Boolean) {
    var ratingState by remember { mutableStateOf(0) }
    var feedbackValue by remember { mutableStateOf("") }

    Column {
        Text(
            "Gimana Makanannya ?\nYuk Rating Resto nya...", style = UiFont.poppinsH5Bold, textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            "Feedback kamu bakal membantu kita untuk mengembangakan Experience loh!",
            style = UiFont.poppinsP2Medium.copy(color = UiColor.neutral500),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(Theme.dimension.size_32dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (i in 1..5) {
                GlideImage(
                    imageModel = R.drawable.ic_star,
                    imageOptions = ImageOptions(
                        colorFilter = ColorFilter.tint(
                            color = if (i <= ratingState) UiColor.primaryYellow500 else UiColor.neutral100
                        )
                    ),
                    modifier = Modifier
                        .size(Theme.dimension.size_30dp)
                        .noRippleClickable {
                            ratingState = i
                        }
                )
            }
        }
        Spacer(modifier = Modifier.height(Theme.dimension.size_32dp))
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(Theme.dimension.size_80dp),
            value = feedbackValue,
            shape = RoundedCornerShape(Theme.dimension.size_12dp),
            placeholder = {
                Text(
                    "Masukkan ulasan disini...",
                    style = UiFont.poppinsP2Medium.copy(color = UiColor.neutral500)
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = UiColor.neutral100,
                cursorColor = UiColor.black,
                unfocusedBorderColor = UiColor.neutral100
            ),
            onValueChange = {
                feedbackValue = it
            },
        )
        Spacer(modifier = Modifier.height(Theme.dimension.size_24dp))
        TemanFilledButton(
            content = "Kirim Penilaian Resto",
            buttonType = ButtonType.Large,
            activeColor = UiColor.primaryRed500,
            activeTextColor = Color.White,
            isEnabled = enableButton,
            borderRadius = Theme.dimension.size_30dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            onRatingClick(ratingState, feedbackValue)
        }
    }
}