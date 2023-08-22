package id.teman.app.data.remote

object HttpRoutes {
    val BASE_ROUTES = "https://apidev.temanofficial.co.id/api/"

    /*
    AUTH
    */
    const val LOGIN_URL = "auth/login_with_phone"
    const val REGISTER_URL = "auth/register_with_phone"
    const val SEND_OTP_URL = "auth/send_otp_code"
    const val VERIFICATION_OTP_URL = "auth/verification_otp_code"
    const val GET_PROFILE = "auth/profile"
    const val GET_HOME_MENUS = "customer/dashboard_menu"
}