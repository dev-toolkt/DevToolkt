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
            implementation("xml-apis:xml-apis-ext:1.3.04")
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(project(":coreTestUtils"))
            implementation(project(":geometryTestUtils"))
        }

        jvmMain.dependencies {
            implementation(libs.batik.anim)
            implementation(libs.batik.svg.dom)
            implementation(libs.batik.css)
            implementation(libs.fop)
        }

        jvmTest.dependencies {}
    }
}
