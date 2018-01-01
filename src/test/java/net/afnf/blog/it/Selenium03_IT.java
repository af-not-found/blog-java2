package net.afnf.blog.it;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Selenium03_IT extends SeleniumTestBase {

    private static Logger logger = LoggerFactory.getLogger(Selenium03_IT.class);

    @Test
    public void test301_init() {

        // DB初期化、10000件投入
        executeSql("/sql/db-schema.sql");
        executeSql("/sql/db-testdata-copy10000.sql");

        wd.get(baseurl + "/_admin/entries/");
        assertThat(wd.getCurrentUrl(), startsWith(baseurl + "/_admin/pub/login"));

        wd.findElement(By.name("username")).click();
        wd.findElement(By.name("username")).clear();
        wd.findElement(By.name("username")).sendKeys("admin");
        wd.findElement(By.name("password")).click();
        wd.findElement(By.name("password")).clear();
        wd.findElement(By.name("password")).sendKeys("pass");
        postAndWait();

        // キャッシュ更新
        wd.findElement(By.xpath("//div[@class='btn-group']//button[.='cache']")).click();
        wd.findElement(By.name("update")).click();

        assertEquals("10004", wd.findElement(By.className("totalNormalCount")).getText());
        assertEquals("18", wd.findElement(By.className("tagCount")).getText());
        assertEquals("34", wd.findElement(By.className("monthCount")).getText());
    }

    @Test
    public void test302_user() {

        wd.get(baseurl);
        assertEquals("Markdownのテスト1234789\ntitle10000\ntitle9999\ntitle9998\ntitle9997\ntitle9996\ntitle9995\ntitle9994\nmore...",
                find(".sb_recents").get(0).getText());

        wd.get(baseurl + "/m/201003");

        wd.findElement(By.cssSelector(".previous a")).click();
        assertThat(wd.getCurrentUrl(), endsWith("201002"));

        wd.findElement(By.cssSelector(".next a")).click();
        assertThat(wd.getCurrentUrl(), endsWith("201003"));

        wd.findElement(By.cssSelector(".previous a")).click();
        assertThat(wd.getCurrentUrl(), endsWith("201002"));

        wd.findElement(By.cssSelector(".next a")).click();
        assertThat(wd.getCurrentUrl(), endsWith("201003"));

        wd.findElement(By.cssSelector(".next a")).click();
        assertThat(wd.getCurrentUrl(), endsWith("201004"));

        wd.findElement(By.linkText("Sep 2012")).click();

        wd.findElement(By.cssSelector(".next a")).click();
        assertThat(wd.getCurrentUrl(), endsWith("201210"));

        wd.findElement(By.cssSelector(".next a")).click();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        String yyyyMM = sdf.format(new Date());
        assertThat(wd.getCurrentUrl(), endsWith(yyyyMM));

        wd.findElement(By.cssSelector(".previous a")).click();
        assertThat(wd.getCurrentUrl(), endsWith("201210"));

        wd.findElement(By.cssSelector(".next a")).click();
        assertThat(wd.getCurrentUrl(), endsWith(yyyyMM));

        wd.findElement(By.cssSelector(".previous a")).click();
        assertThat(wd.getCurrentUrl(), endsWith("201210"));

        wd.findElement(By.cssSelector(".previous a")).click();
        assertThat(wd.getCurrentUrl(), endsWith("201209"));
    }

    @Test
    public void test303_admin() {

        wd.get(baseurl + "/_admin/entries/");
        assertEquals(baseurl + "/_admin/entries/", wd.getCurrentUrl());

        wd.findElement(By.xpath("//div[@class='btn-group']//button[.='metrics']")).click();

        boolean dbconn_active = false;
        boolean instance_uptime = false;
        boolean threads_peak = false;
        boolean classes_loaded = false;

        List<WebElement> elements = wd.findElements(By.cssSelector(".border_table_container tbody tr"));
        for (WebElement element : elements) {
            String text = element.getText();
            logger.info(text);
            dbconn_active |= StringUtils.startsWith(text, "data.source.active.connections[name=dataSource]");
            instance_uptime |= StringUtils.startsWith(text, "process.uptime");
            threads_peak |= StringUtils.startsWith(text, "jvm.threads.peak");
            classes_loaded |= StringUtils.startsWith(text, "jvm.classes.loaded");
        }

        assertTrue(dbconn_active);
        assertTrue(instance_uptime);
        assertTrue(threads_peak);
        assertTrue(classes_loaded);
        assertThat(elements.size(), is(greaterThanOrEqualTo(41)));
    }

    @Test
    public void test304_admin() {

        // 論理削除
        executeSql("/sql/db-logical-delete.sql");

        wd.get(baseurl + "/_admin/entries/");
        assertEquals(baseurl + "/_admin/entries/", wd.getCurrentUrl());

        wd.findElement(By.xpath("//div[@class='btn-group']//button[.='cache']")).click();

        assertEquals("10004", wd.findElement(By.className("totalNormalCount")).getText());
        assertEquals("18", wd.findElement(By.className("tagCount")).getText());
        assertEquals("34", wd.findElement(By.className("monthCount")).getText());

        // キャッシュ更新
        wd.findElement(By.name("update")).click();

        assertEquals("0", wd.findElement(By.className("totalNormalCount")).getText());
        assertEquals("0", wd.findElement(By.className("tagCount")).getText());
        assertEquals("0", wd.findElement(By.className("monthCount")).getText());
    }

    @Test
    public void test305_user() {

        wd.get(baseurl);
        assertElementNotFound(".summary_entry_title");
        assertElementNotFound(".sb_entry_title");
        assertElementNotFound(".sb_tag");
        assertElementNotFound(".sb_month");

        assertEquals(0, StringUtils.countMatches(getRssString(), "<item>"));
    }

    @Test
    public void test306_admin() {

        // 論理削除
        executeSql("/sql/db-truncate.sql");

        wd.get(baseurl + "/_admin/entries/");
        assertEquals(baseurl + "/_admin/entries/", wd.getCurrentUrl());

        wd.findElement(By.xpath("//div[@class='btn-group']//button[.='cache']")).click();

        assertEquals("0", wd.findElement(By.className("totalNormalCount")).getText());
        assertEquals("0", wd.findElement(By.className("tagCount")).getText());
        assertEquals("0", wd.findElement(By.className("monthCount")).getText());

        // キャッシュ更新
        wd.findElement(By.name("update")).click();

        assertEquals("0", wd.findElement(By.className("totalNormalCount")).getText());
        assertEquals("0", wd.findElement(By.className("tagCount")).getText());
        assertEquals("0", wd.findElement(By.className("monthCount")).getText());
    }

    @Test
    public void test307_user() {

        wd.get(baseurl);
        assertElementNotFound(".summary_entry_title");
        assertElementNotFound(".sb_entry_title");
        assertElementNotFound(".sb_tag");
        assertElementNotFound(".sb_month");

        assertEquals(0, StringUtils.countMatches(getRssString(), "<item>"));
    }
}
