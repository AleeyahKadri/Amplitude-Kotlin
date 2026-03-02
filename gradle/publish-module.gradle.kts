import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.plugins.signing.SigningExtension
import org.gradle.api.tasks.SourceSetContainer

apply(plugin = "maven-publish")
apply(plugin = "signing")
apply(plugin = "org.jetbrains.dokka")

// Use Android's built-in source jar for libraries, create custom one for non-Android modules
if (project.plugins.findPlugin("com.android.library") == null) {
    tasks.register<Jar>("sourcesJar") {
        archiveClassifier.set("sources")
        from((project.extensions.getByName("sourceSets") as SourceSetContainer).named("main").get().java.srcDirs)
        from((project.extensions.getByName("sourceSets") as SourceSetContainer).named("main").get().kotlin.srcDirs)
    }
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
    pluginsMapConfiguration.set(
        mapOf("org.jetbrains.dokka.base.DokkaBase" to """{ "separateInheritedMembers": true}""")
    )
}

tasks.register<Jar>("javadocJar") {
    dependsOn("dokkaJavadoc")
    archiveClassifier.set("javadoc")
    from(tasks.named("dokkaJavadoc").get().outputs)
}

group = project.property("PUBLISH_GROUP_ID").toString()
version = project.property("PUBLISH_VERSION").toString()

afterEvaluate {
    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("release") {
                groupId = project.property("PUBLISH_GROUP_ID").toString()
                artifactId = project.property("PUBLISH_ARTIFACT_ID").toString()
                version = project.property("PUBLISH_VERSION").toString()
                if (project.plugins.findPlugin("com.android.library") != null) {
                    from(components["release"])
                    // Android plugin automatically provides source jars
                } else {
                    from(components["java"])
                    artifact(tasks.named("sourcesJar"))
                }

                artifact(tasks.named("javadocJar"))

                pom {
                    name.set(project.property("PUBLISH_NAME").toString())
                    description.set(project.property("PUBLISH_DESCRIPTION").toString())
                    url.set(project.property("POM_URL").toString())
                    licenses {
                        license {
                            name.set(project.property("POM_LICENCE_NAME").toString())
                            url.set(project.property("POM_LICENCE_URL").toString())
                            distribution.set(project.property("POM_LICENCE_DIST").toString())
                        }
                    }
                    developers {
                        developer {
                            id.set(project.property("POM_DEVELOPER_ID").toString())
                            name.set(project.property("POM_DEVELOPER_NAME").toString())
                            email.set(project.property("POM_DEVELOPER_EMAIL").toString())
                        }
                    }
                    scm {
                        connection.set(project.property("POM_SCM_CONNECTION").toString())
                        developerConnection.set(project.property("POM_SCM_DEV_CONNECTION").toString())
                        url.set(project.property("POM_SCM_URL").toString())
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
