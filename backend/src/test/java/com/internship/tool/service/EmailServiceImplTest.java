package com.internship.tool.service;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmailServiceImplTest {

    @Test
    void sendsSimpleMail() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        EmailServiceImpl service = new EmailServiceImpl(mailSender, mock(TemplateEngine.class));

        service.sendSimpleMail("to@example.com", "Subject", "Body");

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendsHtmlMail() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        TemplateEngine templateEngine = mock(TemplateEngine.class);
        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(templateEngine.process(eq("reminder"), any(Context.class))).thenReturn("<p>Hello</p>");
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        EmailServiceImpl service = new EmailServiceImpl(mailSender, templateEngine);

        service.sendHtmlMail("to@example.com", "Subject", "reminder", "data");

        verify(mailSender).send(mimeMessage);
    }

    @Test
    void wrapsHtmlMailFailure() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        TemplateEngine templateEngine = mock(TemplateEngine.class);
        when(templateEngine.process(eq("missing"), any(Context.class))).thenThrow(new RuntimeException("missing"));
        EmailServiceImpl service = new EmailServiceImpl(mailSender, templateEngine);

        assertThrows(RuntimeException.class, () -> service.sendHtmlMail("to@example.com", "Subject", "missing", "data"));
    }
}
