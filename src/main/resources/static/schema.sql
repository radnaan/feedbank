DROP TABLE users CASCADE;
CREATE TABLE users (
    UserID          SERIAL,
    FirstName       VARCHAR,
    LastName        VARCHAR,
    UserName        VARCHAR NOT NULL,
    UserPassword    VARCHAR NOT NULL,
    PRIMARY KEY (UserID)
);

DROP TABLE templates CASCADE;
CREATE TABLE templates (
    TemplateID      SERIAL,
    TemplateName    VARCHAR NOT NULL,
    Editable        BOOLEAN NOT NULL,
    PRIMARY KEY (TemplateID)
);

--changed feedbacktime to integer
DROP TABLE events CASCADE;
CREATE TABLE events (
    EventID         SERIAL,
    EventName       VARCHAR NOT NULL,
    EventCode       VARCHAR NOT NULL,
    EventStatus     VARCHAR NOT NULL CHECK (EventStatus = 'Active' OR EventStatus = 'Inactive with future session' OR EventStatus = 'Inactive without future session' OR EventStatus = 'Ended'),
    TemplateID      INTEGER NOT NULL,
    RequiredLogin   BOOLEAN NOT NULL,
    FeedbackTime    INTEGER NOT NULL,
    AllowAnon       BOOLEAN NOT NULL,
    PRIMARY KEY (EventID),
    FOREIGN KEY (TemplateID) REFERENCES templates(TemplateID) ON DELETE CASCADE
);

DROP TABLE sesh CASCADE;
CREATE TABLE sesh (
    SeshID          SERIAL,
    EventID         INTEGER NOT NULL,
    TemplateID      INTEGER NOT NULL,
    SeshName        VARCHAR NOT NULL,
    SeshEnded       BOOLEAN NOT NULL,
    SeshDateStart   TIMESTAMP NOT NULL,
    SeshDateEnd     TIMESTAMP NOT NULL,
    PRIMARY KEY (SeshID),
    FOREIGN KEY (EventID)    REFERENCES events(EventID)       ON DELETE CASCADE,
    FOREIGN KEY (TemplateID) REFERENCES templates(TemplateID) ON DELETE CASCADE
);

DROP TABLE feedback CASCADE;
CREATE TABLE feedback (
    FeedbackID      SERIAL,
    UserID          INTEGER NOT NULL,
    SeshID          INTEGER NOT NULL,
    TemplateID      INTEGER NOT NULL,
    OverallMood     VARCHAR NOT NULL CHECK (OverallMood = 'Positive' OR OverallMood = 'Neutral' OR OverallMood = 'Negative'),
    DatePlaced      TIMESTAMP NOT NULL,
    PRIMARY KEY (FeedbackID),
    FOREIGN KEY (UserID)     REFERENCES users(UserID)         ON DELETE CASCADE,
    FOREIGN KEY (SeshID)     REFERENCES sesh(SeshID)          ON DELETE CASCADE,
    FOREIGN KEY (TemplateID) REFERENCES templates(TemplateID) ON DELETE CASCADE
);

DROP TABLE questions CASCADE;
CREATE TABLE questions (
    QuestionID      SERIAL,
    TemplateID      INTEGER NOT NULL,
    QuestionText    VARCHAR NOT NULL,
    QuestionType    VARCHAR NOT NULL CHECK (QuestionType = 'Text' OR QuestionType = 'Mood' OR QuestionType = 'Choice'),
    PRIMARY KEY (QuestionID),
    FOREIGN KEY (TemplateID) REFERENCES templates(TemplateID) ON DELETE CASCADE
);

DROP TABLE answers CASCADE;
CREATE TABLE answers (
    AnswerID        SERIAL,
    QuestionID      INTEGER NOT NULL,
    FeedbackID      INTEGER NOT NULL,
    AnswerText      VARCHAR,
    AnswerMood      VARCHAR CHECK (AnswerMood = 'Positive' OR AnswerMood = 'Neutral' OR AnswerMood = 'Negative'),
    PRIMARY KEY (AnswerID),
    FOREIGN KEY (QuestionID) REFERENCES questions(QuestionID) ON DELETE CASCADE,
    FOREIGN KEY (FeedbackID) REFERENCES feedback(FeedbackID)  ON DELETE CASCADE
);

