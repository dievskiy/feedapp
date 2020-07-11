/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.di.modules

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.feedapp.app.R
import com.feedapp.app.data.api.interfaces.RecipeApiSearch
import com.feedapp.app.data.databases.daos.DayDao
import com.feedapp.app.data.databases.daos.FoodProductDao
import com.feedapp.app.data.databases.daos.RecentDao
import com.feedapp.app.data.databases.daos.UserDao
import com.feedapp.app.data.databases.dbclasses.FoodDatabase
import com.feedapp.app.data.databases.dbclasses.UserDatabase
import com.feedapp.app.data.models.calculations.LeftNutrientCalculator
import com.feedapp.app.data.models.calculations.RecipesDetailsCalculator
import com.feedapp.app.data.models.prefs.SharedPrefsHelper
import com.feedapp.app.data.repositories.FoodRepository
import com.feedapp.app.data.repositories.RecentDelegate
import com.feedapp.app.data.repositories.RecipeSearchRepository
import com.feedapp.app.data.repositories.UserRepository
import com.feedapp.app.ui.adapters.DayRecyclerAdapter
import com.feedapp.app.ui.adapters.RecipeIngredientAdapter
import com.feedapp.app.ui.adapters.RecipeStepAdapter
import com.feedapp.app.util.SP_NAME_TAG
import com.feedapp.app.util.TAG
import com.feedapp.app.util.isConnected
import dagger.Module
import dagger.Provides
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton


@Module
class AppModule {

    @Singleton
    @Provides
    fun provideRequestOptions(): RequestOptions {
        return RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .centerCrop()
            .placeholder(R.drawable.white_background)
            .error(R.drawable.white_background)
            .signature(ObjectKey(System.currentTimeMillis().toShort()))
    }

