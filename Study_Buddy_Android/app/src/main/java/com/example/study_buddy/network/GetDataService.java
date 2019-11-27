package com.example.study_buddy.network;

import com.example.study_buddy.model.AccessToken;
import com.example.study_buddy.model.Event;
import com.example.study_buddy.model.Group;
import com.example.study_buddy.model.User;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface GetDataService {
    /** Login related **/
    @FormUrlEncoded
    @POST("/user/login")
    Call<User> postLoginUser(
            @Field("email") String email,
            @Field("password") String password
    );


    @FormUrlEncoded
    @POST("/user/signup")
    Call<User> postSignupUser(
            @Field("firstName") String firstName,
            @Field("lastName") String lastName,
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("/user/google-calendar")
    Call<User> postAccessToken(
            @Header("Authorization") String jwt,
            @Field("access_token") String accessToken,
            @Field("scope") String scope,
            @Field("expires_in") int expiresIn,
            @Field("token_type") String tokenType,
            @Field("id_token") String idToken,
            @Field("refresh_token") String refreshToken,
            @Field("firstName") String firstName,
            @Field("lastName") String LastName,
            @Field("email")String email
    );

    @FormUrlEncoded
    @POST("token")
    Call<AccessToken> postGoogleAuthCode(
            @Field("code") String authorizationCode,
            @Field("client_id") String clientID,
            @Field("client_secret") String clientSecret,
            @Field("redirect_uri")  String redirectUri,
            @Field("grant_type") String grantType,
            @Field("scope") String scope
    );


    /** User data related **/
    @GET("/user/{userId}/account") /****CHANGE THE PATH LATER****/
    Call<User> getCurrentUser(
            @Header("Authorization") String jwt,
            @Path("userId")String userId
    );

    @GET("/user/all")
    Call<List<User>> getAllUser(
            @Header("Authorization") String jwt
    );

    @GET("/user/{userId}/friendlist")
    Call<List<User>> getFriends(
            @Header("Authorization") String jwt,
            @Path("userId")String userId
    );

    @GET("/user/{userId}/suggested-friends")
    Call<List<User>> getSuggestFriends(
            @Header("Authorization") String jwt,
            @Path("userId")String userId
    );

    @FormUrlEncoded
    @PUT("/user/add/friend")
    Call<User> addFriend(
            @Header("Authorization") String jwt,
            @Path("userId")String userId,
            @Field("userId") String newFriendId
    );
    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "/user/delete/friend", hasBody = true)
    Call<User> deleteFriend(
            @Header("Authorization") String jwt,
            @Field("userId") String userId,
            @Field("friendId") String friendId
    );

    @GET("/user/{userId}/event/suggested-meeting-users/{startTime}/{endTime}")
    Call<List<User>> getAvailableFriends(
            @Header("Authorization") String jwt,
            @Path("userId")String userId,
            @Path("startTime")Date startTime,
            @Path("endTime")Date endTime
    );

    @FormUrlEncoded
    @POST("/user/event/add")
    Call<User> postMeetingEventToUser(
            @Header("Authorization") String jwt,
            @Field("userId") String userId,
            @Field("eventId") String eventId
    );

    // set the user's location
    @FormUrlEncoded
    @PUT("/user/location")
    Call<User> putUserLocation(
            @Header("Authorization") String jwt,
            @Field("userId") String userId,
            @Field("longitude") double longitude,
            @Field("latitude") double latitude
    );

    // set the user's notification token
    @FormUrlEncoded
    @PUT("/user/notification-token")
    Call<User> putDeviceToken(
            @Header("Authorization") String jwt,
            @Field("userId") String userId,
            @Field("token") String token
    );

    @GET("/user/{userId}/event/{date}")
    Call<List<Event>> getUserEvents(
            @Header("Authorization") String jwt,
            @Path("userId")String userId,
            @Path("date")Date date
    );


    /** Calendar data related **/
    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "/event/delete", hasBody = true)
    Call<Event> deleteEvent(
            @Header("Authorization") String jwt,
            @Field("eventId") String eventId
    );


    @FormUrlEncoded
    @PUT("/calendar/event/add")
    Call putEvent2Calendar(
            @Field("calendarId") String calendarId,
            @Field("eventId") String eventId
    );

    /** Event data related **/
    @FormUrlEncoded
    @POST("/event/create/event")
    Call<Event> postNewEvent(
            @Header("Authorization") String jwt,
            @Field("eventName") String eventName,
            @Field("eventDescription") String eventDescription,
            @Field("startTime") Date startTime,
            @Field("endTime") Date endTime,
            @Field("repeatType") String repeatType,
            @Field("ownerId") String ownerId
    );


    @FormUrlEncoded
    @POST("/event/create/meeting")
    Call<Event> postNewMeeting(
            @Header("Authorization") String jwt,
            @Field("eventName") String eventName,
        @Field("eventDescription") String eventDescription,
        @Field("startTime") Date startTime,
        @Field("endTime") Date endTime,
        @Field("ownerId") String ownerId,
        @Field("userList") List<String> userIdList,
        @Field("repeatType") String repeatType
    );

    @FormUrlEncoded
    @POST("/event/notify/meeting/invite")
    Call<Event> notifyNewMeeting(
            @Header("Authorization") String jwt,
            @Field("userId") String userId,
            @Field("eventId") String eventId
    );

    @FormUrlEncoded
    @POST("/event/notify/meeting/accept")
    Call<Event> notifyAcceptMeeting(
            @Field("userId") String userId,
            @Field("eventId") String eventId
    );

    @FormUrlEncoded
    @POST("/event/notify/meeting/reject")
    Call<Event> notifyRejectMeeting(
            @Field("userId") String userId,
            @Field("eventId") String eventId
    );

    /** Group date related**/
    @GET("/group/{groupId}")
    Call<Group> getGroup(
            @Header("Authorization") String jwt,
            @Path("groupId") String groupId
    );


    @FormUrlEncoded
    @POST("/group/create")
    Call<Group> createGroup(
            @Header("Authorization") String jwt,
            @Field("groupName") String groupName,
            @Field("groupDescription") String groupDescription
    );

    @FormUrlEncoded
    @PUT("/user/add/group")
    Call<Group> addGroup(
            @Header("Authorization") String jwt,
            @Field("userId") String userId,
            @Field("groupId") String groupId
    );

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "/group/delete/user", hasBody = true)
    Call<Group> deleteGroup(
            @Header("Authorization") String jwt,
            @Field("userId") String userId,
            @Field("groupId") String groupId
    );

}
