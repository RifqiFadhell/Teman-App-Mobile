package id.teman.app.data.remote.bill

import id.teman.app.data.dto.bill.BuyBillRequestDto
import id.teman.app.data.dto.bill.BuyPulsaRequestDto
import id.teman.app.data.dto.bill.ItemBillResponseDto
import id.teman.app.data.dto.bill.ItemCategoryBillDto
import id.teman.app.data.dto.bill.ListBillRequestDto
import id.teman.app.data.dto.bill.ListPulsaRequestDto
import id.teman.app.data.dto.bill.SuccessBuyBill
import id.teman.app.data.remote.ApiServiceInterface
import id.teman.app.data.remote.handleRequestOnFlow
import kotlinx.coroutines.flow.Flow

interface BillRemoteDataSource {
    suspend fun getListPricePulsa(requestDto: ListPulsaRequestDto): Flow<List<ItemBillResponseDto>?>
    suspend fun getListCategoryBill(): Flow<List<ItemCategoryBillDto>?>
    suspend fun getListPriceBill(requestDto: ListBillRequestDto): Flow<List<ItemBillResponseDto>?>
    suspend fun getListCategoryGame(): Flow<List<ItemCategoryBillDto>?>
    suspend fun buyPulsa(requestDto: BuyPulsaRequestDto): Flow<SuccessBuyBill>
    suspend fun buyBill(requestDto: BuyBillRequestDto): Flow<SuccessBuyBill>
}

class DefaultBillDataSource(private val httpClient: ApiServiceInterface): BillRemoteDataSource {

    override suspend fun getListPricePulsa(requestDto: ListPulsaRequestDto): Flow<List<ItemBillResponseDto>?> =
        handleRequestOnFlow {
            httpClient.getPriceListPulsa(requestDto)
        }

    override suspend fun getListCategoryBill(): Flow<List<ItemCategoryBillDto>?> =
        handleRequestOnFlow {
            httpClient.getListCategoryBill()
        }

    override suspend fun getListPriceBill(requestDto: ListBillRequestDto): Flow<List<ItemBillResponseDto>?> =
        handleRequestOnFlow {
            httpClient.getPriceListBill(requestDto)
        }

    override suspend fun getListCategoryGame(): Flow<List<ItemCategoryBillDto>?> =
        handleRequestOnFlow {
            httpClient.getListCategoryGame()
        }

    override suspend fun buyPulsa(requestDto: BuyPulsaRequestDto): Flow<SuccessBuyBill> =
        handleRequestOnFlow {
            httpClient.buyPulsa(requestDto)
        }

    override suspend fun buyBill(requestDto: BuyBillRequestDto): Flow<SuccessBuyBill> =
        handleRequestOnFlow {
            httpClient.buyBillTransaction(requestDto)
        }
}