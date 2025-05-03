package com.andyc.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.DynamicFeatureExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal fun Project.configureBuildTypes(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    extensionType: ExtensionType
) {
    commonExtension.run {
        buildFeatures {
            buildConfig = true
        }

        val xaiApiKey = gradleLocalProperties(rootDir, providers).getProperty("XAI_API_KEY")

        when (extensionType) {
            ExtensionType.APPLICATION -> {
                extensions.configure<ApplicationExtension> {
                    buildTypes {
                        debug {
                            configureDebugBuildType(xaiApiKey)
//                            configureDebugBuildType("")
                        }
                        release {
                            configureReleaseBuildType(commonExtension, xaiApiKey)
//                            configureReleaseBuildType(commonExtension, "")
                        }
                    }
                }
            }
            ExtensionType.LIBRARY -> {
                extensions.configure<LibraryExtension> {
                    buildTypes {
                        debug {
                            configureDebugBuildType(xaiApiKey)
//                            configureDebugBuildType("")
                        }
                        release {
                            configureReleaseBuildType(commonExtension, xaiApiKey)
//                            configureReleaseBuildType(commonExtension, "")
                        }
                    }
                }
            }
            ExtensionType.DYNAMIC_FEATURE -> {
                extensions.configure<DynamicFeatureExtension> {
                    buildTypes {
                        debug {
                            configureDebugBuildType(xaiApiKey)
//                            configureDebugBuildType("")
                        }
                        release {
                            configureReleaseBuildType(commonExtension, xaiApiKey)
//                            configureReleaseBuildType(commonExtension, "")
                            isMinifyEnabled = false
                        }
                    }
                }
            }
        }
    }
}

private fun BuildType.configureDebugBuildType(
    apiKey: String
) {
    buildConfigField("String", "XAI_API_KEY", "\"$apiKey\"")
    buildConfigField("String", "BASE_URL", "\"https://api.x.ai\"")
}

private fun BuildType.configureReleaseBuildType(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    apiKey: String
) {
    buildConfigField("String", "XAI_API_KEY", "\"$apiKey\"")
    buildConfigField("String", "BASE_URL", "\"https://api.x.ai\"")

    isMinifyEnabled = true
    proguardFiles(
        commonExtension.getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
    )
}