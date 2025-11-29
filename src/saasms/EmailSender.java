/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package saasms;

import java.io.UnsupportedEncodingException;
import java.time.LocalTime;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author adisa
 */
public class EmailSender {
     public static void sendEmail(String receiver, String username, String password) {
        String senderEmail = "adebareadesokan@gmail.com";
        String senderPassword = "etqlrvhnwcgpmeqw"; //app password

        String subject = "Login Credentials";
        String messageBody = """
                             Dear User, 
                             
                             Your account has been successfully registered.
                             Username: """ + username + "\nPassword: " + password + "\n\n"
                + "Regards, \n PAUSAAMS";
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver));
            message.setSubject(subject);
            message.setText(messageBody);

            Transport.send(message);
            System.out.println("Email sent successfully!");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public static void sendEmail2(String receiver, String username, String password) {
        String senderEmail = "adebareadesokan@gmail.com";
        String senderPassword = "etqlrvhnwcgpmeqw"; //app password

        String subject = "Login Credentials";
        String messageBody = """
                             Dear User, 
                             
                             Your password has been successfully reset.
                             Contact IT if you didn't request this change.
                             Username: """ + username + "\nPassword: " + password + "\n\n"
                + "Regards, \n PAUSAAMS";
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver));
            message.setSubject(subject);
            message.setText(messageBody);

            Transport.send(message);
            System.out.println("Email sent successfully!");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    public static void sendShiftSwapNotification(String requesterEmail, String targetEmail, String requesterName, String targetName, String decision, java.util.Date shiftDate) {
    final String senderEmail = "adebareadesokan@gmail.com";
    final String senderPassword = "etqlrvhnwcgpmeqw";

    String subject = "Shift Swap " + decision;
    String messageBody = """
                         Dear Staff,

                         A shift swap request has been %s by your supervisor.

                         Requester: %s
                         Target Staff: %s
                         Shift Date: %s

                         Please prepare accordingly and reach out if you have any concerns.

                         Regards,
                         PAUSAAMS
                         """.formatted(decision.toUpperCase(), requesterName, targetName, shiftDate.toString());

    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.port", "587");

    Session session = Session.getInstance(props, new javax.mail.Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(senderEmail, senderPassword);
        }
    });

    try {
        Message messageToRequester = new MimeMessage(session);
        messageToRequester.setFrom(new InternetAddress(senderEmail, "PAUSAAMS Supervisor"));
        messageToRequester.setRecipients(Message.RecipientType.TO, InternetAddress.parse(requesterEmail));
        messageToRequester.setSubject(subject);
        messageToRequester.setText(messageBody);
        Transport.send(messageToRequester);

        Message messageToTarget = new MimeMessage(session);
        messageToTarget.setFrom(new InternetAddress(senderEmail, "PAUSAAMS Supervisor"));
        messageToTarget.setRecipients(Message.RecipientType.TO, InternetAddress.parse(targetEmail));
        messageToTarget.setSubject(subject);
        messageToTarget.setText(messageBody);
        Transport.send(messageToTarget);

        System.out.println("Shift swap emails sent to both requester and target.");
    } catch (MessagingException | UnsupportedEncodingException e) {
        e.printStackTrace();
    }
}
public static void sendSimpleEmail(String to, String subject, String body) {
        String senderEmail = "adebareadesokan@gmail.com";
        String senderPassword = "etqlrvhnwcgpmeqw";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail, "PAUSAAMS System"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public static void sendClockNotificationEmail(String recipientEmail, String staffName, String status, LocalTime clockTime) {
    final String senderEmail = "adebareadesokan@gmail.com";
    final String senderPassword = "etqlrvhnwcgpmeqw"; // Your app password

    String subject = "Attendance: " + status;
    String messageBody = """
                         Dear %s,

                         Your attendance has been marked as "%s" at %s today.

                         If this was not you, please contact your supervisor immediately.

                         Regards,
                         PAUSAAMS System
                         """.formatted(staffName, status, clockTime.toString());

    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.port", "587");

    Session session = Session.getInstance(props, new javax.mail.Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(senderEmail, senderPassword);
        }
    });

    try {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(senderEmail, "PAUSAAMS System"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject(subject);
        message.setText(messageBody);

        Transport.send(message);
        System.out.println("Clock-in/out email sent to " + recipientEmail);
    } catch (MessagingException | UnsupportedEncodingException e) {
        e.printStackTrace();
    }
}

