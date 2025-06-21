import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsSubTargetDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

repositories {
    mavenCentral()
}

kotlin {
    jvm()

    js(IR) {
        browser {
            testWithExtendedTimeout()
        }

        nodejs {
            testWithExtendedTimeout()
        }

        binaries.executable()
    }

    sourceSets {
        commonTest.dependencies {
            implementation(project(":core"))
            implementation(project(":coreTestUtils"))
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

fun KotlinJsSubTargetDsl.testWithExtendedTimeout() {
    testTask {
        useMocha {
            // System tests involve garbage collection
            timeout = "15s"
        }
    }
}
