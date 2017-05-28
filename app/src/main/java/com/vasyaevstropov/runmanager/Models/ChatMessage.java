package com.vasyaevstropov.runmanager.Models;

/**
 * Created by Вася on 28.05.2017.
 */

public class ChatMessage {

    private String messageText;
    private String userName;
    private String messageTime;

    public ChatMessage(String s, String email) {
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }
}