DROP TABLE choices_questions CASCADE;
CREATE TABLE choices_questions (
    ChoiceQuestionID        SERIAL,
    QuestionID      INTEGER NOT NULL,
    ChoiceText      VARCHAR NOT NULL,
    PRIMARY KEY (ChoiceQuestionID),
    FOREIGN KEY (QuestionID) REFERENCES questions(QuestionID) ON DELETE CASCADE
);

DROP TABLE choices_answers CASCADE;
CREATE TABLE choices_answers (
    ChoiceAnswerID        SERIAL,
    AnswerID        INTEGER NOT NULL,
    ChoiceText      VARCHAR NOT NULL,
    PRIMARY KEY (ChoiceAnswerID),
    FOREIGN KEY (AnswerID) REFERENCES answers(AnswerID) ON DELETE CASCADE
);

DROP TABLE user_templates CASCADE;
CREATE TABLE user_templates (
    UserID          INTEGER NOT NULL,
    TemplateID      INTEGER NOT NULL,
    PRIMARY KEY (UserID, TemplateID),
    FOREIGN KEY (UserID)     REFERENCES users(UserID)         ON DELETE CASCADE,
    FOREIGN KEY (TemplateID) REFERENCES templates(TemplateID) ON DELETE CASCADE
);

DROP TABLE user_events CASCADE;
CREATE TABLE user_events (
    UserID          INTEGER NOT NULL,
    EventID         INTEGER NOT NULL,
    UserRole        VARCHAR NOT NULL CHECK (UserRole = 'Host' OR UserRole = 'Attendee'),
    PRIMARY KEY (UserID, EventID),
    FOREIGN KEY (UserID)  REFERENCES users(UserID)   ON DELETE CASCADE,
    FOREIGN KEY (EventID) REFERENCES events(EventID) ON DELETE CASCADE
);

--changed feedbacktime to integer
CREATE OR REPLACE FUNCTION create_event(eventName VARCHAR, code VARCHAR, templateID INTEGER, requiredLogin BOOLEAN, feedbackTime INTEGER, allowAnon BOOLEAN, userID INTEGER)
    RETURNS void
    LANGUAGE plpgsql AS
    $$
    DECLARE
        ID INTEGER; 
    BEGIN
        INSERT INTO events (EventName, EventCode, EventStatus, TemplateID, RequiredLogin, FeedbackTime, AllowAnon)
        VALUES (eventName, code, 'Inactive without future session', templateID, requiredLogin, feedbackTime, allowAnon);

        SELECT currval('events_EventID_seq')
        INTO ID;

        INSERT INTO user_events (UserID, EventID, UserRole)
        VALUES (userID, ID, 'Host');
    END
    $$;
--changed return type from void to integer
CREATE OR REPLACE FUNCTION create_user(firstName VARCHAR, lastName VARCHAR, userName VARCHAR, userPassword VARCHAR)
    RETURNS INTEGER
    LANGUAGE plpgsql AS
    $$
    DECLARE
        ID INTEGER; 
    BEGIN
        INSERT INTO users (FirstName, LastName, UserName, UserPassword)
        VALUES (firstName, lastName, userName, userPassword);

        SELECT currval('users_UserID_seq')
        INTO ID;

        INSERT INTO user_templates (UserID, TemplateID)
        SELECT ID, templateID FROM templates WHERE Editable = FALSE;
        return ID;
    END
    $$;

--new login validator function
CREATE OR REPLACE FUNCTION validate_login(usrname VARCHAR, psswrd VARCHAR)
    RETURNS INTEGER
    LANGUAGE plpgsql AS
    $$
    DECLARE
        ID INTEGER; 
    BEGIN

        SELECT userID FROM users WHERE username=usrname AND userpassword =psswrd
        INTO ID;
		IF ID IS NULL then
		 ID := -1;
		END IF;
        return ID;
    END
    $$;


CREATE OR REPLACE FUNCTION assign_user(usID INTEGER, evID INTEGER)
    RETURNS void
    LANGUAGE SQL AS
    $$
    INSERT INTO user_events (UserID, EventID, UserRole)
    VALUES (usID, evID, 'Attendee');
    $$;

