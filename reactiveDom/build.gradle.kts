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
