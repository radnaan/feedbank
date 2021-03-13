import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class uniquecodeTest {
// This Functions works well.every time the code generates are different
    @RepeatedTest(3)
    void test3() {
        uniquecode code = new uniquecode();
        uniquecode co = new uniquecode();
        uniquecode c = new uniquecode();
        code.setFinalSeed();
        co.setFinalSeed();
        c.setFinalSeed();
        int code1 = code.getFinalSeed();
        int code2 = co.getFinalSeed();
        int code3 = c.getFinalSeed();
        System.out.println(code1);
        System.out.println(code2);
        System.out.println(code3);
        assertNotEquals(code1,code2);
        assertNotEquals(code1,code3);
        assertNotEquals(code3,code2);

    }
    @RepeatedTest(3)
    void test2() {
        uniquecode code = new uniquecode();
        uniquecode co = new uniquecode();
//        uniquecode c = new uniquecode();
        code.setFinalSeed();
        co.setFinalSeed();
//        c.setFinalSeed();
        int code1 = code.getFinalSeed();
        int code2 = co.getFinalSeed();
//        int code3 = c.getFinalSeed();
        System.out.println(code1);
        System.out.println(code2);
//        System.out.println(code3);
        assertNotEquals(code1,code2);
//        assertNotEquals(code1,code3);
//        assertNotEquals(code3,code2);

    }
    @RepeatedTest(3)
    void test4() {
        uniquecode code = new uniquecode();
        uniquecode cod = new uniquecode();
        uniquecode co = new uniquecode();
        uniquecode c = new uniquecode();
        code.setFinalSeed();
        cod.setFinalSeed();
        co.setFinalSeed();
        c.setFinalSeed();
        int code1 = code.getFinalSeed();
        int code2 = co.getFinalSeed();
        int code3 = c.getFinalSeed();
        int code4 = cod.getFinalSeed();
        System.out.println(code1);
        System.out.println(code2);
        System.out.println(code3);
        System.out.println(code4);
        assertNotEquals(code1,code2);
        assertNotEquals(code1,code3);
        assertNotEquals(code1,code4);
        assertNotEquals(code3,code2);
        assertNotEquals(code4,code2);
        assertNotEquals(code4,code3);


    }
    @RepeatedTest(3)
    void test1() {
        uniquecode code = new uniquecode();
        code.setFinalSeed();

        int code1 = code.getFinalSeed();

        System.out.println(code1);


        assertTrue(code1>0);


    }

}