package com.ynr.taximedriver.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SignupModel {

    @Expose
    @SerializedName("message")
    private String message;
    @SerializedName("details")
    private String details;
    @SerializedName("token")
    private String token;
    @SerializedName("user")
    private User user;
    @SerializedName("status")
    private String status;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public static class User {

        @Expose
        @SerializedName("_id")
        private String id;
        @SerializedName("name")
        private String name;
        @SerializedName("userPlatform")
        private String userPlatform;
        @SerializedName("userProfilePic")
        private String userProfilePic;
        @SerializedName("contactNumber")
        private String contactNumber;
        @SerializedName("gender")
        private String gender;
        @SerializedName("birthday")
        private String birthday;
        @SerializedName("email")
        private String email;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUserPlatform() {
            return userPlatform;
        }

        public void setUserPlatform(String userPlatform) {
            this.userPlatform = userPlatform;
        }

        public String getUserProfilePic() {
            return userProfilePic;
        }

        public void setUserProfilePic(String userProfilePic) {
            this.userProfilePic = userProfilePic;
        }

        public String getContactNumber() {
            return contactNumber;
        }

        public void setContactNumber(String contactNumber) {
            this.contactNumber = contactNumber;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
