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

import net.dzikoysk.funnycommands.resources.Bind;
import net.dzikoysk.funnycommands.resources.Origin;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import org.panda_lang.utilities.commons.function.TriFunction;
import org.panda_lang.utilities.inject.InjectorProperty;
import org.panda_lang.utilities.inject.InjectorResources;

import java.lang.annotation.Annotation;

@FunnyComponent
public final class OriginBind implements Bind, TriFunction<InjectorProperty, Annotation, Object[], Object> {

    @Override
    public void accept(InjectorResources resources) {
        resources.on(Origin.class).assignHandler(this::apply);
    }

    @Override
    public Object apply(InjectorProperty parameter, Annotation annotation, Object[] objects) {
        return objects[1];
    }

}
