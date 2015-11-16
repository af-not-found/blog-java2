package net.afnf.blog.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Value("${spring.profiles.active}")
    private String activeProfile = null;

    @Value("${bj2.app.title}")
    private String title;

    @Value("${bj2.app.adminhost}")
    private String adminHost;

    @Value("${bj2.app.salt}")
    private String salt;

    @Value("${bj2.app.cipherSeed}")
    private String cipherSeed;

    private String buildDate;

    @Value("${bj2.assets.baseurl}")
    private String assetsBaseurl;

    @Value("${bj2.assets.srcdir}")
    private String assetsSrcDir = null;

    @Value("${bj2.assets.destdir}")
    private String assetsDestDir = null;

    @Value("${bj2.assets.shell}")
    private String assetsShell = null;

    @Value("${bj2.selenium.targetUrl}")
    private String seleniumTargetUrl = null;

    @Value("${bj2.selenium.webdriver}")
    private String seleniumWebdriver = null;

    @Value("${management.port}")
    private String managementPort;

    @Value("${server.context-path}")
    private String contextPath;

    private static AppConfig instance = null;

    public AppConfig() {
        instance = this;
    }

    public static AppConfig getInstance() {
        return instance;
    }

    public boolean isDevelopment() {
        return StringUtils.indexOf(getTitle(), "dev-") == 0;
    }

    public boolean isDemoSite() {
        return StringUtils.indexOf(getActiveProfile(), "-demo") != -1;
    }

    public boolean isTestSite() {
        return StringUtils.indexOf(getActiveProfile(), "-test") != -1;
    }

    public boolean isProductionAndNormalSite() {
        return StringUtils.equals(getActiveProfile(), "production-normal");
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////

    public String getActiveProfile() {
        return activeProfile;
    }

    public void setActiveProfile(String activeProfile) {
        this.activeProfile = activeProfile;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAdminHost() {
        return adminHost;
    }

    public void setAdminHost(String adminHost) {
        this.adminHost = adminHost;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getCipherSeed() {
        return cipherSeed;
    }

    public void setCipherSeed(String cipherSeed) {
        this.cipherSeed = cipherSeed;
    }

    public String getBuildDate() {
        return buildDate;
    }

    public void setBuildDate(String buildDate) {
        this.buildDate = buildDate;
    }

    public String getAssetsBaseurl() {
        return assetsBaseurl;
    }

    public void setAssetsBaseurl(String assetsBaseurl) {
        this.assetsBaseurl = assetsBaseurl;
    }

    public String getAssetsShell() {
        return assetsShell;
    }

    public void setAssetsShell(String assetsShell) {
        this.assetsShell = assetsShell;
    }

    public String getAssetsSrcDir() {
        return assetsSrcDir;
    }

    public void setAssetsSrcDir(String assetsSrcDir) {
        this.assetsSrcDir = assetsSrcDir;
    }

    public String getAssetsDestDir() {
        return assetsDestDir;
    }

    public void setAssetsDestDir(String assetsDestDir) {
        this.assetsDestDir = assetsDestDir;
    }

    public String getSeleniumTargetUrl() {
        return seleniumTargetUrl;
    }

    public void setSeleniumTargetUrl(String seleniumTargetUrl) {
        this.seleniumTargetUrl = seleniumTargetUrl;
    }

    public String getSeleniumWebdriver() {
        return seleniumWebdriver;
    }

    public void setSeleniumWebdriver(String seleniumWebdriver) {
        this.seleniumWebdriver = seleniumWebdriver;
    }

    public String getManagementPort() {
        return managementPort;
    }

    public void setManagementPort(String managementPort) {
        this.managementPort = managementPort;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }
}
