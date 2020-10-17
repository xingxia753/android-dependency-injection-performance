package com.sloydev.dependencyinjectionperformance.hilt

import dagger.hilt.DefineComponent
import dagger.hilt.components.SingletonComponent

@DefineComponent(parent = SingletonComponent::class)
interface KotlinHiltComponent {
}

@DefineComponent(parent = SingletonComponent::class)
interface JavaHiltComponent {

}