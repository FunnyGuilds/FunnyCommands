/*
 * Copyright (c) 2020 Dzikoysk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.dzikoysk.funnycommands.stereotypes;

import panda.utilities.StringUtils;
import org.panda_lang.utilities.inject.annotations.Injectable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Injectable
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FunnyCommand {

    String name();

    String permission() default StringUtils.EMPTY;

    String description() default StringUtils.EMPTY;

    String usage() default StringUtils.EMPTY;

    String[] aliases() default { };

    String completer() default StringUtils.EMPTY;

    String parameters() default StringUtils.EMPTY;

    boolean playerOnly() default false;

    boolean acceptsExceeded() default false;

    boolean async() default false;

}
