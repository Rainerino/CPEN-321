package com.example.study_buddy.model;

public class user {
    private String id;
    private String username;
    private String profile_img;

   public user(String id, String username, String profile_img){
       this.id = id;
       this.username = username;
       this.profile_img = profile_img;
   }

   public String getId(){
       return id;
   }

   public String getUsername(){
       return username;
   }

   public String getProfile_img(){
       return profile_img;
   }




}
