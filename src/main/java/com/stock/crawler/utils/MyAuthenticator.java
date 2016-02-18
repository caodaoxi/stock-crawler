package com.stock.crawler.utils;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * Created by caodaoxi on 2015/12/17.
 */
public class MyAuthenticator extends Authenticator {
    String userName="";
    String password="";
    public MyAuthenticator(){

    }
    public MyAuthenticator(String userName,String password){
        this.userName=userName;
        this.password=password;
    }
    protected PasswordAuthentication getPasswordAuthentication(){
        return new PasswordAuthentication(userName, password);
    }
}
