package id.teman.app.ui.ordermapscreen.chat

import id.teman.app.R as RApp
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.domain.model.chat.ChatMessageSpec
import id.teman.app.domain.model.order.OrderDetailSpec
import id.teman.app.ui.theme.Theme
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@Destination
@Composable
fun ChatScreen(
    navigator: DestinationsNavigator,
    viewModel: ChatViewModel = hiltViewModel(),
    item: OrderDetailSpec,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    BackHandler {
        navigator.popBackStack()
    }
    val itemSpec by remember { mutableStateOf(item) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                viewModel.initData(itemSpec.requestId)
            } else if (event == Lifecycle.Event.ON_PAUSE) {
                viewModel.stopEmitData()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    val uiState = viewModel.chatUiState
    Scaffold(
        topBar = {
            TopBar(itemSpec) {
                navigator.popBackStack()
            }
        },
        bottomBar = {
            BottomBar {
                viewModel.sendMessage(itemSpec.requestId, it)
            }
        },
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                ChatContent(uiState.chatMessages.reversed())
                if (uiState.loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = UiColor.primaryRed500
                    )
                }
            }
        }
    )
}

@Composable
fun TopBar(item: OrderDetailSpec, onBackClicked: () -> Unit) {
    TopAppBar(
        elevation = Theme.dimension.size_0dp,
        backgroundColor = Color.White,
        contentPadding = PaddingValues(horizontal = Theme.dimension.size_16dp),
        modifier = Modifier.height(Theme.dimension.size_60dp)
    ) {
        Box {
            GlideImage(
                imageModel = RApp.drawable.ic_arrow_back,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(Theme.dimension.size_24dp)
                    .clickable {
                        onBackClicked()
                    }
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(item.driverName, style = UiFont.poppinsH5SemiBold)
                Text(
                    "${item.driverVehicleLicenseNumber} • ${item.driverVehicleBrand}, ${item.driverVehicleType}",
                    style = UiFont.poppinsP2Medium
                )
            }
        }
    }
}

@Composable
fun BottomBar(onSendButtonClicked: (String) -> Unit) {
    val focusManager = LocalFocusManager.current
    var textFieldValue by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(color = Color.White)
            .padding(
                horizontal = Theme.dimension.size_16dp,
                vertical = Theme.dimension.size_12dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            modifier = Modifier.weight(1f),
            value = textFieldValue,
            placeholder = {
                Text("Kirim pesan ke rider...")
            },
            shape = RoundedCornerShape(Theme.dimension.size_30dp),
            onValueChange = {
                textFieldValue = it
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = UiColor.neutral100,
                cursorColor = UiColor.black,
                unfocusedBorderColor = UiColor.neutral100
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }),
        )
        Spacer(modifier = Modifier.width(Theme.dimension.size_18dp))
        GlideImage(
            imageModel = RApp.drawable.ic_chat_send,
            modifier = Modifier
                .size(Theme.dimension.size_32dp)
                .clickable {
                    if (textFieldValue.isNotBlank()) {
                        onSendButtonClicked(textFieldValue.trim())
                        textFieldValue = ""
                    }
                }
        )
    }
}

@Composable
fun ChatContent(chatItems: List<ChatMessageSpec>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFE9E9E9)),
        reverseLayout = true,
        contentPadding = PaddingValues(
            start = Theme.dimension.size_16dp, end = Theme.dimension.size_16dp,
            bottom = Theme.dimension.size_100dp
        )
    ) {
        itemsIndexed(chatItems) { _, item ->
            ChatContentItem(item)
        }
    }
}

@Composable
private fun ChatContentItem(item: ChatMessageSpec) {
    val currentConfig = LocalConfiguration.current
    val screenWidth = currentConfig.screenWidthDp
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
//            buildAnnotatedString {
//                append(item.title)
//                withStyle(style = ParagraphStyle(textAlign = TextAlign.End)) {
//                    withStyle(style = SpanStyle(color = UiColor.neutral300)) {
//                        append(item.sendTime)
//                    }
//                }
//            },
            buildAnnotatedString {
                append(item.message)
                withStyle(style = SpanStyle(color = UiColor.neutral300)) {
                    append("\t\t${item.sendTime}")
                }
            },
            style = UiFont.poppinsP3Medium.copy(color = if (!item.isSelfMessage) UiColor.black else UiColor.white),
            modifier = Modifier
                .padding(top = Theme.dimension.size_24dp)
                .align(if (!item.isSelfMessage) Alignment.Start else Alignment.End)
                .widthIn(Theme.dimension.size_0dp, (screenWidth * 0.7).dp)
                .background(
                    shape = RoundedCornerShape(
                        topStart = Theme.dimension.size_12dp,
                        topEnd = Theme.dimension.size_12dp,
                        bottomEnd = if (!item.isSelfMessage) Theme.dimension.size_12dp else Theme.dimension.size_0dp,
                        bottomStart = if (!item.isSelfMessage) Theme.dimension.size_0dp else Theme.dimension.size_12dp
                    ),
                    color = if (!item.isSelfMessage) UiColor.white else UiColor.tertiaryBlue500
                )
                .padding(
                    horizontal = Theme.dimension.size_12dp,
                    vertical = Theme.dimension.size_10dp
                )
        )
    }
}

@Composable
fun OnLifecycleEvent(onEvent: (owner: LifecycleOwner, event: Lifecycle.Event) -> Unit) {
    val eventHandler = rememberUpdatedState(onEvent)
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

    DisposableEffect(lifecycleOwner.value) {
        val lifecycle = lifecycleOwner.value.lifecycle
        val observer = LifecycleEventObserver { owner, event ->
            eventHandler.value(owner, event)
        }

        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}