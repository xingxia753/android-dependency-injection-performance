package com.sloydev.dependencyinjectionperformance.hilt

import com.sloydev.dependencyinjectionperformance.InjectionTest
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@EntryPoint
@InstallIn(ApplicationComponent::class)
interface HiltApplicationEntryPoint {
    fun inject(injectionTest: InjectionTest)
}