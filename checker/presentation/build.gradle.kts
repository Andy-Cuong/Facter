plugins {
    alias(libs.plugins.facter.android.feature.ui)
}

android {
    namespace = "com.andyc.checker.presentation"
}

dependencies {
    implementation(libs.coil.compose)
    implementation(libs.androidx.activity.compose)

    implementation(projects.core.domain)
    implementation(projects.checker.domain)
}