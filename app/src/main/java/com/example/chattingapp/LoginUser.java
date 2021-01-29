package com.example.chattingapp;

import java.util.Date;

public class LoginUser {
    public String userName;
    public boolean isUserLogin;
    public  String userLoginTime;

    public LoginUser(String userName, boolean isUserLogin) {
        this.userName = userName;
        this.isUserLogin = isUserLogin;


        Date date = new Date();
       // SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        userLoginTime = String.valueOf(date.getTime());
    }


    public String getUserName(){
        return  userName;
    }
    public boolean isUserLogin(){
        return isUserLogin;
    }


}
