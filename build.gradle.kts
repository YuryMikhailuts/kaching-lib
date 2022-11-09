@file:Suppress("UNUSED_VARIABLE")

plugins {
	apply {
		kotlin("multiplatform") version "1.6.10"
		kotlin("plugin.serialization") version "1.6.10"
		`maven-publish`
	}
}

group = "mikhaylutsyury"
version = "1.0.0-SNAPSHOT"

repositories {
	mavenCentral()
}

java {
	sourceCompatibility = JavaVersion.VERSION_11
	targetCompatibility = JavaVersion.VERSION_11
}


kotlin {
	jvm {
		compilations.all {
			kotlinOptions.jvmTarget = "11"
		}
		withJava()
		testRuns["test"].executionTask.configure {
			useJUnitPlatform()
		}
	}
	js(BOTH) {
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
