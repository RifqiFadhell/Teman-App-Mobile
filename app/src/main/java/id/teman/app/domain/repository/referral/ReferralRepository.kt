package id.teman.app.domain.repository.referral

import id.teman.app.data.remote.referral.ReferralRemoteDataSource
import id.teman.app.domain.model.referral.ItemReferral
import id.teman.app.domain.model.referral.toListReferral
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ReferralRepository @Inject constructor(
    private val referralRemoteDataSource: ReferralRemoteDataSource
) {
    suspend fun getHistoryReferral(): Flow<List<ItemReferral>> =
        referralRemoteDataSource.getHistoryReferral().map { it.data.toListReferral() }
}