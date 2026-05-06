package com.internship.tool.config;

import com.internship.tool.service.EmailService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.scheduling.enabled", havingValue = "true")
public class SchedulerConfig {

    private final EmailService emailService;

    public SchedulerConfig(EmailService emailService) {
        this.emailService = emailService;
    }

    // Runs every day at 9 AM
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendDailyReminder() {
        emailService.sendSimpleMail(
                "test@gmail.com",
                "Daily Reminder",
                "Don't forget your tasks!"
        );
    }

    // Runs every 1 minute (for testing)
    @Scheduled(fixedRate = 60000)
    public void deadlineAlert() {
        emailService.sendSimpleMail(
                "test@gmail.com",
                "Deadline Alert",
                "Task deadline is near!"
        );
    }
}
