package net.afnf.blog.common;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmtpManager {

    private static Logger logger = LoggerFactory.getLogger(SmtpManager.class);

    enum AuthType {
        STARTTLS, SMTPS, SMTPAUTH, LOGGER
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

                // パスワードの復号
                if (raw_password == null) {
                    raw_password = Crypto.decrypt(password);
                }

                // 送信準備
                String[] hostport = host.split(":");
                Properties props = new Properties();
                props.put("mail.smtp.host", hostport[0]);
                props.put("mail.smtp.port", hostport[1]);
                props.put("mail.smtp.auth", "true");

                // STARTTLS
                if (StringUtils.equalsIgnoreCase(type, AuthType.STARTTLS.name())) {
                    props.put("mail.smtp.starttls.enable", "true");
                    props.put("mail.smtp.localhost", "192.168.0.1");
                    // セッション作成
                    session = Session.getInstance(props, new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(id, raw_password);
                        }
                    });
                }
                // SMTPS
                else if (StringUtils.equalsIgnoreCase(type, AuthType.SMTPS.name())) {
                    props.put("mail.smtp.localhost", "192.168.0.1");
                    // セッション作成
                    session = Session.getInstance(props, new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(id, raw_password);
                        }
                    });
                }
                // SMTP認証
                else if (StringUtils.equalsIgnoreCase(type, AuthType.SMTPAUTH.name())) {
                    // セッション作成
                    session = Session.getInstance(props, null);
                }

                // 情報設定
                MimeMessage msg = new MimeMessage(session);
                msg.setFrom(new InternetAddress(id));
                msg.setSubject(title);
                msg.setSentDate(new Date());
                msg.setText(message, "iso-2022-jp");
                InternetAddress[] addresses = { new InternetAddress(to) };

                // 送信
                String protocol = (StringUtils.equalsIgnoreCase(type, AuthType.SMTPS.name())) ? "smtps" : "smtp";
                Transport transport = session.getTransport(protocol);
                transport.connect(hostport[0], Integer.parseInt(hostport[1]), id, raw_password);
                transport.sendMessage(msg, addresses);
                transport.close();

                logger.info("smtp mail, title=" + title);
            }
            catch (Throwable mex) {
                logger.error("mail error", mex);
            }
        }
    }
}
