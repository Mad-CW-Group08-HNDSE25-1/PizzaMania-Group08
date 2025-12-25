package com.androidapp.pizzamania;

public class ChatMsgAdapter {

    String message;
    private boolean isUser;

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUser(boolean user) {
        isUser = user;
    }

    public ChatMsgAdapter(boolean isUser, String message) {
        this.isUser = isUser;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public boolean isUser() {
        return isUser;
    }
}
