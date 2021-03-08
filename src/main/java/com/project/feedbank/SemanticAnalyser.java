import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Code from here https://www.baeldung.com/java-http-request
//Compile with java Semantic.java -classpath . *.java

class SemanticAnalyser {
    //Will need to change so that it works for the final distribution
    private final static String requestUrl = "http://localhost:5000/analyse?text=";

    //Give it a sentence, returns a mood object
    public static Mood getClassification(String feedback) {
        Mood classification = new Mood();

        //Make a HTTP request and parse the results
        StringBuffer requestData = makeRequest(feedback);
        String result = getMoodFromJSON(requestData.toString());
        String[] requests = getRequestsFromJSON(requestData.toString());

        classification.setClassification(result);
        classification.setRequests(requests);
        
        return classification;
    }

    private static StringBuffer makeRequest(String sentence) {

        //Sets up a string buffer
        StringBuffer content = new StringBuffer();
        
        //Make a GET request and read the data
        try {
            String encodeStr = URLEncoder.encode(sentence, StandardCharsets.UTF_8.name());
            URL url = new URL(SemanticAnalyser.requestUrl + encodeStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            //Check if response was successful
            int status = con.getResponseCode();

            //If the response was not successful, do not attempt to read
            if (status != HttpURLConnection.HTTP_OK) {
                System.err.println("Error, response code: " + status);
                System.err.println("Is the API running?");
                return content;
            }

            BufferedReader in = new BufferedReader(
            new InputStreamReader(con.getInputStream()));
            String inputLine;
            
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            //Disconnect
            con.disconnect();
        }
        catch (Exception e) {
            return content;
        }

        return content;
    }

    //Hack job, just looks for the mood part of the json and uses that
    private static String getMoodFromJSON(String json) {
        //If there are not enough characters for there to be a full
        //response then don't parse
        if (json.length() < 13) {
            return "";
        }

        String pattern = "\"mood\":\".*\",";

        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(json);

        if (m.find()) {
            String result = m.group(0);
	    System.out.println(result.substring(8, result.length()-2));
            return result.substring(8, result.length()-2);
        }
        else {
            return "";
        }
    }

    //Hack job, just looks for the requests part
    private static String[] getRequestsFromJSON(String json) {
        //If there are not enough characters for there to be a full
        //response then don't parse
        if (json.length() < 13) {
            return new String[0];
        }

        String pattern = "\"requests\":\".*\"";

        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(json);

        if (m.find()) {
            String result = m.group(0);
            return result.substring(12, result.length()-1).split(",");
        }
        else {
            return new String[0];
        }
    }

    static void test() {
        String[] examples = {
            "I really like this presentation",
            "It is a presentation.",
            "I really hate this presentation",
            "The presentation is good but it is a bit confusing. Overall I don't understand what is going on and I can't follow the main points. It would be better if you could bullet point these so that it would be easier to follow.",
            "This talk is super good and really clear. I really understand everything and am super hyped about the whole thing. Amazing!",
            "Could you please change the projecter? I can't see the slides well at all."
        };
        String[] results = {
            "positive",
            "neutral",
            "negative",
            "negative",
            "positive",
            "negative"
        };

        for (int i=0; i<examples.length; i++) {
            Mood m = getClassification(examples[i]);

            if (m.isMood(results[i])) {
                System.out.println("PASS");
            }
            else {
                System.out.println("FAIL");
                System.out.println("  " + "Test: " + examples[i]);
                System.out.println("  " + m.toString());
                System.out.println("  " + "Expected: " + results[i]);
            }
	    String[] requests = m.getRequests();
	    for (int j=0; j<requests.length; j++) {
	        System.out.println("  " + "Request: " + requests[j]);
	    }
        }
    }
}