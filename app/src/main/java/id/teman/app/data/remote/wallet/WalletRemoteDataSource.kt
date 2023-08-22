package id.teman.app.data.remote.wallet

import id.teman.app.data.dto.BaseResponse
import id.teman.app.data.dto.reward.RewardRedeemRequestDto
import id.teman.app.data.dto.reward.RewardRedeemedResponse
import id.teman.app.data.dto.reward.RewardResponseDto
import id.teman.app.data.dto.reward.RewardTransactionResponseDto
import id.teman.app.data.dto.wallet.WalletBalanceDto
import id.teman.app.data.dto.wallet.WalletHistoryTransactionDetail
import id.teman.app.data.dto.wallet.WalletHistoryTransactionDto
import id.teman.app.data.dto.wallet.WalletRequestDto
import id.teman.app.data.dto.wallet.withdrawal.ItemBankDto
import id.teman.app.data.dto.wallet.withdrawal.WalletBankAccountDto
import id.teman.app.data.dto.wallet.withdrawal.WithdrawRequestDto
import id.teman.app.data.remote.ApiServiceInterface
import id.teman.app.data.remote.handleRequestOnFlow
import kotlinx.coroutines.flow.Flow
import okhttp3.RequestBody

interface WalletRemoteDataSource {
    suspend fun getWalletBalance(): Flow<WalletBalanceDto>
    suspend fun getHistoryWalletTransaction(): Flow<WalletHistoryTransactionDto>
    suspend fun topUpWallet(request: WalletRequestDto): Flow<WalletHistoryTransactionDetail>
    suspend fun getDetailTransaction(transactionId: String): Flow<WalletHistoryTransactionDetail>
    suspend fun getListReward(): Flow<RewardResponseDto>
    suspend fun getListRewardTransaction(): Flow<RewardTransactionResponseDto>
    suspend fun getListRewardRedeemed(): Flow<RewardRedeemedResponse>
    suspend fun redeemReward(requestDto: RewardRedeemRequestDto): Flow<BaseResponse>
    suspend fun getWalletBankInformation(): Flow<WalletBankAccountDto>
    suspend fun getListBank(): Flow<List<ItemBankDto>>
    suspend fun updateWalletBankInformation(partMap: Map<String, RequestBody>): Flow<WalletBankAccountDto>
    suspend fun withdrawMoney(request: WithdrawRequestDto): Flow<BaseResponse>
}

class DefaultWalletDataSource(private val httpClient: ApiServiceInterface): WalletRemoteDataSource {

    override suspend fun getWalletBalance(): Flow<WalletBalanceDto> =
        handleRequestOnFlow {
            httpClient.getWalletBalance()
        }

    override suspend fun getHistoryWalletTransaction(): Flow<WalletHistoryTransactionDto> =
        handleRequestOnFlow {
            httpClient.getHistoryWalletTransaction()
        }

    override suspend fun topUpWallet(request: WalletRequestDto): Flow<WalletHistoryTransactionDetail> =
        handleRequestOnFlow {
            httpClient.topUpWalletAmount(request)
        }

    override suspend fun getDetailTransaction(transactionId: String): Flow<WalletHistoryTransactionDetail> =
        handleRequestOnFlow {
            httpClient.getDetailTransaction(transactionId)
        }

    override suspend fun getListReward(): Flow<RewardResponseDto> =
        handleRequestOnFlow {
            httpClient.getListRewards()
        }

    override suspend fun getListRewardTransaction(): Flow<RewardTransactionResponseDto> =
        handleRequestOnFlow {
            httpClient.getListRewardTransaction()
        }

    override suspend fun getListRewardRedeemed(): Flow<RewardRedeemedResponse> =
        handleRequestOnFlow {
            httpClient.getListRewardRedeemed()
        }

    override suspend fun redeemReward(requestDto: RewardRedeemRequestDto): Flow<BaseResponse> =
        handleRequestOnFlow {
            httpClient.redeemReward(requestDto)
        }

    override suspend fun getListBank(): Flow<List<ItemBankDto>> =
        handleRequestOnFlow {
            httpClient.getListBank().orEmpty()
        }

    override suspend fun getWalletBankInformation(): Flow<WalletBankAccountDto> =
        handleRequestOnFlow { httpClient.getBankInformation() }

    override suspend fun updateWalletBankInformation(partMap: Map<String, RequestBody>): Flow<WalletBankAccountDto> =
        handleRequestOnFlow { httpClient.updateWalletBankInformation(partMap) }

    override suspend fun withdrawMoney(request: WithdrawRequestDto): Flow<BaseResponse> =
        handleRequestOnFlow { httpClient.withdrawMoney(request) }
}