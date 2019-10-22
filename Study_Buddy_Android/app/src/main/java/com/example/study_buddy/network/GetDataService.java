package com.example.study_buddy.network;

import com.example.study_buddy.model.user;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface GetDataService {
    @GET("/users") /****CHANGE THE PATH LATER****/
    Call<user> getCurrentUser(String email);

    @GET("/users/{userId}/friends")
    Call<List<user>> getFriends(@Path("userId")String userId);

    @POST("/users")
    void newUser(String email, String password);
}
