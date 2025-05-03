plugins {
    alias(libs.plugins.facter.android.library)
    alias(libs.plugins.facter.android.room)
}

android {
    namespace = "com.andyc.core.database"
}

dependencies {
    implementation(libs.bundles.koin)

    implementation(projects.core.domain)
}