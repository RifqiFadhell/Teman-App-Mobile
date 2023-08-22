package id.teman.app.domain.model.user

import id.teman.app.common.orFalse
import id.teman.app.data.dto.UserDataDto

fun UserDataDto.toUserInfo(): UserInfo {
    return UserInfo(
        id = id.orEmpty(),
        email = email.orEmpty(),
        name = name.orEmpty(),
        role = role.orEmpty(),
        phoneNumber = phoneNumber.orEmpty(),
        isVerified = verified.orFalse(),
        pinStatus = pinStatus.orFalse(),
        kycStatus = UserKycStatus.from(kycStatus),
        userPhoto = userPhoto?.url.orEmpty(),
        point = point,
        notification = notification.orFalse(),
        referralCode = referral.orEmpty()
    )
}