package id.teman.app.data.remote

import id.teman.app.data.dto.BaseResponse
import id.teman.app.data.dto.CouponPromoDto
import id.teman.app.data.dto.HistoryOrders
import id.teman.app.data.dto.HomeBannerItem
import id.teman.app.data.dto.LoginDto
import id.teman.app.data.dto.LoginRequest
import id.teman.app.data.dto.MenusItem
import id.teman.app.data.dto.RegisterRequest
import id.teman.app.data.dto.RequestOtpDto
import id.teman.app.data.dto.RequestOtpResetDto
import id.teman.app.data.dto.UserDataDto
import id.teman.app.data.dto.UserDto
import id.teman.app.data.dto.VerifyOtpRequest
import id.teman.app.data.dto.bill.BuyBillRequestDto
import id.teman.app.data.dto.bill.BuyPulsaRequestDto
import id.teman.app.data.dto.bill.ItemBillResponseDto
import id.teman.app.data.dto.bill.ItemCategoryBillDto
import id.teman.app.data.dto.bill.ListBillRequestDto
import id.teman.app.data.dto.bill.ListPulsaRequestDto
import id.teman.app.data.dto.bill.SuccessBuyBill
import id.teman.app.data.dto.chat.ChatRequestDto
import id.teman.app.data.dto.chat.ChatResponseDto
import id.teman.app.data.dto.chat.SendMessageResponseDto
import id.teman.app.data.dto.driver.DriverBasicInfoDto
import id.teman.app.data.dto.location.DirectionResponseDto
import id.teman.app.data.dto.location.GooglePredictionsDto
import id.teman.app.data.dto.location.PlaceResponseDto
import id.teman.app.data.dto.location.SnappedPointsDto
import id.teman.app.data.dto.notification.NotificationDto
import id.teman.app.data.dto.notification.NotificationReadRequestDto
import id.teman.app.data.dto.notification.NotificationReadResponseDto
import id.teman.app.data.dto.order.OrderDetailResponseDto
import id.teman.app.data.dto.order.OrderRequestDto
import id.teman.app.data.dto.order.OrderRequestStatusDto
import id.teman.app.data.dto.order.OrderResponseDto
import id.teman.app.data.dto.rating.RatingRequestDto
import id.teman.app.data.dto.rating.RatingResponseDto
import id.teman.app.data.dto.referral.ReferralResponseDto
import id.teman.app.data.dto.restaurant.CategoryResponseDto
import id.teman.app.data.dto.restaurant.DetailRestaurantResponseDto
import id.teman.app.data.dto.restaurant.RestaurantResponseDto
import id.teman.app.data.dto.reward.RewardRedeemRequestDto
import id.teman.app.data.dto.reward.RewardRedeemedResponse
import id.teman.app.data.dto.reward.RewardResponseDto
import id.teman.app.data.dto.reward.RewardTransactionResponseDto
import id.teman.app.data.dto.wallet.WalletBalanceDto
import id.teman.app.data.dto.wallet.WalletHistoryTransactionDetail
import id.teman.app.data.dto.wallet.WalletHistoryTransactionDto
import id.teman.app.data.dto.wallet.WalletRequestDto
import id.teman.app.data.dto.wallet.pin.OtpPinWalletRequestDto
import id.teman.app.data.dto.wallet.pin.OtpWalletDto
import id.teman.app.data.dto.wallet.pin.PinWalletRequestDto
import id.teman.app.data.dto.wallet.pin.UpdatePinWalletDto
import id.teman.app.data.dto.wallet.pin.VerifyOtpWalletDto
import id.teman.app.data.dto.wallet.withdrawal.ItemBankDto
import id.teman.app.data.dto.wallet.withdrawal.WalletBankAccountDto
import id.teman.app.data.dto.wallet.withdrawal.WithdrawRequestDto
import id.teman.app.data.remote.HttpRoutes.GET_HOME_MENUS
import id.teman.app.data.remote.HttpRoutes.GET_PROFILE
import id.teman.app.data.remote.HttpRoutes.LOGIN_URL
import id.teman.app.data.remote.HttpRoutes.REGISTER_URL
import id.teman.app.data.remote.HttpRoutes.SEND_OTP_URL
import id.teman.app.data.remote.HttpRoutes.VERIFICATION_OTP_URL
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiServiceInterface {

    /*
    USER
    */

    @POST(LOGIN_URL)
    suspend fun login(@Body request: LoginRequest): LoginDto

    @POST(REGISTER_URL)
    suspend fun register(
        @Body request: RegisterRequest
    ): UserDto

    @POST(SEND_OTP_URL)
    suspend fun sendOtp(): RequestOtpDto

    @POST(VERIFICATION_OTP_URL)
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): UserDto

    @POST("wallet/send_otp_update_pin")
    suspend fun sendOtpReset(): RequestOtpDto

    @POST("wallet/verification_otp_update_pin")
    suspend fun verifyOtpReset(@Body request: VerifyOtpRequest): RequestOtpResetDto

    @GET(GET_PROFILE)
    suspend fun getProfile(): UserDataDto

    @Multipart
    @PATCH("auth/profile")
    @JvmSuppressWildcards
    suspend fun updateProfile(
        @PartMap partMap: Map<String, RequestBody>? = null,
        @Part profileImageFile: MultipartBody.Part? = null,
    ): UserDataDto

    /*
    WALLET
    */
    @GET("wallet")
    suspend fun getWalletBalance(): WalletBalanceDto

    @GET("wallet/transactions")
    suspend fun getHistoryWalletTransaction(): WalletHistoryTransactionDto

    @POST("wallet/topup")
    suspend fun topUpWalletAmount(@Body requestDto: WalletRequestDto): WalletHistoryTransactionDetail

    @GET("wallet/transactions/{id}")
    suspend fun getDetailTransaction(@Path("id") transactionId: String): WalletHistoryTransactionDetail

    @GET("wallet/bank")
    suspend fun getBankInformation(): WalletBankAccountDto

    @GET("wallet/list_bank")
    suspend fun getListBank(): List<ItemBankDto>? = emptyList()

    @Multipart
    @PATCH("wallet/bank")
    @JvmSuppressWildcards
    suspend fun updateWalletBankInformation(@PartMap partMap: Map<String, RequestBody>): WalletBankAccountDto

    @POST("wallet/withdraw")
    suspend fun withdrawMoney(@Body request: WithdrawRequestDto): BaseResponse

    /*
    PIN
    */
    @POST("wallet/update_pin")
    suspend fun setPinWallet(@Body requestDto: PinWalletRequestDto): UpdatePinWalletDto

    @POST("wallet/send_otp_update_pin")
    suspend fun sendOtpWallet(): OtpWalletDto

    @POST("wallet/verification_otp_update_pin")
    suspend fun verifyOtpWallet(@Body request: OtpPinWalletRequestDto): VerifyOtpWalletDto

    /*
    HOME PAGE
    */

    @GET(GET_HOME_MENUS)
    suspend fun getHomeMenus(): List<MenusItem>

    /*
    PROMO
    */

    @GET("customer/banners")
    suspend fun getHomeBanner(): List<HomeBannerItem>

    @GET("customer/food_banners")
    suspend fun getFoodBanner(): List<HomeBannerItem>

    @GET("promotions")
    suspend fun getCouponsPromo(): CouponPromoDto

    /*
    HISTORY
    */
    @GET("customer/requests")
    suspend fun getHistoryOrder(): HistoryOrders


    /*
    BILL
    */
    @GET("customer/bill_menu")
    suspend fun getListCategoryBill(): List<ItemCategoryBillDto>

    @GET("customer/voucher_game_menu")
    suspend fun getListCategoryGame(): List<ItemCategoryBillDto>

    @POST("bill/pulsa/price_list")
    suspend fun getPriceListPulsa(@Body request: ListPulsaRequestDto): List<ItemBillResponseDto>

    @POST("bill/pulsa/buy")
    suspend fun buyPulsa(@Body request: BuyPulsaRequestDto): SuccessBuyBill

    @POST("bill/price_list")
    suspend fun getPriceListBill(@Body request: ListBillRequestDto): List<ItemBillResponseDto>

    @POST("bill/transaction")
    suspend fun buyBillTransaction(@Body request: BuyBillRequestDto): SuccessBuyBill

    /*
    FOOD
    */

    @GET("customer/food_menu")
    suspend fun getFoodMenus(): List<MenusItem>?

    @GET("restaurant_categories")
    suspend fun getMenuCategories(): CategoryResponseDto

    @GET("customer/restaurants?")
    suspend fun getListRestaurant(
        @Query("lat") lat: Double,
        @Query("lng") lang: Double,
        @Query("search") search: String? = "",
        @Query("categories") categories: String? = ""
    ): RestaurantResponseDto

    @GET("customer/restaurants/{restaurantId}?")
    suspend fun getDetailRestaurant(
        @Path("restaurantId") id: String,
        @Query("lat") lat: Double,
        @Query("lng") lang: Double,
    ): DetailRestaurantResponseDto

    /*
    MAPS
    */

    @GET("https://maps.googleapis.com/maps/api/place/autocomplete/json")
    suspend fun getPredictions(
        @Query("key") key: String,
        @Query("input") input: String,
        @Query("location") location: String? = "",
        @Query("radius") radius: String? = "",
    ): GooglePredictionsDto

    @GET("https://maps.googleapis.com/maps/api/place/details/json")
    suspend fun getDetailLocation(
        @Query("key") key: String,
        @Query("place_id") placeId: String,
    ): PlaceResponseDto

    // TODO next solution *PAID*
    @GET("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
    suspend fun getNearbyLocations(
        @Query("key") key: String,
        @Query("keyword") input: String,
        @Query("location") location: String,
        @Query("radius") radius: String,
    )

    @GET("https://maps.googleapis.com/maps/api/directions/json")
    suspend fun getMapDirection(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") apiKey: String,
        @Query("vehicle") vehicleType: String,
        @Query("mode") mode: String,
        @Query("avoid") avoid: String? = null
    ): DirectionResponseDto

    @GET("customer/requests/status/active")
    suspend fun getActiveOrder(): OrderDetailResponseDto

    @POST("customer/requests/estimate")
    suspend fun getOrderDetailEstimation(
        @Body request: OrderRequestDto
    ): OrderResponseDto

    @POST("customer/requests")
    suspend fun searchDriver(@Body request: OrderRequestDto): OrderDetailResponseDto

    @GET("customer/requests/{requestId}/messages")
    suspend fun getChatMessages(
        @Path("requestId") requestId: String
    ): ChatResponseDto

    @POST("customer/requests/{requestId}/messages")
    suspend fun sendChatMessage(
        @Body request: ChatRequestDto,
        @Path("requestId") requestId: String
    ): SendMessageResponseDto

    @POST("customer/requests/{requestId}/rating")
    suspend fun sendRating(
        @Body request: RatingRequestDto,
        @Path("requestId") requestId: String
    ): RatingResponseDto

    @POST("customer/requests/{requestId}/rating_restaurant")
    suspend fun sendRatingResto(
        @Body request: RatingRequestDto,
        @Path("requestId") requestId: String
    ): RatingResponseDto

    @PATCH("customer/requests/{id}/status")
    suspend fun updateOrderStatus(
        @Path("id") id: String,
        @Body status: OrderRequestStatusDto
    ): OrderDetailResponseDto

    @GET("https://roads.googleapis.com/v1/snapToRoads")
    suspend fun getSnappedRoad(
        @Query("path") origin: String,
        @Query("interpolate") interpolate: Boolean = true,
        @Query("key") apiKey: String
    ): SnappedPointsDto

    @GET("notifications")
    suspend fun getNotifications(): NotificationDto

    @POST("notifications/read")
    suspend fun readNotification(@Body request: NotificationReadRequestDto): NotificationReadResponseDto

    @POST("notifications/read_all")
    suspend fun readAllNotification(): NotificationReadResponseDto

    @GET("customer/nearest_drivers")
    suspend fun getNearestDrivers(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
        @Query("type") type: String
    ): List<DriverBasicInfoDto>

    @GET("customer/rewards")
    suspend fun getListRewards(): RewardResponseDto

    @GET("user/point_histories")
    suspend fun getListRewardTransaction(): RewardTransactionResponseDto

    @GET("user/redeems")
    suspend fun getListRewardRedeemed(): RewardRedeemedResponse

    @POST("customer/redeem")
    suspend fun redeemReward(@Body request: RewardRedeemRequestDto): BaseResponse

    @GET("user/referral_code_useds")
    suspend fun getHistoryReferral(): ReferralResponseDto
}