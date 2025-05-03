plugins {
    alias(libs.plugins.facter.android.library)
    alias(libs.plugins.facter.jvm.ktor)
}

android {
    namespace = "com.andyc.auth.data"
}

dependencies {
    // Koin
    implementation(libs.bundles.koin)

    implementation(projects.auth.domain)
    implementation(projects.core.data)
    implementation(projects.core.domain)
}