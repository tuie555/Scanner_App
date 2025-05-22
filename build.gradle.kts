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
        property("sonar.projectKey", "scannerapp")
        property("sonar.host.url", "http://localhost:9000")
        property("sonar.token", "sqa_1af153509682d36187a065fd54d34eb8355cd2b1")
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.androidLint.reportPaths", "app/build/reports/lint-results.xml")

    }
}

