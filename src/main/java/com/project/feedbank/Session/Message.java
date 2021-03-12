package com.project.feedbank.Session;

public class Message {	
    public enum MessageType {QUESTION, 
        MOOD, 
        
    }	
    private MessageType messageType;	    
    private String content;	    
    private String sender;	
    private String question;
    private int mood;
    private String classification;
    public MessageType getType() 
    {	        
        return messageType;	    
    }	
    public void setType(MessageType messageType) 
    {	        
        this.messageType = messageType;	    
    }	
    public String getContent() 
    {	        
        return content;	    
    }	
    public void setContent(String content) 
    {	        
        this.content = content;	    
    }	
    public String getSender() 
    {	        
        return sender;	    
    }	
    public void setSender(String sender) 
    {	        
        this.sender = sender;
    }	
    public String getQuestion() 
    {	        
        return question;	    
    }	
    public void setQuestion(String question) 
    {	        
        this.question = question;	    
    }	
    public int getMood() 
    {	        
        return mood;	    
    }	
    public void setMood(int mood) 
    {	        
        this.mood = mood;	    
    }	

    public String getClassification() 
    {	        
        return classification;	    
    }	
    public void setClassification(String classification) 
    {	        
        this.classification = classification;	    
    }	
}