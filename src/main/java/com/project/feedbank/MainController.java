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

	@Autowired
	private JdbcFBRepository db;
	
	public boolean loggedin(HttpSession httpSession){
			Integer userId = (Integer)httpSession.getAttribute("user");
			if(userId!=null && userId !=-1){
				return true;
			}
			return false;
	}


	@GetMapping("/eventcreate")
	public String eventCreation(HttpSession httpSession) {
		if(!loggedin(httpSession)){
			return "login";
		}
		return "eventcreate";
	}

	@PostMapping("/eventcreate")
	public String creatEvent(HttpSession httpSession) {
		if(!loggedin(httpSession)){
			return "login";
		}
		return "eventcreate";
	}

	@GetMapping("/events")
	public String login(HttpSession httpSession,@RequestParam("eventname") String username,
	@RequestParam("templateid")String tempid,@RequestParam("anonymous") boolean allowanon) {
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
		int userId = db.validateCredentials(username,password);
		if(userId!=-1){
			httpSession.setAttribute("user", userId);
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
	public String register(@RequestParam("firstname") String fname,@RequestParam("lastname") String lname,@RequestParam("username") String username,@RequestParam("password") String password,
	@RequestParam("password") String password2,HttpSession httpSession) {
		if(loggedin(httpSession)){
			return "events";
		}
		if(password.equals(password2)){
			int userId = db.createUser(fname,lname,username, password);
			httpSession.setAttribute("user", userId);
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
