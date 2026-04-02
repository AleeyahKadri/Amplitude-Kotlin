import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.plugins.signing.SigningExtension
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.dokka.gradle.DokkaTaskPartial

apply(plugin = "maven-publish")
apply(plugin = "signing")
apply(plugin = "org.jetbrains.dokka")

if (project.plugins.findPlugin("com.android.library") == null) {
    tasks.register<Jar>("sourcesJar") {
        archiveClassifier.set("sources")
        from(the<SourceSetContainer>()["main"].allSource)
    }
}

tasks.withType(DokkaTaskPartial::class.java).configureEach {
    pluginsMapConfiguration.set(
        mapOf("org.jetbrains.dokka.base.DokkaBase" to """{ "separateInheritedMembers": true}""")
    )
}

val dokkaJavadoc = tasks.named<DokkaTask>("dokkaJavadoc")

tasks.register<Jar>("javadocJar") {
    dependsOn(dokkaJavadoc)
    archiveClassifier.set("javadoc")
    from(dokkaJavadoc.map { it.outputDirectory })
}

group = property("PUBLISH_GROUP_ID")
version = property("PUBLISH_VERSION")

afterEvaluate {
    extensions.configure<PublishingExtension> {
        publications {
            create<MavenPublication>("release") {
                groupId = property("PUBLISH_GROUP_ID") as String
                artifactId = extra["PUBLISH_ARTIFACT_ID"] as String
                version = property("PUBLISH_VERSION") as String

                if (project.plugins.findPlugin("com.android.library") != null) {
                    from(components["release"])
                } else {
                    from(components["java"])
                    artifact(tasks.named("sourcesJar"))
                }

                artifact(tasks.named("javadocJar"))

                pom {
                    name.set(extra["PUBLISH_NAME"] as String)
                    description.set(extra["PUBLISH_DESCRIPTION"] as String)
                    url.set(property("POM_URL") as String)
                    licenses {
                        license {
                            name.set(property("POM_LICENCE_NAME") as String)
                            url.set(property("POM_LICENCE_URL") as String)
                            distribution.set(property("POM_LICENCE_DIST") as String)
                        }
                    }
                    developers {
                        developer {
                            id.set(property("POM_DEVELOPER_ID") as String)
                            name.set(property("POM_DEVELOPER_NAME") as String)
                            email.set(property("POM_DEVELOPER_EMAIL") as String)
                        }
                    }
                    scm {
                        connection.set(property("POM_SCM_CONNECTION") as String)
                        developerConnection.set(property("POM_SCM_DEV_CONNECTION") as String)
                        url.set(property("POM_SCM_URL") as String)
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
    sign(extensions.getByType<PublishingExtension>().publications)
}
