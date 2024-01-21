package com.ynr.taximedriver.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginModel {
    @SerializedName("mobile")
    private String mobileNumber;
    @SerializedName("pin")
    private int pin;
    @SerializedName("message")
    private String message;
    @SerializedName("details")
    private String details;
    @SerializedName("content")
    private Content content;
    @SerializedName("token")
    private String token;
    @SerializedName("user")
    private User user;

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

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

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
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

    public static class User {
        @Expose
        @SerializedName("_id")
        private String id;
        @SerializedName("otpTime")
        private String otpTime;
        @SerializedName("otpPin")
        private String otpPin;
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

        public String getOtpTime() {
            return otpTime;
        }

        public void setOtpTime(String otpTime) {
            this.otpTime = otpTime;
        }

        public String getOtpPin() {
            return otpPin;
        }

        public void setOtpPin(String otpPin) {
            this.otpPin = otpPin;
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

    public static class Content {
        @SerializedName("id")
        private String id;
        @SerializedName("firstname")
        private String firstName;
        @SerializedName("lastname")
        private String lastName;
        @SerializedName("driverPic")
        private String profileImage;
        @SerializedName("birthday")
        @Expose
        private String birthday;
        @SerializedName("gender")
        private String gender;
        @SerializedName("nic")
        private String nic;
        @SerializedName("address")
        @Expose
        private Address address;
        @SerializedName("contactNo")
        @Expose
        private String contactNo;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("accNumber")
        @Expose
        private String accNumber;
        //        @SerializedName("rate")
//        private int rate;
        @SerializedName("driverCode")
        private String driverCode;
        @SerializedName("companyCode")
        private String companyCode;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getProfileImage() {
            return profileImage;
        }

        public void setProfileImage(String profileImage) {
            this.profileImage = profileImage;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public  String getGender() { return gender; }

        public void setGender(String gender) { this.gender = gender; }

        public  String getNic() { return nic; }

        public void setNic(String nic) { this.nic = nic; }

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public String getContactNo() {
            return contactNo;
        }

        public void setContactNo(String contactNo) {
            this.contactNo = contactNo;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

//        public int getRate() { return rate; }
//
//        public void setRate(int rate) { this.rate = rate; }

        public String getDriverCode() {
            return driverCode;
        }

        public void setDriverCode(String driverCode) {
            this.driverCode = driverCode;
        }

        public String getCompanyCode() {
            return companyCode;
        }

        public void setCompanyCode(String companyCode) {
            this.companyCode = companyCode;
        }

        public String getAccNumber() {
            return accNumber;
        }

        public void setAccNumber(String accNumber) {
            this.accNumber = accNumber;
        }

        /*public String getAccNumber() {
            return accNumber;
        }

        public void setAccNumber(String accNumber) {
            this.accNumber = accNumber;
        }*/

        public static class Address {
            @SerializedName("address")
            @Expose
            private String address;
            @SerializedName("street")
            @Expose
            private String street;
            @SerializedName("city")
            @Expose
            private String city;
            @SerializedName("zipcode")
            @Expose
            private String zipcode;
            @SerializedName("country")
            @Expose
            private String country;
            @SerializedName("district")
            private String district;
            @SerializedName("province")
            private String province;

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getStreet() {
                return street;
            }

            public void setStreet(String street) {
                this.street = street;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getZipcode() {
                return zipcode;
            }

            public void setZipcode(String zipcode) {
                this.zipcode = zipcode;
            }

            public String getCountry() {
                return country;
            }

            public void setCountry(String country) {
                this.country = country;
            }

            public String getDistrict() { return district; }

            public void setDistrict(String district) { this.district = district; }

            public String getProvince() { return province; }

            public void setProvince(String province) { this.province = province; }
        }
    }
}
