package com.sloydev.dependencyinjectionperformance

import android.app.Application
import android.os.Build
import android.util.Log
import com.sloydev.dependencyinjectionperformance.hilt.*
import dagger.BindsInstance
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import javax.inject.Inject

class InjectionTest(
    private val mApplication: Application
) {

    @Inject
    lateinit var kotlinHiltComponentBuilder: KotlinHiltComponentBuilder

    @Inject
    lateinit var javaHiltComponentBuilder: JavaHiltComponentBuilder

    private val rounds = 100

    fun runTests(): List<LibraryResult> {
        val results = listOf(
            hiltTest()
        )
        return results
    }

    private fun runTest(
        setup: () -> Unit,
        test: () -> Unit,
        teardown: () -> Unit = {}
    ): TestResult {
        val startup = (1..rounds).map { measureTime { setup() }.also { teardown() } }
        setup()

        val testDurations = (1..rounds).map { measureTime { test() } }
        teardown()
        return TestResult(startup, testDurations)
    }

    private fun hiltTest(): LibraryResult {
        log("Running Hilt")
        lateinit var kotlinComponent: KotlinHiltComponent
        lateinit var javaComponent: JavaHiltComponent

        return LibraryResult("Hilt", mapOf(
            Variant.KOTLIN to runTest(
                setup = {
                    val applicationComponent = (mApplication as Hilt_MyApplication).generatedComponent()
                    EntryPoints.get(applicationComponent, HiltApplicationEntryPoint::class.java).inject(this)
                    kotlinComponent = kotlinHiltComponentBuilder.build()
                },
                test = {
                    EntryPoints.get(kotlinComponent, KotlinHiltTestEntryPoint::class.java).getKotlinHiltTest()
                }
            ),
            Variant.JAVA to runTest(
                setup = {
                    val applicationComponent = (mApplication as Hilt_MyApplication).generatedComponent()
                    EntryPoints.get(applicationComponent, HiltApplicationEntryPoint::class.java).inject(this)
                    javaComponent = javaHiltComponentBuilder.build()
                },
                test = {
                    EntryPoints.get(javaComponent, JavaHiltTestEntryPoint::class.java).getJavaHiltTest()
                }
            )
        ))
    }

    class KotlinHiltTest @Inject constructor(
        val hiltFib20: Fib20
    ) {
    }

    class JavaHiltTest @Inject constructor(
        val javaFib20: FibonacciJava.Fib20
    ) {
    }

    @EntryPoint
    @InstallIn(KotlinHiltComponent::class)
    interface KotlinHiltTestEntryPoint {
        fun getKotlinHiltTest(): KotlinHiltTest
    }

    @EntryPoint
    @InstallIn(JavaHiltComponent::class)
    interface JavaHiltTestEntryPoint {
        fun getJavaHiltTest(): JavaHiltTest
    }
}
