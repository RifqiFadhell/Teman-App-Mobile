package id.teman.app.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import id.teman.app.ui.theme.Theme.dimension
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Composable
fun TextInputFieldIcon(
    title: String,
    textBox: String,
    placeholders: String,
    keyboardOptions: KeyboardOptions,
    modifier: Modifier,
    onChange: (String) -> Unit,
    error: Boolean? = false,
    message: String? = ""
) {
    var value by remember { mutableStateOf("") }
    Column(modifier = modifier) {
        Text(
            title,
            style = UiFont.poppinsP2Medium
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = placeholders,
                    style =
                    UiFont.poppinsP2Medium.copy(color = UiColor.neutral300)
                )
            },
            value = value,
            keyboardOptions = keyboardOptions,
            onValueChange = {
                value = it
                onChange(it)
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = if (!error.orFalse()) UiColor.neutral100 else UiColor.error500,
                cursorColor = if (!error.orFalse()) UiColor.black else UiColor.error500,
                unfocusedBorderColor = if (!error.orFalse()) UiColor.neutral100 else UiColor.error500
            ),
            leadingIcon = {
                Box(
                    modifier = Modifier
                        .size(TextFieldDefaults.MinHeight)
                        .background(color = UiColor.neutral100)
                ) {
                    Text(
                        textBox, style = UiFont.poppinsCaptionSemiBold,
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center
                    )
                }
            }
        )
        if (error.orFalse()) {
            Text(
                text = message.orEmpty(),
                color = UiColor.error500,
                style = UiFont.cabinCaptionSmallSemiBold,
                modifier = Modifier.padding(start = dimension.size_16dp)
            )
        }
    }
}

@Composable
fun TextInputField(
    title: String,
    placeholders: String,
    keyboardOptions: KeyboardOptions,
    modifier: Modifier,
    text: String = "",
    isEnabled: Boolean = true,
    onChange: (String) -> Unit
) {
    var value by remember { mutableStateOf(text) }
    Column(modifier = modifier) {
        Text(
            title,
            style = UiFont.poppinsP2Medium
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = {
                Text(modifier = Modifier.align(Alignment.CenterHorizontally), text = placeholders)
            },
            value = value,
            keyboardOptions = keyboardOptions,
            onValueChange = {
                value = it
                onChange(it)
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = UiColor.neutral100,
                cursorColor = UiColor.black,
                unfocusedBorderColor = UiColor.neutral100
            ),
            enabled = isEnabled
        )
    }
}

@Composable
fun InputNumberWidget(title: String, placeholders: String, onChange: (String) -> Unit) {
    var number by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    Column {
        Text(
            title,
            style = UiFont.poppinsP2Medium,
            modifier = Modifier.padding(top = dimension.size_24dp,
                start = dimension.size_16dp,
                end = dimension.size_16dp)
        )
        OutlinedTextField(
            modifier = Modifier
                .padding(horizontal = dimension.size_16dp, vertical = dimension.size_4dp)
                .fillMaxWidth()
                .height(dimension.size_56dp),
            value = number,
            placeholder = {
                Text(modifier = Modifier.align(Alignment.CenterHorizontally), text = placeholders)
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = UiColor.neutral100,
                cursorColor = UiColor.black,
                unfocusedBorderColor = UiColor.neutral100
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Number),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }),
            onValueChange = {
                number = it
                onChange(number)
            },
        )
    }
}