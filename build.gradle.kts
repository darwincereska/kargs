plugins {
    kotlin("jvm") version "2.2.21"
    id("com.vanniktech.maven.publish") version "0.35.0"
}

group = "org.kargs"
version = "1.0.2"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    // JUnit 5 testing dependencies
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
    testImplementation(kotlin("test"))
    
    // For assertions
    testImplementation("org.assertj:assertj-core:3.24.2")
}

tasks.test {
    useJUnitPlatform()
    
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
 
}

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    coordinates(group.toString(), "kargs", version.toString())

    pom {
        name = "Kargs"
        description = "A cli parsing library."
        inceptionYear = "2025"
        url = "https://github.com/darwincereska/karg"
        licenses {
            license {
                name = "MIT License"
                url = "http://www.opensource.org/licenses/mit-license.php"
            }
        }
        developers {
            developer {
                id = "darwincereska"
                name = "Darwin Cereska"
                url = "https://github.com/darwincereska"
            }
        }
        scm {
            url = "https://github.com/darwincereska/kargs"
            connection = "scm:git:git://github.com/darwincereska/kargs.git"
            developerConnection = "scm:git:ssh://git@github.com:darwincereska:kargs.git"
        }
    }
}
