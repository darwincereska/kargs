plugins {
    kotlin("jvm") version "2.2.21"
    id("com.vanniktech.maven.publish") version "0.35.0"
}

group = "org.kargs"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.2.21")
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
