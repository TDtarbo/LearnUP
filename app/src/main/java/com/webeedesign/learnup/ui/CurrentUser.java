package com.webeedesign.learnup.ui;

public class CurrentUser {
    private static CurrentUser instance;
    private int userId;

    private String userName;

    public CurrentUser() {
        // Private constructor to prevent instantiation outside the class
    }

    public static CurrentUser getInstance() {
        if (instance == null) {
            instance = new CurrentUser();
        }
        return instance;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserId() {
        return userId;
    }

    public  String getUserName(){return userName;}
}


