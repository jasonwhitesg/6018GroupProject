plugins {
    id("com.android.application") version "8.1.2" apply false
    kotlin("android") version "1.9.10" apply false
    id("com.android.library") version "8.1.2" apply false
}

dependencies {
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

