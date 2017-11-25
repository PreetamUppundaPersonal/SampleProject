package co.dlg.test.utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.SubjectTerm;
import java.util.Properties;

public class MailUtil {

    private static String getTextFromMessage(Message message) throws Exception {
        String result = "";
        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            result = getTextFromMimeMultipart(mimeMultipart);
        }
        return result;
    }

    private static String getTextFromMimeMultipart(
            MimeMultipart mimeMultipart) throws Exception {
        String result = "";
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result = result + "\n" + bodyPart.getContent();
                break; // without break same text appears twice in my tests
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result = result + getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
            }
        }
        return result;
    }

    public boolean isMessageRecieved(String subject, String firstname, String emailBody1, String emailBody2) {
        boolean isMailFound = false;
        try {
            Properties props = System.getProperties();
            props.setProperty("mail.store.protocol", "imaps");
            Session session = Session.getDefaultInstance(props, null);
            Store store = session.getStore("imaps");
            store.connect("imap.gmail.com", "sdirlin@gmail.com",
                    "Password@12345");
            Folder folder = store.getFolder("Inbox");
            folder.open(Folder.READ_WRITE);
            Message[] messages = null;

            Message mailFromClaimsDLG = null;

            for (int i = 0; i <= 5; i++) {
                messages = folder.search(new SubjectTerm(subject), folder.getMessages());
                //Wait for 10 seconds
                if (folder.getUnreadMessageCount() < 1) {
                    Thread.sleep(5000);
                } else {
                    break;
                }
            }
            //messages = folder.search(new SubjectTerm(subject), folder.getMessages());
            for (Message mail : messages) {
                if (!mail.isSet(Flags.Flag.SEEN)) {
                    mailFromClaimsDLG = mail;
                    getTextFromMessage(mailFromClaimsDLG);
                    if ((getTextFromMessage(mailFromClaimsDLG).contains("Dear " + firstname))
                            && getTextFromMessage(mailFromClaimsDLG).contains(emailBody1)
                            && getTextFromMessage(mailFromClaimsDLG).contains(emailBody2)) {
                        isMailFound = true;
                        //deleteAllMessages();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isMailFound;
    }

    public boolean replyToSavedClaimMessage(String emailAddress, String firstname) {
        boolean isMailFound = false;
        try {
            Properties props = System.getProperties();
            props.setProperty("mail.store.protocol", "imaps");
            Session session = Session.getDefaultInstance(props, null);
            Store store = session.getStore("imaps");
            store.connect("imap.gmail.com", emailAddress,
                    "Password@12345");
            Folder folder = store.getFolder("Inbox");
            folder.open(Folder.READ_WRITE);
            Message[] messages = null;

            Message mailFromClaimsDLG = null;

            for (int i = 0; i <= 5; i++) {
                messages = folder.search(new SubjectTerm(
                                "Sandbox:"),
                        folder.getMessages());
                //Wait for 10 seconds
                if (messages.length == 0) {
                    Thread.sleep(2000);
                } else
                    break;
            }

            for (Message mail : messages) {
                if (!mail.isSet(Flags.Flag.SEEN)) {
                    mailFromClaimsDLG = mail;
                    getTextFromMessage(mailFromClaimsDLG);
                    if ((getTextFromMessage(mailFromClaimsDLG).contains("Dear " + firstname))
                            && getTextFromMessage(mailFromClaimsDLG).contains("You've completed part of your online claim with us but we've seen that you haven't submitted it yet.")
                            && getTextFromMessage(mailFromClaimsDLG).contains("If you would like to submit this just sign in online and complete the claim within the next 7 days.")
                            && getTextFromMessage(mailFromClaimsDLG).contains("You can access the claim via your 'View my claims' page using your username and password.")) {
                        isMailFound = true;
                        String sub = mail.getSubject();
                        String replyTo = mail.getReplyTo()[0].toString();
                        sendEmail(replyTo, "RE: " + sub, "Test, Thanks");
                        //deleteAllMessages();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isMailFound;
    }

    public String isResetPasswordMessageRecieved(String emailAddress, String firstname) {
        String resetPasswordLink = "";
        int startIndex = -1;
        int endIndex = -1;
        try {
            Properties props = System.getProperties();
            props.setProperty("mail.store.protocol", "imaps");
            Session session = Session.getDefaultInstance(props, null);
            Store store = session.getStore("imaps");
            store.connect("imap.gmail.com", emailAddress,
                    "Password@12345");
            Folder folder = store.getFolder("Inbox");
            folder.open(Folder.READ_WRITE);
            Message[] messages = null;

            Message mailFromClaimsDLG = null;

            for (int i = 0; i <= 5; i++) {
                messages = folder.search(new SubjectTerm(
                                "Sandbox: DLG Online Claims - Password Reset"),
                        folder.getMessages());
                //Wait for 10 seconds
                if (messages.length == 0) {
                    Thread.sleep(5000);
                } else
                    break;
            }

            for (Message mail : messages) {
                if (!mail.isSet(Flags.Flag.SEEN)) {
                    mailFromClaimsDLG = mail;
                    getTextFromMessage(mailFromClaimsDLG);
                    if ((getTextFromMessage(mailFromClaimsDLG).contains("Dear " + firstname))
                            && getTextFromMessage(mailFromClaimsDLG).contains("You have chosen to reset your password.")
                            && getTextFromMessage(mailFromClaimsDLG).contains("Please use the following link to reset your password.")) {
                        startIndex = getTextFromMessage(mailFromClaimsDLG).indexOf("https://");
                        endIndex = getTextFromMessage(mailFromClaimsDLG).indexOf("Regards");
                        resetPasswordLink = getTextFromMessage(mailFromClaimsDLG).substring(startIndex, endIndex).trim();
                        //deleteAllMessages();
                        break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return resetPasswordLink;
    }


    public static void sendEmail(String to, String sub, String msg) {
        //Get properties object
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        //get Session
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("sdirlin@gmail.com", "Password@12345");
                    }
                });
        //compose message
        try {
            MimeMessage message = new MimeMessage(session);
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(sub);
            message.setText(msg);
            //send message
            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteAllMessages() {
        try {
            Properties props = System.getProperties();
            props.setProperty("mail.store.protocol", "imaps");
            Session session = Session.getDefaultInstance(props, null);
            Store store = session.getStore("imaps");
            store.connect("imap.gmail.com", "sdirlin@gmail.com",
                    "Password@12345");
            Folder folder = store.getFolder("Inbox");
            folder.open(Folder.READ_WRITE);
            for (int i = 0; i < folder.getMessages().length; i++) {
                folder.getMessages()[i].setFlag(Flags.Flag.DELETED, true);
            }
        } catch (Exception e) {

        }
    }

}
