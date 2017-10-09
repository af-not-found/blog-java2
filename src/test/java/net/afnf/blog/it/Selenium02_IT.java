package net.afnf.blog.it;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import net.afnf.blog.service.EntryService;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Selenium02_IT extends SeleniumTestBase {

    @Test
    public void test201_init() {

        // DB初期化、10000件投入
        executeSql("/sql/db-schema.sql");
        executeSql("/sql/db-testdata-copy10000.sql");

        wd.get(baseurl + "/_admin/entries/");
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
    public void test202_user() {

        wd.get(baseurl);
        assertEquals("Markdownのテスト1234789\ntitle10000\ntitle9999\ntitle9998\ntitle9997\ntitle9996\ntitle9995\ntitle9994\nmore...",
                find(".sb_recents").get(0).getText());

        wd.findElement(By.linkText("3")).click();
        wd.findElement(By.linkText("8")).click();
        wd.findElement(By.linkText("14")).click();
        wd.findElement(By.linkText("20")).click();
        wd.findElement(By.linkText("26")).click();
        wd.findElement(By.linkText(">")).click();
        wd.findElement(By.linkText(">")).click();
        wd.findElement(By.linkText(">")).click();
        wd.findElement(By.linkText("35")).click();
        wd.findElement(By.linkText("Sep 2012")).click();
        wd.findElement(By.linkText("title9430")).click();
        wd.findElement(By.id("name")).click();
        wd.findElement(By.id("name")).clear();
        wd.findElement(By.id("name")).sendKeys("fwe");
        wd.findElement(By.id("content")).click();
        wd.findElement(By.id("content")).clear();
        wd.findElement(By.id("content")).sendKeys("feafefea");
        postAndCloseModal();

        find(".sb_tag a").get(2).click();
        wd.findElement(By.linkText("12")).click();
        wd.findElement(By.linkText(">")).click();
        wd.findElement(By.linkText("7")).click();
        wd.findElement(By.linkText("11")).click();
        wd.findElement(By.linkText("<")).click();
        wd.findElement(By.linkText("<")).click();
        wd.findElement(By.linkText("<")).click();
        wd.findElement(By.linkText("<")).click();
        wd.findElement(By.linkText("14")).click();
        wd.findElement(By.linkText("20")).click();
        wd.findElement(By.linkText("26")).click();
        wd.findElement(By.linkText("32")).click();
        wd.findElement(By.linkText("38")).click();
        wd.findElement(By.linkText("44")).click();
        find(".sb_tag a").get(3).click();
        wd.findElement(By.linkText("14")).click();
        find(".sb_recents .sb_more a").get(0).click();
        wd.findElement(By.linkText("title9997")).click();

        find(".sb_tags .sb_more a").get(0).click();
        wd.findElement(By.linkText("tag4")).click();
        find(".sb_tags .sb_more a").get(0).click();
        List<WebElement> matches = find(".sb_matched");
        assertEquals(1, matches.size());
        assertThat(matches.get(0).getText(), startsWith("tag4 ("));
        matches = find(".label-success");
        assertEquals(1, matches.size());
        assertThat(matches.get(0).getText(), startsWith("tag4 ("));

        wd.findElement(By.linkText("タグb")).click();
        matches = find(".sb_matched");
        assertEquals(1, matches.size());
        assertThat(matches.get(0).getText(), startsWith("タグb ("));
        matches = find(".label-success");
        assertEquals(1, matches.size());
        assertThat(matches.get(0).getText(), startsWith("タグb ("));

        wd.findElement(By.linkText("tag1")).click();
        wd.findElement(By.linkText("ALL")).click();
        wd.findElement(By.linkText("12")).click();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        String yyyyMM = sdf.format(new Date());
        wd.get(baseurl + "/m/" + yyyyMM);

        List<WebElement> cmcount = find(".cmcount");
        assertThat(cmcount.get(0).getText(), endsWith("(3)"));
        assertThat(cmcount.get(1).getText(), endsWith("(1)"));
        assertThat(cmcount.get(2).getText(), endsWith("(670)"));
        assertEquals(3, cmcount.size());

        assertEquals(EntryService.LIMIT, StringUtils.countMatches(getRssString(), "<item>"));
    }

    @Test
    public void test203_admin() {
        wd.get(baseurl + "/_admin/entries/");
        assertEquals(baseurl + "/_admin/entries/", wd.getCurrentUrl());

        wd.findElement(By.linkText("5")).click();
        wd.findElement(By.linkText("10")).click();
        wd.findElement(By.linkText("15")).click();
        wd.findElement(By.linkText(">")).click();
        wd.findElement(By.linkText(">")).click();
        wd.findElement(By.linkText("20")).click();
        wd.findElement(By.linkText(">")).click();
        wd.findElement(By.linkText(">")).click();
        wd.findElement(By.linkText(">")).click();
        wd.findElement(By.linkText(">")).click();
        wd.findElement(By.linkText(">")).click();
        wd.findElement(By.linkText(">")).click();
        wd.findElement(By.linkText("29")).click();

        wd.findElement(By.linkText("title9132")).click();
        wd.findElement(By.id("r2")).click();
        postAndWait();
        assertAjaxRet(".ajaxform");

        wd.findElement(By.xpath("//div[@class='btn-group']//button[.='comments']")).click();
        wd.findElement(By.linkText("6")).click();
        wd.findElement(By.linkText(">")).click();
        wd.findElement(By.linkText(">")).click();
        wd.findElement(By.linkText("11")).click();
        wd.findElement(By.linkText("16")).click();
        wd.findElement(By.linkText("21")).click();
        wd.findElement(By.linkText("26")).click();
        wd.findElement(By.linkText("<")).click();
        wd.findElement(By.linkText("<")).click();
        wd.findElement(By.linkText("<")).click();
        wd.findElement(By.linkText("<")).click();
        wd.findElement(By.linkText("<")).click();
        wd.findElement(By.linkText("<")).click();
        wd.findElement(By.linkText("<")).click();
        wd.findElement(By.linkText("<")).click();
        wd.findElement(By.linkText("17")).click();

        wd.findElement(By.id("c678_r2")).click();
        wd.findElement(By.id("c677_r1")).click();
        wd.findElement(By.id("c676_r1")).click();
        wd.findElement(By.id("c676_r2")).click();

        wd.findElement(By.linkText("18")).click();
        wd.findElement(By.id("c662_r1")).click();
        wd.findElement(By.id("c661_r1")).click();
        wd.findElement(By.id("c657_r2")).click();
        wd.findElement(By.id("c655_r2")).click();
        wd.findElement(By.id("c654_r1")).click();
        wd.findElement(By.id("c653_r1")).click();
        wd.findElement(By.id("c651_r2")).click();

        wd.findElement(By.linkText("22")).click();
        wd.findElement(By.linkText("25")).click();
        wd.findElement(By.linkText("28")).click();
        wd.findElement(By.linkText("32")).click();
        wd.findElement(By.linkText("35")).click();
        wd.findElement(By.linkText("38")).click();
        wd.findElement(By.linkText("39")).click();
        wd.findElement(By.linkText("44")).click();
        wd.findElement(By.linkText("47")).click();
        wd.findElement(By.linkText("50")).click();
        wd.findElement(By.linkText("51")).click();
        wd.findElement(By.id("c6_r1")).click();
        wd.findElement(By.linkText("2")).click();
        assertThat(wd.findElement(By.className("cmcount")).getText(), endsWith("(1)"));
    }

    @Test
    public void test204_user() throws ParseException {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMM");
        String yyyyMM = sdf1.format(new Date());
        wd.get(baseurl + "/m/" + yyyyMM);

        List<WebElement> cmcount = find(".cmcount");
        assertThat(cmcount.get(0).getText(), endsWith("(3)"));
        assertThat(cmcount.get(1).getText(), endsWith("(1)"));
        assertThat(cmcount.get(2).getText(), endsWith("(668)"));
        assertEquals(3, cmcount.size());

        assertEquals(4, find(".summary_entry_title").size());
        assertEquals(8, find(".sb_entry_title").size());
        assertEquals(18, find(".sb_tag").size());
        assertEquals(34, find(".sb_month").size());

        List<WebElement> matches;

        SimpleDateFormat sdf2 = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
        String mmmyyyy = sdf2.format(new Date());
        wd.findElement(By.linkText(mmmyyyy)).click();
        find(".sb_archive .sb_more a").get(0).click();
        matches = find(".sb_matched");
        assertEquals(1, matches.size());
        assertThat(matches.get(0).getText(), startsWith(mmmyyyy + " ("));

        mmmyyyy = sdf2.format(sdf1.parse("201101"));
        wd.findElement(By.linkText(mmmyyyy)).click();
        find(".sb_archive .sb_more a").get(0).click();
        matches = find(".sb_matched");
        assertEquals(1, matches.size());
        assertThat(matches.get(0).getText(), startsWith(mmmyyyy + " ("));

        mmmyyyy = sdf2.format(sdf1.parse("201102"));
        wd.findElement(By.linkText(mmmyyyy)).click();
        find(".sb_archive .sb_more a").get(0).click();
        matches = find(".sb_matched");
        assertEquals(1, matches.size());
        assertThat(matches.get(0).getText(), startsWith(mmmyyyy + " ("));

        mmmyyyy = sdf2.format(sdf1.parse("201103"));
        wd.findElement(By.linkText(mmmyyyy)).click();
        find(".sb_archive .sb_more a").get(0).click();

        mmmyyyy = sdf2.format(sdf1.parse("201104"));
        wd.findElement(By.linkText(mmmyyyy)).click();
        find(".sb_archive .sb_more a").get(0).click();

        List<WebElement> months = find(".sb_month");
        assertEquals(34, months.size());
        assertEquals(sdf2.format(new Date()) + " (4)", months.get(0).getText());
        assertEquals(sdf2.format(sdf1.parse("201210")) + " (271)", months.get(1).getText());
        assertEquals(sdf2.format(sdf1.parse("201209")) + " (300)", months.get(2).getText());
        assertEquals(sdf2.format(sdf1.parse("201208")) + " (309)", months.get(3).getText());

        wd.findElement(By.linkText("tag1")).click();
        wd.findElement(By.linkText("tag0")).click();
        find(".sb_tags .sb_more a").get(0).click();
        wd.findElement(By.linkText("tag8")).click();
        find(".sb_tags .sb_more a").get(0).click();
        wd.findElement(By.linkText("tag9")).click();

        find(".sb_tags .sb_more a").get(0).click();
        List<WebElement> tags = find(".sb_tag");
        assertEquals(18, tags.size());
        assertEquals("tag1 (10003)", tags.get(0).getText());
        assertEquals("tag3 (10003)", tags.get(1).getText());
        assertEquals("タグa (10002)", tags.get(2).getText());
        assertEquals("tag9 (625)", tags.get(16).getText());
        assertEquals("tag12 (624)", tags.get(17).getText());
    }

    @Test
    public void test205_user() {

        wd.get(baseurl + "/?page=323");
        wd.findElement(By.linkText("327")).click();
        wd.findElement(By.linkText("334")).click();
        wd.findElement(By.linkText("<")).click();
        wd.findElement(By.linkText(">")).click();
        wd.findElement(By.linkText("title1")).click();

        wd.findElement(By.id("name")).click();
        wd.findElement(By.id("name")).clear();
        wd.findElement(By.id("name")).sendKeys("test");
        wd.findElement(By.id("content")).click();
        wd.findElement(By.id("content")).clear();
        wd.findElement(By.id("content")).sendKeys("test");
        postAndCloseModal();

        wd.navigate().back();
        wd.findElement(By.linkText("333")).click();
        wd.findElement(By.linkText(">")).click();

        List<WebElement> cmcount = find(".cmcount");
        assertThat(cmcount.get(0).getText(), endsWith("(1)"));
        assertThat(cmcount.get(1).getText(), endsWith("(3)"));
        assertThat(cmcount.get(2).getText(), endsWith("(1)"));
        assertThat(cmcount.get(3).getText(), endsWith("(668)"));
        assertEquals(4, cmcount.size());
    }

    @Test
    public void test206_user() {
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
    public void test207_admin() {

        wd.get(baseurl + "/_admin/entries/");
        assertEquals(baseurl + "/_admin/entries/", wd.getCurrentUrl());
        assertElementNotFound(".state_draft");
        assertElementNotFound(".state_deleted");

        wd.get(baseurl + "/_admin/entries/10000");
        assertEquals(baseurl + "/_admin/entries/10000", wd.getCurrentUrl());
        wd.findElement(By.id("r2")).click();
        postAndWait();
        assertAjaxRet(".ajaxform");

        wd.get(baseurl + "/_admin/entries/10002");
        assertEquals(baseurl + "/_admin/entries/10002", wd.getCurrentUrl());
        wd.findElement(By.id("r0")).click();
        postAndWait();
        assertAjaxRet(".ajaxform");

        wd.get(baseurl + "/_admin/entries/");
        assertEquals(baseurl + "/_admin/entries/", wd.getCurrentUrl());
        assertEquals(1, find(".state_draft").size());
        assertEquals(1, find(".state_deleted").size());
    }

    @Test
    public void test208_user() {

        wd.get(baseurl);
        assertEquals("Markdownのテスト1234789\ntitle10000\ntitle9998\ntitle9996\ntitle9995\ntitle9994\ntitle9993\ntitle9992\nmore...",
                find(".sb_recents").get(0).getText());
    }

}
