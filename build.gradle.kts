// Top-level build file where you can add configuration options common to all sub-projects/modules.


plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.sonarqube)
}
sonar {
    properties {
        property("sonar.projectKey", "tuie555_Scanner_App")
        property("sonar.host.url", "https://sonarcloud.io")
        property ("sonar.organization", "scannerapp")
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.androidLint.reportPaths", "app/build/reports/lint-results.xml")

    }
}
