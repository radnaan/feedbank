import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class passwrdreaderTest {
//passwrdreader function has problem261. I have tried input correct inform and incorrect information.
// All give correct feedback.
//I changed the passwrdreader file with add parameter instead of the scannner input.
    @Test
    void correctinput1() throws FileNotFoundException {
        passwrdreader pass = new passwrdreader();
        pass.run("1923071","261");
        assertEquals("your login message",pass.s);
    }
    @Test
    void correctinput2() throws FileNotFoundException {
        passwrdreader pass = new passwrdreader();
        pass.run("1923077","261");
        assertEquals("your login message",pass.s);
    }
    @Test
    void NonExMes() throws FileNotFoundException {
        passwrdreader pass = new passwrdreader();
        pass.run("261","1923071");
        assertEquals("your error message",pass.s);
    }
    @Test
    void errorMes() throws FileNotFoundException {
        passwrdreader pass = new passwrdreader();
        pass.run("1923071","1923071");
        assertEquals("your error message",pass.s);
    }
    @Test
    void EmpMes() throws FileNotFoundException {
        passwrdreader pass = new passwrdreader();
        pass.run("","");
        assertEquals("your error message",pass.s);
    }
}