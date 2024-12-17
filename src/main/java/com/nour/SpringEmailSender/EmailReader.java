package com.nour.SpringEmailSender;

import com.sun.mail.imap.IMAPFolder;
import javax.mail.*;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import java.util.Properties;

public class EmailReader {
    public static void main(String[] args) {
        String host = "imap.gmail.com";
        String username = "nourin22123456@gmail.com";
        String password = "anyz zrgt shgh phem"; // Use app-specific password

        Properties props = new Properties();
        props.put("mail.imap.host", host);
        props.put("mail.imap.port", "993");
        props.put("mail.imap.ssl.enable", "true");

        Session session = Session.getInstance(props);

        try {
            Store store = session.getStore("imap");
            store.connect(username, password);

            IMAPFolder inbox = (IMAPFolder) store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            inbox.addMessageCountListener(new MessageCountListener() {
                @Override
                public void messagesAdded(MessageCountEvent e) {
                    Message[] messages = e.getMessages();
                    System.out.println("New message(s) received: " + messages.length);

                    for (Message message : messages) {
                        try {
                            System.out.println("Subject: " + message.getSubject());

                            // Safely process 'From' addresses
                            Address[] fromAddresses = message.getFrom();
                            if (fromAddresses != null) {
                                for (Address address : fromAddresses) {
                                    System.out.println("From: " + address.toString());
                                }
                            }

                            System.out.println("-----------------------------");
                        } catch (MessagingException me) {
                            me.printStackTrace();
                        }
                    }
                }

                @Override
                public void messagesRemoved(MessageCountEvent e) {
                    // Optionally handle message deletions
                }
            });

            // Create a separate thread to continuously listen for new emails
            Thread emailListeningThread = new Thread(() -> {
                try {
                    System.out.println("Listening for new emails...");
                    while (true) {
                        // Continuously listen for new messages
                        inbox.idle(); // This enters idle mode without blocking the main thread
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            emailListeningThread.start(); // Start the listener thread

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
