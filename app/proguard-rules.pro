# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in F:\android-sdk/tools/proguard/proguard-android.txt
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
-dontobfuscate
-keep public interface android.content.pm.IPackageInstallObserver { *; }
-keep class android.content.pm.IPackageInstallObserver$Stub { *; }
-keep public class android.preference.MultiSelectListPreference { *; }
-keep public class com.google.protobuf.ExtensionRegistryLite { *; }
-keep public class com.google.protobuf.ExtensionRegistry { *; }
-keep final class com.google.protobuf.ExtensionRegistryFactory { *; }
-keep public class android.support.v4.content.FileProvider { *; }
-keep public class android.util.LruCache { *; }
-keep public class com.github.yeriomin.yalpstore.install.InstallerPrivilegedReflection$* { *; }
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!code/allocation/variable
-dontwarn sun.misc.Unsafe
-optimizationpasses 5
-allowaccessmodification
-dontskipnonpubliclibraryclasses
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable