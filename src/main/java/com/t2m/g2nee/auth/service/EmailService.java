package com.t2m.g2nee.auth.service;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;

    @Async
    public void sendSimpleMessage(String emailAddress) throws Exception {
        MimeMessage message = javaMailSender.createMimeMessage();
        message.setFrom(new InternetAddress("g2neeShop@gmail.com", "G2neeShop"));// 보내는 사람
        message.addRecipients(MimeMessage.RecipientType.TO, emailAddress);
        message.setSubject("[G2nee Shop] 로그인 감지");

        String body = "<p>새로운 로그인이 감지되었습니다.</p>"
                + "<p>본인이 로그인한 것이 아니라면 비밀번호를 재설정해주십시오.</p>"
                + "<p><a href=\"http://www.g2nee.shop\">비밀번호 변경하기</a></p>";

        message.setText(body, "utf-8", "html");// 내용, charset 타입, subtype

        javaMailSender.send(message);
    }
}
