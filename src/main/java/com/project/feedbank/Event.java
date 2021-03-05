package com.project.feedbank;

import java.io.Serializable;

public class Event implements Serializable {
    int eventId;
    String eventName;

    public Event(){
        eventId = 0;
        eventName = "";
    }


    public Event(int id, String name, boolean edit){
        eventId =  id;
        eventName = name;
    }

    
    public int getEventid(){
        return eventId;
    }
    public String getEventname(){
        return eventName;
    }


}
