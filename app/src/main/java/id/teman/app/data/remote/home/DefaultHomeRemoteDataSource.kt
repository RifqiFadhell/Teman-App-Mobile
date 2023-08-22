package id.teman.app.data.remote.home

import id.teman.app.data.dto.HomeBannerItem
import id.teman.app.data.dto.MenusItem
import id.teman.app.data.remote.ApiServiceInterface
import id.teman.app.data.remote.handleRequestOnFlow
import kotlinx.coroutines.flow.Flow

interface HomeRemoteDataSource {

    suspend fun getBannerPromo(): Flow<List<HomeBannerItem>>
    suspend fun getBannerFood(): Flow<List<HomeBannerItem>>
    suspend fun getHomeMenus(): Flow<List<MenusItem>>
}

class DefaultHomeRemoteDataSource(private val httpClient: ApiServiceInterface) :
    HomeRemoteDataSource {

    override suspend fun getBannerPromo(): Flow<List<HomeBannerItem>> =
        handleRequestOnFlow {
            httpClient.getHomeBanner()
        }

    override suspend fun getBannerFood(): Flow<List<HomeBannerItem>> =
        handleRequestOnFlow {
            httpClient.getFoodBanner()
        }

    override suspend fun getHomeMenus(): Flow<List<MenusItem>> =
        handleRequestOnFlow {
            httpClient.getHomeMenus()
        }
}