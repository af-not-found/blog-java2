package net.afnf.blog.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.mozilla.javascript.tools.ToolErrorReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

import net.afnf.blog.config.AppConfig;

public class AssetsFunction {

    private static Logger logger = LoggerFactory.getLogger(AssetsFunction.class);

    private static final String[] assetExts = { "css", "js" };

    public static boolean tryUpdate(String resourceUrlPath) {
        boolean modified = false;

        AppConfig appConfig = AppConfig.getInstance();

        // ファイル名取得
        String name = resourceUrlPath;
        if (resourceUrlPath.indexOf("/") != -1) {
            name = StringUtils.substringAfterLast(resourceUrlPath, "/");
        }

        // ソースディレクトリがある場合
        File assetsDir = new File(appConfig.getAssetsSrcDir());
        File srcDir = new File(assetsDir, name);
        if (srcDir.exists() && srcDir.isDirectory()) {

            // ファイルが更新されているかチェック
            Collection<File> files = FileUtils.listFiles(srcDir, assetExts, false);
            File destFile = new File(appConfig.getAssetsDestDir(), name);
            if (destFile.exists() == false) {
                modified = true;
            }
            else {
                long prevmod = destFile.lastModified();
                for (File file : files) {
                    if (file.lastModified() > prevmod) {
                        modified = true;
                        break;
                    }
                }
            }

            // 更新されていれば、minifyを実行
            if (modified) {

                Reader reader = null;
                Writer writer = null;

                try {
                    StringBuilder buf = new StringBuilder();
                    for (File file : files) {
                        buf.append(FileUtils.readFileToString(file, "UTF-8"));
                        buf.append("\n");
                    }
                    reader = new StringReader(buf.toString());

                    writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destFile), "UTF-8"));
                    if (StringUtils.endsWith(name, "js")) {
                        JavaScriptCompressor compressor = new JavaScriptCompressor(reader, new ToolErrorReporter(false));
                        compressor.compress(writer, 400, true, false, false, false);
                        logger.info("MyVersionResourceResolver minify js : " + name);
                    }
                    else if (StringUtils.endsWith(name, "css")) {
                        CssCompressor compressor = new CssCompressor(reader);
                        compressor.compress(writer, 400);
                        logger.info("MyVersionResourceResolver minify css : " + name);
                    }
                }
                catch (Exception e) {
                    logger.warn("minify failed", e);
                }
                finally {
                    IOUtils.closeQuietly(reader);
                    IOUtils.closeQuietly(writer);
                }
            }
        }

        return modified;
    }

}
