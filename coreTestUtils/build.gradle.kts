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
        commonMain.dependencies {
            implementation(libs.kotlin.test)
            implementation(project(":core"))
        }
    }
}
