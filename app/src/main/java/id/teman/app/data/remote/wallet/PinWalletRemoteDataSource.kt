package id.teman.app.data.remote.wallet

import id.teman.app.data.dto.wallet.pin.OtpPinWalletRequestDto
import id.teman.app.data.dto.wallet.pin.OtpWalletDto
import id.teman.app.data.dto.wallet.pin.PinWalletRequestDto
import id.teman.app.data.dto.wallet.pin.UpdatePinWalletDto
import id.teman.app.data.dto.wallet.pin.VerifyOtpWalletDto
import id.teman.app.data.remote.ApiServiceInterface
import id.teman.app.data.remote.handleRequestOnFlow
import kotlinx.coroutines.flow.Flow

interface PinWalletRemoteDataSource {
    suspend fun updatePinWallet(request: PinWalletRequestDto): Flow<UpdatePinWalletDto>
    suspend fun sendOtpWallet(): Flow<OtpWalletDto>
    suspend fun verifyOtpWallet(request: OtpPinWalletRequestDto): Flow<VerifyOtpWalletDto>
}

class DefaultPinWalletDataSource(private val httpClient: ApiServiceInterface): PinWalletRemoteDataSource {

    override suspend fun updatePinWallet(request: PinWalletRequestDto): Flow<UpdatePinWalletDto> =
        handleRequestOnFlow {
            httpClient.setPinWallet(request)
        }

    override suspend fun sendOtpWallet(): Flow<OtpWalletDto> =
        handleRequestOnFlow {
            httpClient.sendOtpWallet()
        }

    override suspend fun verifyOtpWallet(request: OtpPinWalletRequestDto): Flow<VerifyOtpWalletDto> =
        handleRequestOnFlow {
            httpClient.verifyOtpWallet(request)
        }
}