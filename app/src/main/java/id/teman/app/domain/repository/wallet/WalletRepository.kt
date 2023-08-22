package id.teman.app.domain.repository.wallet

import id.teman.app.common.orZero
import id.teman.app.data.dto.reward.RewardRedeemRequestDto
import id.teman.app.data.dto.wallet.WalletRequestDto
import id.teman.app.data.dto.wallet.withdrawal.WithdrawRequestDto
import id.teman.app.data.remote.wallet.WalletRemoteDataSource
import id.teman.app.domain.model.reward.ItemReward
import id.teman.app.domain.model.reward.ItemRewardRedeemed
import id.teman.app.domain.model.reward.ItemRewardTransaction
import id.teman.app.domain.model.reward.toHistoryPoint
import id.teman.app.domain.model.reward.toListRewardRedeemed
import id.teman.app.domain.model.reward.toListRewards
import id.teman.app.domain.model.wallet.WalletItemDetailSpec
import id.teman.app.domain.model.wallet.toWalletDetailItem
import id.teman.app.domain.model.wallet.toWalletListDetailSpec
import id.teman.app.domain.model.wallet.withdrawal.ItemBankSpec
import id.teman.app.domain.model.wallet.withdrawal.WalletBankInformationSpec
import id.teman.app.domain.model.wallet.withdrawal.WalletDataTransferSpec
import id.teman.app.domain.model.wallet.withdrawal.convertToListBank
import id.teman.app.domain.model.wallet.withdrawal.toWalletBankInformationSpec
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import okhttp3.RequestBody

class WalletRepository @Inject constructor(
    private val remoteWalletRemoteDataSource: WalletRemoteDataSource
) {
    suspend fun getWalletBalance(): Flow<Double> =
        remoteWalletRemoteDataSource.getWalletBalance().map { it.balance.orZero() }

    suspend fun getWalletHistory(): Flow<List<WalletItemDetailSpec>> =
        remoteWalletRemoteDataSource.getHistoryWalletTransaction().map { it.data.toWalletListDetailSpec() }

    suspend fun topUpBalanceWallet(amount: Int): Flow<WalletItemDetailSpec> =
        remoteWalletRemoteDataSource.topUpWallet(WalletRequestDto(amount)).map { it.toWalletDetailItem() }

    suspend fun getDetailTransaction(transactionId: String): Flow<WalletItemDetailSpec> =
        remoteWalletRemoteDataSource.getDetailTransaction(transactionId).map { it.toWalletDetailItem() }

    suspend fun getRewardRedeemed(): Flow<List<ItemRewardRedeemed>> =
        remoteWalletRemoteDataSource.getListRewardRedeemed().map { it.data.toListRewardRedeemed() }

    suspend fun getHistoryPoint(): Flow<List<ItemRewardTransaction>> =
        remoteWalletRemoteDataSource.getListRewardTransaction().map { it.data.toHistoryPoint() }

    suspend fun getRewards(): Flow<List<ItemReward>> =
        remoteWalletRemoteDataSource.getListReward().map { it.data.toListRewards() }

    suspend fun redeemReward(id: String): Flow<String> =
        remoteWalletRemoteDataSource.redeemReward(requestDto = RewardRedeemRequestDto(id)).map { it.message.orEmpty() }

    suspend fun getWalletBankInformation(): Flow<WalletBankInformationSpec> =
        remoteWalletRemoteDataSource.getWalletBankInformation().map { it.toWalletBankInformationSpec() }

    suspend fun updateWalletBankInformation(partMap: Map<String, RequestBody>): Flow<WalletBankInformationSpec> = remoteWalletRemoteDataSource
        .updateWalletBankInformation(partMap).map { it.toWalletBankInformationSpec() }

    suspend fun withdrawMoney(pin: String, spec: WalletDataTransferSpec): Flow<String> = remoteWalletRemoteDataSource
        .withdrawMoney(
            WithdrawRequestDto(
            pin = pin,
            bankName = spec.bankName,
            accountNumber = spec.accountNumber,
            accountName = spec.accountName,
            amount = spec.withdrawalAmount.toString()
        )
        ).map { it.message.orEmpty() }

    suspend fun getListBank(): Flow<List<ItemBankSpec>> =
        remoteWalletRemoteDataSource.getListBank().map { it.convertToListBank() }
}