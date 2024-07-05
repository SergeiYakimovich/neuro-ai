plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
//    maven(uri("https://releases.aspose.com/java/repo/"))

//    flatDir { dirs("libs") }
}

dependencies {
    implementation("org.beykery:neuroph:2.92")

    implementation(files("libs/aspose-ocr-24.6.1.jar"))
    implementation("com.microsoft.onnxruntime:onnxruntime:1.16.0")
//    implementation("com.aspose:aspose-pdf:24.2")

    implementation("net.sourceforge.tess4j:tess4j:4.5.1")

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