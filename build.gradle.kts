import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.1.7.RELEASE"
	id("io.spring.dependency-management") version "1.0.8.RELEASE"
	kotlin("jvm") version "1.2.71"
	kotlin("plugin.spring") version "1.2.71"
	kotlin("plugin.jpa") version "1.2.71"
	kotlin("plugin.allopen") version "1.2.71"
	kotlin("kapt") version "1.2.71"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

val developmentOnly by configurations.creating
configurations {
	runtimeClasspath {
		extendsFrom(developmentOnly)
	}
}

repositories {
	mavenCentral()
}

allOpen {
	annotation("javax.persistence.Entity")
	annotation("javax.persistence.Embeddable")
	annotation("javax.persistence.MappedSuperclass")
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-mustache")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.h2database:h2")
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(module = "junit")
		exclude(module = "mockito-core")
	}
	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
	testImplementation("com.ninja-squad:springmockk:1.1.2")
	kapt("org.springframework.boot:spring-boot-configuration-processor")

}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}

tasks.withType<Test> {
	testLogging {
		lifecycle {
			events = setOf(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
			exceptionFormat = TestExceptionFormat.FULL
			showExceptions = true
			showCauses = true
			showStackTraces = true
			showStandardStreams = true
		}
		info.events = lifecycle.events
		info.exceptionFormat = lifecycle.exceptionFormat

		val failedTests = mutableListOf<TestDescriptor>()
		val skippedTests = mutableListOf<TestDescriptor>()

		// See https://github.com/gradle/kotlin-dsl/issues/836
		addTestListener(object : TestListener {
			override fun beforeSuite(suite: TestDescriptor) {}
			override fun beforeTest(testDescriptor: TestDescriptor) {}
			override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {
				when (result.resultType) {
					TestResult.ResultType.FAILURE -> failedTests.add(testDescriptor)
					TestResult.ResultType.SKIPPED -> skippedTests.add(testDescriptor)
					else -> Unit
				}
			}

			override fun afterSuite(suite: TestDescriptor, result: TestResult) {
				if (suite.parent == null) { // root suite
					logger.lifecycle("----")
					logger.lifecycle("Test result: ${result.resultType}")
					logger.lifecycle(
							"Test summary: ${result.testCount} tests, " +
									"${result.successfulTestCount} succeeded, " +
									"${result.failedTestCount} failed, " +
									"${result.skippedTestCount} skipped")
					failedTests.takeIf { it.isNotEmpty() }?.prefixedSummary("\tFailed Tests")
					skippedTests.takeIf { it.isNotEmpty() }?.prefixedSummary("\tSkipped Tests:")
				}
			}

			private infix fun List<TestDescriptor>.prefixedSummary(subject: String) {
				logger.lifecycle(subject)
				forEach { test -> logger.lifecycle("\t\t${test.displayName()}") }
			}

			private fun TestDescriptor.displayName() = parent?.let { "${it.name} - $name" } ?: name
		})
	}
	useJUnitPlatform()
}
