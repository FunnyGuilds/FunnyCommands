# FunnyCommands [![Build Status](https://travis-ci.org/FunnyGuilds/FunnyCommands.svg?branch=master)](https://travis-ci.org/FunnyGuilds/FunnyCommands)
FunnyGuilds command framework based on top of the [Panda](https://github.com/panda-lang/panda) dependency injection. Supported features:
* Dynamic loading of commands
* Configurable placeholders to extend annotation based API
* Customizable dependency injection 
* Supports reloading 
* Null safety

### Install
FunnyCommands uses official GitHub Packages, the artifact is available by adding this declaration to your `pom.xml`. 
```xml
<dependency>
  <groupId>net.dzikoysk</groupId>
  <artifactId>funnycommands</artifactId>
  <version>0.1.1</version>
</dependency>
```

Located in Maven repository: [repo.panda-lang.org](https://repo.panda-lang.org/)

```xml
<repository>
    <id>panda-repository</id>
    <name>Panda Repository</name>
    <url>https://repo.panda-lang.org/</url>
</repository>
```

Requirements:
* Java 8 or higher
* Spigot 1.8.8 or higher
* Panda Utilities

### Usage
As an example, we can take `/test <player> <guild>` command.

```java
@FunnyComponent
private static final class TestCommand {

    @FunnyCommand(
        name = "${fc.test-alias}",
        permission = "fc.test",
        usage = "/${fc.test-alias} <player>",
        completer = { "online-players", "guilds"},
        parameters = { "player:target", "guild:arg-guild" }
    )
    SenderResponse test(CommandSender sender, @Arg("target") @Nillable Player target, @Arg("arg-guild") Option<Guild> guild) {
        return new SenderResponse(target, "Test ${fc.time} > " + sender + " called " + target + " and " + guild.getOrNull());
    }

}
```

The configuration for this kind of command may look like this:

```java
this.funnyCommands = FunnyCommands.configuration(() -> this)
        .placeholders(PLACEHOLDERS)
        .registerComponents()
        .type(new PlayerType(super.getServer()))
        .type("guild", ((origin, required, guild) -> guildService.guilds.get(guild)))
        .create();
```

Full and up-to-date example is available in [FunnyCommandsAcceptanceTestPlugin](https://github.com/FunnyGuilds/FunnyCommands/blob/master/funnycommands-test/src/main/java/net/dzikoysk/funnycommands/acceptance/FunnyCommandsAcceptanceTestPlugin.java) class
