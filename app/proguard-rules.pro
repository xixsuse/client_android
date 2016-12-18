# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/albertogiunta/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:
#-keep class io.realm.annotations.RealmModule
#-keep @io.realm.annotations.RealmModule class *
#-keep class io.realm.internal.Keep
#-keep @io.realm.internal.Keep class * { *; }
#-dontwarn javax.**
#-dontwarn io.realm.**

# Obfuscation parameters:
#-dontobfuscate

#-ignorewarnings
-useuniqueclassmembernames
-keepattributes SourceFile,LineNumberTable
-allowaccessmodification
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers


# Ignore warnings:
#-dontwarn org.mockito.**
#-dontwarn org.junit.**
#-dontwarn com.robotium.**
-dontwarn org.joda.convert.**

# Ignore warnings: We are not using DOM model
-dontwarn com.fasterxml.jackson.databind.ext.DOMSerializer
# Ignore warnings: https://github.com/square/okhttp/wiki/FAQs
-dontwarn com.squareup.okhttp.internal.huc.**
# Ignore warnings: https://github.com/square/okio/issues/60
-dontwarn okio.**
# Ignore warnings: https://github.com/square/retrofit/issues/435
-dontwarn com.google.appengine.api.urlfetch.**

-dontwarn retrofit2.Platform$Java8
-dontwarn sun.misc.Unsafe
-dontwarn java.lang.invoke.*

#-keep class com.jaus.albertogiunta.justrntrain_oraritreni.** { *; }

# Keep the pojos used by GSON or Jackson
-keep class com.futurice.project.models.pojo.** { *; }

# Keep GSON stuff
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.** { *; }

# Keep Jackson stuff
-keep class org.codehaus.** { *; }
-keep class com.fasterxml.jackson.annotation.** { *; }

# Keep these for GSON and Jackson
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod

# Keep Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

-keepclassmembers class * {
    @retrofit.** *;
}