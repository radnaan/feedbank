import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
//The length of the password asked for at least 6.If input less than 6 the program should just return 6 symbol pwd.
// The test fail for input 4 and 5
//when I test I have changed the passwrdGen file. cancel out the scanner sep, using the parameter instead  of scanner.
class passwrdGenTest {
    @Test
    void specCase(){
        passwrdGen passwrd= new passwrdGen();
        passwrd.setPassword(50);
        String result= passwrd.getPassword().toString();
        assertTrue( result.length()==50);
    }
    @RepeatedTest(10)
    void setgeneratePasswordTest() {
       passwrdGen passwrd= new passwrdGen();
        int temp = (int)(Math.random()*10);
        System.out.println(temp);
        passwrd.setPassword(temp);
       String result= passwrd.getPassword().toString();

        assertTrue(6<= result.length());
    }


}

