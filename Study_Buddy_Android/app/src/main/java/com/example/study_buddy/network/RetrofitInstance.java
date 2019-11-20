package com.example.study_buddy.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    private static Retrofit retrofit;
    /**********************CHANGE THIS LATER**************************/
    private static final String BASE_URL =
            "http://128.189.208.116:8080";
    //Jeanne_Buckridge47@hotmail.com
    //Mq15CwUM7nOXbAQ
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
