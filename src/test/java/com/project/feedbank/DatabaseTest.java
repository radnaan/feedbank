import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.*;
class DatabaseTest {
Database dbs= new Database();

    @Test
    void getConnection() {
        System.out.println("get connection");
        Connection result= dbs.getConnection();
        assertEquals(result!= null, true);
    }
}