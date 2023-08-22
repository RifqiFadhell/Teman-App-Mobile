package id.teman.app.data.remote.referral

import id.teman.app.data.dto.referral.ReferralResponseDto
import id.teman.app.data.remote.ApiServiceInterface
import id.teman.app.data.remote.handleRequestOnFlow
import kotlinx.coroutines.flow.Flow

interface ReferralRemoteDataSource {
    suspend fun getHistoryReferral(): Flow<ReferralResponseDto>
}

class DefaultReferralDataSource(private val httpClient: ApiServiceInterface): ReferralRemoteDataSource {
    override suspend fun getHistoryReferral(): Flow<ReferralResponseDto> =
        handleRequestOnFlow {
            httpClient.getHistoryReferral()
        }
}