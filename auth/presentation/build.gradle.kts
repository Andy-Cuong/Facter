plugins {
    alias(libs.plugins.facter.android.feature.ui)
}

android {
    namespace = "com.andyc.auth.presentation"
}

dependencies {
    implementation(libs.androidx.appcompat)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.ui.auth)

    // Facebook SDK
    implementation(libs.facebook.login)

    implementation(projects.auth.domain)
    implementation(projects.core.domain)
}