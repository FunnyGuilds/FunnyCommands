package net.dzikoysk.funnycommands.resources.types;

import net.dzikoysk.funnycommands.resources.CommandDataType;
import net.dzikoysk.funnycommands.resources.Origin;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

@FunnyComponent
public final class BooleanType implements CommandDataType<Boolean> {

    private static final List<String> DEFAULT_TRUE_VALUES = Arrays.asList("true", "yes", "prawda", "tak", "y", "1");
    private static final List<String> DEFAULT_FALSE_VALUES = Arrays.asList("false", "no", "falsz", "nie", "n", "0");

    private final List<String> trueValues;
    private final List<String> falseValues;

    public BooleanType(List<String> trueValues, List<String> falseValues) {
        this.trueValues = trueValues;
        this.falseValues = falseValues;
    }

    public BooleanType() {
        this.trueValues = DEFAULT_TRUE_VALUES;
        this.falseValues = DEFAULT_FALSE_VALUES;
    }

    @Override
    public Boolean apply(Origin origin, Parameter parameter, String argument) {
        if (containsIgnoreCase(trueValues, argument)) {
            return true;
        }

        if (containsIgnoreCase(falseValues, argument)) {
            return false;
        }

        return null;
    }

    private boolean containsIgnoreCase(List<String> values, String argument) {
        for (String value : values) {
            if (value.equalsIgnoreCase(argument)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getName() {
        return "boolean";
    }

}
