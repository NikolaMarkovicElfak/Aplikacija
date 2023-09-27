package com.example.meet;

public class UserJoin {
    private String userName;
    private String eventKey;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public String getUserName() {
        return userName;
    }

    public String getEventKey() {
        return eventKey;
    }

    public UserJoin()
    {

    }

    public UserJoin(String userName, String eventKey) {
        this.userName = userName;
        this.eventKey = eventKey;
    }
}
