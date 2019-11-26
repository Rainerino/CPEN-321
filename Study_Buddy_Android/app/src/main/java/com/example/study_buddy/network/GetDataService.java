package com.example.study_buddy.network;

import com.example.study_buddy.model.Event;
import com.example.study_buddy.model.Group;
import com.example.study_buddy.model.User;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface GetDataService {
    /** Login related **/
    @FormUrlEncoded
    @POST("/user/login")
    Call<User> postLoginUser(
            @Field("email") String email,
            @Field("password") String password);


    @FormUrlEncoded
    @POST("/user/signup")
    Call<User> postSignupUser(
            @Field("firstName") String firstName,
            @Field("lastName") String lastName,
            @Field("email") String email,
            @Field("password") String password
    );

    /** User data related **/
    @GET("/user/{userId}/account") /****CHANGE THE PATH LATER****/
    Call<User> getCurrentUser(@Path("userId")String userId);

    @GET("/user/all")
    Call<List<User>> getAllUser();

    @GET("/user/{userId}/friendlist")
    Call<List<User>> getFriends(@Path("userId")String userId);

    @GET("/user/{userId}/suggested-friends")
    Call<List<User>> getSuggestFriends(@Path("userId")String userId);

    @FormUrlEncoded
    @PUT("/user/{userId}/friendlist")
    Call<User> addFriend(
            @Path("userId")String userId,
            @Field("userId") String newFriendId);

    @FormUrlEncoded
    @POST("/user/event/add")
    Call<User> postMeetingEventToUser(
        @Field("userId") String userId,
        @Field("eventId") String eventId
    );
//    @FormUrlEncoded
//    @HTTP(method = "DELETE", path = "/event/delete", hasBody = true)
//    Call<Event> deleteEvent(@Field("eventId") String eventId);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "/user/delete/event/user", hasBody = true)
    Call<User> deleteUserFromEvent(
            @Field("userId") String userId,
            @Field("eventId") String eventId
    );

    // set the user's location
    @FormUrlEncoded
    @PUT("/user/location")
    Call<User> putUserLocation(
        @Field("userId") String userId,
        @Field("longitude") double longitude,
        @Field("latitude") double latitude
    );

    // set the user's notification token
    @FormUrlEncoded
    @PUT("/user/notification-token")
    Call<User> putDeviceToken(
            @Field("userId") String userId,
            @Field("token") String token
    );

    @GET("/user/{userId}/event/{date}")
    Call<List<Event>> getUserEvents(
            @Path("userId")String userId,
            @Path("date")Date date); // it might be easier with String since


    /** Calendar data related **/

    @GET("/calendar/{calendarId}/event/all")
    Call<List<Event>> getAllEvents(@Path("calendarId")String calendarId);

    @GET("/calendar/{calendarId}/event/today")
    Call<List<Event>> getTodaysEvents(@Path("calendarId")String calendarId);



    @FormUrlEncoded
    @PUT("/calendar/event/add")
    Call putEvent2Calendar(
            @Field("calendarId") String calendarId,
            @Field("eventId") String eventId);

    /** Event data related **/
    @FormUrlEncoded
    @POST("/event/create/event")
    Call<Event> postNewEvent(
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
    Call<Group> getGroup(@Path("groupId") String groupId);


}
