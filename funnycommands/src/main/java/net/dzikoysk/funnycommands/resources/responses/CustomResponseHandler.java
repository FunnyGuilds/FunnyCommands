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

import java.util.function.BiFunction;

public class CustomResponseHandler<R> implements ResponseHandler<R> {

    private final Class<R> responseType;
    private final BiFunction<Context, R, Boolean> responseHandler;

    public CustomResponseHandler(Class<R> responseType, BiFunction<Context, R, Boolean> responseHandler) {
        this.responseType = responseType;
        this.responseHandler = responseHandler;
    }

    @Override
    public Boolean apply(Context context, R response) {
        return responseHandler.apply(context, response);
    }

    @Override
    public Class<R> getResponseType() {
        return responseType;
    }

}
