package com.checkmate.backend.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Value("${sendgrid.from.email}")
    private String fromEmail;

    @Value("${sendgrid.from.name}")
    private String fromName;

    public void sendEmail(String toEmail, String subject, String body) {
        if (toEmail == null || toEmail.trim().isEmpty()) {
            System.out.println("EMAIL-DEBUG: No personal email found, skipping email send");
            return;
        }

        try {
            Email from = new Email(fromEmail, fromName);
            Email to = new Email(toEmail);
            Content content = new Content("text/html", body);
            Mail mail = new Mail(from, subject, to, content);

            SendGrid sg = new SendGrid(sendGridApiKey);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);
            System.out.println("EMAIL-DEBUG: Status=" + response.getStatusCode()
                    + " to=" + toEmail);

        } catch (IOException e) {
            System.err.println("EMAIL-DEBUG: Failed to send email to " + toEmail
                    + " - " + e.getMessage());
        }
    }

    // Email when checklist assigned to user
    public void sendChecklistAssignedEmail(String toEmail, String userName,
            String checklistName) {
        String subject = "New Checklist Assigned: " + checklistName;
        String body = "<h2>Hello " + userName + "!</h2>"
                + "<p>Admin has assigned you a new checklist:</p>"
                + "<h3>" + checklistName + "</h3>"
                + "<p>Please login to Checkmate to view your tasks.</p>"
                + "<br><p>Team Checkmate</p>";
        sendEmail(toEmail, subject, body);
    }

    // Email for due date reminder
    public void sendReminderEmail(String toEmail, String userName,
            String taskTitle, String checklistName) {
        String subject = "Reminder: Task due tomorrow — " + taskTitle;
        String body = "<h2>Hello " + userName + "!</h2>"
                + "<p>This is a reminder that the following task is due <strong>tomorrow</strong>:</p>"
                + "<h3>" + taskTitle + "</h3>"
                + "<p>Checklist: " + checklistName + "</p>"
                + "<p>Please login to Checkmate and complete it on time.</p>"
                + "<br><p>Team Checkmate</p>";
        sendEmail(toEmail, subject, body);
    }
}
