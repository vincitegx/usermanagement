package com.davidogbodu.usermanagement.mail;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class MailService {

    public static final String NEW_USER_ACCOUNT_VERIFICATION = "New User Account Verification";
    public static final String UTF_8_ENCODING = "UTF-8";
    public static final String EMAIL_TEMPLATE = "emailtemplate";
    public static final String TEXT_HTML_ENCONDING = "text/html";
    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;
    @Value("${FRONTEND_DOMAIN}")
    private String host;
    @Value("${organization.properties.mail}")
    private String fromEmail;

    @Async
    public void sendHtmlEmail(EventDto eventDto) {
        try {
            Context context = new Context();
            context.setVariable("name", eventDto.getData().get("name"));
            context.setVariable("subject", eventDto.getData().get("subject"));
            context.setVariable("expiresAt", eventDto.getData().get("expiresAt"));
            context.setVariable("token", eventDto.getData().get("token"));
//            context.setVariables(Map.of("name", name, "url", EmailUtils.getVerificationUrl(host, token)));
            String text = templateEngine.process(EMAIL_TEMPLATE, context);
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);
            helper.setPriority(1);
            helper.setSubject(eventDto.getData().get("subject"));
            helper.setFrom(fromEmail);
            helper.setTo(eventDto.to);
            helper.setText(text, true);
            emailSender.send(message);
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            throw new RuntimeException(exception.getMessage());
        }
    }

    private MimeMessage getMimeMessage() {
        return emailSender.createMimeMessage();
    }

    private String getContentId(String filename) {
        return "<" + filename + ">";
    }
}
