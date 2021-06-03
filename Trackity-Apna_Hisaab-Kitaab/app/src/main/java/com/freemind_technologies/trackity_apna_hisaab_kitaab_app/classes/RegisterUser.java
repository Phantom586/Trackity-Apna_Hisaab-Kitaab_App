/*
 * Copyright 2021 FreeMind Technologies. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.freemind_technologies.trackity_apna_hisaab_kitaab_app.classes;

import com.google.gson.annotations.SerializedName;

public class RegisterUser {

    @SerializedName("response")
    private ResponseResult responseResult;
    @SerializedName("result")
    private RegisterResult result;

    public RegisterUser(ResponseResult responseResult, RegisterResult result) {

        this.responseResult = responseResult;
        this.result = result;

    }

    public ResponseResult getResponseResult() { return responseResult; }

    public RegisterResult getResult() { return result; }

}