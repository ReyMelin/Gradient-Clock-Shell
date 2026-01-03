# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Capacitor WebView optimizations
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}

# Keep Capacitor classes
-keep class com.getcapacitor.** { *; }
-keepclassmembers class com.getcapacitor.** { *; }

# Keep our main activities and services
-keep class com.reymelin.GC.MainActivity { *; }
-keep class com.reymelin.GC.GradientClockWidget { *; }
-keep class com.reymelin.GC.GradientClockWallpaperService { *; }
-keep class com.reymelin.GC.GradientClockWatchFaceService { *; }
-keep class com.reymelin.GC.WidgetConfigActivity { *; }
-keep class com.reymelin.GC.WallpaperSettingsActivity { *; }

# Keep WebView related classes
-keep class android.webkit.** { *; }
-keepclassmembers class android.webkit.** { *; }

# Keep Wear OS classes
-keep class androidx.wear.** { *; }
-keepclassmembers class androidx.wear.** { *; }

# Preserve line numbers for debugging
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep annotations
-keepattributes *Annotation*

# AndroidX optimizations
-dontwarn androidx.**
-keep class androidx.** { *; }

# JavaScript Interface (if added in future)
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Optimization flags for release builds
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose

# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}
