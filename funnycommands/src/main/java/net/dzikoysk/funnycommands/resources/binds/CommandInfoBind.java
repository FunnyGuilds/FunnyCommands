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

package net.dzikoysk.funnycommands.resources.binds;

import net.dzikoysk.funnycommands.commands.CommandInfo;
import net.dzikoysk.funnycommands.commands.CommandUtils;
import net.dzikoysk.funnycommands.resources.Bind;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import org.panda_lang.utilities.inject.Resources;

@FunnyComponent
public final class CommandInfoBind implements Bind {

    @Override
    public void accept(Resources resources) {
        resources.on(CommandInfo.class).assignHandler((property, annotation, objects) ->
                CommandUtils.getContext(objects).getCommandStructure().getMetadata().getCommandInfo());
    }

}
