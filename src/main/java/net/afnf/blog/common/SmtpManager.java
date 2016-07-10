package net.afnf.blog.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message.RecipientType;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;

import net.afnf.blog.config.AppConfig;

@Configuration
public class SmtpManager {

    private static Logger logger = LoggerFactory.getLogger(SmtpManager.class);

    enum AuthType {
        STARTTLS, SMTPS, SMTPAUTH, LOGGER, GMAILAPI
    }

    @Value("${bj2.mail.smtp.type}")
    private String type;
    @Value("${bj2.mail.smtp.host}")
    private String host;
    @Value("${bj2.mail.smtp.to}")
    private String to;
    @Value("${bj2.mail.smtp.id}")
    private String id;
    @Value("${bj2.mail.smtp.password}")
    private String password;

    private static String raw_password = null;

    /**
     * メッセージの送信
     * 
     * @param title タイトル
     * @param message メッセージ
     * @return 成功すればtrue
     */
    public void send(String title, String message) {

        // LOGGER
        if (StringUtils.equalsIgnoreCase(type, AuthType.LOGGER.name())) {
            logger.info("logger mail, title=" + title + ", message=" + message);
        }
        // 送信スレッド開始
        else {
            new SenderThread(title, message).start();
        }
    }

    class SenderThread extends Thread {
        private String title;
        private String message;

        public SenderThread(String title, String message) {
            this.title = title;
            this.message = message;
        }

        @Override
        public void run() {

            try {
                Session session = null;
                String[] hostport = host.split(":");

                boolean starttls = StringUtils.equalsIgnoreCase(type, AuthType.STARTTLS.name());
                boolean smtps = StringUtils.equalsIgnoreCase(type, AuthType.SMTPS.name());
                boolean gmailapi = StringUtils.equalsIgnoreCase(type, AuthType.GMAILAPI.name());

                // Gmail API以外
                if (gmailapi == false) {

                    // パスワードの復号
                    if (raw_password == null) {
                        raw_password = Crypto.decrypt(password);
                    }

                    // 送信準備
                    Properties props = new Properties();
                    props.put("mail.smtp.host", hostport[0]);
                    props.put("mail.smtp.port", hostport[1]);
                    props.put("mail.smtp.auth", "true");

                    // Authenticator（SMTPAUTHでは使用しない）
                    Authenticator authenticator = null;

                    // STARTTLS / SMTPS
                    if (starttls || smtps) {
                        props.put("mail.smtp.localhost", "192.168.0.1");
                        if (starttls) {
                            props.put("mail.smtp.starttls.enable", "true");
                        }
                        authenticator = new Authenticator() {
                            @Override
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(id, raw_password);
                            }
                        };
                    }

                    // セッション作成
                    session = Session.getInstance(props, authenticator);
                }

                // 情報設定
                MimeMessage msg = new MimeMessage(session);
                msg.setFrom(new InternetAddress(id));
                msg.setSubject(title);
                msg.setSentDate(new Date());
                msg.setText(message, "iso-2022-jp");
                InternetAddress toAddress = new InternetAddress(to);

                // Gmail API
                if (gmailapi) {
                    msg.setRecipient(RecipientType.TO, toAddress);

                    // Messageを生成
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    msg.writeTo(bytes);
                    String encodedEmail = Base64.encodeBase64URLSafeString(bytes.toByteArray());
                    Message gmailmsg = new Message();
                    gmailmsg.setRaw(encodedEmail);

                    // 送信
                    Gmail service = getGmailService(System.getProperty("user.home"), AppConfig.getInstance().getTitle());
                    service.users().messages().send("me", gmailmsg).execute();
                }
                // Gmail API以外
                else {
                    // 送信
                    String protocol = smtps ? "smtps" : "smtp";
                    Transport transport = session.getTransport(protocol);
                    transport.connect(hostport[0], Integer.parseInt(hostport[1]), id, raw_password);
                    transport.sendMessage(msg, new InternetAddress[] { toAddress });
                    transport.close();
                }

                logger.info("smtp mail, title=" + title);
            }
            catch (Throwable mex) {
                logger.error("mail error", mex);
            }
        }
    }

    private static Gmail getGmailService(String basedir, String appName) throws Exception {

        // 機密情報ファイルのパス
        File DATA_STORE_DIR = new java.io.File(basedir, "gmail-secrets");
        File SECRET_JSON = new java.io.File(DATA_STORE_DIR, "client_secret.json");

        // 準備
        FileDataStoreFactory DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
        HttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        // 送信のみ
        List<String> SCOPES = Arrays.asList(GmailScopes.GMAIL_SEND);

        // Credential取得
        InputStream in = FileUtils.openInputStream(SECRET_JSON);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets,
                SCOPES).setDataStoreFactory(DATA_STORE_FACTORY).setAccessType("offline").build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");

        // Gmailインスタンス生成
        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(appName).build();
    }

    //    /**
    //     * StoredCredentialを生成するために、target/client_secret.jsonを準備してこのメインクラスを実行する
    //     * 
    //     * @param args 引数
    //     * @throws Throwable 例外
    //     */
    //    public static void main(String[] args) throws Throwable {
    //        Gmail gmail = getGmailService("target", "test");
    //        System.out.println(gmail.toString());
    //    }
}
