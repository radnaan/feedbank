package com.project.feedbank;

import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.feedbank.DBEntities.*;
/**
 * This class is responsible for handling and redirecting http requests.
 * For example, to return the login page when a GET request for the page is recieved.
 */
@Controller
public class MainController  implements ErrorController  {

    private static final String error_redirect_path = "/error";


	@Autowired
	private JdbcFBRepository db;
	
	/**
	 * Determines whether the user is logged in by examining the http session.
	 * @param httpSession
	 * @return user has already logged in and established a session
	 */
	public boolean loggedin(HttpSession httpSession){
			Integer userId = (Integer)httpSession.getAttribute("user");
			if(userId!=null && userId !=-1){
				return true;
			}
			return false;
	}

	/**
	 * Generates feedback page for given session.
	 * Retrieves questions from template associated with session.
	 * @param httpSession
	 * @param model
	 * @param eventid
	 * @param sessionid
	 * @param anon whether the user joined as anonymous
	 * @return
	 */
	@GetMapping("/feedback")
	public String attendeeSession(HttpSession httpSession,Model model,@RequestParam("eventid") int eventid,
	@RequestParam("sessionid") int sessionid,@RequestParam(value ="a",defaultValue = "false") boolean anon) {
		if(!loggedin(httpSession)){
			return "redirect:login";
		}
		SessionInfo sessionInfo = db.getSessionInfo(eventid,sessionid);
		model.addAttribute("sessionInfo",sessionInfo);
		ArrayList<Questions> questions = db.getQuestions(sessionInfo.templateId);
		model.addAttribute("questions",questions);
		model.addAttribute("sessionid",sessionid);
		if(anon==false){
			model.addAttribute("uname", db.getUserName( (int)httpSession.getAttribute("user")));
		}else{
			model.addAttribute("uname", "anonymous");

		}
		return "feedback";
	}

	/**
	 * Generates host session page
	 * 
	 * @param httpSession
	 * @param model
	 * @param eventid
	 * @param sessionid
	 * @return
	 */
	@GetMapping("/session")
	public String hostSession(HttpSession httpSession,Model model,@RequestParam("eventid") int eventid,@RequestParam("sessionid") int sessionid) {
		if(!loggedin(httpSession)){
			return "redirect:login";
		}
		SessionInfo sessionInfo = db.getSessionInfo(eventid,sessionid);
		model.addAttribute("sessionInfo",sessionInfo);
		model.addAttribute("sessionid",sessionid);
		model.addAttribute("uname", "U"+httpSession.getAttribute("user"));
		return "session";
	}
	/**
	 * Generates the page for creating sessions.
	 * @param httpSession
	 * @param model
	 * @param eventid
	 * @return
	 */
	@GetMapping("/sessioncreate")
	public String sessionCreation(HttpSession httpSession,Model model,@RequestParam("eventid") int eventid) {
		if(!loggedin(httpSession)){
			return "redirect:login";
		}
		//Get templates user is subscribed to
		ArrayList<Template> templates = db.getTemplates((int)httpSession.getAttribute("user"));
		model.addAttribute("eventid",eventid);

		model.addAttribute("templates", templates);
		return "sessioncreate";
	}

	/**
	 * Create session using form variables stored in request parameters.
	 * @param httpSession
	 * @param model
	 * @param sessionname
	 * @param tempid
	 * @param eventid
	 * @param startdate
	 * @param enddate
	 * @return
	 */
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

