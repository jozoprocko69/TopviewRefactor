package com.example.mbrecka.topviewrefactor.di

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mbrecka.topviewrefactor.*
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Singleton
@Component(
        modules = [
            AndroidModule::class,
            ActivityBuilderModule::class,
            ViewModelModule::class
        ]
)
interface AppComponent {
    fun inject(application: MyApp)
}

@Module
abstract class ActivityBuilderModule {

    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity

}

@Module
class AndroidModule(val application: Application) {

    @Provides
    fun provideApplicationContext(): Context {
        return application
    }
}

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(SignpostViewModel::class)
    internal abstract fun bindSignpostViewModel(viewModel: SignpostViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(IncidentViewModel::class)
    internal abstract fun bindIncidentViewModel(viewModel: IncidentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EndOfRouteViewModel::class)
    internal abstract fun bindEndOfRouteViewModel(viewModel: EndOfRouteViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OnlyVisibleViewModel::class)
    internal abstract fun bindOnlyVisibleViewModel(viewModel: OnlyVisibleViewModel): ViewModel

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

}