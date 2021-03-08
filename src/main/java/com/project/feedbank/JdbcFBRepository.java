package com.project.feedbank;

import java.security.SecureRandom;
import java.sql.Array;
import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.naming.event.EventDirContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcFBRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Incomplete
    // queries db to check if login info valid
    public int validateCredentials(String username, String password) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withFunctionName("validate_login");
        int userId = jdbcCall.executeFunction(Integer.class, username, password);
        return userId;
    }

    // Incomplete
    // attempts to new usert db
    // if successful returns empty string, otherwise error message
    public int createUser(String fname, String lname, String username, String password) {
        // jdbcTemplate.update("INSERT INTO
        // USERS(firstname,lastname,username,userpassword) VALUES
        // (?,?,?,?);",fname,lname,username,password);
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withFunctionName("create_user");
        int userId = jdbcCall.executeFunction(Integer.class, fname, lname, username, password);
        return userId;
    }

    public String createTemplate(int userId, String templateName, String[] questions, String[] qTypes) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withFunctionName("create_template");
        jdbcCall.executeFunction(void.class, userId, templateName, questions, qTypes, questions);
        System.out.println("template created");
        return "";
    }

    

    public String createEvent(int userId, String eventname, int templateId, boolean allowAnon) {
        String password = generatePassword();
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withFunctionName("create_event");
        jdbcCall.executeFunction(void.class, eventname ,password  ,templateId ,allowAnon ,5 ,allowAnon ,userId );
        System.out.println("event created");
        return "";

    }

    public String createSession(int eventid , int tempid ,String shname ,Date shstartdate,Date shenddate ) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withFunctionName("create_session");
        jdbcCall.executeFunction(void.class, eventid ,  tempid , shname , shstartdate, shenddate);
        System.out.println("session created");
        return "";

    }

    public int joinEvent(String eventCode,int uid){
        int eventId = -1;
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withFunctionName("validate_code");
        eventId = jdbcCall.executeFunction(int.class, eventCode);
        System.out.println("joining");
        if(eventId!=-1){
            jdbcCall = new SimpleJdbcCall(jdbcTemplate).withFunctionName("assign_user");
            jdbcCall.executeFunction(void.class, uid, eventId);
            System.out.println("joined");

        }
        return eventId;
    }

    public ArrayList<Template> getTemplates(int userId){
        PreparedStatement statement = null;
		ResultSet results = null;
        ArrayList<Template> templates = new ArrayList<>();
        try {
            Connection conn = jdbcTemplate.getDataSource().getConnection();
            statement =  conn.prepareStatement("SELECT * FROM get_templates(?)");
            statement.setInt(1,userId);
			results = statement.executeQuery();
			while (results.next()) {
				Template template = new Template(results.getInt(1),results.getString(2),results.getBoolean(3));
                templates.add(template);
			}
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                    if (statement != null) {
                            statement.close();
                    }
            } catch (SQLException e) {
                    System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            }
    }
        return templates;
    }

    public String[] getQuestions(int templateId){
        String[] questions = null;
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withFunctionName("get_session_info");
        Array arryQuestions = jdbcCall.executeFunction(Array.class, templateId);
        try {
            questions = (String[]) arryQuestions.getArray();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return questions;

    }

    public SessionInfo getSessionInfo(int evid, int sshid){
        PreparedStatement statement = null;
		ResultSet results = null;
        SessionInfo sesh =null;
        try {
            Connection conn = jdbcTemplate.getDataSource().getConnection();
            statement =  conn.prepareStatement("SELECT * FROM get_session_info(?,?)");
            statement.setInt(1,evid);
            statement.setInt(2,sshid);

			results = statement.executeQuery();

			while (results.next()) {
			    sesh = new SessionInfo( results.getString(1),results.getString(2) ,
                results.getString(3),results.getTimestamp(4), results.getInt(5));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                    if (statement != null) {
                            statement.close();
                    }
            } catch (SQLException e) {
                    System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            }
    }
        return sesh;
    }
    public ArrayList<Event> getEvents(int userId){
        PreparedStatement statement = null;
		ResultSet results = null;
        ArrayList<Event> events = new ArrayList<>();
        try {
            Connection conn = jdbcTemplate.getDataSource().getConnection();
            statement =  conn.prepareStatement("SELECT * FROM get_events(?)");
            statement.setInt(1,userId);
			results = statement.executeQuery();

			while (results.next()) {
				Event event = new Event(results.getInt(1), results.getString(2),results.getString(3) ,results.getString(4),
                 results.getBoolean(5), results.getInt(6) ,results.getBoolean(7),results.getString(8),
                 results.getInt(9),results.getTimestamp(10));
                 events.add(event);

			}
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                    if (statement != null) {
                            statement.close();
                    }
            } catch (SQLException e) {
                    System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            }
    }
        return events;
    }
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
