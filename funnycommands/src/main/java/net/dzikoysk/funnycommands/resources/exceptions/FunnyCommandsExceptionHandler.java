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

package net.dzikoysk.funnycommands.resources.exceptions;

import net.dzikoysk.funnycommands.FunnyCommandsException;
import net.dzikoysk.funnycommands.resources.ExceptionHandler;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;

@FunnyComponent
public final class FunnyCommandsExceptionHandler implements ExceptionHandler<FunnyCommandsException> {

    @Override
    public Boolean apply(FunnyCommandsException e) {
        e.printStackTrace();
        return true;
    }

    @Override
    public Class<FunnyCommandsException> getExceptionType() {
        return FunnyCommandsException.class;
    }

}
