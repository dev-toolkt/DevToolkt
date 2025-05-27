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
            implementation(libs.kotlinx.benchmark.runtime)
            implementation(project(":core"))
            implementation(project(":geometry"))
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(project(":coreTestUtils"))
            implementation(project(":geometryTestUtils"))
        }

        jvmMain.dependencies {}

        jvmTest.dependencies {}
    }
}
