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

package net.dzikoysk.funnycommands.resources.responses;

import net.dzikoysk.funnycommands.resources.Context;
import net.dzikoysk.funnycommands.resources.ResponseHandler;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;

import java.util.Arrays;

@FunnyComponent
public final class MultilineResponseHandler implements ResponseHandler<MultilineResponse> {

    @Override
    public Boolean apply(Context context, MultilineResponse multilineResponse) {
        Arrays.stream(multilineResponse.getLines()).forEach(line -> multilineResponse.getTarget()
                .orElseGet(context.getCommandSender())
                .sendMessage(context.format(line)));

        return true;
    }

    @Override
    public Class<MultilineResponse> getResponseType() {
        return MultilineResponse.class;
    }

}
