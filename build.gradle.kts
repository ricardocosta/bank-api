import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val flywayVersion by extra("8.4.4")
val jacksonVersion by extra("2.13.1")
val kotlinVersion by extra("1.6.10")
val kotlinCoroutinesVersion by extra("1.6.0")
val kotlinLoggingVersion by extra("2.1.21")
val mockitoKotlinVersion by extra("2.2.0")
val mockitoVersion by extra("4.3.1")
val postgresVersion by extra("42.3.2")
val postgresR2dbcVersion by extra("0.8.11.RELEASE")
val reactorKotlinVersion by extra("1.1.5")
val r2dbcPoolVersion by extra("0.8.5.RELEASE")
val reactorBlockHoundVersion by extra("1.0.6.RELEASE")
val reactorTestVersion by extra("3.4.14")
val springBootVersion by extra("2.6.3")
val springDocVersion by extra("1.6.6")
val testContainersVersion by extra("1.16.3")

plugins {
    id("com.geoffgranum.gradle-conventional-changelog") version "0.3.1"
    id("com.github.ben-manes.versions") version "0.42.0"
    id("com.star-zero.gradle.githook") version "1.2.1"
    id("io.gitlab.arturbosch.detekt") version "1.19.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
    id("org.springframework.boot") version "2.7.5"
    id("ru.netris.commitlint") version "1.4.1"
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.spring") version "1.6.10"
    application
    jacoco
}

group = "com.ricardocosta"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

tasks.getByName<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
    jvmArgs = listOf("-XX:+AllowRedefinitionToAddDeleteMethods")
}

repositories {
    mavenCentral()
    maven(url = "https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:$reactorKotlinVersion")
    implementation("io.projectreactor.tools:blockhound:$reactorBlockHoundVersion")
    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")
    implementation("org.flywaydb:flyway-core:$flywayVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$kotlinCoroutinesVersion")
    implementation("org.springdoc:springdoc-openapi-webflux-ui:$springDocVersion")
    implementation("org.springdoc:springdoc-openapi-kotlin:$springDocVersion")
    implementation("org.springframework.boot:spring-boot-starter-actuator:$springBootVersion")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc:$springBootVersion")
    implementation("org.springframework.boot:spring-boot-starter-validation:$springBootVersion")
    implementation("org.springframework.boot:spring-boot-starter-webflux:$springBootVersion")
    implementation(platform("org.testcontainers:testcontainers-bom:$testContainersVersion"))

    developmentOnly("org.springframework.boot:spring-boot-devtools:$springBootVersion")

    runtimeOnly("io.r2dbc:r2dbc-postgresql:$postgresR2dbcVersion")
    runtimeOnly("org.postgresql:postgresql:$postgresVersion")

    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:$mockitoKotlinVersion")
    testImplementation("io.projectreactor:reactor-test:$reactorTestVersion")
    testImplementation("org.mockito:mockito-inline:$mockitoVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testContainersVersion")
    testImplementation("org.testcontainers:postgresql:$testContainersVersion")
    testImplementation("org.testcontainers:r2dbc:$testContainersVersion")
}

changelog {
    appName = "Bank API"
    repoUrl = "https://github.com/ricardocosta/bank-api"
    match = "^fix|^feat|^chore|^perf|^refactor|BREAKING"
}

detekt {
    toolVersion = "1.19.0"
    parallel = true
}

githook {
    failOnMissingHooksDir = true
    createHooksDirIfNotExist = false
    hooks {
        register("commit-msg") {
            task = "commitlint"
        }

        register("pre-commit") {
            task = "ktlintCheck detekt"
        }
    }
}

jacoco {
    toolVersion = "0.8.7"
    reportsDirectory.set(layout.projectDirectory.dir("coverage"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named("dependencyUpdates", DependencyUpdatesTask::class.java).configure {
    fun isNonStable(version: String): Boolean {
        val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
        val regex = "^[0-9,.v-]+(-r)?$".toRegex()
        val isStable = stableKeyword || regex.matches(version)
        return isStable.not()
    }

    rejectVersionIf {
        isNonStable(candidate.version)
    }

    checkForGradleUpdate = true
    gradleReleaseChannel = "current"
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

val jacocoExclusions = listOf(
    "com/ricardocosta/api/bank/BankAPIApplication*",
    "**/*\$logger\$*.class"
)

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
    reports {
        xml.required.set(true)
    }

    classDirectories.setFrom(
        sourceSets.main.get().output.asFileTree.matching {
            exclude(jacocoExclusions)
        }
    )
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            classDirectories.setFrom(
                sourceSets.main.get().output.asFileTree.matching {
                    exclude(jacocoExclusions)
                }
            )
            limit {
                minimum = "0.95".toBigDecimal()
            }
        }
    }
}

tasks.create<Delete>("cleanCoverage") {
    group = "build"
    delete = setOf(
        file(layout.projectDirectory.dir("coverage"))
    )
}

tasks.clean {
    finalizedBy("cleanCoverage")
}