public static void sendShiftSwapStatusEmail(
    String requesterEmail, String targetEmail,
    String requesterName, String targetName,
    String status, java.util.Date shiftDate
) {
    final String senderEmail = "adebareadesokan@gmail.com";
    final String senderPassword = "etqlrvhnwcgpmeqw"; // Gmail app password

    String subject = "Shift Swap Request " + status;

    String messageBody = """
        Dear %s,

        This is to inform you that the shift swap request between:
        - Requester: %s
        - Target: %s
        - Date: %s

        Has been %s by your supervisor.

        If you have any questions or concerns, kindly contact your supervisor.

        Regards,
        PAUSAAMS System
        """.formatted("%s", requesterName, targetName, shiftDate.toString(), status.toUpperCase());

    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.port", "587");

    Session session = Session.getInstance(props,
        new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

    try {
        // Email to requester
        Message messageToRequester = new MimeMessage(session);
        messageToRequester.setFrom(new InternetAddress(senderEmail, "PAUSAAMS System"));
        messageToRequester.setRecipients(Message.RecipientType.TO, InternetAddress.parse(requesterEmail));
        messageToRequester.setSubject(subject);
        messageToRequester.setText(String.format(messageBody, requesterName));
        Transport.send(messageToRequester);

        // Email to target
        Message messageToTarget = new MimeMessage(session);
        messageToTarget.setFrom(new InternetAddress(senderEmail, "PAUSAAMS System"));
        messageToTarget.setRecipients(Message.RecipientType.TO, InternetAddress.parse(targetEmail));
        messageToTarget.setSubject(subject);
        messageToTarget.setText(String.format(messageBody, targetName));
        Transport.send(messageToTarget);

        System.out.println("Shift swap status email sent to both staff.");
    } catch (MessagingException | UnsupportedEncodingException e) {
        e.printStackTrace();
    }
}

public static void sendRosterToStaff(String receiverEmail, String staffName, String[][] weeklyRoster) {
    final String senderEmail = "adebareadesokan@gmail.com";
    final String senderPassword = "etqlrvhnwcgpmeqw";

    String subject = "Your Weekly Shift Roster";
    StringBuilder messageBody = new StringBuilder();
    messageBody.append("Dear ").append(staffName).append(",\n\n");
    messageBody.append("Here is your assigned shift schedule for the week:\n\n");

    // Now using { dayName, shiftType, fullDate }
    for (String[] dayShift : weeklyRoster) {
        messageBody
            .append(dayShift[0])        // Day name (e.g., "Monday")
            .append(" (").append(dayShift[2]) // Full date (e.g., "Monday, 17 June 2025")
            .append("): ").append(dayShift[1]) // Shift type (e.g., "Morning")
            .append("\n");
    }

    messageBody.append("\nPlease be punctual and contact your supervisor if you have any concerns.\n");
    messageBody.append("\nRegards,\nPAUSAAMS Team");

    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.port", "587");

    Session session = Session.getInstance(props,
        new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

    try {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(senderEmail, "PAUSAAMS Supervisor"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiverEmail));
        message.setSubject(subject);
        message.setText(messageBody.toString());

        Transport.send(message);
        System.out.println("Roster email sent to " + receiverEmail);
    } catch (MessagingException | UnsupportedEncodingException e) {
        e.printStackTrace();
    }
}
}

   