CREATE OR REPLACE FUNCTION create_session(evID INTEGER, tempID INTEGER, shName VARCHAR, shStartDate TIMESTAMP, shEndDate TIMESTAMP)
    RETURNS void
    LANGUAGE SQL AS
    $$
    INSERT INTO sesh (EventID, TemplateID, SeshName, SeshEnded, SeshDateStart, SeshDateEnd)
    VALUES (evID, tempID, shName, FALSE, shStartDate, shEndDate);
    $$;

CREATE OR REPLACE FUNCTION create_template(userID INTEGER, tempName VARCHAR, questions VARCHAR[], types VARCHAR[], choices VARCHAR[][])
    RETURNS void
    LANGUAGE plpgsql AS
    $$
    DECLARE
        ID INTEGER; 
        questID INTEGER;
    BEGIN
        INSERT INTO templates (TemplateName, Editable)
        VALUES (tempName, TRUE);

        SELECT currval('templates_TemplateID_seq')
        INTO ID;

        INSERT INTO user_templates (UserID, TemplateID) 
        VALUES (userID, ID);

        FOR i IN 1 .. array_upper(questions, 1) LOOP
            INSERT INTO questions (TemplateID, QuestionText, QuestionType)
            VALUES (ID, questions[i], types[i]);
            
            -- SELECT currval('questions_QuestionID_seq')
            -- INTO questID;
            
            -- IF types[i] = 'Choice' THEN
            --     FOR j IN 1 .. coalesce(array_upper(choices[i], 1), 0) LOOP
            --     INSERT INTO choice_questions (QuestionID, ChoiceText)
            --     VALUES (questID, choices[i][j]);
            -- END LOOP;
            -- END IF;
        END LOOP;
    END
    $$;

CREATE OR REPLACE FUNCTION create_default_template(tempName VARCHAR, questions VARCHAR[], types VARCHAR[], choices VARCHAR[][])
    RETURNS void
    LANGUAGE plpgsql AS
    $$
    DECLARE
        ID INTEGER;
        questID INTEGER;
    BEGIN
        INSERT INTO templates (TemplateName, Editable)
        VALUES (tempName, FALSE);

        SELECT currval('templates_TemplateID_seq')
        INTO ID;

        INSERT INTO user_templates (UserID, TemplateID)
        SELECT userID, ID FROM users;

        FOR i IN 1 .. array_upper(questions, 1) LOOP
            INSERT INTO questions (TemplateID, QuestionText, QuestionType)
            VALUES (ID, questions[i], types[i]);
            
            -- SELECT currval('questions_QuestionID_seq')
            -- INTO questID;
            
            -- IF types[i] = 'Choice' THEN
            --     FOR j IN 1 .. coalesce(array_upper(choices[i], 1), 0) LOOP
            --     INSERT INTO choice_questions (QuestionID, ChoiceText)
            --     VALUES (questID, choices[i][j]);
            -- END LOOP;
            -- END IF;
        END LOOP;
    END
    $$;

CREATE OR REPLACE FUNCTION add_feedback(userID INTEGER, seshID INTEGER, tempID INTEGER, mood VARCHAR, datePlaced TIMESTAMP, questionIDs INTEGER[], answertxt VARCHAR[], answermoods VARCHAR[], choices VARCHAR[][])
    RETURNS void
    LANGUAGE plpgsql AS
    $$
    DECLARE
        ID INTEGER; 
        answID INTEGER;
    BEGIN
        INSERT INTO feedback (UserID, SeshID, TemplateID, OverallMood, DatePlaced)
        VALUES (userID, seshID, tempID, mood, datePlaced);

        SELECT currval('feedback_FeedbackID_seq')
        INTO ID;

        FOR i IN 1 .. array_upper(questionIDs, 1) LOOP
            INSERT INTO answers (QuestionID, FeedbackID, AnswerText, AnswerMood)
            VALUES (questionIDs[i], ID, answertxt[i], answermoods[i]);
            
            -- SELECT currval('answers_AnswerID_seq')
            -- INTO answID;
            
            -- IF types[i] = 'Choice' THEN
            --     FOR j IN 1 .. coalesce(array_upper(choices[i], 1), 0) LOOP
            --     INSERT INTO choice_questions (QuestionID, ChoiceText)
            --     VALUES (questID, choices[i][j]);
            -- END LOOP;
            -- END IF;
        END LOOP;
    END
    $$;

