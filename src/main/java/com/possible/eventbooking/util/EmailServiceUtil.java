package com.possible.eventbooking.util;

import com.possible.eventbooking.dto.EmailDto;
import com.possible.eventbooking.dto.EmailResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;



@Service
@AllArgsConstructor
@Slf4j
public class EmailServiceUtil {

    private final JavaMailSender mailSender;


    public EmailResponse sendMail(EmailDto emailDto) {
        return null;
    }

    public void sendEmail(EmailDto emailDetails){
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom("noreply@gmail.com");
            mailMessage.setTo(String.join(",", emailDetails.getToAddress()));
            mailMessage.setText(emailDetails.getContent());
            mailMessage.setSubject(emailDetails.getSubject());

            mailSender.send(mailMessage);
            log.info("Message sent to: {}", emailDetails.getToAddress());

        } catch (MailException e) {
            throw new RuntimeException(e);
        }
    }


}
