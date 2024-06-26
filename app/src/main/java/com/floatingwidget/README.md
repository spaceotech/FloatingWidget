# Floating Widget App

This Android application demonstrates a floating widget that can overlay other apps. It provides a button that, when clicked, starts a service to display a floating widget on the screen.

## Updates Made

### Project Updates:
- Updated `compileSdkVersion` to 34.
- Updated JDK version and Gradle version to support SDK 34.

### Dependency Updates:
- Updated dependencies (`appcompat`, `constraintlayout`, etc.) to their latest versions compatible with SDK 34.

### Permission Handling:
- Implemented permission request flow for `ACTION_MANAGE_OVERLAY_PERMISSION` to ensure the app can draw over other apps.
- Added logic to handle permission requests and display appropriate messages to the user.

### Code Refactoring:
- Refactored deprecated code in `FloatWidgetService`:
    - Removed deprecated `startService` method and replaced with updated method.
    - Optimized code for smoother functioning of the floating button.

### Detailed Comments:
- Added detailed comments throughout the codebase to explain functionality and important implementation details.


