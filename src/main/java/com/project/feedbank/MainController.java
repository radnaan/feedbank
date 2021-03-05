package com.project.feedbank;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
	public String eventCreation(HttpSession httpSession,Model model) {
		if(!loggedin(httpSession)){
			return "redirect:login";
		}
		ArrayList<Template> templates = db.getTemplates((int)httpSession.getAttribute("user"));

		model.addAttribute("templates", templates);
		return "eventcreate";
	}

	@PostMapping("/eventcreate")
	public String createEvent(HttpSession httpSession,@RequestParam("eventname") String eventname,
	@RequestParam("templateid")int tempid,@RequestParam("anonymous") boolean allowanon) {
		if(!loggedin(httpSession)){
			return "redirect:login";
		}
		db.createEvent((int)httpSession.getAttribute("user"), eventname, tempid, allowanon);
		return "events";
	}


	@GetMapping("/createtemplate")
	public String templateCreation(HttpSession httpSession) {
		if(!loggedin(httpSession)){
			return "redirect:login";
		}
		return "createtemplate";
	}

	@PostMapping("/createtemplate")
	public String createTemplate(HttpSession httpSession,@RequestParam Map<String,String> allParams) {
		if(!loggedin(httpSession)){
			return "redirect:login";
		}
		String templateName = "";
		String[] questions = new String[allParams.size()-1];
		String[] qTypes = new String[allParams.size()-1];
		int idx = 0;
		for(Map.Entry<String, String> entry : allParams.entrySet()){
			if(entry.getKey().equals("templatename"))
				templateName = entry.getValue();
			else{
				questions[idx]=(entry.getValue());
				qTypes[idx]=("Text");
				idx+=1;
			}
		}
		db.createTemplate((int)httpSession.getAttribute("user"), templateName, questions, qTypes);

		return "redirect:events";
	}

	@GetMapping("/events")
	public String eventsLanding(HttpSession httpSession) {
		if(!loggedin(httpSession)){
			return "redirect:login";
		}
		return "events";
	}
	@GetMapping("/login")
	public String loginForm(HttpSession httpSession) {
		if(loggedin(httpSession)){
			return "redirect:events";
		}
		return "login";
	}

	@PostMapping("login")
	public String login(@RequestParam("username") String username,@RequestParam("password") String password,
	HttpSession httpSession) {
		int userId = db.validateCredentials(username,password);
		if(userId!=-1){
			httpSession.setAttribute("user", userId);
			return "redirect:events";
		}
		return "login";
	}

	@GetMapping("/signup")
	public String signup(HttpSession httpSession) {
		if(loggedin(httpSession)){
			return "redirect:events";
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
			return "redirect:events";
		}
		return "redirect:/signup";
	}

	@GetMapping("/signout")
	public String logout(HttpSession httpSession) {
		httpSession.removeAttribute("user");
		return "redirect:login";
	}

	@GetMapping("/anonymous_account")
	public String anonymous_account(HttpSession httpSession) {
		
		return "login";
	}

}
