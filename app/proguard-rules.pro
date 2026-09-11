# United Pay — Enterprise R8 / Proguard Rules

# 1. Strip all Android logging calls in release builds (Zero Log Leaks)
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
}

# 2. Keep Security & Keystore classes from obfuscation
-keep class com.unitedpay.core.security.** { *; }
-keepclassmembers class com.unitedpay.core.security.** { *; }

# 3. SQLCipher & Room
-keep class net.sqlcipher.** { *; }
-keep class androidx.room.** { *; }

# 4. Retrofit & OkHttp
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.squareup.okhttp3.** { *; }
-keep interface com.squareup.okhttp3.** { *; }
-dontwarn okio.**

# 5. Prevent Deobfuscation of Models
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !static !transient <methods>;
    !private <methods>;
}
