package com.example.study_buddy.network;

import com.example.study_buddy.App;
import com.example.study_buddy.R;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    private static Retrofit retrofit;
    /**********************CHANGE THIS LATER**************************/

    private static final String BASE_URL = "http://" +
            App.getContext().getResources().getString(R.string.ipAddress) + ":8080";

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
