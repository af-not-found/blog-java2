package net.afnf.blog.selenium;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import net.afnf.blog.common.AfnfUtil;

public class WebDriverWrapper implements WebDriver, TakesScreenshot, JavascriptExecutor {

    private RemoteWebDriver instance;
    private ExpectedCondition<Boolean> expectation = null;
    private static Map<String, Class<? extends RemoteWebDriver>> webDriverMap = new HashMap<String, Class<? extends RemoteWebDriver>>();

    static {
        webDriverMap.put("firefox", org.openqa.selenium.firefox.FirefoxDriver.class);
        webDriverMap.put("phantomjs", org.openqa.selenium.phantomjs.PhantomJSDriver.class);
        webDriverMap.put("chrome", org.openqa.selenium.chrome.ChromeDriver.class);
        webDriverMap.put("ie", org.openqa.selenium.ie.InternetExplorerDriver.class);
    }

    public RemoteWebDriver getInstance() {
        return instance;
    }

    public WebDriverWrapper(String driverName) throws Exception {
        instance = webDriverMap.get(driverName).newInstance();

        if (instance instanceof InternetExplorerDriver) {
            DesiredCapabilities capabilities = (DesiredCapabilities) ((InternetExplorerDriver) getInstance()).getCapabilities();
            capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
        }

        expectation = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver)
                        .executeScript("return document.readyState == 'complete' && afnfblog.loading == false").equals(true);
            }
        };
    }

    protected void waitForPageLoaded() {
        AfnfUtil.sleep(10);
        Wait<WebDriver> wait = new WebDriverWait(this, 1000);
        wait.until(expectation);
    }

    @Override
    public WebElement findElement(By by) {
        WebElement e = getInstance().findElement(by);
        if (e instanceof RemoteWebElement) {
            e = new ClickAndWaitRemoteWebElement(e, this);
        }
        return e;
    }

    @Override
    public List<WebElement> findElements(By by) {
        List<WebElement> list = getInstance().findElements(by);
        List<WebElement> newList = new ArrayList<>(list.size());
        for (WebElement e : list) {
            if (e instanceof RemoteWebElement) {
                e = new ClickAndWaitRemoteWebElement(e, this);
            }
            newList.add(e);
        }
        return newList;
    }

    @Override
    public void get(String url) {
        getInstance().get(url);
    }

    @Override
    public String getCurrentUrl() {
        return getInstance().getCurrentUrl();
    }

    @Override
    public String getTitle() {
        return getInstance().getTitle();
    }

    @Override
    public String getPageSource() {
        return getInstance().getPageSource();
    }

    @Override
    public void close() {
        getInstance().close();
    }

    @Override
    public void quit() {
        getInstance().quit();
    }

    @Override
    public Set<String> getWindowHandles() {
        return getInstance().getWindowHandles();
    }

    @Override
    public String getWindowHandle() {
        return getInstance().getWindowHandle();
    }

    @Override
    public TargetLocator switchTo() {
        return getInstance().switchTo();
    }

    @Override
    public Navigation navigate() {
        return getInstance().navigate();
    }

    @Override
    public Options manage() {
        return getInstance().manage();
    }

    @Override
    public Object executeScript(String script, Object... args) {
        return getInstance().executeScript(script, args);
    }

    @Override
    public Object executeAsyncScript(String script, Object... args) {
        return getInstance().executeAsyncScript(script, args);
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        return getInstance().getScreenshotAs(target);
    }
}
