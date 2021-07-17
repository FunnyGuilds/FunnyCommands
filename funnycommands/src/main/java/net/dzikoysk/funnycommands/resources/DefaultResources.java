package net.dzikoysk.funnycommands.resources;

import net.dzikoysk.funnycommands.resources.binds.ArgsBind;
import net.dzikoysk.funnycommands.resources.binds.ArgumentBind;
import net.dzikoysk.funnycommands.resources.binds.CommandSenderBind;
import net.dzikoysk.funnycommands.resources.binds.ContextBind;
import net.dzikoysk.funnycommands.resources.binds.PlayerBind;
import net.dzikoysk.funnycommands.resources.binds.RandomUUIDBind;
import net.dzikoysk.funnycommands.resources.completers.ChatColorsCompleter;
import net.dzikoysk.funnycommands.resources.completers.DyeColorsCompleter;
import net.dzikoysk.funnycommands.resources.completers.EmptyCompleter;
import net.dzikoysk.funnycommands.resources.completers.EntityTypesCompleter;
import net.dzikoysk.funnycommands.resources.completers.MaterialsCompleter;
import net.dzikoysk.funnycommands.resources.completers.OnlinePlayersCompleter;
import net.dzikoysk.funnycommands.resources.completers.PotionEffectTypesCompleter;
import net.dzikoysk.funnycommands.resources.completers.SoundsCompleter;
import net.dzikoysk.funnycommands.resources.completers.TimeUnitsCompleter;
import net.dzikoysk.funnycommands.resources.completers.WorldsCompleter;
import net.dzikoysk.funnycommands.resources.exceptions.FunnyCommandsExceptionHandler;
import net.dzikoysk.funnycommands.resources.responses.BooleanResponseHandler;
import net.dzikoysk.funnycommands.resources.responses.MultilineResponseHandler;
import net.dzikoysk.funnycommands.resources.responses.SenderResponseHandler;
import net.dzikoysk.funnycommands.resources.responses.StringResponseHandler;
import net.dzikoysk.funnycommands.resources.types.BooleanType;
import net.dzikoysk.funnycommands.resources.types.DoubleType;
import net.dzikoysk.funnycommands.resources.types.FloatType;
import net.dzikoysk.funnycommands.resources.types.IntegerType;
import net.dzikoysk.funnycommands.resources.types.LongType;
import net.dzikoysk.funnycommands.resources.types.ShortType;
import net.dzikoysk.funnycommands.resources.types.StringType;
import panda.utilities.ObjectUtils;
import panda.utilities.iterable.ResourcesIterable;

import java.util.Arrays;
import java.util.Collections;

public final class DefaultResources {

    public static Iterable<? extends Bind> BINDS = Arrays.asList(
            new ArgsBind(),
            new ArgumentBind(),
            new CommandSenderBind(),
            new ContextBind(),
            new PlayerBind(),
            new RandomUUIDBind()
    );

    public static Iterable<? extends Completer> COMPLETES = Arrays.asList(
            new ChatColorsCompleter(),
            new DyeColorsCompleter(),
            new EmptyCompleter(),
            new EntityTypesCompleter(),
            new MaterialsCompleter(),
            new OnlinePlayersCompleter(),
            new PotionEffectTypesCompleter(),
            new SoundsCompleter(),
            new TimeUnitsCompleter(),
            new WorldsCompleter()
    );

    public static Iterable<? extends ExceptionHandler<?>> EXCEPTION_HANDLERS = Collections.singletonList(
            new FunnyCommandsExceptionHandler()
    );

    public static Iterable<? extends ResponseHandler<?>> RESPONSE_HANDLERS = Arrays.asList(
            new BooleanResponseHandler(),
            new MultilineResponseHandler(),
            new SenderResponseHandler(),
            new StringResponseHandler()
    );

    public static Iterable<? extends CommandDataType<?>> TYPES = Arrays.asList(
            new BooleanType(),
            new DoubleType(),
            new FloatType(),
            new IntegerType(),
            new LongType(),
            new ShortType(),
            new StringType()
    );

    public static Iterable<? extends Object> ALL = new ResourcesIterable<>(
            ObjectUtils.cast(BINDS),
            ObjectUtils.cast(COMPLETES),
            ObjectUtils.cast(EXCEPTION_HANDLERS),
            ObjectUtils.cast(RESPONSE_HANDLERS),
            ObjectUtils.cast(TYPES)
    );

}
