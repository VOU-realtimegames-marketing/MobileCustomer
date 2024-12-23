import com.google.protobuf.gradle.proto
plugins {
    id("com.android.application")
    id("com.google.protobuf")
}

android {
    namespace = "com.example.customer"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.customer"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    sourceSets {
        getByName("main") {
            proto {
                srcDir("src/main/java/com/example/customer/proto")
            }
        }
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.1"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.69.0" // CURRENT_GRPC_VERSION
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                maybeCreate("java").apply {
                    option("lite")
                }
            }
            task.plugins {
                maybeCreate("grpc").apply {
                    option("lite")
                }
            }
        }
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("io.grpc:grpc-okhttp:1.69.0") // CURRENT_GRPC_VERSION
    implementation("io.grpc:grpc-protobuf-lite:1.69.0") // CURRENT_GRPC_VERSION
    implementation("io.grpc:grpc-stub:1.69.0") // CURRENT_GRPC_VERSION
    implementation("org.apache.tomcat:annotations-api:6.0.53")
}