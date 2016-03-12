package net.afnf.blog.selenium;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import net.afnf.blog.it.SeleniumTestBase;

class ClickAndWaitRemoteWebElement implements WebElement {

    private static Log logger = LogFactory.getLog(ClickAndWaitRemoteWebElement.class);

    private WebElement parent = null;
    private WebDriverWrapper wd = null;

    public ClickAndWaitRemoteWebElement(WebElement e, WebDriverWrapper wd) {
        parent = e;
        this.wd = wd;
    }

    @Override
    public void click() {
        try {
            parent.click();
        }
        catch (TimeoutException e) {
            // phantomjsが偶発的にTimeoutExceptionを出してしまう
            if (wd.getInstance() instanceof PhantomJSDriver) {
                SeleniumTestBase.takeScreenShot(wd, "TimeoutException");
                logger.warn("TimeoutException", e);
            }
            else {
                throw e;
            }
        }
        wd.waitForPageLoaded();
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> arg0) throws WebDriverException {
        return parent.getScreenshotAs(arg0);
    }

    @Override
    public void clear() {
        parent.clear();
    }

    @Override
    public WebElement findElement(By arg0) {
        return parent.findElement(arg0);
    }

    @Override
    public List<WebElement> findElements(By arg0) {
        return parent.findElements(arg0);
    }

    @Override
    public String getAttribute(String arg0) {
        return parent.getAttribute(arg0);
    }

    @Override
    public String getCssValue(String arg0) {
        return parent.getCssValue(arg0);
    }

    @Override
    public Point getLocation() {
        return parent.getLocation();
    }

    @Override
    public Dimension getSize() {
        return parent.getSize();
    }

    @Override
    public String getTagName() {
        return parent.getTagName();
    }

    @Override
    public String getText() {
        return parent.getText();
    }

    @Override
    public boolean isDisplayed() {
        return parent.isDisplayed();
    }

    @Override
    public boolean isEnabled() {
        return parent.isEnabled();
    }

    @Override
    public boolean isSelected() {
        return parent.isSelected();
    }

    @Override
    public void sendKeys(CharSequence... arg0) {
        parent.sendKeys(arg0);
    }

    @Override
    public void submit() {
        parent.submit();
    }

    @Override
    public Rectangle getRect() {
        return parent.getRect();
    }
}