package com.project.feedbank;

import java.security.SecureRandom;
import java.sql.Types;
import java.util.Random;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcFBRepository {
    

    @Autowired
    private JdbcTemplate jdbcTemplate;

    //Incomplete
    //queries db to check if login info valid
    public int validateCredentials(String username,String password){
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withFunctionName("validate_login");
        int userId = jdbcCall.executeFunction(Integer.class,username,password);        
        return userId;
    }

    //Incomplete
    //attempts to new usert db
    //if successful returns empty string, otherwise error message
    public int createUser(String fname, String lname,String username, String password){
        //jdbcTemplate.update("INSERT INTO USERS(firstname,lastname,username,userpassword) VALUES  (?,?,?,?);",fname,lname,username,password);
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withFunctionName("create_user");
        int userId = jdbcCall.executeFunction(Integer.class, fname,lname,username,password);
        return userId;
    }

    public String createEvent(int userId,String eventname, int templateId,boolean allowAnon){
        String password = generatePassword();

        return "";
    }


    private String generatePassword () {
        Scanner S = new Scanner(System.in);
        System.out.println("Enter length");
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
        
    
        System.out.println(password.toString());
    
        S.close();
        return password.toString();
        }
}
