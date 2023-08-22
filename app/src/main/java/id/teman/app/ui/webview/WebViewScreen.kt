package id.teman.app.ui.webview

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.view.View.LAYER_TYPE_HARDWARE
import android.webkit.*
import android.webkit.WebView.RENDERER_PRIORITY_BOUND
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import id.teman.app.common.CustomLoading
import id.teman.app.common.TopBar
import id.teman.coreui.typography.UiColor

@Composable
@Destination
fun WebViewScreen(navigator: DestinationsNavigator, title: String, url: String) {
    val context = LocalContext.current
    val activity = context as Activity
    var isLoading by remember { mutableStateOf(true) }
    Scaffold(
        topBar = {
            TopBar(title = title) {
                navigator.popBackStack()
            }
        },
        content = {
            AndroidView(factory = {
                WebView(context)
            }, update = {
                it.apply {
                    webViewClient = object: WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            isLoading = false
                        }

                        override fun onPageStarted(
                            view: WebView?,
                            url: String?,
                            favicon: Bitmap?
                        ) {
                            super.onPageStarted(view, url, favicon)
                        }

                        override fun onReceivedError(
                            view: WebView?,
                            request: WebResourceRequest?,
                            error: WebResourceError?
                        ) {
                            super.onReceivedError(view, request, error)
                            isLoading = false
                        }

                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            val requestUrl = request?.url.toString()
                            return if (requestUrl.contains("gojek://")
                                || requestUrl.contains("shopeeid://")
                                || requestUrl.contains("//wsa.wallet.airpay.co.id/")

                                // This is handle for sandbox Simulator
                                || requestUrl.contains("/gopay/partner/")
                                || requestUrl.contains("/shopeepay/")) {
                                val intent = Intent(Intent.ACTION_VIEW, request?.url)
                                activity.startActivity(intent)
                                true
                            } else {
                                false
                            }

                        }
                    }
                    with(settings) {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
                        loadsImagesAutomatically = true
                    }
                    loadUrl(url)
                }
            }, modifier = Modifier.fillMaxSize())
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = UiColor.primaryRed500
                    )
                }
            }
        })
}