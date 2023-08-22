package id.teman.app.domain.repository.home

import id.teman.app.data.remote.home.HomeRemoteDataSource
import id.teman.app.domain.model.home.BannerHomeSpec
import id.teman.app.domain.model.home.QuickMenuModel
import id.teman.app.domain.model.home.quickHomeMenus
import id.teman.app.domain.model.home.toBannerHomeSpec
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HomeRepository @Inject constructor(
    private val homeRemoteDataSource: HomeRemoteDataSource
) {
    suspend fun getHomeMenus(): Flow<List<QuickMenuModel>> =
        homeRemoteDataSource.getHomeMenus().map { it.quickHomeMenus() }

    suspend fun getHomeBanners(): Flow<List<BannerHomeSpec>> =
        homeRemoteDataSource.getBannerPromo().map { it.toBannerHomeSpec() }

    suspend fun getFoodBanners(): Flow<List<BannerHomeSpec>> =
        homeRemoteDataSource.getBannerFood().map { it.toBannerHomeSpec() }
}