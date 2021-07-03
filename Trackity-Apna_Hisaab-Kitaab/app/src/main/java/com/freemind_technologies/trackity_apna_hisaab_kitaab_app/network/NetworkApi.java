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

package com.freemind_technologies.trackity_apna_hisaab_kitaab_app.network;

import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.classes.ImportDataResult;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.classes.RegisterUser;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.classes.SyncDBResult;
import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.classes.VerifyUserResult;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface NetworkApi {

//    String API_URL = "http://ec2-52-66-203-52.ap-south-1.compute.amazonaws.com/";
    String API_URL = "https://freemindtechnologies.com/";

    @Multipart
    @POST("Trackity/registerUser.php")
    Call<RegisterUser> registerUser(@Part("full_name") RequestBody full_name, @Part("email") RequestBody email, @Part("password") RequestBody pass);

//    @Multipart
//    @POST("Exp_Tracker/registerUser.php")
//    Call<RegisterUser> registerUser(@Part("full_name") RequestBody full_name, @Part("email") RequestBody email, @Part("password") RequestBody pass);

//    @FormUrlEncoded
//    @POST("Trackity/registerUser.php")
//    Call<RegisterUser> registerUser(@Field("full_name") String full_name, @Field("email") String email, @Field("password") String pass);

    @Multipart
    @POST("Trackity/syncExpenseTableData.php")
    Call<SyncDBResult> syncExpenseTableData(@Part("data") RequestBody data);

    @Multipart
    @POST("Trackity/syncExpenseTypeData.php")
    Call<SyncDBResult> syncExpenseTypeData(@Part("data") RequestBody data);

    @Multipart
    @POST("Trackity/importData.php")
    Call<ImportDataResult> importData(@Part("user_id") RequestBody user_id);

    @Multipart
    @POST("Trackity/verifyUser.php")
    Call<VerifyUserResult> verifyUser(@Part("email") RequestBody email);

//    @Multipart
//    @POST("Exp_Tracker/verifyUser.php")
//    Call<VerifyUserResult> verifyUser(@Part("email") RequestBody email);

//    @FormUrlEncoded
//    @POST("Trackity/verifyUser.php")
//    Call<VerifyUserResult> verifyUser(@Field("email") String email);

//    @GET("/Exp_Tracker/fetchExpenseTypes.php")
//    Call<ExpenseTypeResult> fetchExpenseTypes();

}
