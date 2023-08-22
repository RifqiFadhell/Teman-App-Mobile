package id.teman.app.utils

import android.net.Uri
import androidx.core.net.toUri
import id.teman.app.ui.destinations.RegisterScreenDestination

object DeeplinkPattern {

    private val BaseUri = "https://teman.app.id".toUri()

    val RegisterPattern = "$BaseUri/${RegisterScreenDestination.route}"

    fun redirectToRegisterUri(): Uri = RegisterPattern.toUri()
}