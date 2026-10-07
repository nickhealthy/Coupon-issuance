plugins {
	java
	id("org.springframework.boot") version "4.1.1"
	id("io.spring.dependency-management") version "1.1.7"
	id("com.google.cloud.tools.jib") version "3.5.2"// Jib 플러그인 추가
}

group = "com.apiece"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")

	compileOnly("org.projectlombok:lombok:1.18.48")
	annotationProcessor("org.projectlombok:lombok:1.18.48")

	runtimeOnly("com.mysql:mysql-connector-j")
	testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testCompileOnly("org.projectlombok:lombok:1.18.48")
	testAnnotationProcessor("org.projectlombok:lombok:1.18.48")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

jib {
	from {
		image = "eclipse-temurin:25-jre"
		platforms {
			platform {
				architecture = "arm64"
				os = "linux"
			}
		}
	}
	to {
		image = "coupon-servie"
		tags = setOf("latest", project.version.toString())
	}
	container {
		ports = listOf("8080")
		creationTime.set("USE_CURRENT_TIMESTAMP")
	}
}