package com.sloydev.dependencyinjectionperformance

import android.app.Application
import android.os.Build
import android.util.Log
import com.sloydev.dependencyinjectionperformance.custom.DIContainer
import com.sloydev.dependencyinjectionperformance.custom.customJavaModule
import com.sloydev.dependencyinjectionperformance.custom.customKotlinModule
import com.sloydev.dependencyinjectionperformance.hilt.*
import com.sloydev.dependencyinjectionperformance.katana.katanaJavaModule
import com.sloydev.dependencyinjectionperformance.katana.katanaKotlinModule
import com.sloydev.dependencyinjectionperformance.koin.koinJavaModule
import com.sloydev.dependencyinjectionperformance.koin.koinKotlinModule
import dagger.BindsInstance
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import org.kodein.di.Kodein
import org.kodein.di.direct
import org.kodein.di.erased.instance
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.get
import org.rewedigital.katana.Component
import org.rewedigital.katana.Katana
import org.rewedigital.katana.android.environment.AndroidEnvironmentContext
import org.rewedigital.katana.android.environment.AndroidEnvironmentContext.Profile.SPEED
import org.rewedigital.katana.createComponent
import javax.inject.Inject

class InjectionTest(
    private val mApplication: Application
) : KoinComponent {

    @Inject
    lateinit var kotlinHiltComponentBuilder: KotlinHiltComponentBuilder

    @Inject
    lateinit var javaHiltComponentBuilder: JavaHiltComponentBuilder

    private val rounds = 100

    fun runTests(): List<LibraryResult> {
        val results = listOf(
            koinTest(),
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

    private fun koinTest(): LibraryResult {
        log("Running Koin...")
        return LibraryResult("Koin", mapOf(
            Variant.KOTLIN to runTest(
                setup = {
                    startKoin {
                        modules(koinKotlinModule)
                    }
                },
                test = { get<Fib8>() },
                teardown = { stopKoin() }
            ),
            Variant.JAVA to runTest(
                setup = {
                    startKoin {
                        modules(koinJavaModule)
                    }
                },
                test = { get<FibonacciJava.Fib8>() },
                teardown = { stopKoin() }
            )
        ))
    }

    private fun kodeinTest(): LibraryResult {
        log("Running Kodein...")
        lateinit var kodein: Kodein
        return LibraryResult("Kodein", mapOf(
            Variant.KOTLIN to runTest(
                setup = { kodein = Kodein { import(kodeinKotlinModule) } },
                test = { kodein.direct.instance<Fib8>() }
            ),
            Variant.JAVA to runTest(
                setup = { kodein = Kodein { import(kodeinKotlinModule) } },
                test = { kodein.direct.instance<Fib8>() }
            )
        ))
    }

    private fun katanaTest(): LibraryResult {
        log("Running Katana...")
        Katana.environmentContext = AndroidEnvironmentContext(profile = SPEED)
        lateinit var component: Component
        return LibraryResult("Katana", mapOf(
            Variant.KOTLIN to runTest(
                setup = { component = createComponent(modules = listOf(katanaKotlinModule)) },
                test = { component.injectNow<Fib8>() }
            ),
            Variant.JAVA to runTest(
                setup = { component = createComponent(modules = listOf(katanaJavaModule)) },
                test = { component.injectNow<FibonacciJava.Fib8>() }
            )
        ))
    }

    private fun customTest(): LibraryResult {
        log("Running Custom...")
        return LibraryResult("Custom", mapOf(
            Variant.KOTLIN to runTest(
                setup = { DIContainer.loadModule(customKotlinModule) },
                test = { DIContainer.get<Fib8>() },
                teardown = { DIContainer.unloadModules() }
            ),
            Variant.JAVA to runTest(
                setup = { DIContainer.loadModule(customJavaModule) },
                test = { DIContainer.get<FibonacciJava.Fib8>() },
                teardown = { DIContainer.unloadModules() }
            )
        ))
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
        val hiltFib8: Fib8
    ) {
    }

    class JavaHiltTest @Inject constructor(
        val javaFib8: FibonacciJava.Fib8
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
