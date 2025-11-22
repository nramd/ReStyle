# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Preserve line number information for debugging stack traces
-keepattributes SourceFile,LineNumberTable

# Hide the original source file name
-renamesourcefileattribute SourceFile

# ===== Kotlin =====
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

# ===== Jetpack Compose =====
-keep class androidx.compose.** { *; }
-keepclassmembers class androidx.compose.** {
    *;
}
-dontwarn androidx.compose.**

# Keep Composable functions
-keep @androidx.compose.runtime.Composable class * { *; }
-keep @androidx.compose.runtime.Composable public * { *; }

# ===== AndroidX =====
-keep class androidx.** { *; }
-keep interface androidx.** { *; }
-dontwarn androidx.**

# ===== Material3 =====
-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**

# ===== Navigation =====
-keep class androidx.navigation.** { *; }
-keepnames class androidx.navigation.fragment.NavHostFragment

# ===== Coroutines =====
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# ===== Data Classes =====
# Keep data classes for serialization
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}

# ===== Enum =====
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ===== Parcelable =====
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

# ===== WebView (if used in future) =====
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# ===== Gson (if used for JSON parsing) =====
#-keepattributes Signature
#-keepattributes *Annotation*
#-keep class com.google.gson.** { *; }

# ===== Retrofit (if used for API calls) =====
#-keepattributes Signature, InnerClasses, EnclosingMethod
#-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
#-keepclassmembers,allowshrinking,allowobfuscation interface * {
#    @retrofit2.http.* <methods>;
#}

# ===== OkHttp (if used) =====
#-dontwarn okhttp3.**
#-dontwarn okio.**
#-keep class okhttp3.** { *; }
#-keep interface okhttp3.** { *; }

# ===== Room Database (if used in future) =====
#-keep class * extends androidx.room.RoomDatabase
#-keep @androidx.room.Entity class *
#-dontwarn androidx.room.paging.**