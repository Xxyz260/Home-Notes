# Home Notes v1.0

Package: `site.chatgpt.vi52.homenotes`

A deliberately small Android home-screen widget that displays one freeform note per widget instance.

## Behavior

- Adding the widget opens the platform widget configuration activity.
- Long-pressing the widget and choosing the launcher's standard reconfigure action opens the same activity on Android 12+.
- Tapping the widget also opens that editor, because `RemoteViews` supports `TextView` but not an editable `EditText`.
- Each widget instance stores its own note, text size, system font, text RGB/opacity, and background RGB/opacity.
- Fonts are enumerated from Android's installed system fonts at runtime. No font is bundled.
- The app has no launcher activity and no network permission.
- The app icon is a transparent Android vector drawable containing a conventional serif capital T.

## Requirements

- Android 10 / API 29 or later.
- Android Studio compatible with Android Gradle Plugin 9.4, or Gradle 9.6 with Android SDK 37 installed.
- JDK 17+.

## Build

Open the project in Android Studio and build the `app` module. If Gradle 9.6 is already installed, you can also run:

```sh
gradle assembleDebug
```

The debug APK will be written under `app/build/outputs/apk/debug/`.

There are no external libraries or frameworks.
