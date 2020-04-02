package net.dzikoysk.funnycommands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface FunnyCommand {

    String name();

    String permission();

    String[] parameters() default { };

}
