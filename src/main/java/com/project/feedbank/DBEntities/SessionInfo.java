package com.project.feedbank.DBEntities;

import java.io.Serializable;
import java.sql.Timestamp;

public class SessionInfo implements Serializable {
    public String eventName;
    public String eventCode ;
    public String sessionName;
    public int templateId;
    public String sessionEndDate;



    public SessionInfo(String eventname ,String eventcode, String sessionname, Timestamp sessionenddate,int tempid ) 
    {
        eventName = eventname;
        eventCode = eventcode;
        sessionName = sessionname;
        sessionEndDate= sessionenddate.toString(); 
        templateId = tempid;
    }

    
    public String getEventname(){
        return eventName;
    }


}
