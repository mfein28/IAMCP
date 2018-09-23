package com.mattfein.iamcp;

import java.io.Serializable;

public class LinkedInProfile implements Serializable {

    private String emailAddress;
    private String firstName;
    private String lastName;
    private String picURL;

    public LinkedInProfile(String firstName, String lastName, String emailAddress, String picURL){
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.picURL = picURL;
}


    public String getEmailAddress(){
        return this.emailAddress;
    }

    public String getFirstName(){
        return this.firstName;
    }

    public String getLastName(){
        return this.lastName;
    }

    public String getPicURL(){
        return this.picURL;
    }

    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    public void setEmailAddress(String emailAddress){
        this.emailAddress = emailAddress;
    }

    public void setPicURL(String picURL){
        this.picURL = picURL;
    }

}
