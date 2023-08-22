package id.teman.app.data.remote.promo

import id.teman.app.data.dto.CouponPromoDto
import id.teman.app.data.remote.ApiServiceInterface
import id.teman.app.data.remote.handleRequestOnFlow
import kotlinx.coroutines.flow.Flow

interface PromoRemoteDataSource {
    suspend fun getCoupons(): Flow<CouponPromoDto>
}

class DefaultPromoRemoteDataSource(private val httpClient: ApiServiceInterface) : PromoRemoteDataSource {
    override suspend fun getCoupons(): Flow<CouponPromoDto> =
        handleRequestOnFlow {
            httpClient.getCouponsPromo()
        }
}