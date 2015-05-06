package net.afnf.blog.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import net.afnf.blog.SpringTestBase;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserServiceTest extends SpringTestBase {

    @Autowired
    private UserService us;

    @Test
    public void testRegisterAdmin() {
        assertEquals(0, us.getUserCount());

        us.registerAdmin("admin", "pass");

        assertEquals(1, us.getUserCount());

        try {
            us.registerAdmin("admin2", "pass");
            fail("unexpected");
        }
        catch (Exception e) {
            assertEquals("illegal1", e.getMessage());
        }
    }

}
