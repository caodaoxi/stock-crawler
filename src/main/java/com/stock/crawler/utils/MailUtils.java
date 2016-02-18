package com.stock.crawler.utils;

import com.stock.crawler.config.Configuration;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Calendar;
import java.util.Properties;

/**
 * Created by caodaoxi on 2015/12/17.
 */
public class MailUtils {
    public static void sendMessage(String to, String subject, String messageText) throws MessagingException {
        // 第一步：配置javax.mail.Session对象
        Properties props = new Properties();
        props.put("mail.smtp.host", Configuration.SMTPHOST);
        props.put("mail.smtp.starttls.enable","true");//使用 STARTTLS安全连接
        props.put("mail.smtp.port", Configuration.MAILPORT);             //google使用465或587端口
        props.put("mail.smtp.auth", "true");        // 使用验证
        //props.put("mail.debug", "true");
        Session mailSession = Session.getInstance(props,new MyAuthenticator(Configuration.MAILSENDERUSERNAME,Configuration.MAILSENDERPASSWORD));

        // 第二步：编写消息
        InternetAddress fromAddress = new InternetAddress(Configuration.MAILSENDERUSERNAME);
        InternetAddress toAddress = new InternetAddress(to);

        MimeMessage message = new MimeMessage(mailSession);

        message.setFrom(fromAddress);
        message.addRecipient(Message.RecipientType.TO, toAddress);

        message.setSentDate(Calendar.getInstance().getTime());
        message.setSubject(subject);
        message.setContent(messageText, "text/html;charset=gb2312");

        // 第三步：发送消息
        Transport transport = mailSession.getTransport("smtp");
        transport.connect(Configuration.SMTPHOST, Configuration.JDBCUSERNAME, Configuration.MAILPASSWORD);
        transport.send(message, message.getRecipients(Message.RecipientType.TO));
    }

    public static void main(String[] args) throws MessagingException {
        MailUtils.sendMessage("cdx@gbdex.com", "股票", "开始卖了");
    }
}
