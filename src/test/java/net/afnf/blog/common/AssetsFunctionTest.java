package net.afnf.blog.common;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import net.afnf.blog.config.AppConfig;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AssetsFunctionTest {

    private static final String ORIG_DIR = "src/assets";
    private static final String FROM_DIR = "src/test/data/assets/from";
    private static final String TO_DIR = "src/test/data/assets/to";
    private static final String MINIFY_SH = "_minify.sh";

    @BeforeClass
    public static void beforeClass() throws IOException {
        FileUtils.copyFile(new File(ORIG_DIR, MINIFY_SH), new File(FROM_DIR, MINIFY_SH));
        FileUtils.deleteDirectory(new File(TO_DIR));
        new File(TO_DIR, "static").mkdirs();
    }

    @Before
    public void before() throws IOException {
        AppConfig appConfig = new AppConfig();
        appConfig.setAssetsShell("bash");
        appConfig.setAssetsBaseurl("http://test.local/context");
        appConfig.setAssetsSrcDir(FROM_DIR);
        appConfig.setAssetsDestDir(TO_DIR);
    }

    @AfterClass
    public static void afterClass() {
        FileUtils.deleteQuietly(new File(FROM_DIR, MINIFY_SH));
    }

    @Test
    public void testConvertAssetUrl1() throws IOException {

        int version = 0;

        String url;
        {
            version++;
            url = AssetsFunction.getInstance().url("/static/test1.min.js");
            assertEquals("http://test.local/context/static/test1.min.js;v=" + version, url);
        }
        {
            url = AssetsFunction.getInstance().url("/static/test1.min.js");
            assertEquals("http://test.local/context/static/test1.min.js;v=" + version, url);
        }
        {
            touch("src/test/data/assets/from/test1.min.js/1_test.js");
            version++;
            url = AssetsFunction.getInstance().url("/static/test1.min.js");
            assertEquals("http://test.local/context/static/test1.min.js;v=" + version, url);
        }
        {
            url = AssetsFunction.getInstance().url("/static/test1.min.js");
            assertEquals("http://test.local/context/static/test1.min.js;v=" + version, url);
        }
        {
            touch("src/test/data/assets/from/test1.min.js/2_test.js");
            version++;
            url = AssetsFunction.getInstance().url("/static/test1.min.js");
            assertEquals("http://test.local/context/static/test1.min.js;v=" + version, url);
        }
    }

    @Test
    public void testConvertAssetUrl2() throws IOException {

        int version = 45714;
        touch(TO_DIR + "/static/test2.min.css");
        FileUtils.write(new File(TO_DIR + "/static/test2.min.css.version"), "" + version);

        String url;
        {
            url = AssetsFunction.getInstance().url("/static/test2.min.css");
            assertEquals("http://test.local/context/static/test2.min.css;v=" + version, url);
        }
        {
            url = AssetsFunction.getInstance().url("/static/test2.min.css");
            assertEquals("http://test.local/context/static/test2.min.css;v=" + version, url);
        }
        {
            touch("src/test/data/assets/from/test2.min.css/1_test.js");
            version++;
            url = AssetsFunction.getInstance().url("/static/test2.min.css");
            assertEquals("http://test.local/context/static/test2.min.css;v=" + version, url);
        }
        {
            url = AssetsFunction.getInstance().url("/static/test2.min.css");
            assertEquals("http://test.local/context/static/test2.min.css;v=" + version, url);
        }
        {
            touch("src/test/data/assets/from/test2.min.css/2_test.js");
            version++;
            url = AssetsFunction.getInstance().url("/static/test2.min.css");
            assertEquals("http://test.local/context/static/test2.min.css;v=" + version, url);
        }
    }

    @Test
    public void testConvertAssetUrl3() throws IOException {

        int version = 0;
        FileUtils.write(new File(TO_DIR + "/static/test3.min.js"), "alert(1);");

        String url;
        {
            url = AssetsFunction.getInstance().url("/static/test3.min.js");
            assertEquals("http://test.local/context/static/test3.min.js;v=" + version, url);
        }
        {
            url = AssetsFunction.getInstance().url("/static/test3.min.js");
            assertEquals("http://test.local/context/static/test3.min.js;v=" + version, url);
        }
    }

    public static void touch(String path) {
        try {
            AfnfUtil.sleep(1001);
            FileUtils.touch(new File(path));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
