package id.teman.app.domain.model.chat

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatMessageSpec(
    val isSelfMessage: Boolean,
    val sendTime: String,
    val message: String,
) : Parcelable