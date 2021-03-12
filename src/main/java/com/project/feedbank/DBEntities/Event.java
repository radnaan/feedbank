package com.project.feedbank.DBEntities;

import java.io.Serializable;
import java.sql.Timestamp;

public class Event implements Serializable {
    public int eventId;
    public String eventName;
    public String eventCode ;
    public String eventStatus;
    public boolean requiredLogin; 
    public int feedbackTime ; 
    public boolean allowAnon ;
    public String userRole ; 
    public int nextSeshid; 
    public String nextSeshdate;
    public Event(){
        eventId = 0;
        eventName = "";
    }


    public Event(int eventid, String eventname ,String eventcode ,String eventstatus , boolean requiredlogin 
    , int feedbacktime , boolean allowanon ,String userrole , int nextseshid , Timestamp nextseshdate ) 
    {
        eventId = eventid;
        eventName = eventname;
        eventCode = eventcode;
        eventStatus = eventstatus;
        requiredLogin= requiredlogin; 
        feedbackTime =  feedbacktime; 
        allowAnon  = allowanon;
        userRole  = userrole; 
        nextSeshid = nextseshid; 
        if(nextseshdate!=null){
            nextSeshdate = nextseshdate.toString();
        }
    }

    
    public int getEventid(){
        return eventId;
    }
    public String getEventname(){
        return eventName;
    }


}
