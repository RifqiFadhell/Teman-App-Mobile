package id.teman.app.domain.repository.promo

import id.teman.app.data.remote.promo.PromoRemoteDataSource
import id.teman.app.domain.model.promo.PromoUiModel
import id.teman.app.domain.model.promo.toPromoModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PromoRepository @Inject constructor(
    private val promoRemoteDataSource: PromoRemoteDataSource
) {
    suspend fun getAllPromo(): Flow<List<PromoUiModel>> =
        promoRemoteDataSource.getCoupons().map { it.data?.toPromoModel().orEmpty() }
}