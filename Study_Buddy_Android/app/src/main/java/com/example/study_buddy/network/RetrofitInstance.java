package com.example.study_buddy.network;

import com.example.study_buddy.App;
import com.example.study_buddy.R;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    private static Retrofit retrofit;
    private static Retrofit retrofitForAccessToken;
    /**********************CHANGE THIS LATER**************************/

    private static final String BASE_URL = "http://" +
            App.getContext().getResources().getString(R.string.ipAddress) + ":8080";

    private static final String OAuth_URL = "https://oauth2.googleapis.com/";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getAccessTokenFromGoogle() {
        if (retrofitForAccessToken == null) {
            retrofitForAccessToken = new Retrofit.Builder()
                    .baseUrl(OAuth_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitForAccessToken;
    }

}
