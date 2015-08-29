package net.afnf.blog.selenium;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.afnf.blog.it.SeleniumTestBase;

public class SeleniumTestWatcher extends TestWatcher {

    private static Logger logger = LoggerFactory.getLogger(SeleniumTestWatcher.class);

    public WebDriver wd;

    @Override
    protected void succeeded(Description description) {
        super.succeeded(description);
        log(description, true);
    }

    @Override
    protected void failed(Throwable e, Description description) {
        SeleniumTestBase.takeScreenShot(wd, String.format("_%s_%s", description.getClassName(), description.getMethodName()));
        super.failed(e, description);
        log(description, false);
    }

    protected void log(Description description, boolean success) {
        logger.info(description.getMethodName() + " " + (success ? "succeed" : "failed"));
    }
}
