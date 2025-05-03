plugins {
    alias(libs.plugins.facter.android.library)
    alias(libs.plugins.facter.jvm.ktor)
}

android {
    namespace = "com.andyc.core.data"
}

dependencies {
    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)

    // Koin
    implementation(libs.bundles.koin)

    implementation(projects.core.domain)
    implementation(projects.core.database)
}