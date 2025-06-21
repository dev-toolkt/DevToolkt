plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

group = "dev.toolkt"

repositories {
    mavenCentral()
}

kotlin {
    jvm()

    js(IR) {
        browser()
        nodejs()
    }

    sourceSets {
        jsMain.dependencies {
            implementation(npm("path-data-polyfill", "1.0.10"))

            implementation(project(":core"))
            implementation(project(":geometry"))
            implementation(project(":pureDom"))
            implementation(project(":reactive"))
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
