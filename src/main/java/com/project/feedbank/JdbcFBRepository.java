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

import com.project.feedbank.DBEntities.*;

@Repository
public class JdbcFBRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public int validateCredentials(String username, String password) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withFunctionName("validate_login");
        int userId = jdbcCall.executeFunction(Integer.class, username, password);
        return userId;
    }
    public String getUserName(int uid) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withFunctionName("get_username");
        String username = jdbcCall.executeFunction(String.class, uid);
        return username;    }
    public int createUser(String fname, String lname, String username, String password) {

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withFunctionName("create_user");
        int userId = jdbcCall.executeFunction(Integer.class, fname, lname, username, password);
        return userId;
    }

    public String createTemplate(int userId, String templateName, String[] questions, String[] qTypes) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withFunctionName("create_template");
        jdbcCall.executeFunction(void.class, userId, templateName, questions, qTypes, questions);
        return "";
    }

    

    public String createEvent(int userId, String eventname, int templateId, boolean allowAnon) {
        String password = generatePassword();
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withFunctionName("create_event");
        jdbcCall.executeFunction(void.class, eventname ,password  ,templateId ,allowAnon ,5 ,allowAnon ,userId );
        return "";

    }

    public String createSession(int eventid , int tempid ,String shname ,Date shstartdate,Date shenddate ) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withFunctionName("create_session");
        jdbcCall.executeFunction(void.class, eventid ,  tempid , shname , shstartdate, shenddate);
        System.out.println("Session created");
        return "";

    }

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

    public ArrayList<Template> getTemplates(int userId){
        /*PreparedStatement statement = null;
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
        return templates;*/
        return (ArrayList<Template> )jdbcTemplate.query(
            "SELECT * FROM get_templates("+userId+")",
            (results, rowNum) ->
                    new Template(results.getInt(1),results.getString(2),results.getBoolean(3)));
    }
    public ArrayList<Questions> getQuestions(int templateId){
       /* PreparedStatement statement = null;
		ResultSet results = null;
        ArrayList<Questions> questions = new ArrayList<>();
        try {
            Connection conn = jdbcTemplate.getDataSource().getConnection();
            statement =  conn.prepareStatement("SELECT * FROM get_questions(?)");
            statement.setInt(1,templateId);
			results = statement.executeQuery();

			while (results.next()) {
				Questions question = new Questions(results.getInt(1), results.getString(2),results.getString(3));
                questions.add(question);

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
        return questions;*/
        return (ArrayList<Questions> )jdbcTemplate.query(
            "SELECT * FROM get_questions("+templateId+")",
            (results, rowNum) ->
                    new Questions(results.getInt(1), results.getString(2),results.getString(3)));
    }
    public SessionInfo getSessionInfo(int evid, int sshid){
        /*PreparedStatement statement = null;
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
        return sesh;*/
        ArrayList<SessionInfo> list = (ArrayList<SessionInfo>)jdbcTemplate.query(
            "SELECT * FROM get_session_info("+evid+","+sshid+")",
            (results, rowNum) ->
                    new SessionInfo( results.getString(1),results.getString(2) ,
                    results.getString(3),results.getTimestamp(4), results.getInt(5)

                    ));
        return list.get(0);
    }
    
    public List<Event> getEvents(int userId){
        /*PreparedStatement statement = null;
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
        return events;*/

        return (List<Event> )jdbcTemplate.query(
            "SELECT * FROM get_events("+userId+")",
            (results, rowNum) ->
                    new Event(results.getInt(1), results.getString(2),results.getString(3) ,results.getString(4),
                    results.getBoolean(5), results.getInt(6) ,results.getBoolean(7),results.getString(8),
                    results.getInt(9),results.getTimestamp(10)

                    ));
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
