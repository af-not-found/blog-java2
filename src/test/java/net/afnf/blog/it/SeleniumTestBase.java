package net.afnf.blog.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClients;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Timeouts;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;

import net.afnf.blog.SpringTestBase;
import net.afnf.blog.common.AfnfUtil;
import net.afnf.blog.config.AppConfig;
import net.afnf.blog.mapper.EntryMapperCustomized;
import net.afnf.blog.selenium.SeleniumTestWatcher;
import net.afnf.blog.selenium.WebDriverWrapper;

public class SeleniumTestBase extends SpringTestBase {

    protected static final long POST_WAIT = 500;
    protected static final long NO_ELEMENT_WAIT = 200;
    protected static final long AJAX_WAIT = 200;
    protected static final long IMPLICITLY_WAIT = 4000;
    protected static final long CACHE_UPDATE_WAIT = 2000;

    protected static String baseurl;
    protected static WebDriverWrapper wd;

    @Rule
    public SeleniumTestWatcher watcher = new SeleniumTestWatcher();

    @Autowired
    protected EntryMapperCustomized em;

    @Before
    public void before() throws Exception {

        // @BeforeClassではAppConfigが取れないのでこの方法で。
        if (baseurl == null) {
            AppConfig appConfig = AppConfig.getInstance();
            baseurl = appConfig.getSeleniumTargetUrl();
            wd = new WebDriverWrapper(appConfig.getSeleniumWebdriver());
            wd.manage().window().setSize(new Dimension(1000, 700));
            wd.manage().timeouts().implicitlyWait(IMPLICITLY_WAIT, TimeUnit.MILLISECONDS);
            wd.manage().timeouts().pageLoadTimeout(IMPLICITLY_WAIT, TimeUnit.MILLISECONDS);
        }
        watcher.wd = wd;
    }

    @AfterClass
    public static void afterClass() {
        if (wd != null) {
            wd.quit();
            baseurl = null;
        }
    }

    protected void assertElementNotFound(String cssPath) {
        assertElementNotFound(null, cssPath);
    }

    protected void assertElementNotFound(String message, String cssPath) {
        Timeouts timeouts = wd.manage().timeouts();
        timeouts.implicitlyWait(NO_ELEMENT_WAIT, TimeUnit.MILLISECONDS);
        assertEquals(message, 0, find(cssPath).size());
        timeouts.implicitlyWait(IMPLICITLY_WAIT, TimeUnit.MILLISECONDS);
    }

    protected List<WebElement> find(String cssPath) {
        return wd.findElements(By.cssSelector(cssPath));
    }

    protected void postAndWait() {
        wd.findElement(By.name("post")).click();
    }

    protected void postAndCloseModal() {
        wd.findElement(By.name("post")).click();
        // FirefoxDriverだと遅延が必要っぽい
        if (wd.getInstance() instanceof FirefoxDriver) {
            AfnfUtil.sleep(200);
        }
        wd.findElement(By.id("close_modal")).click();
    }

    protected void assertAjaxRet(String filter) {

        int i = 0;
        do {
            AfnfUtil.sleep(AJAX_WAIT);
            String ret1 = find(filter + " .ajaxret").get(0).getText();
            if (StringUtils.isNotBlank(ret1)) {
                return;
            }
        }
        while (++i <= 2);

        fail("ajaxRet is blank");
    }

    public static void takeScreenShot(WebDriver wd, String suffix) {
        if (wd != null) {
            File tmpFile = ((TakesScreenshot) wd).getScreenshotAs(OutputType.FILE);
            try {
                FileUtils.copyFile(tmpFile, new File("target/" + System.currentTimeMillis() + suffix + ".png"));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected String getRssString() {
        String rssString = null;
        try {
            rssString = IOUtils.toString(new URL(baseurl + "/rss.xml").openStream(), "UTF-8");
        }
        catch (Exception e) {
            new IllegalStateException(e);
        }
        return rssString;
    }

    protected void checkSecurityHeaders() {

        // http-headerを読む方法がSeleniumに無いらしいので、HttpClientを使う
        HttpUriRequest req = new HttpGet(baseurl);
        try (CloseableHttpResponse res = HttpClients.createDefault().execute(req)) {
            assertEquals("1; mode=block", res.getFirstHeader("X-XSS-Protection").getValue());
            assertEquals("nosniff", res.getFirstHeader("x-content-type-options").getValue());
            assertEquals("DENY", res.getFirstHeader("x-frame-options").getValue());
            assertNull(res.getFirstHeader("Strict-Transport-Security"));
            assertNull(res.getFirstHeader("Cache-Control"));
        }
        catch (Exception e) {
            fail(e.toString());
        }
    }
}
