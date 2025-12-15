import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.plugins.signing.SigningExtension
import org.gradle.kotlin.dsl.*

apply(plugin = "maven-publish")
apply(plugin = "signing")
apply(plugin = "org.jetbrains.dokka")

// Use Android's built-in source jar for libraries, create custom one for non-Android modules
if (project.plugins.findPlugin("com.android.library") == null) {
    val sourceSets = project.extensions.getByType<SourceSetContainer>()
    tasks.register<Jar>("sourcesJar") {
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
    }
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaTaskPartial>().configureEach {
    pluginsMapConfiguration.set(
        mapOf("org.jetbrains.dokka.base.DokkaBase" to """{ "separateInheritedMembers": true}""")
    )
}

tasks.register<Jar>("javadocJar") {
    dependsOn("dokkaJavadoc")
    archiveClassifier.set("javadoc")
    from(tasks.named("dokkaJavadoc").get().outputs)
}

val publishGroupId: String by project
val publishVersion: String by project
val publishName: String by project.extra
val publishDescription: String by project.extra
val publishArtifactId: String by project.extra

group = publishGroupId
version = publishVersion

afterEvaluate {
    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("release") {
                groupId = publishGroupId
                artifactId = publishArtifactId
                version = publishVersion
                
                if (project.plugins.findPlugin("com.android.library") != null) {
                    from(components["release"])
                    // Android plugin automatically provides source jars
                } else {
                    from(components["java"])
                    artifact(tasks.named("sourcesJar"))
                }

                artifact(tasks.named("javadocJar"))

                pom {
                    val pomUrl: String by project
                    val pomLicenceName: String by project
                    val pomLicenceUrl: String by project
                    val pomLicenceDist: String by project
                    val pomDeveloperId: String by project
                    val pomDeveloperName: String by project
                    val pomDeveloperEmail: String by project
                    val pomScmConnection: String by project
                    val pomScmDevConnection: String by project
                    val pomScmUrl: String by project
                    
                    name.set(publishName)
                    description.set(publishDescription)
                    url.set(pomUrl)
                    licenses {
                        license {
                            name.set(pomLicenceName)
                            url.set(pomLicenceUrl)
                            distribution.set(pomLicenceDist)
                        }
                    }
                    developers {
                        developer {
                            id.set(pomDeveloperId)
                            name.set(pomDeveloperName)
                            email.set(pomDeveloperEmail)
                        }
                    }
                    scm {
                        connection.set(pomScmConnection)
                        developerConnection.set(pomScmDevConnection)
                        url.set(pomScmUrl)
                    }
                }
            }
        }
    }
}

extra["signing.keyId"] = rootProject.extra["signing.keyId"]
extra["signing.password"] = rootProject.extra["signing.password"]
extra["signing.secretKeyRingFile"] = rootProject.extra["signing.secretKeyRingFile"]

configure<SigningExtension> {
    sign(the<PublishingExtension>().publications)
}
