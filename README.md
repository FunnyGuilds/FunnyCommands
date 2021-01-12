# FunnyCommands [![Build Status](https://travis-ci.org/FunnyGuilds/FunnyCommands.svg?branch=master)](https://travis-ci.org/FunnyGuilds/FunnyCommands)
FunnyGuilds command framework based on top of the [Panda](https://github.com/panda-lang/panda) dependency injection. Supported features:
* Dynamic loading of commands
* Configurable placeholders to extend annotation based API
* Customizable dependency injection 
* Supports reloading 
* Null safety

### Install
FunnyCommands artifact is available in Panda repository. Add these declarations to your `pom.xml`. 
```xml
<dependency>
  <groupId>net.dzikoysk</groupId>
  <artifactId>funnycommands</artifactId>
  <version>0.3.4</version>
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
        .placeholders(PLACEHOLDERS)
        .registerProcessedComponents()
        .type(new PlayerType(super.getServer()))
        .type("guild", ((origin, required, guild) -> guildService.guilds.get(guild)))
        .hook();
```

### Guides
`#soon™`

At this moment you can see full and up-to-date example in [FunnyCommandsAcceptanceTestPlugin](https://github.com/FunnyGuilds/FunnyCommands/blob/master/funnycommands-test/src/main/java/net/dzikoysk/funnycommands/acceptance/FunnyCommandsAcceptanceTestPlugin.java) class

### FAQ
**Q**: The `configuration.registerProcessedComponents()` does not detect my component classes <br>
**A**: To use processed components (these components are collected at compile time) you have to add transformer to your maven shade plugin:
```xml
<configuration>
    <transformers>
        <transformer implementation="org.apache.maven.plugins.shade.resource.AppendingTransformer">
            <resource>META-INF/annotations/net.dzikoysk.funnycommands.stereotypes.FunnyComponent</resource>
        </transformer>
    </transformers>
</configuration>
```
In case of any problems there is always possibility to use `configuration.registerAllComponents(<Plugin Class>)`.

**Q**: I've used transformer, but some of my components does not exist in my production build <br>
**A**: Make sure, that you are not using minimizing jar option from maven shade plugin. In that case, you have to exclude packages with components from shading:
```xml
<minimizeJar>true</minimizeJar>
<filters>
    <filter>
        <artifact>net.dzikoysk:funnycommands</artifact>
        <includes>
            <include>**</include>
        </includes>
    </filter>
</filters>
```
