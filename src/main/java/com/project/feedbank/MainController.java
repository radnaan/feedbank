package com.project.feedbank;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

	public final static String tempu = "admin";
	public static final String tempp = "password";

	@Autowired
	private JdbcFBRepository db;
	
	public boolean loggedin(HttpSession httpSession){
			String user = (String)httpSession.getAttribute("user");
			if(user!=null && user !="loggedout"){
				return true;
			}
			return false;
	}

	@GetMapping("/events")
	public String login(HttpSession httpSession) {
		if(!loggedin(httpSession)){
			return "login";
		}
		return "events";
	}


	@GetMapping("/login")
	public String loginForm(HttpSession httpSession) {
		if(loggedin(httpSession)){
			return "events";
		}
		return "login";
	}

	@PostMapping("login")
	public String login(@RequestParam("username") String username,@RequestParam("password") String password,
	HttpSession httpSession) {
		if(db.validateCredentials(username,password)||(username.equals(tempu) && password.equals(tempp))){
			httpSession.setAttribute("user", username);
			return "events";
		}
		return "login";
	}

	@GetMapping("/signup")
	public String signup(HttpSession httpSession) {
		if(loggedin(httpSession)){
			return "events";
		}
		return "signup";
	}

	@PostMapping("/signup")
	public String register(@RequestParam("username") String username,@RequestParam("password") String password,
	@RequestParam("password") String password2,HttpSession httpSession) {
		if(loggedin(httpSession)){
			return "events";
		}
		if(password.equals(password2)){
			db.createUser(username, password);
			httpSession.setAttribute("user", username);
			System.out.println("Created user "+username);
			return "events";
		}
		return "/signup";
	}

	@GetMapping("/signout")
	public String logout(HttpSession httpSession) {
		httpSession.removeAttribute("user");
		return "login";
	}

	@GetMapping("/anonymous_account")
	public String anonymous_account(HttpSession httpSession) {
		
		return "login";
	}

}
