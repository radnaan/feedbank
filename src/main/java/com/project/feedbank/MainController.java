package com.project.feedbank;

import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

	@GetMapping("/feedback")
	public String attendeeSession(HttpSession httpSession,Model model,@RequestParam("eventid") int eventid,@RequestParam("sessionid") int sessionid) {
		if(!loggedin(httpSession)){
			return "redirect:login";
		}
		SessionInfo sessionInfo = db.getSessionInfo(eventid,sessionid);
		model.addAttribute("sessionInfo",sessionInfo);
		String[] questions = db.getQuestions(sessionInfo.templateId);
		model.addAttribute("questions",sessionInfo);

		return "feedback";
	}

	@GetMapping("/session")
	public String hostSession(HttpSession httpSession,Model model,@RequestParam("eventid") int eventid,@RequestParam("sessionid") int sessionid) {
		if(!loggedin(httpSession)){
			return "redirect:login";
		}
		SessionInfo sessionInfo = db.getSessionInfo(eventid,sessionid);
		model.addAttribute("sessionInfo",sessionInfo);
		return "session";
	}
	@GetMapping("/sessioncreate")
	public String sessionCreation(HttpSession httpSession,Model model,@RequestParam("eventid") int eventid) {
		if(!loggedin(httpSession)){
			return "redirect:login";
		}
		ArrayList<Template> templates = db.getTemplates((int)httpSession.getAttribute("user"));
		model.addAttribute("eventid",eventid);

		model.addAttribute("templates", templates);
		return "sessioncreate";
	}

	@PostMapping("/sessioncreate")
	public String createSession(HttpSession httpSession,Model model,@RequestParam("sessionname") String sessionname,
	@RequestParam("templateid")int tempid,@RequestParam("eventid") int eventid,@RequestParam("startdate") String startdate,@RequestParam("enddate") String  enddate) {
		if(!loggedin(httpSession)){
			return "redirect:login";
		}
		
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
		try {
			Date startDate = (Date)formatter.parse(startdate);
			Date endDate = (Date)formatter.parse(enddate); 
			db.createSession(eventid, tempid, sessionname, startDate, endDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		model.addAttribute("eventid",eventid);
		computeEvents(httpSession);

		return  "redirect:events";
	}

	@GetMapping("/error")
	public String error(HttpSession httpSession) {
		if(!loggedin(httpSession)){
			return "redirect:login";
		}
		return "redirect:events";
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
		computeEvents(httpSession);

		return "redirect:events";
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

	@GetMapping("/join")
	public String eventJoin(HttpSession httpSession) {

		return "join";
	}

	@PostMapping("/join")
	public String joinEvent(HttpSession httpSession,@RequestParam("code") String code) {
		int validCode = db.joinEvent(code,(int)httpSession.getAttribute("user"));
		if(validCode!=-1){
			computeEvents(httpSession);
		}
		return "redirect:events";
	}
	
	
	@GetMapping("/QRJoin")
	public String QRJoin(@RequestParam("code") String qrcode) {
			
		return "join";
	}

	//expensive function
	public void computeEvents(HttpSession httpSession){
		//ArrayList<Event> events = db.getEvents((int) httpSession.getAttribute("user"));
		//httpSession.setAttribute("events", events);
	}

	@GetMapping("/events")
	public String eventsLanding(HttpSession httpSession, Model model) {
		if(!loggedin(httpSession)){
			return "redirect:login";
		}
		//ArrayList<Event> events = (ArrayList<Event>) httpSession.getAttribute("events");
		ArrayList<Event> events = db.getEvents((int) httpSession.getAttribute("user"));

		model.addAttribute("events", events);
		for(Event e : events){
			//System.out.println(e.userRole);
			//System.out.println(e.eventStatus);
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
			computeEvents(httpSession);
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
			computeEvents(httpSession);
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
