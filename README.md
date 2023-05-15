Multi-Use App for Take-Home Projects

Author: Chris Jones

Implemented as a KMM project, with only Android app implemented.

All domain code (repository, networking, persistence, etc..) in common code.

UI is Android specific, with classic Android Views (no Compose.. *frown*)

Detailed design and development plan found in [/DEVPLAN.md](DEVPLAN.md)

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
    - [ToDoService](/shared/src/commonMain/kotlin/com/kotlineering/interview/domain/todo/ToDoService.kt) ->
    - [ToDoRepository](/shared/src/commonMain/kotlin/com/kotlineering/interview/domain/todo/ToDoRepository.kt) ->
    - [ToDoApi](/shared/src/commonMain/kotlin/com/kotlineering/interview/domain/todo/ToDoApi.kt)

There is also dev settings (code can be found weaved into the above stack, and is present in the UI when the build is DEBUG)

To switch between the two apps, modify [mobile_navigation.xml](/androidApp/src/main/res/navigation/mobile_navigation.xml), set `app:startDestination` to either `@+id/nav_todo_home` or `@+id/nav_stocks_home`

Usage: StocksApp
- Launch, and see!

Usage: Todo App
- Tap "New" to create a new TODO.
- Swipe items in the list to delete TODOs.
- Tap "Show Completed" to see all TODOs.
- When all TODOs are visible, they can be reordered by long-pressing an item then dragging it to its new location.
- Changes are store locally, but fake remote APIs are called to simulate remote updates.
- Backend (https://jsonplaceholder.typicode.com/) returns a fixed set, and will reset any local changes made.

Libraries used:
- Koin for dependency injection.
- SqlDelight for persistence.
- Ktor-client for remote api.

- Mockito for testing.
- Robolectric fo testing (needed for in-memory SqlDelight DB, might have been able to instantiate a JVM driver instead of Android driver?)
