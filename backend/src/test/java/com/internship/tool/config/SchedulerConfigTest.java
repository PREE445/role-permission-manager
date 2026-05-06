package com.internship.tool.config;

import com.internship.tool.service.EmailService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SchedulerConfigTest {

    @Test
    void scheduledJobsSendExpectedEmails() {
        EmailService emailService = mock(EmailService.class);
        SchedulerConfig schedulerConfig = new SchedulerConfig(emailService);

        schedulerConfig.sendDailyReminder();
        schedulerConfig.deadlineAlert();

        verify(emailService).sendSimpleMail("test@gmail.com", "Daily Reminder", "Don't forget your tasks!");
        verify(emailService).sendSimpleMail("test@gmail.com", "Deadline Alert", "Task deadline is near!");
    }
}
