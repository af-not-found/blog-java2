package net.afnf.blog.common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.afnf.blog.config.AppConfig;

public class AssetsFunctionTest {

    private static final String FROM_DIR = "src/test/data/assets/from";
    private static final String TO_DIR = "src/test/data/assets/to/static";

    @BeforeClass
    public static void beforeClass() throws IOException {
        FileUtils.deleteDirectory(new File(TO_DIR));
        new File(TO_DIR).mkdirs();
    }

    @Before
    public void before() throws IOException {
        AppConfig appConfig = new AppConfig();
        appConfig.setAssetsSrcDir(FROM_DIR);
        appConfig.setAssetsDestDir(TO_DIR);
    }

    @Test
    public void testConvertAssetUrl1() throws IOException {

        assertTrue(AssetsFunction.tryUpdate("/static/test1.min.js"));
        assertFalse(AssetsFunction.tryUpdate("/static/test1.min.js"));

        touch("src/test/data/assets/from/test1.min.js/1_test.js");
        assertTrue(AssetsFunction.tryUpdate("/static/test1.min.js"));
        assertFalse(AssetsFunction.tryUpdate("/static/test1.min.js"));

        touch("src/test/data/assets/from/test1.min.js/1_test.js");
        assertTrue(AssetsFunction.tryUpdate("/static/test1.min.js"));
    }

    @Test
    public void testConvertAssetUrl2() throws IOException {

        assertTrue(AssetsFunction.tryUpdate("/static/test2.min.css"));
        assertFalse(AssetsFunction.tryUpdate("/static/test2.min.css"));

        touch("src/test/data/assets/from/test2.min.css/1_test.css");
        assertTrue(AssetsFunction.tryUpdate("/static/test2.min.css"));
        assertFalse(AssetsFunction.tryUpdate("/static/test2.min.css"));

        touch("src/test/data/assets/from/test2.min.css/2_test.css");
        assertTrue(AssetsFunction.tryUpdate("/static/test2.min.css"));

        // 関係ないファイルを更新したらfalse
        touch("src/test/data/assets/from/test1.min.js/1_test.js");
        assertFalse(AssetsFunction.tryUpdate("/static/test2.min.css"));
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
