package net.afnf.blog.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import net.afnf.blog.SpringTestBase;
import net.afnf.blog.common.JsonResponseDemoSiteErrorException;
import net.afnf.blog.common.JsonResponseException;
import net.afnf.blog.config.AppConfig;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

public class TokenCheckableActionTest extends SpringTestBase {

    @Test
    public void testCheckToken1() {
        String prevType = AppConfig.getInstance().getActiveProfile();

        AppConfig.getInstance().setActiveProfile("dev-demo");
        TokenCheckableAction target = new BlogAction();
        try {
            target.checkToken();
            fail();
        }
        catch (Exception e) {
            assertTrue(e instanceof JsonResponseDemoSiteErrorException);
        }
        finally {
            AppConfig.getInstance().setActiveProfile(prevType);
        }
    }

    @Test
    public void testCheckToken2() {
        TokenCheckableAction target = new BlogAction();
        MockHttpServletRequest mock = new MockHttpServletRequest();
        target.request = mock;

        try {
            target.checkToken();
            fail();
        }
        catch (Exception e) {
            assertTrue(e instanceof JsonResponseException);
        }

        try {
            mock.setMethod("post");
            target.checkToken();
            fail();
        }
        catch (Exception e) {
            assertTrue(e instanceof JsonResponseException);
        }

        try {
            mock.addHeader("X-Requested-With", "XMLHttpRequest");
            target.checkToken();
            fail();
        }
        catch (Exception e) {
            assertTrue(e instanceof JsonResponseException);
        }
    }

    @Test
    public void testGetClientInfo1() {
        TokenCheckableAction target = new BlogAction();
        MockHttpServletRequest mock = new MockHttpServletRequest();
        mock.addHeader("User-Agent", "testua");
        mock.setRemoteAddr("220.1.1.2");
        target.request = mock;

        assertEquals("220.1.1.2, testua", target.getClientInfo());
    }

    @Test
    public void testGetClientInfo2() {
        TokenCheckableAction target = new BlogAction();
        MockHttpServletRequest mock = new MockHttpServletRequest();
        mock.addHeader("User-Agent", "testua");
        mock.setRemoteAddr("220.1.1.2");
        target.request = mock;

        mock.addHeader("X-Forwarded-For", "  ");
        assertEquals("220.1.1.2, testua", target.getClientInfo());
    }

    @Test
    public void testGetClientInfo3() {
        TokenCheckableAction target = new BlogAction();
        MockHttpServletRequest mock = new MockHttpServletRequest();
        mock.addHeader("User-Agent", "testua");
        mock.setRemoteAddr("220.1.1.2");
        target.request = mock;

        mock.addHeader("X-Forwarded-For", "1.1.1.1");
        assertEquals("1.1.1.1, testua", target.getClientInfo());
    }

    @Test
    public void testGetClientInfo4() {
        TokenCheckableAction target = new BlogAction();
        MockHttpServletRequest mock = new MockHttpServletRequest();
        mock.addHeader("User-Agent", "testua");
        mock.setRemoteAddr("220.1.1.2");
        target.request = mock;

        mock.addHeader("X-Forwarded-For", "1.1.1.1,2.2.2.2");
        assertEquals("2.2.2.2, testua", target.getClientInfo());
    }

    @Test
    public void testGetClientInfo5() {
        TokenCheckableAction target = new BlogAction();
        MockHttpServletRequest mock = new MockHttpServletRequest();
        mock.addHeader("User-Agent", "testua");
        mock.setRemoteAddr("220.1.1.2");
        target.request = mock;

        mock.addHeader("X-Forwarded-For", "1.1.1.1,unix:");
        assertEquals("1.1.1.1, testua", target.getClientInfo());
    }

    @Test
    public void testGetClientInfo6() {
        TokenCheckableAction target = new BlogAction();
        MockHttpServletRequest mock = new MockHttpServletRequest();
        mock.addHeader("User-Agent", "testua");
        mock.setRemoteAddr("220.1.1.2");
        target.request = mock;

        mock.addHeader("X-Forwarded-For", "1.1.1.1,2.2.2.2,unix:");
        assertEquals("2.2.2.2, testua", target.getClientInfo());
    }

}