		return  "redirect:events";
	}

	/**
	 * On page error redirect to main events page if logged in, otherwise redirect to login portal.
	 * @param httpSession
	 * @return
	 */
	@GetMapping("/error")
	public String error(HttpSession httpSession) {
		if(!loggedin(httpSession)){
			return "redirect:login";
		}
		return "redirect:events";
	}

	/**
	 * Generates page for creating events.
	 * @param httpSession
	 * @param model
	 * @return
	 */
	@GetMapping("/eventcreate")
	public String eventCreation(HttpSession httpSession,Model model) {
		if(!loggedin(httpSession)){
			return "redirect:login";
		}
		ArrayList<Template> templates = db.getTemplates((int)httpSession.getAttribute("user"));

		model.addAttribute("templates", templates);
		return "eventcreate";
	}

	/**
	 * Create event using form variables stored in request parameters.
	 * @param httpSession
	 * @param eventname
	 * @param tempid
	 * @param allowanon
	 * @return
	 */
	@PostMapping("/eventcreate")
	public String createEvent(HttpSession httpSession,@RequestParam("eventname") String eventname,
	@RequestParam("templateid")int tempid,@RequestParam(value ="anonymous",defaultValue = "false") boolean allowanon) {
		if(!loggedin(httpSession)){
			return "redirect:login";
		}
		db.createEvent((int)httpSession.getAttribute("user"), eventname, tempid, allowanon);

		return "redirect:events";
	}


	/**
	 *  Generates the page for creating templates.
	 * @param httpSession
	 * @return
	 */
	@GetMapping("/createtemplate")
	public String templateCreation(HttpSession httpSession) {
		if(!loggedin(httpSession)){
			return "redirect:login";
		}
		return "createtemplate";
	}

	/**
	 * Create template using form variables stored in request parameters.
	 * @param httpSession
	 * @param map containing entries of questions created for the template
	 * @return
	 */
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

	/**
	 *  Generates the page for joining events.
	 * @param httpSession
	 * @return
	 */
	@GetMapping("/join")
	public String eventJoin(HttpSession httpSession) {
		return "join";
	}

	/**
	 * Join event using code sent from request parameter.
	 * @param httpSession
	 * @param code
	 * @return
	 */
	@PostMapping("/join")
	public String joinEvent(HttpSession httpSession,@RequestParam("code") String code) {
		db.joinEvent(code,(int)httpSession.getAttribute("user"));
		return "redirect:events";
	}

	/**
	 * Generates the main events page.
	 * @param httpSession
	 * @param model
	 * @return
	 */
	@GetMapping("/events")
	public String eventsLanding(HttpSession httpSession, Model model) {
		if(!loggedin(httpSession)){
			return "redirect:login";
		}
		ArrayList<Event> events = (ArrayList<Event>) db.getEvents((int) httpSession.getAttribute("user"));
		model.addAttribute("events", events);
		model.addAttribute("event_num", events.size());
		if(httpSession.getAttribute("anonymous")==null){
			model.addAttribute("anonymous", "false");
		}else{
			model.addAttribute("anonymous", "true");

		}
		return "events";
	}
	/**
	 * Generates the page for loggining in.
	 * @param httpSession
	 * @return
	 */
	@GetMapping("/login")
	public String loginForm(HttpSession httpSession) {
		if(loggedin(httpSession)){
			return "redirect:events";
		}
		return "login";
	}

	/**
	 * Logs in user with form variables stored in request parameters.
	 * @param username
	 * @param password
	 * @param httpSession
	 * @return
	 */
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
	/**
	 * Generates the signup page.
	 * @param httpSession
	 * @return
	 */
	@GetMapping("/signup")
	public String signup(HttpSession httpSession) {
		if(loggedin(httpSession)){
			return "redirect:events";
		}
		return "signup";
	}

	/**
	 * Register user using form variables stored in request parameters.
	 * @param fname
	 * @param lname
	 * @param username
	 * @param password
	 * @param password2
	 * @param httpSession
	 * @return
	 */
	@PostMapping("/signup")
	public String register(@RequestParam("firstname") String fname,@RequestParam("lastname") String lname,@RequestParam("username") String username,@RequestParam("password") String password,
	@RequestParam("password") String password2,HttpSession httpSession) {
		if(loggedin(httpSession)){
			return "events";
		}
		if(password.equals(password2)){
			int userId = db.createUser(fname,lname,username, password);
			httpSession.setAttribute("user", userId);
			return "redirect:events";
		}
		return "redirect:/signup";
	}
	/**
	 * Signs out user by removing session attributes and redirecting to login page.
	 * @param httpSession
	 * @return
	 */
	@GetMapping("/signout")
	public String logout(HttpSession httpSession) {
		httpSession.removeAttribute("user");
		httpSession.removeAttribute("anonymous");

		return "redirect:login";
	}

	@GetMapping("/anonymous_account")
	public String anonymous_account(HttpSession httpSession) {
		httpSession.setAttribute("anonymous","true");
		return register("","","anonymous","password","password",httpSession);
	}

	/**
	 * On occurence of error, redirect to stored error path.
	 * @return
	 */
	@Override
	public String getErrorPath() {
		return error_redirect_path;
	}

}
