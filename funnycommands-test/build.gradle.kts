import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

description = "FunnyCommands|Test"

apply(plugin = "com.github.johnrengelman.shadow")

dependencies {
    implementation(project(":funnycommands"))
    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
}

tasks.withType<ShadowJar> {
    archiveFileName.set("FunnyCommands-Test ${project.version}.jar")

    doLast {
        copy {
            from("build/libs/${archiveFileName.get()}")
            into("src/main/env/plugins")
        }
    }
}