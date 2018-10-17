## Android architecture components: Lifecycle
-keep class * implements androidx.lifecycle.LifecycleObserver {
    <init>(...);
}

-keepclassmembers class * implements androidx.lifecycle.LifecycleObserver {
    <init>(...);
}

# keep Lifecycle State and Event enums values
-keepclassmembers class androidx.lifecycle.Lifecycle$State { *; }
-keepclassmembers class androidx.lifecycle.Lifecycle$Event { *; }
# keep methods annotated with @OnLifecycleEvent even if they seem to be unused
# (Mostly for LiveData.LifecycleBoundObserver.onStateChange(), but who knows)
-keepclassmembers class * {
    @androidx.lifecycle.OnLifecycleEvent *;
}

-keep class * implements androidx.lifecycle.LifecycleObserver {
    <init>(...);
}
