import Mood.Mood;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SemanticAnalyserTest {
    @Test
    void test(){
        Mood m = new Mood();
        SemanticAnalyser semantic = new SemanticAnalyser();
        String sem="This presentation is not bad";
        Mood s = semantic.getClassification(sem);
        System.out.println(sem);
        String[] requests = s.getRequests();
        assertEquals(s.toString(),"Mood: positive");
    }


    @Test
    void case1() {
        main("This presentation is not bad","positive");
    }
    @Test
    void case2() {
        main("Could you please change the projector? I can't see the slides well at all","negative");
    }
    @Test
    void case3(){
        main("This talk is super good and really clear. I really understand everything and am super hyped about the whole thing. Amazing!","positive");
    }
    @Test
  void case4(){
        main("Is't clearer if there are more information","positive");
    }
    @Test
    void case5(){
        main("This presentation is good but it is a bit confusing","negative");
    }
    @Test
    void case6(){
        main("I really like this presentation","positive");
    }

    @Test
    void case7(){
        main("It is a presentation","neutral");
    }
    @Test
    void case8(){
        main("This presentation is good. I would like more information","positive");
    }
    @Test
    void case9(){
        main("The presentation is good but it is a bit confusing. Overall I don't understand what is going on and I can't follow the main points. It would be better if you could bullet point these so that it would be easier to follow","negative");
    }
    @Test
    void case10(){
        main("I really hate this presentation","negative");
    }
    void main(String sem, String mood){
        Mood m = new Mood();
        SemanticAnalyser semantic = new SemanticAnalyser();
        Mood s = semantic.getClassification(sem);
        System.out.println(sem);
        String[] requests = s.getRequests();
        assertEquals(s.toString(),"Mood: "+mood);
    }

}