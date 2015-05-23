package net.afnf.blog.it;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import net.afnf.blog.common.Crypto;
import net.afnf.blog.service.TokenService;

import org.apache.commons.lang3.StringUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Selenium01_IT extends SeleniumTestBase {

    private static Logger logger = LoggerFactory.getLogger(Selenium01_IT.class);

    @Test
    public void test101_init() {

        // DB初期化
        executeSql("/sql/db-schema.sql");
    }

    @Test
    public void test102_admin() {

        wd.get(baseurl + "/_admin/entries/");
        assertThat(wd.getCurrentUrl(), startsWith(baseurl + "/_admin/pub/login"));

        wd.findElement(By.name("username")).click();
        wd.findElement(By.name("username")).clear();
        wd.findElement(By.name("username")).sendKeys("admin");
        wd.findElement(By.name("password")).click();
        wd.findElement(By.name("password")).clear();
        wd.findElement(By.name("password")).sendKeys("pass");
        postAndCloseModal();
        assertThat(wd.getCurrentUrl(), startsWith(baseurl + "/_admin/pub/login"));

        wd.findElement(By.name("username")).click();
        wd.findElement(By.name("username")).clear();
        wd.findElement(By.name("username")).sendKeys("admin");
        wd.findElement(By.name("password")).click();
        wd.findElement(By.name("password")).clear();
        wd.findElement(By.name("password")).sendKeys("test");
        postAndWait();
        assertThat(wd.getCurrentUrl(), startsWith(baseurl + "/_admin/pub/login"));
        assertThat(wd.getCurrentUrl(), containsString("faliled"));

        wd.findElement(By.name("username")).click();
        wd.findElement(By.name("username")).clear();
        wd.findElement(By.name("username")).sendKeys("admin");
        wd.findElement(By.name("password")).click();
        wd.findElement(By.name("password")).clear();
        wd.findElement(By.name("password")).sendKeys("pass");
        postAndWait();
        assertEquals(baseurl + "/_admin/entries/", wd.getCurrentUrl());

        assertElementNotFound(".summary_entry_title");

        // キャッシュ更新
        wd.findElement(By.xpath("//div[@class='btn-group']//button[.='cache']")).click();
        wd.findElement(By.name("update")).click();
        waitForCacheUpdate();

        assertEquals("0", wd.findElement(By.className("totalNormalCount")).getText());
        assertEquals("0", wd.findElement(By.className("tagCount")).getText());
        assertEquals("0", wd.findElement(By.className("monthCount")).getText());

        // セキュリティ用ヘッダのチェック
        checkSecurityHeaders();
    }

    @Test
    public void test103_user() {

        wd.get(baseurl);
        assertElementNotFound(".summary_entry_title");
        assertElementNotFound(".sb_entry_title");
        assertElementNotFound(".sb_tag");
        assertElementNotFound(".sb_month");

        assertEquals(0, StringUtils.countMatches(getRssString(), "<item>"));

        // セキュリティ用ヘッダのチェック
        checkSecurityHeaders();
    }

    @Test
    public void test104_admin() {

        wd.get(baseurl + "/_admin/entries/");
        assertEquals(baseurl + "/_admin/entries/", wd.getCurrentUrl());

        wd.findElement(By.xpath("//div[@class='btn-group']//button[.='new']")).click();
        assertEquals("-1", find("#id").get(0).getAttribute("value"));

        wd.findElement(By.id("title")).click();
        wd.findElement(By.id("title")).clear();
        wd.findElement(By.id("title")).sendKeys("blog1");
        wd.findElement(By.id("tags")).click();
        wd.findElement(By.id("tags")).clear();
        wd.findElement(By.id("tags")).sendKeys("tag1,tag2,tag3");
        wd.findElement(By.id("content")).click();
        wd.findElement(By.id("content")).clear();
        wd.findElement(By.id("content"))
                .sendKeys(
                        "#てすと123\nabc\n\n#code\n````\nif(1==1){\n    alert(1)\n}\n````\n\n#point\n1. aaega\n1. 433\n1. 4343\n 1. 224\n 1. あああ");
        postAndWait();
        assertAjaxRet(".ajaxform");
        assertEquals("1", find("#id").get(0).getAttribute("value"));
        assertEquals(1, em.countWithCond(null).intValue());

        // postボタンが無効状態であること
        assertEquals("true", wd.findElement(By.name("post")).getAttribute("disabled"));

        // postできるようにtitle変更
        wd.findElement(By.id("title")).click();
        wd.findElement(By.id("title")).clear();
        wd.findElement(By.id("title")).sendKeys("blog12");

        // postボタンが有効状態であること
        assertNull(wd.findElement(By.name("post")).getAttribute("disabled"));

        postAndWait();
        assertEquals("1", find("#id").get(0).getAttribute("value"));
        assertEquals(1, em.countWithCond(null).intValue());

        wd.findElement(By.xpath("//div[@class='btn-group']//button[.='entries']")).click();
        assertEquals(1, find(".summary_entry_title").size());
        wd.findElement(By.linkText("blog12")).click();
        wd.findElement(By.id("title")).click();
        wd.findElement(By.id("title")).clear();
        wd.findElement(By.id("title")).sendKeys("blog1234");
        wd.findElement(By.id("content")).click();
        wd.findElement(By.id("content")).clear();
        wd.findElement(By.id("content"))
                .sendKeys(
                        "#てすと123\nabc\n\n#code\n````\nif(1==1){\n    alert(1)\n}\n````\n\n#point\n1. aaega\n1. 433\n1. 4343\n 1. 224\n 1. あああ\n 1. bbb");
        wd.findElement(By.id("r1")).click(); // normal
        postAndWait();
        assertAjaxRet(".ajaxform");

        wd.findElement(By.xpath("//div[@class='btn-group']//button[.='entries']")).click();
        assertEquals(1, find(".summary_entry_title").size());
        wd.findElement(By.linkText("blog1234")).click();
    }

    @Test
    public void test105_user() {

        wd.get(baseurl);
        assertEquals(1, find(".summary_entry_title").size());
        assertEquals(1, find(".sb_entry_title").size());
        assertEquals(3, find(".sb_tag").size());
        assertEquals(1, find(".sb_month").size());

        wd.findElement(By.linkText("blog1234")).click();
        assertEquals(1, find(".comments_container").size());
        assertElementNotFound(".comments_container .comment");

        wd.findElement(By.id("name")).click();
        wd.findElement(By.id("name")).clear();
        wd.findElement(By.id("name")).sendKeys("test2");
        wd.findElement(By.id("content")).click();
        wd.findElement(By.id("content")).clear();
        wd.findElement(By.id("content")).sendKeys("aaaaaaa\"aaaaaa");
        postAndCloseModal();

        assertEquals(1, find(".comments_container .comment").size());
        assertElementNotFound(".comments_container .comment_content");

        find(".sb_recents .sb_more a").get(0).click();
        wd.findElement(By.cssSelector(".headerdiv > h1 > a")).click();

        find(".sb_entry_title a").get(0).click();
        find(".sb_tag a").get(1).click();
        find(".sb_month a").get(0).click();
        find(".sb_recents .sb_more a").get(0).click();
        wd.findElement(By.cssSelector(".headerdiv > h1 > a")).click();

        assertEquals(1, StringUtils.countMatches(getRssString(), "<item>"));
    }

    @Test
    public void test106_user() {
        wd.get(baseurl);

        long future = System.currentTimeMillis() + TokenService.VALID_MS * 3;
        long past = System.currentTimeMillis() - 1;

        String[] tokens = {
                null,
                "aaaaaa",
                "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
                "cwdkJZ3ioCMDTmyJ9PIsGnXzrLF5HlfhbMqipf3cIUE", // 1388695273224
                Crypto.encrypt("" + future),
                Crypto.encrypt("" + past),
                Crypto.encrypt("aaaaaaaa"),
                null };
        int[] counts = { 2, 2, 2, 2, 2, 2, 2, 3 };

        JavascriptExecutor je = (JavascriptExecutor) wd;

        for (int i = 0; i < tokens.length; i++) {
            wd.findElement(By.linkText("blog1234")).click();
            if (tokens[i] != null) {
                logger.debug(i + " : " + tokens[i] + " : " + Crypto.decrypt(tokens[i]));
                je.executeScript("afnfblog.convToken = function(){return '" + tokens[i] + "';}");
            }
            else {
                je.executeScript("afnfblog.convToken = undefined;");
            }
            wd.findElement(By.id("name")).click();
            wd.findElement(By.id("name")).clear();
            wd.findElement(By.id("name")).sendKeys("&tes\n\n\n\'t\\3<b>abbb</b>\"");
            wd.findElement(By.id("content")).click();
            wd.findElement(By.id("content")).clear();
            wd.findElement(By.id("content")).sendKeys("aaaaaaaabbbbbbbbbbaaaaaaaa\"a&aa");
            postAndCloseModal();
            assertEquals("loop" + i, counts[i], find(".comments_container .comment").size());
            assertElementNotFound("loop" + i, ".comments_container .comment_content");
        }
    }

    @Test
    public void test107_admin() {
        wd.get(baseurl + "/_admin/entries/");
        assertEquals(baseurl + "/_admin/entries/", wd.getCurrentUrl());

        assertEquals(1, find(".summary_entry_title").size());

        wd.findElement(By.xpath("//div[@class='btn-group']//button[.='new']")).click();
        wd.findElement(By.id("title")).click();
        wd.findElement(By.id("title")).clear();
        wd.findElement(By.id("title")).sendKeys("ブログ12345");
        wd.findElement(By.id("tags")).click();
        wd.findElement(By.id("tags")).clear();
        wd.findElement(By.id("tags")).sendKeys("tag1, tag3, タグa");
        wd.findElement(By.id("content")).click();
        wd.findElement(By.id("content")).clear();
        wd.findElement(By.id("content")).sendKeys("#テスト\naaa|bbb|ccc\n---|---|---\n1|2|3\nああああ|いいいいい|うううううう\n#|*|abbb");
        // wd.findElement(By.id("r0")).click(); デフォルトでdraft
        postAndWait();
        assertAjaxRet(".ajaxform");
        assertEquals("2", find("#id").get(0).getAttribute("value"));
        assertEquals(2, em.countWithCond(null).intValue());

        wd.findElement(By.xpath("//div[@class='btn-group']//button[.='entries']")).click();
        assertEquals(2, find(".summary_entry_title").size());

        wd.findElement(By.xpath("//div[@class='btn-group']//button[.='cache']")).click();
    }

    @Test
    public void test108_user() {

        wd.get(baseurl);

        // draftなので増えない
        assertEquals(1, find(".summary_entry_title").size());
        assertEquals(1, find(".sb_entry_title").size());
        assertEquals(3, find(".sb_tag").size());
        assertEquals(1, find(".sb_month").size());

        wd.findElement(By.linkText("blog1234")).click();
        wd.findElement(By.id("name")).click();
        wd.findElement(By.id("name")).clear();
        wd.findElement(By.id("name")).sendKeys("test2\nbbbbb<script>location.href='/a';</script>");
        wd.findElement(By.id("content")).click();
        wd.findElement(By.id("content")).clear();
        wd.findElement(By.id("content")).sendKeys(
                "aaaaaaaabbbbbbbbbbbbb\n ああああああ<script>location.href='/b';</script>ああああaaa\"aa\n<b>aaa</b>");
        postAndCloseModal();

        assertEquals(4, find(".comments_container .comment").size());
        assertElementNotFound(".comments_container .comment_content");

        find(".sb_entry_title a").get(0).click();
        find(".sb_tag a").get(2).click();
        find(".sb_month a").get(0).click();
        find(".sb_recents .sb_more a").get(0).click();
        wd.findElement(By.cssSelector(".headerdiv > h1 > a")).click();

        wd.findElement(By.linkText("blog1234")).click();
        wd.findElement(By.id("name")).click();
        wd.findElement(By.id("name")).clear();
        wd.findElement(By.id("name")).sendKeys("test3");
        wd.findElement(By.id("content")).click();
        wd.findElement(By.id("content")).clear();
        wd.findElement(By.id("content")).sendKeys("aaaaaaabbbbbb\n あああ\n<b>aaa</b> vv");
        postAndCloseModal();

        assertEquals(5, find(".comments_container .comment").size());
        assertElementNotFound(".comments_container .comment_content");

        String redirected = baseurl + "/";
        {
            wd.get(baseurl + "/t/aaaaaaaaaaaaaa");
            assertEquals(redirected, wd.getCurrentUrl());

            wd.get(baseurl + "/t/bbbbbbbb?page=11");
            assertEquals(redirected, wd.getCurrentUrl());

            wd.get(baseurl + "/m/aaaaa");
            assertEquals(redirected, wd.getCurrentUrl());

            wd.get(baseurl + "/999999999");
            assertEquals(redirected, wd.getCurrentUrl());

            wd.get(baseurl + "/2");
            assertEquals(redirected, wd.getCurrentUrl());

            wd.get(baseurl + "/1");
            assertEquals(baseurl + "/1", wd.getCurrentUrl());
        }
    }

    @Test
    public void test109_admin() {
        wd.get(baseurl + "/_admin/entries/");
        assertEquals(baseurl + "/_admin/entries/", wd.getCurrentUrl());

        assertEquals(2, find(".summary_entry_title").size());

        wd.findElement(By.linkText("ブログ12345")).click();
        wd.findElement(By.id("content")).click();
        wd.findElement(By.id("content")).clear();
        wd.findElement(By.id("content")).sendKeys("#テスト\naaa|bbb|ccc\n---|---|---\n1|2|3\nああああ|いいいいい|うううううう\n#|*|abbbccc");
        wd.findElement(By.id("r1")).click(); // normal
        wd.findElement(By.cssSelector("span")).click();
        postAndWait();
        assertAjaxRet(".ajaxform");

        wd.findElement(By.xpath("//div[@class='btn-group']//button[.='entries']")).click();
        assertEquals(2, find(".summary_entry_title").size());

        wd.findElement(By.xpath("//div[@class='btn-group']//button[.='comments']")).click();
        assertEquals(5, find(".comments_container .comment_content").size());
        assertEquals(5, find(".comments_container .waiting_comment").size());
        assertElementNotFound(".comments_container .normal_comment");
        assertElementNotFound(".comments_container .deleted_comment");
        wd.findElement(By.id("c2_r1")).click();
        waitForLoaded();
        assertAjaxRet("#c2");

        wd.findElement(By.xpath("//div[@class='btn-group']//button[.='comments']")).click();
        assertEquals(5, find(".comments_container .comment_content").size());
        assertEquals(4, find(".comments_container .waiting_comment").size());
        assertEquals(1, find(".comments_container .normal_comment").size());
        assertElementNotFound(".comments_container .deleted_comment");
        wd.findElement(By.id("c3_r2")).click();
        waitForLoaded();
        assertAjaxRet("#c3");

        wd.findElement(By.xpath("//div[@class='btn-group']//button[.='comments']")).click();
        assertEquals(5, find(".comments_container .comment_content").size());
        assertEquals(3, find(".comments_container .waiting_comment").size());
        assertEquals(1, find(".comments_container .normal_comment").size());
        assertEquals(1, find(".comments_container .deleted_comment").size());
    }

    @Test
    public void test110_user() {

        wd.get(baseurl);
        assertEquals(2, find(".summary_entry_title").size());
        assertEquals(2, find(".sb_entry_title").size());
        assertEquals(4, find(".sb_tag").size());
        assertEquals(1, find(".sb_month").size());

        wd.findElement(By.cssSelector(".summary_entry_title")).click();
        wd.findElement(By.linkText("blog1234")).click();

        assertEquals(4, find(".comments_container .comment").size());
        assertEquals(1, find(".comments_container .comment_content").size());

        wd.findElement(By.id("name")).click();
        wd.findElement(By.id("name")).clear();
        wd.findElement(By.id("name")).sendKeys("aaaa");
        wd.findElement(By.id("content")).click();
        wd.findElement(By.id("content")).clear();
        wd.findElement(By.id("content")).sendKeys("bbbbbbbb");
        postAndCloseModal();

        assertEquals(5, find(".comments_container .comment").size());
        assertEquals(1, find(".comments_container .comment_content").size());

        wd.findElement(By.linkText("ブログ12345")).click();

        assertElementNotFound(".comments_container .comment");
        assertElementNotFound(".comments_container .comment_content");

        wd.findElement(By.id("name")).click();
        wd.findElement(By.id("name")).clear();
        wd.findElement(By.id("name")).sendKeys("ああああああああああああ");
        wd.findElement(By.id("content")).click();
        wd.findElement(By.id("content")).clear();
        wd.findElement(By.id("content")).sendKeys(
                "aaaaaaaabbbbbbbbbbbbb\n ああああああああああaaaaa\n<b>aaa</b>jfdytr\n\n\n\ni7tfjyg\n\n\nfj75rfjytft");
        postAndCloseModal();

        assertEquals(1, find(".comments_container .comment").size());
        assertElementNotFound(".comments_container .comment_content");

        wd.findElement(By.linkText("タグa")).click();
        List<WebElement> matches = find(".sb_matched");
        assertEquals(1, matches.size());
        assertEquals("タグa (1)", matches.get(0).getText());
        matches = find(".label-success");
        assertEquals(1, matches.size());
        assertEquals("タグa (1)", matches.get(0).getText());

        wd.findElement(By.linkText("tag1")).click();
        matches = find(".sb_matched");
        assertEquals(1, matches.size());
        assertEquals("tag1 (2)", matches.get(0).getText());
        matches = find(".label-success");
        assertEquals(1, matches.size());
        assertEquals("tag1 (2)", matches.get(0).getText());
    }

    @Test
    public void test111_admin() {
        wd.get(baseurl + "/_admin/entries/");
        assertEquals(baseurl + "/_admin/entries/", wd.getCurrentUrl());

        wd.findElement(By.xpath("//div[@class='btn-group']//button[.='new']")).click();
        wd.findElement(By.id("title")).click();
        wd.findElement(By.id("content")).click();
        wd.findElement(By.id("content")).clear();
        wd.findElement(By.id("content"))
                .sendKeys(
                        "#Markdownのテスト1234\n てすとてすとてすとて **すとてすとてす** とてすとてすとてすと。\nてすと```てすとてすとてすとてすと```てすとてすとてす _とてすとてすとてす_ とてすとてすと。\naaaalongaaaalogaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaaaalongaa\n<br/><br/>aa3a<span style=\"color:red\" class=\"md_element\">e3a4</span>3aa\n\n#テーブル1224\n1|2 |3\n---|---|---\na|b|c\nxxxxxxx|yyyyyyyy|zzzzzz*long* **longlong**\n\n\n# 複数行の整形済テキスト\n````\nclass HelloWorld {\n  public static void main() {\n    int i = 1;\n    System.out.println(\"Hello, World! : \" + i);\n  }\n}\n````\n\n## PHPでhello world\n````\n<?php\n  echo \"Hello, World!\\n\";\n?>\n````\n\n\n## 順序\n1. a1\n1. 順序付きリストのアイテム\n 1. 子供1\n 1. 子供1\n1. 順序付きリストの別のアイテム\n  \n\n### リスト\n* aa\n * [抗酸化物質](//ja.wikipedia.org/wiki/%E6%8A%97%E9%85%B8%E5%8C%96%E7%89%A9%E8%B3%AA)\n * cc\n* e\n\n\n#### リンク\n* [ローカルホスト2](http://localhost)\n* [ローカルホスト1](http://localhost)\n* ![alt](//upload.wikimedia.org/wikipedia/commons/a/ad/Wikipedia-logo-v2-ja.png)");
        wd.findElement(By.id("title")).click();
        wd.findElement(By.id("title")).clear();
        wd.findElement(By.id("title")).sendKeys("Markdownのテスト1234");
        wd.findElement(By.id("tags")).click();
        wd.findElement(By.id("tags")).sendKeys("\\9");
        wd.findElement(By.id("tags")).click();
        wd.findElement(By.id("tags")).clear();
        wd.findElement(By.id("tags")).sendKeys("tag1, tag3, タグa, タグb, タタ グ-=c, たぐd.json, tag-e.xml, 択f.html");
        wd.findElement(By.id("r1")).click(); // normal

        wd.findElement(By.cssSelector("span.md_element")).click();

        postAndWait();
        assertAjaxRet(".ajaxform");

        wd.findElement(By.xpath("//div[@class='btn-group']//button[.='cache']")).click();
        wd.findElement(By.name("update")).click();
        waitForCacheUpdate();

        assertEquals("3", wd.findElement(By.className("totalNormalCount")).getText());
        assertEquals("9", wd.findElement(By.className("tagCount")).getText());
        assertEquals("1", wd.findElement(By.className("monthCount")).getText());
    }

    @Test
    public void test112_user() {

        wd.get(baseurl);
        assertEquals(3, find(".summary_entry_title").size());
        assertEquals(3, find(".sb_entry_title").size());
        assertEquals(1, find(".sb_month").size());

        List<WebElement> cmcount = find(".cmcount");
        assertThat(cmcount.get(0).getText(), endsWith("(1)"));
        assertThat(cmcount.get(1).getText(), endsWith("(5)"));
        assertEquals(2, cmcount.size());

        wd.findElement(By.cssSelector("h3.summary_entry_title")).click();
        wd.findElement(By.linkText("Markdownのテスト1234")).click();
        wd.findElement(By.cssSelector("span.md_element")).click();
        wd.findElement(By.id("name")).click();
        wd.findElement(By.id("name")).clear();
        wd.findElement(By.id("name")).sendKeys("aaa");
        wd.findElement(By.id("content")).click();
        wd.findElement(By.id("content")).clear();
        wd.findElement(By.id("content")).sendKeys("bbbbb");
        postAndCloseModal();

        assertEquals(1, find(".comments_container .comment").size());
        assertElementNotFound(".comments_container .comment_content");

        wd.findElement(By.id("content")).click();
        wd.findElement(By.id("name")).click();
        wd.findElement(By.id("name")).clear();
        wd.findElement(By.id("name")).sendKeys("vvvvv");
        wd.findElement(By.id("content")).click();
        wd.findElement(By.id("content")).clear();
        wd.findElement(By.id("content")).sendKeys("cccccccc");
        postAndCloseModal();

        assertEquals(2, find(".comments_container .comment").size());
        assertElementNotFound(".comments_container .comment_content");

        wd.findElement(By.id("name")).click();
        wd.findElement(By.id("name")).clear();
        wd.findElement(By.id("name")).sendKeys("dddddd");
        wd.findElement(By.id("content")).click();
        wd.findElement(By.id("content")).clear();
        wd.findElement(By.id("content")).sendKeys("vvvvvvvvvvvvvvvvvvv");
        postAndCloseModal();

        assertEquals(3, find(".comments_container .comment").size());
        assertElementNotFound(".comments_container .comment_content");

        wd.findElement(By.cssSelector(".headerdiv > h1 > a")).click();
        cmcount = find(".cmcount");
        assertThat(cmcount.get(0).getText(), endsWith("(3)"));
        assertThat(cmcount.get(1).getText(), endsWith("(1)"));
        assertThat(cmcount.get(2).getText(), endsWith("(5)"));
        assertEquals(3, cmcount.size());
    }

    @Test
    public void test113_user() {

        String[] expectedTags = {
                "tag1 (3)",
                "tag3 (3)",
                "タグa (2)",
                "tag-e.xml (1)",
                "tag2 (1)",
                "たぐd.json (1)",
                "タグb (1)",
                "タタ グ-=c (1)",
                "択f.html (1)" };

        wd.get(baseurl);

        List<WebElement> tags = find(".sb_tag");
        assertEquals(expectedTags.length, tags.size());
        for (int i = 0; i < expectedTags.length; i++) {
            assertEquals(expectedTags[i], tags.get(i).getText());
        }

        for (int i = 0; i < expectedTags.length; i++) {
            String tagName = StringUtils.substringBefore(expectedTags[i], " (");
            int entryCount = Integer.parseInt(StringUtils.substringBetween(expectedTags[i], " (", ")"));

            wd.findElement(By.linkText(tagName)).click();
            List<WebElement> matches = find(".sb_matched");
            assertEquals("tagName=" + tagName, 1, matches.size());
            assertEquals("tagName=" + tagName, expectedTags[i], matches.get(0).getText());
            matches = find(".label-success");
            assertEquals("tagName=" + tagName, 1, matches.size());
            assertEquals("tagName=" + tagName, expectedTags[i], matches.get(0).getText());

            assertEquals("tagName=" + tagName, entryCount, find(".summary_entry_title").size());
        }
    }
}
