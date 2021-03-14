package com.project.feedbank;

import java.security.SecureRandom;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import com.project.feedbank.DBEntities.*;

@Repository
public class JdbcFBRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    /**
     * Queries database to determine if given username and passsword are correct for a user.
     * @param username
     * @param password
     * @return user id if credentials valid, otherwise -1
     */
    public int validateCredentials(String username, String password) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withFunctionName("validate_login");
        int userId = jdbcCall.executeFunction(Integer.class, username, password);
        return userId;
    }

    /**
     * Queries the database to get the username associated with given userId
     * @param uid
     * @return username
     */
    public String getUserName(int uid) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withFunctionName("get_username");
        String username = jdbcCall.executeFunction(String.class, uid);
        return username;    }
    
    /**
     * Creates a new user using given user details.
     * @param fname
     * @param lname
     * @param username
     * @param password
     * @return id of new user
     */
    public int createUser(String fname, String lname, String username, String password) {

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withFunctionName("create_user");
        int userId = jdbcCall.executeFunction(Integer.class, fname, lname, username, password);
        return userId;
    }

    /**
     * Creates a new template using given template details and questions.
     * @param userId
     * @param templateName
     * @param questions
     * @param qTypes
     * @return
     */
    public String createTemplate(int userId, String templateName, String[] questions, String[] qTypes) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withFunctionName("create_template");
        jdbcCall.executeFunction(void.class, userId, templateName, questions, qTypes, questions);
        return "";
    }

    
    /**
     * Creates a new event using given event details.
     * @param userId
     * @param eventname
     * @param templateId
     * @param allowAnon
     * @return
     */
    public String createEvent(int userId, String eventname, int templateId, boolean allowAnon) {
        String password = generatePassword();
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withFunctionName("create_event");
        jdbcCall.executeFunction(void.class, eventname ,password  ,templateId ,allowAnon ,5 ,allowAnon ,userId );
        return "";

    }

    /**
     * Creates a new session using given session details.
     * @param eventid
     * @param tempid
     * @param shname
     * @param shstartdate
     * @param shenddate
     * @return
     */
    public String createSession(int eventid , int tempid ,String shname ,Date shstartdate,Date shenddate ) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withFunctionName("create_session");
        jdbcCall.executeFunction(void.class, eventid ,  tempid , shname , shstartdate, shenddate);
        return "";

    }

    /**
     * Queries the database to get information regarding events the user is assigned to
     * @param eventCode
     * @param uid
     * @return
     */
    public int joinEvent(String eventCode,int uid){
        int eventId = -1;
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withFunctionName("validate_code");
        eventId = jdbcCall.executeFunction(Integer.class, eventCode);
        if(eventId!=-1){
            jdbcCall = new SimpleJdbcCall(jdbcTemplate).withFunctionName("assign_user");
            jdbcCall.executeFunction(void.class, uid, eventId);

        }
        return eventId;
    }

    /**
     * Queries the database to get information regarding templates the user is assigned to
     * @param userId
     * @return
     */
    public ArrayList<Template> getTemplates(int userId){
        return (ArrayList<Template> )jdbcTemplate.query(
            "SELECT * FROM get_templates("+userId+")",
            (results, rowNum) ->
                    new Template(results.getInt(1),results.getString(2),results.getBoolean(3)));
    }

    /**
     * Queries the database to get information regarding questions of the given template
     * @param templateId
     * @return
     */
    public ArrayList<Questions> getQuestions(int templateId){
        return (ArrayList<Questions> )jdbcTemplate.query(
            "SELECT * FROM get_questions("+templateId+")",
            (results, rowNum) ->
                    new Questions(results.getInt(1), results.getString(2),results.getString(3)));
    }
    /**
     * Queries the database to get information regarding the given session
     * @param evid event id
     * @param sshid session id
     * @return
     */
    public SessionInfo getSessionInfo(int evid, int sshid){
        ArrayList<SessionInfo> list = (ArrayList<SessionInfo>)jdbcTemplate.query(
            "SELECT * FROM get_session_info("+evid+","+sshid+")",
            (results, rowNum) ->
                    new SessionInfo( results.getString(1),results.getString(2) ,
                    results.getString(3),results.getTimestamp(4), results.getInt(5)

                    ));
        return list.get(0);
    }
    
    /**
     * Queries the database to get information regarding events the user is assigned to
     * @param userId
     * @return list of events
     */
    public List<Event> getEvents(int userId){

        return (List<Event> )jdbcTemplate.query(
            "SELECT * FROM get_events("+userId+")",
            (results, rowNum) ->
                    new Event(results.getInt(1), results.getString(2),results.getString(3) ,results.getString(4),
                    results.getBoolean(5), results.getInt(6) ,results.getBoolean(7),results.getString(8),
                    results.getInt(9),results.getTimestamp(10)

                    ));
    }

    /**
     * Generates a random string to be used as a code
     * @return random string
     */
    private String generatePassword () {
        Scanner S = new Scanner(System.in);
        int length = 8;
    
        final char[] lowercase = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        final char[] uppercase = "ABCDEFGJKLMNPRSTUVWXYZ".toCharArray();
        final char[] numbers = "0123456789".toCharArray();
        final char[] symbols = "^$?!@#%&".toCharArray();
        final char[] allAllowed = "abcdefghijklmnopqrstuvwxyzABCDEFGJKLMNPRSTUVWXYZ0123456789^$?!@#%&".toCharArray();
    
        //Use cryptographically secure random number generator
        Random random = new SecureRandom();
    
        StringBuilder password = new StringBuilder();
    
        for (int i = 0; i < length-4; i++) {
            password.append(allAllowed[random.nextInt(allAllowed.length)]);
        }
    
        //inserts random chars in random posistions
        password.insert(random.nextInt(password.length()), lowercase[random.nextInt(lowercase.length)]);
        password.insert(random.nextInt(password.length()), uppercase[random.nextInt(uppercase.length)]);
        password.insert(random.nextInt(password.length()), numbers[random.nextInt(numbers.length)]);
        password.insert(random.nextInt(password.length()), symbols[random.nextInt(symbols.length)]);
        
    
    
        S.close();
        return password.toString();
        }


}
