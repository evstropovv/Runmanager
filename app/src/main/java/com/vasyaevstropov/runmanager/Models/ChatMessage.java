package com.vasyaevstropov.runmanager.Models;


import java.util.Date;

public class ChatMessage {

    private String messageText;
    private String userName;
    private Long messageTime;
    public ChatMessage(){

    }

    public ChatMessage(String messageText, String email) {
        this.messageText = messageText;
        this.userName = email;
        messageTime = new Date().getTime();
    }

    public String getMessageText() {
        return messageText;
    }

    public String getUserName() {
        return userName;
    }

    public Long getMessageTime() {
        return messageTime;
    }

}
