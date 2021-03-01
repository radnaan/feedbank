package com.project.feedbank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcFBRepository {
    

    @Autowired
    private JdbcTemplate jdbcTemplate;

    //Incomplete
    //queries db to check if login info valid
    public boolean validateCredentials(String username,String password){
        int exists = jdbcTemplate.queryForObject("SELECT count(*) FROM users WHERE username=? AND userpassword = ?", new Object[] {username,password},Integer.class);
        if(exists>0){
            return true;
        }
        return false;
    }

    //Incomplete
    //attempts to new usert db
    //if successful returns empty string, otherwise error message
    public String createUser(String username, String password){
        jdbcTemplate.update("INSERT INTO USERS(firstname,lastname,username,userpassword) VALUES  (?,?,?,?);",username,username,username,password);
        return "";
    }

}
