package net.dzikoysk.funnycommands.stereotypes;

import org.panda_lang.utilities.commons.StringUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FunnyCommand {

    String name();

    String permission() default StringUtils.EMPTY;

    String description() default StringUtils.EMPTY;

    String usage() default StringUtils.EMPTY;

    String[] aliases() default  { };

}
