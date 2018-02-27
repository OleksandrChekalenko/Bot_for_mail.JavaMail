import javax.mail.*;
import javax.mail.Flags.Flag;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class RecieveGmail {

    private static String saveDirectory = "D:/Downloads/Attachments";

    public void downloadAttachment(Message message) throws MessagingException, IOException {
        String contentType = message.getContentType();
        String messageContent = "";

        // store attachment file name, separated by comma
        String attachFiles = "";

        if (contentType.contains("multipart")) {
            // content may contain attachments
            Multipart multiPart = (Multipart) message.getContent();
            int numberOfParts = multiPart.getCount();
            for (int partCount = 0; partCount < numberOfParts; partCount++) {
                MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                    // this part is attachment
                    String fileName = part.getFileName();
                    attachFiles += fileName + ", ";
                    part.saveFile(saveDirectory + File.separator + fileName);
                } else {
                    // this part may be the message content
                    messageContent = part.getContent().toString();
                }
            }

            if (attachFiles.length() > 1) {
                attachFiles = attachFiles.substring(0, attachFiles.length() - 2);
            }
        } else if (contentType.contains("text/plain")
                || contentType.contains("text/html")) {
            Object content = message.getContent();
            if (content != null) {
                messageContent = content.toString();
            }
        }
    }

    public void sent(Session session, String sender) throws MessagingException {

        String fromEmail = sender.substring(sender.indexOf("<") + 1, sender.indexOf(">"));
        String fromName = sender.substring(0,sender.indexOf("<"));

        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress("nature2578@gmail.com"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(fromEmail));
            message.setSubject("Test");
            message.setText(fromName + ", thank you for your file.");

            Transport.send(message);

        } catch (AddressException e) {
            e.getMessage();
        } catch (MessagingException e) {
            e.getMessage();
        }


        System.out.println("Sent autoAnswer successfully......");
    }

    public void read() {

        Properties props = new Properties();

        try {
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("nature2578@gmail.com",
                            "srtsrvts123");
                }
            });

            Store store = session.getStore("imaps");
            store.connect("smtp.gmail.com", "nature2578", "srtsrvts123");



            Folder inbox = store.getFolder("inbox");
            inbox.open(Folder.READ_WRITE);

            /*Flags seen = new Flags(Flag.SEEN);*/
            FlagTerm unSeenFlagTern = new FlagTerm(new Flags(Flag.SEEN), false);

            Message[] unreadMessages = inbox.search(unSeenFlagTern);
            //System.out.println( "Get unread message count:  " + inbox.getUnreadMessageCount());
            System.out.println("Unread messages:  " + unreadMessages.length);
            System.out.println("Read messages and download attachments...");

            for (int i = 0; i < unreadMessages.length; i++) {
                unreadMessages[i].setFlag(Flag.SEEN, true);
                downloadAttachment(unreadMessages[i]);
                sent(session, unreadMessages[i].getFrom()[0].toString());
            }
            Message[] unreadMessagesAfter = inbox.search(unSeenFlagTern);
            System.out.println("Unread messages:  " + unreadMessagesAfter.length);


            System.out.println("Total Messages: " + inbox.getMessageCount());

            inbox.close(true);
            store.close();



        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
