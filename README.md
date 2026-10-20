# Home Notes v1.0

Package: `site.chatgpt.vi52.homenotes`

A deliberately small Android home-screen widget that displays one freeform note per widget instance.

## Behavior

- Adding the widget opens the platform widget configuration activity.
- Long-pressing the widget and choosing the launcher's standard reconfigure action opens the same activity on Android 12+.
- Tapping the widget also opens that editor, because `RemoteViews` supports `TextView` but not an editable `EditText`.
- Each widget instance stores its own note, text size, system font, text RGB/opacity, and background RGB/opacity.
- Fresh defaults are 24 sp text and 100% text/background opacity, with black text on white in light mode and white text on black in dark mode.
- New widgets start with the appearance settings from the most recently saved widget, even if that widget has since been deleted. Note text starts fresh. Saving or canceling one widget does not change other widgets; only saving updates the defaults for future widgets.
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

## Release signing

Release builds use a private PKCS12 keystore and a local `signing.properties` file
in the project root. Both are excluded from Git. Copy the supplied private
`signing.properties` into the project root and update `storeFile` to the absolute
path of your keystore when moving to another machine. It contains `storeFile`,
`storePassword`, `keyAlias`, and `keyPassword`.

```sh
gradle assembleRelease
```

The signed APK is written to `app/build/outputs/apk/release/app-release.apk`.
Release builds fail signing validation if the local signing file is absent;
debug builds do not require it. `gradle bundleRelease` uses the same key for an
Android App Bundle.

Keep a secure backup of the keystore and store its passwords separately. Reuse
this signing identity for future directly distributed updates. If publishing
with Google Play App Signing, follow Play Console's enrollment instructions:
this key can serve as the upload key, while Play manages the app signing key.
An existing published app must use its established signing identity or an
approved key migration.
