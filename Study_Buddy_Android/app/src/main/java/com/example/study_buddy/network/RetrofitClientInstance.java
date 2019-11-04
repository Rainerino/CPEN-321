package com.example.study_buddy.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {

    private static Retrofit retrofit;
    /**********************CHANGE THIS LATER**************************/
    private static final String BASE_URL =
            "http://ec2-18-191-87-244.us-east-2.compute.amazonaws.com:8080";
    // NOTE: this url changes!
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
