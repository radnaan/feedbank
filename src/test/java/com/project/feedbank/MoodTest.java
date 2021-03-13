package Mood;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoodTest {
    //test of mood changeClassification function. It works good returns expected result.
    @Test
    void PositiveString(){
        Mood m = new Mood();
        m.changeClassification("positive");
        assertEquals("Mood: positive",m.toString());
    }
    @Test
    void NegativeString(){
        Mood m = new Mood();
        m.changeClassification("negative");
        assertEquals("Mood: negative",m.toString());
    }
    @Test
    void NeutralTest(){
        Mood m= new Mood();
        m.changeClassification("neutral");
        assertEquals("Mood: neutral",m.toString());
    }
    @Test
    void UnclassTest1(){
        Mood m= new Mood();
        m.changeClassification("");
        assertEquals("Mood: unclassified",m.toString());
    }
    @Test
    void UnclassTest2(){
        Mood m= new Mood();
        m.changeClassification("qwer");
        assertEquals("Mood: unclassified",m.toString());
    }

}