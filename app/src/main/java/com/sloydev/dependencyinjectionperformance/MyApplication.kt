package com.sloydev.dependencyinjectionperformance

import android.app.Application
import com.sloydev.dependencyinjectionperformance.hilt.KotlinHiltComponentBuilder
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication: Application() {
    @Inject
    lateinit var kotlinHiltComponentBuilder: KotlinHiltComponentBuilder
}