    @Singleton
    @Provides
    fun provideSP(application: Application): SharedPreferences {
        return application.getSharedPreferences(SP_NAME_TAG, Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideSPEditor(sharedPreferences: SharedPreferences): SharedPreferences.Editor {
        return sharedPreferences.edit()
    }


    @Singleton
    @Provides
    fun provideCalendar(): Calendar {
        return Calendar.getInstance()
    }


    @Singleton
    @Provides
    fun provideSPHelper(sp: SharedPreferences): SharedPrefsHelper {
        return SharedPrefsHelper(sp)
    }

    @Singleton
    @Provides
    fun provideGlide(
        application: Application,
        requestOptions: RequestOptions
    ): RequestManager {
        return GlideApp.with(application).applyDefaultRequestOptions(requestOptions)
    }

    @Singleton
    @Provides
    fun provideRetrofitInstance(@Named("OkHttpClientUSDA") okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .baseUrl("https://api.nal.usda.gov/")
            .build()
    }

    @Singleton
    @Provides
    @Named("OkHttpClientUSDA")
    fun provideOkHttpClientUSDA(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(3, TimeUnit.MINUTES) // connect timeout
            .writeTimeout(3, TimeUnit.MINUTES)
            .readTimeout(3, TimeUnit.MINUTES)
            .build()
    }

    @Singleton
    @Provides
    fun provideFoodDatabase(application: Application): FoodDatabase {
        return FoodDatabase.invoke(application)
    }

    @Singleton
    @Provides
    fun provideUserDatabase(application: Application): UserDatabase {
        return UserDatabase.invoke(application)
    }

    @Singleton
    @Provides
    fun provideFoodProductDao(database: FoodDatabase): FoodProductDao {
        return database.getProductDao()
    }


    @Singleton
    @Provides
    fun provideDayDao(database: UserDatabase): DayDao {
        return database.getDayDao()
    }

    @Singleton
    @Provides
    fun provideUserDao(database: UserDatabase): UserDao {
        return database.getUserDao()
    }


    @Singleton
    @Provides
    fun provideFoodProductRepository(
        application: Application,
        foodProductDao: FoodProductDao
    ): FoodRepository {
        return FoodRepository(application, foodProductDao)
    }

    @Singleton
    @Provides
    fun provideUserRepository(
        userDao: UserDao,
        leftCalculator: LeftNutrientCalculator
    ): UserRepository {
        return UserRepository(userDao, leftCalculator)
    }

    @Provides
    fun provideRecentDelegate(
        recentDao: RecentDao
    ): RecentDelegate {
        return RecentDelegate(recentDao)
    }

    @Provides
    fun provideRecentDao(
        userDatabase: UserDatabase
    ): RecentDao {
        return userDatabase.getRecentDao()
    }


    @Provides
    fun provideDetailedRecipeRepo() =
        RecipesDetailsCalculator()

    @Singleton
    @Provides
    fun provideDayRecyclerAdapter(): DayRecyclerAdapter {
        return DayRecyclerAdapter(arrayListOf())
    }

    @Singleton
    @Provides
    fun provideRecipeIngredientAdapter(application: Application): RecipeIngredientAdapter {
        return RecipeIngredientAdapter(application, arrayListOf(), 1)
    }

    @Singleton
    @Provides
    fun provideStepRecyclerAdapter(): RecipeStepAdapter {
        return RecipeStepAdapter(arrayListOf())
    }

    @Singleton
    @Provides
    fun provideRecipeSearchRepository(
        application: Application,
        recipeApiSearch: RecipeApiSearch
    )
            : RecipeSearchRepository {
        return RecipeSearchRepository(application, recipeApiSearch)
    }

    @Singleton
    @Provides
    @Named("retrofitSpoonacular")
    fun provideRetrofitInstanceSpoonacular(@Named("OkHttpClientSpoonacular") okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .baseUrl("https://api.spoonacular.com/")
            .build()
    }

    @Singleton
    @Provides
    fun provideRecipeApiSearchResult(@Named("retrofitSpoonacular") retrofit: Retrofit): RecipeApiSearch {
        return retrofit.create(RecipeApiSearch::class.java)
    }


    @Singleton
    @Provides
    @Named("OkHttpClientSpoonacular")
    fun provideOkHttpClient(
        application: Application,
        httpLoggingInterceptor: HttpLoggingInterceptor,
        @Named("networkInterceptor") networkInterceptor: Interceptor,
        @Named("offlineInterceptor") offlineInterceptor: Interceptor
    ): OkHttpClient {

        val cacheSize = (5 * 1024 * 1024).toLong()
        val cache = Cache(application.cacheDir, cacheSize)
        val client = OkHttpClient.Builder()
            .cache(cache)
            .connectTimeout(3, TimeUnit.MINUTES) // connect timeout
            .writeTimeout(3, TimeUnit.MINUTES)
            .readTimeout(3, TimeUnit.MINUTES)
            .addInterceptor(httpLoggingInterceptor) // used if network off OR on
            .addInterceptor(offlineInterceptor) // only used when network is on
            .addInterceptor(networkInterceptor) // only used when network is on

        return client.build()
    }


    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor {}
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY;
        return httpLoggingInterceptor
    }

    @Singleton
    @Provides
    @Named("networkInterceptor")
            /**
             * This interceptor will be called ONLY if the network is available
             * @return
             */
    fun networkInterceptor(): Interceptor {
        return Interceptor { chain ->
            val response: Response = chain.proceed(chain.request())
            when (response.code()) {
                402 -> {
                    Log.d(
                        TAG, "Unable to load data (402) response.isSuccessful = " +
                                "${response.isSuccessful}"
                    )
                }
            }
            val cacheControl = CacheControl.Builder()
                .maxAge(5, TimeUnit.SECONDS)
                .build()
            response.newBuilder()
                .removeHeader(HEADER_PRAGMA)
                .removeHeader(HEADER_CACHE_CONTROL)
                .header(HEADER_CACHE_CONTROL, cacheControl.toString())
                .build()
        }
    }


    @Singleton
    @Provides
    @Named("offlineInterceptor")
    fun offlineInterceptor(application: Application): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()
            // prevent caching when network is on. For that we use the "networkInterceptor"
            if (!application.isConnected()) {
                val cacheControl = CacheControl.Builder()
                    .maxStale(7, TimeUnit.DAYS)
                    .build()
                request = request.newBuilder()
                    .removeHeader(HEADER_PRAGMA)
                    .removeHeader(HEADER_CACHE_CONTROL)
                    .cacheControl(cacheControl)
                    .build()
            }
            chain.proceed(request)
        }
    }

    @Singleton
    @Provides
    fun provideLeftNutrientCalculator(): LeftNutrientCalculator {
        return LeftNutrientCalculator()
    }

    private val HEADER_CACHE_CONTROL = "Cache-Control"
    private val HEADER_PRAGMA = "Pragma"

}