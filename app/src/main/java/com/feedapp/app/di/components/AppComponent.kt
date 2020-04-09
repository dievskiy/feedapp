package com.feedapp.app.di.components

import android.app.Application
import com.feedapp.app.AppDelegate
import com.feedapp.app.di.modules.ActivitiesBuilderModule
import com.feedapp.app.di.modules.AppModule
import com.feedapp.app.di.modules.ViewModelFactoryModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton


@Component(
    modules =
    [
        AndroidSupportInjectionModule::class,
        ActivitiesBuilderModule::class,
        AppModule::class,
        ViewModelFactoryModule::class
    ]
)
@Singleton
interface AppComponent : AndroidInjector<AppDelegate> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }


}
