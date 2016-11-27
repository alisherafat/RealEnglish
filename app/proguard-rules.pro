# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\ALI-PC\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include remotePath and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }



-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-keepattributes JavascriptInterface

-keep public class ir.realenglish.app.utils.JavaScriptInjections
-keep public class * implements ir.realenglish.app.utils.JavaScriptInjections
-keepclassmembers class ir.realenglish.app.utils.JavaScriptInjections {
    <methods>;
}


-keep class .R
-keep class **.R$* {
    <fields>;
}

-keep class com.activeandroid.** { *; }
-keep class com.activeandroid.**.** { *; }
-keep class * extends com.activeandroid.Model
-keep class * extends com.activeandroid.serializer.TypeSerializer



##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }


# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

-keepclassmembers class com.example.device {
  public *;
}

##---------------End: proguard configuration for Gson  ----------

-keep class android.support.** { *; }
-keep interface android.** { *; }


-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }


-dontwarn android.support.**