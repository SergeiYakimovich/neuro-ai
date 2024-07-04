plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.beykery:neuroph:2.92")


    implementation(platform("ai.djl:bom:0.28.0"))
    implementation("ai.djl.pytorch:pytorch-model-zoo")
    implementation("ai.djl:api")
    implementation("ai.djl:examples:0.6.0")
    implementation("ai.djl:model-zoo")
    implementation("ai.djl:basicdataset")
    implementation("commons-cli:commons-cli:1.8.0")

    implementation("org.telegram:telegrambots:6.5.0")

    compileOnly("org.projectlombok:lombok:1.18.20")
    annotationProcessor("org.projectlombok:lombok:1.18.20")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}