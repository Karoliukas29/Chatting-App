package com.example.chattingapp;

import java.util.Date;

public class LoginUser {
    public String userId;
    public String userName;
    public boolean isUserLogin;
    public  String userLoginTime;
    public static LoginUser loginUser = new LoginUser("", false,"");



    private LoginUser(String userName, boolean isUserLogin, String userId) {
        this.userName = userName;
        this.isUserLogin = isUserLogin;
        this.userId = userId;


        Date date = new Date();
        userLoginTime = String.valueOf(date.getTime());
    }


    public String getUserName(){
        return  userName;
    }
    public boolean isUserLogin(){
        return isUserLogin;
    }


}
