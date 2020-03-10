package com.litchi.bbs.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * 邮件服务器配置类
 *
 * @author CuiWJ
 * date:2018/12/11
 */

@Configuration
@PropertySource(value = {"classpath:mail-sender.properties"})
public class MailSenderConfig {

    private Logger log = LoggerFactory.getLogger(MailSenderConfig.class);

    @Value("${mail.username}")
    private String username;
    @Value("${mail.password}")
    private String password;
    @Value("${mail.host}")
    private String host;
    @Value("${mail.port}")
    private int port;
    @Value("${mail.protocol}")
    private String protocol;

    @Bean
    public JavaMailSenderImpl getMailSender() {
        log.info("==>初始化MailSender");
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setProtocol(protocol);
        mailSender.setDefaultEncoding("utf8");
        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.ssl.enable", true);
        mailSender.setJavaMailProperties(javaMailProperties);
        return mailSender;
    }
}
