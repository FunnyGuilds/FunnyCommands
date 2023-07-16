# FunnyCommands ![FunnyCommands CI](https://github.com/FunnyGuilds/FunnyCommands/actions/workflows/gradle.yml/badge.svg) ![maven](https://maven.reposilite.com/api/badge/latest/releases/net/dzikoysk/funnycommands?color=40c14a&name=Latest%20Release&prefix=v)

FunnyGuilds command framework based on top of the [Panda](https://github.com/panda-lang/panda) dependency injection. Supported features:
* Dynamic loading of commands
* Configurable placeholders to extend annotation based API
* Customizable dependency injection 
* Supports reloading 
* Null safety

### Install
FunnyCommands artifact is available in [repo.panda-lang.org](https://repo.panda-lang.org/) repository. 
Add these declarations to your `pom.xml`. 

```xml
<repositories>
  <repository>
    <id>panda-repository</id>
    <name>Panda Repository</name>
    <url>https://repo.panda-lang.org/releases</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>net.dzikoysk</groupId>
    <artifactId>funnycommands</artifactId>
    <version>0.7.0</version>
  </dependency>
</dependencies>
```

Or in Gradle:

```groovy
repositories {
  maven { url "https://repo.panda-lang.org/releases" }
}

dependencies {
  implementation "net.dzikoysk:funnycommands:0.7.0"
}
```

Requirements:
* Java 8 or higher
* Spigot 1.8.8 or higher
* Panda Utilities

### Preview
As an example, we can take pointless `/test <player> [guild]` command.

```java
@FunnyComponent
private static final class TestCommand {

    @FunnyCommand(
        name = "${fc.test-alias}",
        description = "Test command",
        permission = "fc.test",
        usage = "/${fc.test-alias} <player> [guild]",
        completer = "online-players:5 guilds:5",
        parameters = "player:target [guild:arg-guild]"
    )
    SenderResponse test(CommandSender sender, @Arg("target") @Nullable Player target, @Arg("arg-guild") Option<Guild> guild) {
        return new SenderResponse(target, "Test ${fc.time} > " + sender + " called " + target + " and " + guild.getOrNull());
    }

}
```

The configuration for this kind of command may look like this:

```java
this.funnyCommands = FunnyCommands.configuration(() -> this)
        .registerDefaultComponents()
        .placeholders(PLACEHOLDERS)
        .type(new PlayerType(super.getServer()))
        .type("guild", ((context, required, guild) -> guildService.guilds.get(guild)))
        .hook();
```

### Guides
`#soon™`

At this moment you can see full and up-to-date example in [FunnyCommandsAcceptanceTestPlugin](https://github.com/FunnyGuilds/FunnyCommands/blob/master/funnycommands-test/src/main/java/net/dzikoysk/funnycommands/acceptance/FunnyCommandsAcceptanceTestPlugin.java) class
