package com.feedapp.app.di.modules

import com.feedapp.app.data.api.interfaces.USDAApiServiceFood
import com.feedapp.app.data.databases.daos.FoodProductDao
import com.feedapp.app.data.repositories.SearchFoodRepository
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit


@Module
class ProductSearchModule {

    @Provides
    fun provideUSDAFoodApi(retrofit: Retrofit): USDAApiServiceFood {
        return retrofit.create(USDAApiServiceFood::class.java)
    }

    @Provides
    fun provideSearchFoodRepository(
        foodProductDao: FoodProductDao,
        usdaApiServiceFood: USDAApiServiceFood
    )
            : SearchFoodRepository {
        return SearchFoodRepository(usdaApiServiceFood, foodProductDao)
    }

}
