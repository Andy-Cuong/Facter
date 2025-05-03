plugins {
    alias(libs.plugins.facter.android.library)
    alias(libs.plugins.facter.jvm.ktor)
}

android {
    namespace = "com.andyc.checker.network"
}

dependencies {
    implementation(libs.openai.client)

    implementation(libs.bundles.koin)

    implementation(projects.checker.domain)
    implementation(projects.core.domain)
    implementation(projects.core.data)
}