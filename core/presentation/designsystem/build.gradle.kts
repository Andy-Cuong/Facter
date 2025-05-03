plugins {
    alias(libs.plugins.facter.android.library.compose)
}

android {
    namespace = "com.andyc.core.presentation.designsystem"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.ui.text)
    debugImplementation(libs.androidx.ui.tooling)
    api(libs.androidx.material3)
}