package com.example.customer.utils;

import android.app.Activity;
import android.content.SharedPreferences;

import com.example.customer.Config.Config;

import vou.proto.RpcLoginUser;
import vou.proto.UserOuterClass;

public class Utils {
    public static String checkPasswordComplexity(String password) {
        if (password == null || password.isEmpty()) {
            return "Password cannot be empty.";
        }

        if (password.length() < 8) {
            return "Password must be at least 8 characters long.";
        }

        if (!password.matches(".*[a-z].*")) {
            return "Password must contain at least one lowercase letter.";
        }

        if (!password.matches(".*[A-Z].*")) {
            return "Password must contain at least one uppercase letter.";
        }

        if (!password.matches(".*\\d.*")) {
            return "Password must contain at least one number.";
        }

        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            return "Password must contain at least one special character (e.g., !@#$%^&*).";
        }

        return "";
    }

    public static String checkEmail(String email) {
        if (email == null || email.isEmpty()) {
            return "Email cannot be empty.";
        }

        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

        if (!email.matches(emailRegex)) {
            return "Invalid email format.";
        }

        return "";
    }

    public static String checkFullName(String fullName){
        if (fullName == null || fullName.trim().isEmpty()) {
            return "Full name cannot be empty.";
        }
        fullName = fullName.trim();

        if (fullName.length() < 3) {
            return "Full name must be at least 3 characters long.";
        }
        if (fullName.length() > 50) {
            return "Full name cannot be longer than 50 characters.";
        }

        String regex = "^[a-zA-Z\\s-]+$";
        if (!fullName.matches(regex)) {
            return "Full name can only contain letters, spaces, and hyphens.";
        }

        if (fullName.contains("  ")) {
            return "Full name cannot contain multiple consecutive spaces.";
        }

        return "";
    }

    public static String checkUserName(String userName){
        if (userName == null || userName.isEmpty()) {
            return "User name cannot be empty.";
        }

        if (userName.length() < 3) {
            return "User name must be at least 3 characters long.";
        }
        if (userName.length() > 16) {
            return "User name cannot be longer than 16 characters.";
        }

        if (userName.startsWith("_")) {
            return "User name cannot start with an underscore.";
        }
        if (userName.endsWith("_")) {
            return "User name cannot end with an underscore.";
        }
        String regex = "^[a-zA-Z0-9_]+$";
        if (!userName.matches(regex)) {
            return "User name can only contain letters, numbers, and underscores.";
        }

        return "";
    }

    public static void saveUserInfo(Activity activity,RpcLoginUser.LoginUserResponse response){
        SharedPreferences userInfo=activity.getSharedPreferences(Config.userInfo, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor=userInfo.edit();

        editor.putBoolean("isLoggedIn",true);

        UserOuterClass.User user=response.getUser();
        editor.putString("username",user.getUsername());
        editor.putString("full_name",user.getFullName());
        editor.putString("email",user.getEmail());
        editor.putString("role",user.getRole());

        editor.putString("session_id",response.getSessionId());

        editor.putString("access_token",response.getAccessToken());
        editor.putLong("access_token_expires_at_seconds",response.getAccessTokenExpiresAt().getSeconds());
        editor.putInt("access_token_expires_at_nanos",response.getAccessTokenExpiresAt().getNanos());

        editor.putString("refresh_token",response.getRefreshToken());
        editor.putLong("refresh_token_expires_at_seconds",response.getRefreshTokenExpiresAt().getSeconds());
        editor.putInt("refresh_token_expires_at_nanos",response.getRefreshTokenExpiresAt().getNanos());

        editor.apply();
    }

    public static void removeUserInfo(Activity activity){
        SharedPreferences userInfo=activity.getSharedPreferences(Config.userInfo, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor=userInfo.edit();
        editor.clear();
        editor.apply();
    }

    public static String getUserName(Activity activity){
        SharedPreferences userInfo=activity.getSharedPreferences(Config.userInfo, Activity.MODE_PRIVATE);
        return userInfo.getString("username","");
    }

    public static String getFullName(Activity activity){
        SharedPreferences userInfo=activity.getSharedPreferences(Config.userInfo, Activity.MODE_PRIVATE);
        return userInfo.getString("full_name","");
    }

    public static String getEmail(Activity activity){
        SharedPreferences userInfo=activity.getSharedPreferences(Config.userInfo, Activity.MODE_PRIVATE);
        return userInfo.getString("email","");
    }

    public static String getRole(Activity activity){
        SharedPreferences userInfo=activity.getSharedPreferences(Config.userInfo, Activity.MODE_PRIVATE);
        return userInfo.getString("role","");
    }
}

