plugins {
    id("com.android.application") version "9.4.1"
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.10"
}

val ugcApiPort = providers.gradleProperty("ugcApiPort").orElse("8080").get().toInt()
require(ugcApiPort in 1..65535) { "ugcApiPort must be between 1 and 65535" }

android {
    namespace = "ru.performanceugc.app"
    compileSdk = 36
    defaultConfig {
        applicationId = "ru.performanceugc.app"
        minSdk = 24
        targetSdk = 36
        buildConfigField("int", "UGC_API_PORT", ugcApiPort.toString())
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.compose.material3:material3:1.4.0")
}
