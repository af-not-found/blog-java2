package net.afnf.blog.common;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.afnf.blog.config.AppConfig;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssetsFunction {

    private static Logger logger = LoggerFactory.getLogger(AssetsFunction.class);

    protected static Map<String, Integer> versionMap = new HashMap<String, Integer>();

    private static AssetsFunction instance = new AssetsFunction();

    public static AssetsFunction getInstance() {
        return instance;
    }

    public String url(String path) {

        AppConfig appConfig = AppConfig.getInstance();

        // バージョン取得
        Integer version = versionMap.get(path);
        if (version == null) {
            // .versionがあれば読み取り、なければ0
            version = 0;
            try {
                // jarから起動された場合は、getResourceAsStreamで読む
                String from = appConfig.getAssetsDestDir();
                if (StringUtils.startsWith(from, "/")) {
                    try (InputStream inputStream = this.getClass().getResourceAsStream(from + path + ".version")) {
                        if (inputStream != null) {
                            version = NumberUtils.toInt(IOUtils.toString(inputStream, "UTF-8"), 0);
                        }
                    }
                    catch (Throwable e) {
                        logger.warn("failed to version, path=" + path + ", e=" + e.toString());
                    }
                }
                // srcから起動された場合は、Fileとして読む
                else {
                    File verFile = new File(from, path + ".version");
                    if (verFile.exists()) {
                        String versionStr = FileUtils.readFileToString(verFile);
                        version = NumberUtils.toInt(StringUtils.trim(versionStr));
                    }
                }
            }
            catch (Throwable e) {
                logger.warn("get version failed", e);
            }
            // マップに追加
            versionMap.put(path, version);
        }

        // shellが設定されていれば実行
        String shell = appConfig.getAssetsShell();
        if (StringUtils.isNotBlank(shell) && path.indexOf("/img/") == -1) {
            try {
                synchronized (AssetsFunction.class) {

                    // ファイル名取得
                    String name = path;
                    if (path.indexOf("/") != -1) {
                        name = StringUtils.substringAfterLast(path, "/");
                    }

                    // ソースディレクトリがある場合
                    File assetsDir = new File(appConfig.getAssetsSrcDir());
                    File srcDir = new File(assetsDir, name);
                    if (srcDir.exists() && srcDir.isDirectory()) {

                        // ファイルが更新されているかチェック
                        boolean modified = false;
                        File destFile = new File(appConfig.getAssetsDestDir(), path);
                        if (destFile.exists() == false) {
                            modified = true;
                        }
                        else {
                            long prevmod = destFile.lastModified();
                            Collection<File> files = FileUtils.listFiles(srcDir, null, false);
                            for (File file : files) {
                                if (file.lastModified() > prevmod) {
                                    modified = true;
                                    break;
                                }
                            }
                        }

                        // 更新されていれば、_minify.shを実行
                        if (modified) {
                            ArrayList<String> cmdlist = new ArrayList<String>();
                            cmdlist.add(shell);
                            cmdlist.add("_minify.sh");
                            cmdlist.add(name);
                            cmdlist.add(destFile.getParentFile().getAbsolutePath().replaceAll("\\\\", "/"));
                            ProcessBuilder pb = new ProcessBuilder(cmdlist).directory(assetsDir).redirectErrorStream(true);
                            Process process = pb.start();
                            String stdout = IOUtils.toString(process.getInputStream());
                            int exitValue = process.waitFor();
                            logger.debug("exitValue : " + exitValue);
                            logger.debug(stdout.trim());

                            // 成功すればバージョンを更新
                            if (exitValue == 0) {
                                File verFile = new File(appConfig.getAssetsDestDir(), path + ".version");
                                version = version == null ? 0 : version + 1;
                                versionMap.put(path, version);
                                FileUtils.write(verFile, version.toString());
                            }
                            else {
                                logger.warn("minify failed, exitValue=" + exitValue);
                            }
                        }
                    }
                }
            }
            catch (Throwable e) {
                logger.warn("assets minify failed", e);
            }
        }

        // URL構築
        StringBuilder sb = new StringBuilder();
        sb.append(appConfig.getAssetsBaseurl());
        sb.append(path);
        if (version != null) {
            sb.append(";v=");
            sb.append(version);
        }
        return sb.toString();
    }
}
