# Android Injection Performance

This project aims to measure the performance of several Dependency Injection frameworks (or Service Locators) in different devices.

## Libraries tested
- [Koin](https://insert-koin.io/) - 2.0.0-alpha-3
- [Kodein](http://kodein.org/Kodein-DI/) - 5.3.0
- [Hilt](https://dagger.dev/hilt) - 2.29.1-alpha
- [Katana](https://github.com/rewe-digital-incubator/katana/) - 1.2.2

## The test
The test data are classes with dependencies in a structure similar to Fibonacci sequence, to simulate multiple levels of transitive dependencies.
For each library there is a test with Kotlin classes and one with Java classes, because some libraries seem to be affected by this difference.

Each test injects one of this dependencies 100 times and prints the maximum time, the minimum and the average.

The project contains an Android application that run the tests on its onCreate and prints the result to the Logcat.

The actual test is implemented in the class [InjectionTest.kt](https://github.com/Sloy/android-dependency-injection-performance/blob/master/app/src/main/java/com/sloydev/dependencyinjectionperformance/InjectionTest.kt)

## Results
Results can be quite different between different devices, so here are some results in different devices with different Android versions. Each table contains the **median** time of each library's setup and injection. More results are welcomed.

- [OPPO Reno Ace](#oppo-reno-ace)

### OPPO Reno Ace
OPPO OP4A89 with Android 10.0
 
Library | Setup Kotlin | Setup Java | Inject Kotlin | Inject Java
--- | ---:| ---:| ---:| ---:
**Koin** | 1.50 ms | 1.37 ms  | 0.07 ms | 0.14 ms
**Kodein** | 1.98 ms | 2.12 ms  | 0.04 ms | 0.15 ms
**Katana** | 0.15 ms | 0.16 ms  | 0.05 ms | 0.04 ms
**Custom** | 0.03 ms | 0.03 ms  | 0.03 ms | 0.03 ms
**Hilt** | 0.00 ms | 0.00 ms  | 0.02 ms | 0.02 ms
