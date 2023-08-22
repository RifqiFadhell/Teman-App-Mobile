package id.teman.app.domain.repository.bill

import id.teman.app.data.dto.bill.BuyBillRequestDto
import id.teman.app.data.dto.bill.BuyPulsaRequestDto
import id.teman.app.data.dto.bill.ListBillRequestDto
import id.teman.app.data.dto.bill.ListPulsaRequestDto
import id.teman.app.data.dto.bill.SuccessBuyBill
import id.teman.app.data.remote.bill.BillRemoteDataSource
import id.teman.app.domain.model.bill.BillSpec
import id.teman.app.domain.model.bill.CategoryBillSpec
import id.teman.app.domain.model.bill.SuccessBillSpec
import id.teman.app.domain.model.bill.toListBillSpec
import id.teman.app.domain.model.bill.toListCategorySpec
import id.teman.app.domain.model.bill.toSuccessBillSpec
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BillRepository @Inject constructor(
    private val remoteDataSource: BillRemoteDataSource
) {
    suspend fun getListPricePulsa(requestDto: ListPulsaRequestDto): Flow<List<BillSpec>> =
        remoteDataSource.getListPricePulsa(requestDto).map { it.toListBillSpec() }

    suspend fun getListBillPrice(requestDto: ListBillRequestDto): Flow<List<BillSpec>> =
        remoteDataSource.getListPriceBill(requestDto).map { it.toListBillSpec() }

    suspend fun getListCategoryBill(): Flow<List<CategoryBillSpec>> =
        remoteDataSource.getListCategoryBill().map { it.toListCategorySpec() }

    suspend fun getListCategoryGame(): Flow<List<CategoryBillSpec>> =
        remoteDataSource.getListCategoryGame().map { it.toListCategorySpec() }

    suspend fun buyPulsa(requestDto: BuyPulsaRequestDto): Flow<SuccessBillSpec> =
        remoteDataSource.buyPulsa(requestDto).map { it.toSuccessBillSpec() }

    suspend fun buyBill(requestDto: BuyBillRequestDto): Flow<SuccessBillSpec> =
        remoteDataSource.buyBill(requestDto).map { it.toSuccessBillSpec() }
}