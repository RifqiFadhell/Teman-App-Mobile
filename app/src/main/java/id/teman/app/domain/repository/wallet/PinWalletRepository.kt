package id.teman.app.domain.repository.wallet

import id.teman.app.common.orZero
import id.teman.app.data.dto.wallet.pin.OtpPinWalletRequestDto
import id.teman.app.data.dto.wallet.pin.PinWalletRequestDto
import id.teman.app.data.remote.wallet.PinWalletRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PinWalletRepository @Inject constructor(
    private val remotePinWallet: PinWalletRemoteDataSource
) {
    suspend fun updatePinWallet(requestDto: PinWalletRequestDto): Flow<String> =
        remotePinWallet.updatePinWallet(requestDto).map { it.message.orEmpty() }

    suspend fun sendOtpRequestWallet(): Flow<Int> =
        remotePinWallet.sendOtpWallet().map { it.attemption.orZero() }

    suspend fun verifyOtpWallet(requestDto: OtpPinWalletRequestDto): Flow<String> =
        remotePinWallet.verifyOtpWallet(requestDto).map { it.token.orEmpty() }
}