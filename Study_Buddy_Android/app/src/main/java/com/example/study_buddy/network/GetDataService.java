package com.example.study_buddy.network;

import com.example.study_buddy.model.User;

import java.util.List;

import retrofit2.Call;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface GetDataService {
    /** Login related **/
    @FormUrlEncoded
    @POST("/login")
    Call<User> postLoginUser(
            @Field("email") String email,
            @Field("password") String password);

    @FormUrlEncoded
    @POST("/signup")
    Call<User> postSignupUser(
            @Field("firstName") String firstName,
            @Field("lastName") String lastName,
            @Field("email") String email,
            @Field("password") String password
    );



    /** User data related **/
    @GET("/User/{userId}/account") /****CHANGE THE PATH LATER****/
    Call<User> getCurrentUser(@Path("userId")String userId);

    @GET("/User/{userId}/friendlist")
    Call<List<User>> getFriends(@Path("userId")String userId);

    @GET("/User/{userId}/suggested-friends")
    Call<List<String>> getSuggestFriends(@Path("userId")String userId);

    @FormUrlEncoded
    @PUT("/User/{userId}/friendlist")
    Call<User> addFriend(@Path("userId")String userId, @Field("userId") String newFriendId);

    @POST("/users")
    void newUser(String email, String password);




}
