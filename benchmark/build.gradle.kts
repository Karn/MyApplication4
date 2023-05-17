plugins {
    id("com.android.test")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "io.karn.benchmark"
    compileSdk = 33

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    defaultConfig {
        minSdk = 24
        targetSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        // This benchmark buildType is used for benchmarking, and should function like your
        // release build (for example, with minification on). It"s signed with a debug key
        // for easy local/CI testing.
        create("benchmark") {
            isDebuggable = true
            signingConfig = getByName("debug").signingConfig
            matchingFallbacks += listOf("release")
        }
    }

    targetProjectPath = ":app"
    experimentalProperties["android.experimental.self-instrumenting"] = true


    testOptions {
        managedDevices {
            devices {
                maybeCreate<com.android.build.api.dsl.ManagedVirtualDevice>("pixel6Api31Arm64").apply {
                    device = "Pixel 4"
                    apiLevel = 31
                    // https://developer.android.com/studio/test/gradle-managed-devices#gmd-atd
                    systemImageSource = "aosp"
                }
                maybeCreate<com.android.build.api.dsl.ManagedVirtualDevice>("pixel6Api31").apply {
                    device = "Pixel 4"
                    apiLevel = 31
                    // https://developer.android.com/studio/test/gradle-managed-devices#gmd-atd
                    systemImageSource = "aosp-atd"
                }
            }
        }
    }
}

dependencies {
    implementation("androidx.test.ext:junit:1.1.5")
    implementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("androidx.test.uiautomator:uiautomator:2.2.0")
    implementation("androidx.benchmark:benchmark-macro-junit4:1.2.0-alpha14")
}

androidComponents {
    beforeVariants(selector().all()) {
        it.enable = it.buildType == "benchmark"
    }
}

tasks.register("runBenchmark") {
    doFirst {
        println(
            "Hello from benchmark/build.gradle.kts ${
                com.android.utils.Environment.instance.getSystemProperty(
                    com.android.utils.Environment.SystemProperty.OS_ARCH
                )
            } ${
                com.android.utils.Environment.instance.getSystemProperty(
                    com.android.utils.Environment.SystemProperty.OS_NAME
                )
            }"
        )
    }

    finalizedBy("pixel6Api31BenchmarkAndroidTest")
}
