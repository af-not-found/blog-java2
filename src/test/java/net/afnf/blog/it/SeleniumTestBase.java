package net.afnf.blog.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.afnf.blog.SpringTestBase;
import net.afnf.blog.common.AfnfUtil;
import net.afnf.blog.config.AppConfig;
import net.afnf.blog.mapper.EntryMapperCustomized;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Timeouts;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;

public class SeleniumTestBase extends SpringTestBase {

    protected static final long POST_WAIT = 500;
    protected static final long NO_ELEMENT_WAIT = 200;
    protected static final long AJAX_WAIT = 200;
    protected static final long IMPLICITLY_WAIT = 10000;

    protected static String baseurl;
    protected static WebDriver wd;

    @Rule
    public SeleniumTestWatcher watcher = new SeleniumTestWatcher();

    @Autowired
    protected EntryMapperCustomized em;

    protected static Map<String, String> webDriverMap = new HashMap<String, String>();
    static {
        webDriverMap.put("firefox", "org.openqa.selenium.firefox.FirefoxDriver");
        webDriverMap.put("phantomjs", "org.openqa.selenium.phantomjs.PhantomJSDriver");
        webDriverMap.put("chrome", "org.openqa.selenium.chrome.ChromeDriver");
        webDriverMap.put("ie", "org.openqa.selenium.ie.InternetExplorerDriver");
    }

    @Before
    public void before() throws Exception {
        if (baseurl == null) {
            AppConfig appConfig = AppConfig.getInstance();
            baseurl = appConfig.getSeleniumTargetUrl();
            wd = (WebDriver) Class.forName(webDriverMap.get(appConfig.getSeleniumWebdriver())).newInstance();
            wd.manage().window().setSize(new Dimension(1000, 700));
            wd.manage().timeouts().implicitlyWait(IMPLICITLY_WAIT, TimeUnit.MILLISECONDS);
        }
        watcher.wd = wd;
    }

    @AfterClass
    public static void afterClass() {
        wd.quit();
        baseurl = null;
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

    protected void waitForPageLoaded() {

        ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver).executeScript("return afnfblog.loading").equals(false);
            }
        };

        Wait<WebDriver> wait = new WebDriverWait(wd, IMPLICITLY_WAIT / 1000);
        try {
            wait.until(expectation);
        }
        catch (Throwable error) {
            assertFalse("waitForPageLoaded timeout", true);
        }
    }

    protected void waitForLoaded() {
        AfnfUtil.sleep(POST_WAIT);
        waitForPageLoaded();
    }

    protected void postAndWait() {
        wd.findElement(By.name("post")).click();
        waitForLoaded();
    }

    protected void postAndCloseModal() {
        wd.findElement(By.name("post")).click();
        waitForLoaded();
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
        File tmpFile = ((TakesScreenshot) wd).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(tmpFile, new File("target/" + System.currentTimeMillis() + suffix + ".png"));
        }
        catch (IOException e) {
            e.printStackTrace();
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
}
