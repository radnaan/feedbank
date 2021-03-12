package com.project.feedbank.Session;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import static java.lang.String.format;

import com.project.feedbank.Semantic.*;

@Controller
public class LiveSessionController {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/session/{sessionId}/sendMessage")
    public void sendMessage(@DestinationVariable String sessionId, @Payload Message sessionMessage) {
        Mood mood = SemanticAnalyser.getClassification(sessionMessage.getContent());
        sessionMessage.setClassification(mood.getClassification());
        messagingTemplate.convertAndSend(format("/live-session/%s", sessionId), sessionMessage);
    }


}