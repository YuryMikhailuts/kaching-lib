@file:Suppress("UNUSED_VARIABLE")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	apply {
		kotlin("multiplatform") version "1.6.21"
		`maven-publish`
	}
}

group = "mikhaylutsyury"
version = "1.0.2-SNAPSHOT"

repositories {
	mavenCentral()
}

java {
	sourceCompatibility = JavaVersion.VERSION_11
	targetCompatibility = JavaVersion.VERSION_11
}

val commonCompileArgs = listOf(
	"-opt-in=kotlin.RequiresOptIn",
	"-Xuse-experimental=io.ktor.locations.KtorExperimentalLocationsAPI",
)
tasks.withType(KotlinCompile::class).all {
	kotlinOptions.freeCompilerArgs += commonCompileArgs
}
kotlin {
	jvm {
		compilations.all {
			kotlinOptions.jvmTarget = "11"
			kotlinOptions.freeCompilerArgs += commonCompileArgs
		}
		withJava()
		testRuns["test"].executionTask.configure {
			useJUnitPlatform()
		}
	}
	js(BOTH) {
		compilations.all {
			kotlinOptions.freeCompilerArgs += commonCompileArgs
		}
		browser {
			testTask {
				useMocha {
					timeout = "30000"
				}
			}
			commonWebpackConfig {
				cssSupport.enabled = true
			}
		}
	}
	sourceSets {
		val commonMain by getting {
			dependencies {
				implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
				implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
				implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")
			}
		}
		val commonTest by getting {
			dependencies {
				implementation(kotlin("test"))
				implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
			}
		}
		val jvmMain by getting
		val jvmTest by getting
		val jsMain by getting {
			dependencies {
				implementation(npm("jstreemap", "1.28.2"))
			}
		}
		val jsTest by getting
	}
	publishing {
		publications {
			all { }
		}
	}
}
