package com.example.mbrecka.topviewrefactor

import android.app.Activity
import android.app.Application
import com.example.mbrecka.topviewrefactor.di.AndroidModule
import com.example.mbrecka.topviewrefactor.di.AppComponent
import com.example.mbrecka.topviewrefactor.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

/**
 * Created by Matej on 29. 7. 2016.
 */
class MyApp : Application(), HasActivityInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    lateinit var component: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()

        component = DaggerAppComponent.builder()
                .androidModule(AndroidModule(this))
                .build()
                .also {
                    it.inject(this)
                }
    }

    override fun activityInjector(): AndroidInjector<Activity>? {
        return activityInjector
    }
}