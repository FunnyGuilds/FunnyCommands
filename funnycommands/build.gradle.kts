description = "FunnyCommands|Core"

dependencies {
    // Panda stack
    api("org.panda-lang.utilities:di:1.8.0")

    // General
    val spigotVersion = "1.16.5-R0.1-SNAPSHOT"
    compileOnlyApi("org.spigotmc:spigot-api:$spigotVersion")
    compileOnlyApi("org.jetbrains:annotations:26.0.2")
    compileOnlyApi("com.google.code.findbugs:jsr305:3.0.2")

    // Tests
    val junit = "5.11.0"
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junit")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junit")
    testImplementation("org.spigotmc:spigot-api:$spigotVersion")
}