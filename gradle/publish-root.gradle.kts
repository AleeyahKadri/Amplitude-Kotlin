import java.util.Properties
import java.io.FileInputStream

// Create variables with empty default values
extra["ossrhUsername"] = ""
extra["ossrhPassword"] = ""
extra["sonatypeStagingProfileId"] = ""
extra["signing.keyId"] = ""
extra["signing.password"] = ""
extra["signing.secretKeyRingFile"] = ""

val secretPropsFile = project.rootProject.file("local.properties")
if (secretPropsFile.exists()) {
    // Read local.properties file first if it exists
    val p = Properties()
    FileInputStream(secretPropsFile).use { p.load(it) }
    p.forEach { name, value -> extra[name.toString()] = value }
} else {
    // Use system environment variables
    extra["ossrhUsername"] = System.getenv("OSSRH_USERNAME") ?: ""
    extra["ossrhPassword"] = System.getenv("OSSRH_PASSWORD") ?: ""
    extra["sonatypeStagingProfileId"] = System.getenv("SONATYPE_STAGING_PROFILE_ID") ?: ""
    extra["signing.keyId"] = System.getenv("SIGNING_KEY_ID") ?: ""
    extra["signing.password"] = System.getenv("SIGNING_PASSWORD") ?: ""
    extra["signing.secretKeyRingFile"] = System.getenv("SIGNING_SECRET_KEY_RING_FILE") ?: ""
}

// Set up Sonatype repository
configure<io.github.gradlenexus.publishplugin.NexusPublishExtension> {
    repositories {
        sonatype {
            stagingProfileId.set(extra["sonatypeStagingProfileId"].toString())
            username.set(extra["ossrhUsername"].toString())
            password.set(extra["ossrhPassword"].toString())
        }
    }
}
