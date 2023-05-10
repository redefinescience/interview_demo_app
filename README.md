Multi-Use App for Take-Home Projects

Author: Chris Jones

Implemented as a KMM project, with only Android app implemented.

All domain code (repository, networking, persistence, etc..) in common code.

UI is Android specific, with classic Android Views (no Compose.. *frown*)

Detailed design and development plan found in [/DEVPLAN.md](DEVPLAN-STOCKS.md)

Tests were implemented in [/shared/src/androidUnitTest](/shared/src/androidUnitTest) even for common code to quickly utilize JVM, so we can use Mockito.
Instead of this, we could probably modify /shared/src/commonTest to run JVM (or Android too, just no Robolectric allowed..) instead of KNative.

Should open and run from Android Studio.

Stack of components

Android Presentation Layer (platform specific)
* [/androidApp/.../android/...](/androidApp/src/main/java/com/kotlineering/interview/android)
  + [MainActivity](/androidApp/src/main/java/com/kotlineering/interview/android/MainActivity.kt) ->
    - [Stocks App](/androidApp/src/main/java/com/kotlineering/interview/android/ui/stocks)
      + [HomeFragment](/androidApp/src/main/java/com/kotlineering/interview/android/ui/stocks/StocksHomeFragment.kt) ->
      + [HomeViewModel](/androidApp/src/main/java/com/kotlineering/interview/android/ui/stocks/StocksViewModel.kt) ->
    - [ToDo App](/androidApp/src/main/java/com/kotlineering/interview/android/ui/todo)
      + [HomeFragment](/androidApp/src/main/java/com/kotlineering/interview/android/ui/todo/ToDoHomeFragment.kt) ->
      + [HomeViewModel](/androidApp/src/main/java/com/kotlineering/interview/android/ui/todo/ToDoHomeViewModel.kt) ->

Domain Layer (shared by Android & iOS)
* [/shared/.../domain/...](/shared/src/commonMain/kotlin/com/kotlineering/interview/domain)
  + [Stocks App](/shared/src/commonMain/kotlin/com/kotlineering/interview/domain/stocks)
    - [StocksService](/shared/src/commonMain/kotlin/com/kotlineering/interview/domain/stocks/StocksService.kt) ->
    - [StocksRepository](/shared/src/commonMain/kotlin/com/kotlineering/interview/domain/stocks/StocksRepository.kt) ->
    - [StocksApi](/shared/src/commonMain/kotlin/com/kotlineering/interview/domain/stocks/StocksApi.kt)
  + [ToDo App](/shared/src/commonMain/kotlin/com/kotlineering/interview/domain/stocks)

There is also dev settings (code can be found weaved into the above stack, and is present in the UI when the build is DEBUG)

Libraries used:
- Koin for dependency injection.
- SqlDelight for persistence.
- Ktor-client for remote api.

- Mockito for testing.
- Robolectric fo testing (needed for in-memory SqlDelight DB, might have been able to instantiate a JVM driver instead of Android driver?)
