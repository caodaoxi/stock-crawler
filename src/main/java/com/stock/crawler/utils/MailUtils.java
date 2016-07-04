package com.stock.crawler.utils;

import com.stock.crawler.config.Configuration;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Calendar;
import java.util.Properties;

/**
 * Created by caodaoxi on 2015/12/17.
 */
public class MailUtils {
    public static void sendMessage(String to, String subject, String messageText) throws MessagingException {
        Properties props = new Properties();
        // 开启debug调试
        props.setProperty("mail.debug", "true");
        // 发送服务器需要身份验证
        props.setProperty("mail.smtp.auth", "true");
        // 设置邮件服务器主机名
        props.setProperty("mail.host", "mail.jzsec.com");
        // 发送邮件协议名称
        props.setProperty("mail.transport.protocol", "smtp");

        // 设置环境信息
        Session session = Session.getInstance(props);

        // 创建邮件对象
        Message msg = new MimeMessage(session);
        msg.setSubject(subject);
        // 设置邮件内容
        msg.setText(messageText);
        // 设置发件人
        msg.setFrom(new InternetAddress("caodaoxi@jzsec.com"));

        Transport transport = session.getTransport();
        // 连接邮件服务器
        transport.connect("caodaoxi@jzsec.com", "Abcd1234");
        // 发送邮件
        transport.sendMessage(msg, new Address[] {new InternetAddress(to)});
        // 关闭连接
        transport.close();
    }

    public static void main(String[] args) throws MessagingException {
//        MailUtils.sendMessage("caodaoxi@jzsec.com", "jkkk", "jksfds");
        System.out.println(Math.pow(1.02, 200));
    }
}
