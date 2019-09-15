package com.easy.easychat.entity;

public class User {
    private String mobileNo;
    private String userName;
    private String email;
    private boolean status;

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "User{" +
                "mobileNo='" + mobileNo + '\'' +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                '}';
    }
}
