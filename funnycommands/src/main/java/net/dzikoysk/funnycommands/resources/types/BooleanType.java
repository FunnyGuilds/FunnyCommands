package net.dzikoysk.funnycommands.resources.types;

import net.dzikoysk.funnycommands.resources.CommandDataType;
import net.dzikoysk.funnycommands.resources.Context;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import org.jetbrains.annotations.Nullable;
import org.panda_lang.utilities.inject.InjectorProperty;

import java.util.Arrays;
import java.util.List;

@FunnyComponent
public final class BooleanType extends AbstractType<Boolean> implements CommandDataType<Boolean> {

    private static final List<String> DEFAULT_TRUE_VALUES = Arrays.asList("true", "yes", "prawda", "tak", "y", "1");
    private static final List<String> DEFAULT_FALSE_VALUES = Arrays.asList("false", "no", "falsz", "nie", "n", "0");

    private final List<String> trueValues;
    private final List<String> falseValues;

    public BooleanType(List<String> trueValues, List<String> falseValues) {
        super("boolean", Boolean.class);
        this.trueValues = trueValues;
        this.falseValues = falseValues;
    }

    public BooleanType() {
        this(DEFAULT_TRUE_VALUES, DEFAULT_FALSE_VALUES);
    }

    @Override
    public @Nullable Boolean apply(Context context, InjectorProperty required, String argument) {
        if (containsIgnoreCase(trueValues, argument)) {
            return true;
        }

        if (containsIgnoreCase(falseValues, argument)) {
            return false;
        }

        return null;
    }

    private boolean containsIgnoreCase(Iterable<String> values, String argument) {
        for (String value : values) {
            if (value.equalsIgnoreCase(argument)) {
                return true;
            }
        }
        return false;
    }

}
