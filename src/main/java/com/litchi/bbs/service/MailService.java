package com.litchi.bbs.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.util.Map;

/**
 * 邮件服务
 *
 * @author CuiWJ
 * Created on 2018/12/11
 */
@Service
@PropertySource(value = {"classpath:mail-sender.properties"})
public class MailService {
    private Logger logger = LoggerFactory.getLogger(MailService.class);

    @Autowired
    private JavaMailSenderImpl mailSender;

    @Value("${mail.username}")
    private String from;

    /**
     * 发送模板邮件
     *
     * @param to       目标邮箱地址
     * @param subject  邮件主题
     * @return 发送成功true, 否则false
     */
    public boolean send(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message);
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(content, true);
            mailSender.send(message);
            return true;
        } catch (MessagingException e) {
            logger.error("发送邮件失败" + e.getMessage());
            return false;
        }

    }
}
