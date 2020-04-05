# FunnyCommands
FunnyGuilds command framework based on top of the [Panda](https://github.com/panda-lang/panda) dependency injection. Supported features:
* Dynamic loading of commands
* Configurable placeholders to improve annotation based API
* Customizable dependency injection 
* Supports reloading

```java
@FunnyCommand(name = "${fc.test-alias}", permission = "fc.test")
private static final class TestCommand {

    @Command({ "player:target" })
    SenderResponse test(@Arg("target") Player target) {
        return new SenderResponse(target, "Test ${fc.time}");
    }

}
```

Full and up-to-date example is available in [FunnyCommandsAcceptanceTestPlugin](https://github.com/FunnyGuilds/FunnyCommands/blob/master/src/test/java/net/dzikoysk/funnycommands/acceptance/FunnyCommandsAcceptanceTestPlugin.java) class