CREATE OR REPLACE FUNCTION get_users(evID INTEGER)
    RETURNS TABLE
        (UserID INTEGER,
         FirstName VARCHAR,
         LastName VARCHAR,
         UserName VARCHAR)
    LANGUAGE SQL AS
    $$
    SELECT userID, firstName, lastName, username
    FROM user_events INNER JOIN users USING (UserID)
    WHERE UserRole = 'Attendee' AND EventID = evID;
    $$;

CREATE OR REPLACE FUNCTION get_events(usID INTEGER)
    RETURNS TABLE
        (EventID INTEGER,
         EventName VARCHAR,
         EventCode VARCHAR,
         EventStatus VARCHAR,
         RequiredLogin BOOLEAN,
         FeedbackTime INTEGER,
         AllowAnon BOOLEAN,
         UserRole VARCHAR,
         NextSeshID INTEGER,
         NextSeshDate TIMESTAMP
        )
    LANGUAGE SQL AS
    $$
    SELECT * FROM update_activity();
    SELECT eventID, eventName, eventCode, eventStatus, requiredLogin, feedbackTime, allowAnon, userRole, seshID, seshdatestart
    FROM (
        SELECT *, rank() 
        OVER (PARTITION BY eventID ORDER BY seshdatestart ASC) 
        FROM (
            SELECT *
            FROM (
                SELECT *
                FROM user_events INNER JOIN events USING (EventID)
                WHERE UserID = usID
            ) AS EventsIncludingUser
            LEFT OUTER JOIN 
                (SELECT * 
                FROM sesh
                WHERE seshdateend > CURRENT_TIMESTAMP
            ) AS FutureSessions
            USING (eventID)
        ) AS FutureSessionsForUser
    ) as NextSessions where rank = 1;
    $$;

CREATE OR REPLACE FUNCTION update_activity()
    RETURNS void
    LANGUAGE SQL AS
    $$
    UPDATE events SET EventStatus = 'Inactive without future session' WHERE EventStatus <> 'Ended';
    UPDATE events SET EventStatus = 'Active' 
    WHERE EventID IN (
        SELECT eventID 
        FROM events INNER JOIN sesh 
        USING (EventID) WHERE seshdatestart < CURRENT_TIMESTAMP AND seshdateend > CURRENT_TIMESTAMP AND eventStatus = 'Inactive without future session'
    );
    UPDATE events SET EventStatus = 'Inactive with future session' 
    WHERE EventID IN (
        SELECT eventID 
        FROM events INNER JOIN sesh 
        USING (EventID) WHERE seshdatestart > CURRENT_TIMESTAMP AND eventStatus = 'Inactive without future session'
    );
    $$;

CREATE OR REPLACE FUNCTION get_templates(usID INTEGER)
    RETURNS TABLE
        (TemplateID INTEGER,
         TemplateName VARCHAR,
         Editable BOOLEAN)
    LANGUAGE SQL AS
    $$
    SELECT templateID, templateName, editable
    FROM user_templates INNER JOIN templates USING (TemplateID)
    WHERE UserID = usID;
    $$;

CREATE OR REPLACE FUNCTION get_questions(tempID INTEGER)
    RETURNS TABLE
        (QuestionID INTEGER,
         QuestionText VARCHAR,
         QuestionType VARCHAR)
    LANGUAGE SQL AS
    $$
    SELECT questionID, questionText, questionType
    FROM questions
    WHERE TemplateID = tempID;
    $$;

CREATE OR REPLACE FUNCTION end_event(eID INTEGER)
    RETURNS void
    LANGUAGE SQL AS
    $$
    UPDATE events SET EventStatus = 'Ended' 
    WHERE EventID = eID;
    $$;

CREATE OR REPLACE FUNCTION end_session(seID INTEGER)
    RETURNS void
    LANGUAGE SQL AS
    $$
    UPDATE sesh SET seshdateend = CURRENT_TIMESTAMP 
    WHERE seshID = seID;
    $$;