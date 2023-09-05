package com.omar.security.service.impl;

import com.omar.security.service.EmailSenderService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeoutException;

@Service
@AllArgsConstructor
public class EmailSenderServiceImpl implements EmailSenderService {

    private final static Logger LOGGER = LoggerFactory.getLogger(EmailSenderServiceImpl.class);
    private JavaMailSender mailSender;

    @Override
    @Async
    public void sendEmail(String to, String emailBody) throws TimeoutException {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,"utf-8");

            helper.setText(emailBody,true);
            helper.setFrom("omargomaa.dev@gmail.com");
            helper.setTo(to);
            helper.setSubject("Email Verification");

            mailSender.send(mimeMessage);

            LOGGER.info("Email Sent successfully");
        }catch (MailException | MessagingException e){
            LOGGER.warn("Error while sending Email .. : " + e.getMessage());

            throw new TimeoutException("Please try again later");
        }

    }
}
