# FunnyCommands [![Build Status](https://travis-ci.org/FunnyGuilds/FunnyCommands.svg?branch=master)](https://travis-ci.org/FunnyGuilds/FunnyCommands)
FunnyGuilds command framework based on top of the [Panda](https://github.com/panda-lang/panda) dependency injection. Supported features:
* Dynamic loading of commands
* Configurable placeholders to extend annotation based API
* Customizable dependency injection 
* Supports reloading 

### Install
FunnyCommands uses official GitHub Packages, the artifact is available by adding this declaration to your `pom.xml`. 
```xml
<dependency>
  <groupId>net.dzikoysk</groupId>
  <artifactId>funnycommands</artifactId>
  <version>0.1.0</version>
</dependency>
```

Requirements:
* Java 8 or higher
* Spigot 1.8.8 or higher
* Panda Utilities

### Usage
As an example, we can take `/test <player> <guild>` command.

```java
@FunnyCommand(name = "test", permission = "fc.test", usage = "/test <player> <guild>")
private static final class TestCommand {

    @Executor({ "player:target", "guild:arg-guild" })
    SenderResponse test(@Sender CommandSender sender, @Arg("target") @Nillable Player target, @Arg("arg-guild") Option<Guild> guild) {
        return new SenderResponse(target, "Test at ${fc.time} > " + sender + " called " + target + " and " + guild.getOrNull());
    }

}
```

The configuration for this kind of command may look like this:

```java
FunnyCommands funnyCommands = FunnyCommands.configuration(() -> this)
        .placeholders(placeholders)
        .commands(TestCommand.class)
        .type("player", (origin, required, username) -> {
            return super.getServer().getPlayer(username);
        })
        .type("guild", ((origin, required, guild) -> {
            return guildService.guilds.get(guild);
        }))
        .dynamicBind(((origin, resources) -> {
            resources.annotatedWithTested(Sender.class).assignInstance(origin.getCommandSender());
        }))
        .responseHandler(SenderResponse.class, (context, response) -> {
            response.getSender()
                    .getOrElse(context::getCommandSender)
                    .sendMessage(context.format(response));

            return true;
        })
        .exceptionHandler(FunnyCommandsException.class, e -> {
            e.printStackTrace();
            return true; // handled
        })
        .create();
```

Full and up-to-date example is available in [FunnyCommandsAcceptanceTestPlugin](https://github.com/FunnyGuilds/FunnyCommands/blob/master/funnycommands-test/src/main/java/net/dzikoysk/funnycommands/acceptance/FunnyCommandsAcceptanceTestPlugin.java) class
