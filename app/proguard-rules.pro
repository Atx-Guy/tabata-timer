# ============================================================
# Tabata Timer — ProGuard / R8 rules
# ============================================================

# ---------- Kotlin ----------
-keepclassmembers class kotlin.Metadata { *; }
-keepclassmembers @kotlin.Metadata class * { *; }

# ---------- Gson (used for Workout serialization in navigation) ----------
# Keep all Room entities and any class annotated with @Entity
-keepclassmembers class com.ryan.tabatatimer.model.** { *; }
-keep class com.ryan.tabatatimer.model.** { *; }

# Prevent Gson from stripping field names used during deserialization
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# ---------- Room ----------
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao interface *
-dontwarn androidx.room.**

# ---------- AndroidX / Compose ----------
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# ---------- Media3 / ExoPlayer ----------
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**
-keep class com.google.android.exoplayer2.** { *; }
-dontwarn com.google.android.exoplayer2.**

# ---------- Coroutines ----------
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
-dontwarn kotlinx.coroutines.**

# ---------- Lifecycle ----------
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# ---------- Services ----------
-keep class com.ryan.tabatatimer.service.** { *; }

# ---------- General Android ----------
# Keep all Parcelable implementations
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep BuildConfig fields
-keep class com.ryan.tabatatimer.BuildConfig { *; }
