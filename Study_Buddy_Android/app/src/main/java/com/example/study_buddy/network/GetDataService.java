package com.example.study_buddy.network;

import com.example.study_buddy.model.user;

import java.util.List;

import retrofit2.Call;

import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface GetDataService {
    @GET("/user/{userId}/account") /****CHANGE THE PATH LATER****/
    Call<user> getCurrentUser(@Path("userId")String userId);

    @GET("/user/{userId}/friendlist")
    Call<List<user>> getFriends(@Path("userId")String userId);

    @GET("/user/{userId}/suggested-friends")
    Call<List<String>> getSuggestFriends(@Path("userId")String userId);

    @FormUrlEncoded
    @PUT("/user/{userId}/friendlist")
    Call<user> addFriend(@Path("userId")String userId, @Field("userId") String newFriendId);

    @POST("/users")
    void newUser(String email, String password);
}
