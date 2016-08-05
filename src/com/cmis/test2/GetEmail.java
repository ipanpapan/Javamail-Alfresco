package com.cmis.test2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.search.FlagTerm;

import com.sun.mail.imap.IMAPFolder;

public class GetEmail {
	static IMAPFolder folder = null;
    static Store store = null;
    static String subject = null;
    
    public static void connect (String _email, String _password) throws MessagingException
    {
    	Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");

        Session session = Session.getDefaultInstance(props, null);

        store = session.getStore("imaps");
        store.connect("imap.googlemail.com", _email, _password);

        folder = (IMAPFolder) store.getFolder("inbox");
    }
    
    public static void getAllEmail (String _email, String _password) throws MessagingException, IOException
    {
    	connect(_email, _password);
    	folder.open(Folder.READ_ONLY);
        //folder.open(Folder.READ_WRITE); //kalo READ_WRITE ngebuat yang belom dibaca jadi kebaca
          
        //Message[] messages = folder.getMessages();
        FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
        Message messages[] = folder.search(ft);
        
        seeEmail(messages);
    }
    
    public static void seeEmail (Message messages[]) throws MessagingException, IOException
    {
    	EmailDaoImpl haha = new EmailDaoImpl();
    	for (int i=0; i < messages.length;i++) 
        {
        	System.out.println("*****************************************************************************");
        	System.out.println("MESSAGE " + (i + 1) + ":");
        	Message msg =  messages[i];
        	String contentType = msg.getContentType();
    		String messageContent = "";
    		String attachFiles = "";
    		MimeBodyPart part = null;
    		List<String> etechment = new ArrayList<String>();
            
        	subject = msg.getSubject();
        	
        	if (contentType.contains("multipart"))
    		{
                // content may contain attachments
                Multipart multiPart = (Multipart) msg.getContent();
                int numberOfParts = multiPart.getCount();                
                for (int partCount = 0; partCount < numberOfParts; partCount++)
                {
                    part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                    if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition()))
                    {
                        // this part is attachment
                        String fileName = part.getFileName();
                        etechment.add(fileName);
                        //attachFiles += fileName + ", ";
                        part.saveFile("D:/Attachment" + File.separator + fileName);
                    }
                    else
                    {
                        // this part may be the message content
                        messageContent = part.getContent().toString();
                    }
                }

                if (attachFiles.length() > 1) {
                    attachFiles = attachFiles.substring(0, attachFiles.length() - 2);
                }
            } else if (contentType.contains("text/plain")
                    || contentType.contains("text/html")) {
                Object content = msg.getContent();
                if (content != null) {
                    messageContent = content.toString();
                }
            }
        	
        	if (messageContent.contains("javax"))
    			messageContent = "";
        	        		
        	System.out.println("Subject: " + subject);
        	        	
        	Address[] froms = msg.getFrom();
        	String email = froms == null ? null : ((InternetAddress) froms[0]).getAddress();
        	
        	List<String> toAddresses = new ArrayList<String>();
        	int j = 0;
        	Address[] recipients = msg.getRecipients(Message.RecipientType.TO);
        	String recipient = "";
        	for (Address address : recipients) {
        		if (j == 0)
        		{
        			recipient = address == null ? null : ((InternetAddress) address).getAddress();
        			j = 1;
        		}        			
        		else
        		{
        			String tuadres = address == null ? null : ((InternetAddress) address).getAddress();
            	    toAddresses.add(tuadres);
        		}        		
        	}

        	//System.out.println(toAddresses);
        	//System.out.println(attachFiles);
        	
        	haha.hmm(i, subject, messageContent, msg.getReceivedDate(), email, toAddresses, recipient, etechment);
        }
    }
    
    public static void getNewEmail ()
    {
    	folder.addMessageCountListener(new MessageCountAdapter() {
            public void messagesAdded(MessageCountEvent ev) {
                Message[] msgs = ev.getMessages();
                try {
					seeEmail(msgs);
				} catch (MessagingException | IOException e) {
					e.printStackTrace();
				}
                try {
                	MainEmail.pushToAlfresco();
					System.out.println("asd");
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            });
        
        WaitEmail.startListening(folder);
    }
    }
