Stocks App Take Home Project

Author: Chris Jones

Implemented as a KMM project, with only Android app implemented.

All domain code (repository, networking, persistence, etc..) in common code.

UI is Android specific, with classic Android Views (no Compose.. *frown*)

Detailed design and development plan found in [/DEVPLAN.md](DEVPLAN.md)

- Domain layer code found in [/shared/src/commonMain](/shared/src/commonMain) and [/shared/src/androidMain](/shared/src/androidMain).
- Presentation layer code found in [/androidApp/src/main](/androidApp/src/main).

Tests were implemented in [/shared/src/androidUnitTest](/shared/src/androidUnitTest) even for common code to quickly utilize JVM, so we can use Mockito.
Instead of this, we could probably modify /shared/src/commonTest to run JVM instead of KNative.

Should open and run from Android Studio.

Stack of components

[(/androidApp)](/androidApp)
- [MainActivity](/androidApp/src/main/java/com/kotlineering/stocksapp/android/MainActivity.kt) ->
- [HomeFragment](/androidApp/src/main/java/com/kotlineering/stocksapp/android/ui/home/HomeFragment.kt) ->
- [HomeViewModel](/androidApp/src/main/java/com/kotlineering/stocksapp/android/ui/home/HomeViewModel.kt) ->

[(/shared)](/shared)
- [StocksService](/shared/src/commonMain/kotlin/com/kotlineering/stocksapp/domain/stocks/StocksService.kt) ->
- [StocksRepository](/shared/src/commonMain/kotlin/com/kotlineering/stocksapp/domain/stocks/repository/StocksRepository.kt) ->
- [StocksApi](/shared/src/commonMain/kotlin/com/kotlineering/stocksapp/domain/stocks/repository/remote/StocksApi.kt)

There is also dev settings (code can be found weaved into the above stack, and is present in the UI when the build is DEBUG)

Libraries used:
- Koin for dependency injection.
- SqlDelight for persistence.
- Ktor-client for remote api.

- Mockito for testing.
- Robolectric fo testing (needed for in-memory SqlDelight DB, might have been able to instantiate a JVM driver instead of Android driver?)

Far more time was spent on this than suggested.. About ten hours total.
- On-boarding SqlDelight took more time than expected.
- The unittests took much longer than expected. (I could not seem to get kotlin-all-open to work... then found mockito extension MockMaker exists..)
- Over-perfecting things, localizing the currency.. refactoring my state objects numerous times, refactoring the interface to ktor-client to make it look nice, etc... (when spec/requirements are specific, this over-perfecting doesn't happen. When it is this open-ended, it is more difficult to keep from doing that.)
- A lot of silly little issues with things that I haven't worked with in a long time, that is generally new project setup related.
- A lot of silly little UI things, it's been a couple years since I've built custom views, and general UI work. (my past two years of work have been a lot of domain layer work)
- In existing projects, these 'silly little issues' are usually resolved by reviewing existing code, or quickly asking peers..
