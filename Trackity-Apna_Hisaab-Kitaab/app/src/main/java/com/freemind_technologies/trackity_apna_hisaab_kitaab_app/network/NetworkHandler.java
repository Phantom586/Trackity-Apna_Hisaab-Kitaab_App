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

import android.content.Context;
import android.util.Log;

import com.freemind_technologies.trackity_apna_hisaab_kitaab_app.BuildConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkHandler {

    private final static String TAG = "NetworkHandler";

    private final static String TAG_INTERCEPTOR = "NetworkHandler";

    boolean debug = BuildConfig.DEBUG;

    private static NetworkHandler sInstance;

    private static Context mContext;

    private NetworkApi mNetworkApi;

    private NetworkHandler() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient();

        if (debug) {

            client = client.newBuilder().addInterceptor(interceptor)
                    //.addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                    .addNetworkInterceptor(chain -> {
                        Log.i(TAG_INTERCEPTOR, "inside intercept callback");
                        Request request = chain.request();
                        long t1 = System.nanoTime();
                        String requestLog = String.format("Sending request %s on %s%n%s",
                                request.url(), chain.connection(), request.headers());
                        if (request.method().compareToIgnoreCase("post") == 0) {
                            requestLog = "\n" + requestLog + "\n" + bodyToString(request);
                        }
                        Log.d(TAG_INTERCEPTOR, "request" + "\n" + requestLog);

                        Response response = chain.proceed(request);
                        long t2 = System.nanoTime();

                        String responseLog = String.format("Received response for %s in %.1fms%n%s",
                                response.request().url(), (t2 - t1) / 1e6d, response.headers());

                        String bodyString = response.body().string();

                        Log.d(TAG_INTERCEPTOR, "response only" + "\n" + bodyString);

                        Log.d(TAG_INTERCEPTOR, "response" + "\n" + responseLog + "\n" + bodyString);

                        return response.newBuilder()
                                .body(ResponseBody.create(response.body().contentType(), bodyString))
                                .build();
                    }).build();
        }

        Retrofit retrofit =
                new Retrofit.Builder()
                        .baseUrl(NetworkApi.API_URL)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .client(client)
                        .build();
        mNetworkApi = retrofit.create(NetworkApi.class);
    }

    public static synchronized NetworkHandler getNetworkHandler(Context context) {
        if (sInstance == null) {
            sInstance = new NetworkHandler();
            mContext = context;
        }
        return sInstance;
    }

    public NetworkApi getNetworkApi() {
        return mNetworkApi;
    }

    /**
     * Only for debug purpose
     * @param request
     * @return
     */
    private static String bodyToString(final Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "Something went wrong";
        }
    }

}
