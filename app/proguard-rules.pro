# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/carter/java_developer_tools/android-studio/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


#指定代码的压缩级别
-optimizationpasses 3

#包明不混合大小写
-dontusemixedcaseclassnames

#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses

#优化  不优化输入的类文件
-dontoptimize

#预校验
-dontpreverify

#混淆时是否记录日志
-verbose

# 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#保护注解
-keepattributes *Annotation*

# 保持哪些类不被混淆
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

#如果有引用v4包可以添加下面这行
-keep public class * extends android.support.v7.app.Fragment


#忽略警告
-ignorewarning

# 对R文件下的所有类及其方法，都不能被混淆
-keep class **.R$* { *; }


-keep class butterknife.** { *; }
-keep class retrofit2.** { *; }
-keep class com.annimon.stream
-keep class rx.** { *; }
-keep class com.jakewharton.rxbinding.** { *; }
-keep class android.support.** { *; }
-keep class okio.** { *; }
-keep class okhttp3.** { *; }
-keep class io.netty.** { *; }
-keep class org.apache.harmony.beans.** { *; }
-keep class org.msgpack.** { *; }
-keep class org.junit.** { *; }
-keep class javax.** { *; }
-keep class org.json.simple.** { *; }
-keep class com.squareup.javawriter
-keep class javassist.** { *; }
-keep class com.facebook.** { *; }
-keep class org.hamcrest.** { *; }
-keep class com.google.gson.** { *; }
-keep class android.support.** { *; }
-keep class com.google.** { *; }
#tencent bug tools不被混淆
-keep class com.tencent.bugly.** { *; }
-keep class bolts.** { *; }
-keep class org.apache.http.** { *; }

-keep class com.hiveview.manager.** { *; }
-keep class com.hiveview.manager2.** { *; }
-keep class com.j256.ormlite.** { *; }



#实体类不参与混淆
-keep class com.hiveview.dianshang.entity.** { *; }
-keep class com.hiveview.dianshang.view.** { *; }
