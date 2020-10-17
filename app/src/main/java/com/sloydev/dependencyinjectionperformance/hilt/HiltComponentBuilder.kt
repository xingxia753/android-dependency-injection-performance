package com.sloydev.dependencyinjectionperformance.hilt

import dagger.hilt.DefineComponent
import javax.inject.Singleton

@DefineComponent.Builder
@Singleton
interface KotlinHiltComponentBuilder {
    fun build(): KotlinHiltComponent
}


@DefineComponent.Builder
interface JavaHiltComponentBuilder {
    fun build(): JavaHiltComponent
}