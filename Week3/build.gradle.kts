plugins {
    id("com.android.application") version "8.1.2" apply false
    id("com.android.library") version "8.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
    id("com.google.devtools.ksp") version "1.9.10-1.0.13" apply false
}
// probalby should get rid of this stuff endeded up going with bindings to send information
buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.0")
        classpath ("com.android.tools.build:gradle:7.0.+")

    }
}


tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

