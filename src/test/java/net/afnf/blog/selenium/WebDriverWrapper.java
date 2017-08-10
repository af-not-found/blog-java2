package net.afnf.blog.selenium;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WebDriverWrapper implements WebDriver, TakesScreenshot, JavascriptExecutor {

    private RemoteWebDriver instance;
    private ExpectedCondition<Boolean> expectation = null;
    private static Map<String, Class<? extends RemoteWebDriver>> webDriverMap = new HashMap<String, Class<? extends RemoteWebDriver>>();

    static {
        webDriverMap.put("firefox", org.openqa.selenium.firefox.FirefoxDriver.class);
        webDriverMap.put("chrome", org.openqa.selenium.chrome.ChromeDriver.class);
        webDriverMap.put("chrome-headless", org.openqa.selenium.chrome.ChromeDriver.class);
        webDriverMap.put("ie", org.openqa.selenium.ie.InternetExplorerDriver.class);
    }

    public RemoteWebDriver getInstance() {
        return instance;
    }

    public WebDriverWrapper(String driverName) throws Exception {

        Class<? extends RemoteWebDriver> clazz = webDriverMap.get(driverName);

        if (clazz.equals(InternetExplorerDriver.class)) {
            DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
            capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
            instance = new InternetExplorerDriver(capabilities);
        }
        else if (clazz.equals(FirefoxDriver.class)) {
            // FIXME selenium 2.53.xではもはやFirefoxDriverが正常動作しない
            DesiredCapabilities capabilities = DesiredCapabilities.firefox();
            capabilities.setCapability("marionette", true);
            instance = new FirefoxDriver(capabilities);
        }
        else if(driverName.equals("chrome-headless")) {
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("--headless");
            chromeOptions.addArguments("--disable-gpu");
            instance = new ChromeDriver(chromeOptions);
        }
        else {
            instance = webDriverMap.get(driverName).newInstance();
        }

        expectation = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver)
                        .executeScript("return document.readyState == 'complete' && afnfblog.loading == false").equals(true);
            }
        };
    }

    protected void waitForPageLoaded() {
        FluentWait<WebDriver> wait = new WebDriverWait(this, 100);

        wait.until(new Function<WebDriver, Boolean>() {
            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver)
                        .executeScript("return document.readyState == 'complete' && afnfblog.loading == false").equals(true);
            }
        });
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
