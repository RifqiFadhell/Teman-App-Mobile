package id.teman.app.ui.ordermapscreen.done

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.R
import id.teman.app.common.ButtonType
import id.teman.app.common.orFalse
import id.teman.app.ui.theme.Theme
import id.teman.app.ui.theme.buttons.TemanFilledButton
import id.teman.app.utils.addCharEveryFour
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Composable
fun BoxScope.OrderFinished(
    spec: OrderFinishedSpec = OrderFinishedSpec(),
    alignment: Alignment = Alignment.BottomCenter,
    onButtonClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .align(alignment)
            .fillMaxWidth()
            .background(
                shape = RoundedCornerShape(
                    topEnd = Theme.dimension.size_32dp,
                    topStart = Theme.dimension.size_32dp,
                ),
                color = Color.White
            )
            .padding(vertical = Theme.dimension.size_32dp, horizontal = Theme.dimension.size_16dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            spec.title,
            textAlign = TextAlign.Center,
            style = UiFont.poppinsH5Bold
        )
        GlideImage(
            imageModel = R.drawable.order_finished_bg,
            modifier = Modifier.size(Theme.dimension.size_200dp),
            imageOptions = ImageOptions(
                contentScale = ContentScale.Fit
            )
        )
        TemanFilledButton(
            content = spec.button,
            buttonType = ButtonType.Large,
            activeColor = UiColor.primaryRed500,
            activeTextColor = Color.White,
            isEnabled = true,
            borderRadius = Theme.dimension.size_30dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            onButtonClick()
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun BoxScope.BillFinished(
    spec: OrderFinishedSpec = OrderFinishedSpec(),
    alignment: Alignment = Alignment.BottomCenter,
    onButtonClick: () -> Unit
) {
    val finalPrice = if (spec.price?.contains("-", false).orFalse()) spec.price?.replace("-", "")
        .orEmpty() else spec.price.orEmpty()
    Scaffold(
        content = {
            Column(
                modifier = Modifier
                    .align(alignment)
                    .fillMaxWidth()
                    .background(
                        shape = RoundedCornerShape(
                            topEnd = Theme.dimension.size_32dp,
                            topStart = Theme.dimension.size_32dp,
                        ),
                        color = Color.White
                    )
                    .padding(
                        horizontal = Theme.dimension.size_16dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    spec.title,
                    textAlign = TextAlign.Center,
                    style = UiFont.poppinsH5Bold,
                    modifier = Modifier.padding(bottom = Theme.dimension.size_16dp)
                )
                GlideImage(
                    modifier = Modifier
                        .padding(horizontal = Theme.dimension.size_32dp)
                        .size(Theme.dimension.size_64dp),
                    imageOptions = ImageOptions(alignment = Alignment.Center),
                    imageModel = spec.icon,
                    failure = {
                        R.drawable.ic_no_image
                    }
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Theme.dimension.size_16dp)
                )
                if (spec.serialNumber.isNullOrEmpty()) {
                    Text(finalPrice,
                        style = UiFont.poppinsP2Medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = Theme.dimension.size_12dp,
                                start = Theme.dimension.size_16dp,
                                end = Theme.dimension.size_16dp
                            ),
                        textAlign = TextAlign.Center
                    )
                }
                Text(
                    modifier = Modifier,
                    text = stringResource(
                        id = R.string.finish_bill,
                        spec.category.orEmpty(),
                        spec.number.orEmpty()
                    ),
                    textAlign = TextAlign.Center,
                    style = UiFont.poppinsP2Medium
                )
                if (spec.serialNumber.orEmpty().isNotEmpty()) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Theme.dimension.size_24dp)
                    )
                    val list = spec.serialNumber?.split("/").orEmpty()
                    val final = ArrayList<String>()
                    val title = arrayListOf("Token: ", "Nama: ", "Type: ", "Watt: ", "Kwh: ")
                    val serialNumber = if (spec.category == "PLN") {
                        list.zip(title).forEach { pair ->
                            final.add(pair.component2() + pair.component1())
                        }
                        final.joinToString("\n")
                    } else {
                        spec.serialNumber.orEmpty()
                    }
                    CopySerialNumber(
                        title = serialNumber,
                        serialNumber = spec.serialNumber.orEmpty(),
                        provider = "${spec.category} $finalPrice",
                        token = if (spec.category == "PLN") list.getOrNull(0).orEmpty() else ""
                    )
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Theme.dimension.size_24dp)
                    )
                }
                Text(
                    spec.caption,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(
                        top = Theme.dimension.size_36dp,
                        bottom = Theme.dimension.size_16dp
                    ),
                    style = UiFont.poppinsP3SemiBold.copy(color = UiColor.black)
                )
                GlideImage(
                    imageModel = R.drawable.ic_bill_completed,
                    modifier = Modifier.size(Theme.dimension.size_200dp),
                    imageOptions = ImageOptions(
                        contentScale = ContentScale.Fit
                    )
                )
            }
        }, bottomBar = {
            TemanFilledButton(
                content = spec.button,
                buttonType = ButtonType.Large,
                activeColor = UiColor.primaryRed500,
                activeTextColor = Color.White,
                isEnabled = true,
                borderRadius = Theme.dimension.size_30dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Theme.dimension.size_16dp)
            ) {
                onButtonClick()
            }
        })
}


@Composable
fun CopySerialNumber(provider: String, title: String, serialNumber: String, token: String = "") {
    val clipboardManager = LocalClipboardManager.current
    var showSuccessCopy by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.dimension.size_16dp)
            .background(
                UiColor.primaryYellow50,
                shape = RoundedCornerShape(Theme.dimension.size_8dp)
            )
    ) {
        Text(
            provider,
            style = UiFont.poppinsP2Medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = Theme.dimension.size_12dp,
                    start = Theme.dimension.size_16dp,
                    end = Theme.dimension.size_16dp
                ),
            textAlign = TextAlign.Center
        )
        Text(
            title,
            style = UiFont.poppinsP3SemiBold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = Theme.dimension.size_6dp,
                    start = Theme.dimension.size_16dp,
                    end = Theme.dimension.size_16dp
                ),
            textAlign = TextAlign.Center
        )
        Text(
            "Salin Token",
            style = UiFont.poppinsP2Medium.copy(color = UiColor.tertiaryBlue500),
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = Theme.dimension.size_8dp,
                    bottom = Theme.dimension.size_8dp
                )
                .clickable {
                    clipboardManager.setText(AnnotatedString(token.ifEmpty { serialNumber }))
                    showSuccessCopy = true
                },
            textAlign = TextAlign.Center
        )
    }
    if (showSuccessCopy) {
        Text(
            text = "Code Berhasil Di Copy",
            color = UiColor.success500,
            style = UiFont.cabinP2Medium,
            modifier = Modifier.padding()
        )
    }
}

data class OrderFinishedSpec(
    val id: String = "",
    val title: String = "Perjalanan Selesai",
    val caption: String = "Terimakasih telah percaya kepada kami.",
    val button: String = "ke Beranda",
    val category: String? = "",
    val number: String? = "",
    val serialNumber: String? = "",
    val price: String? = "",
    val icon: String? = ""
)