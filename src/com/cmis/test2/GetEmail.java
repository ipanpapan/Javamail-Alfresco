package com.cmis.test2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Date;

import java.text.SimpleDateFormat;
import java.text.ParseException;

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
    static String isiFinal = "";
    static EmailDaoImpl haha;
    private static boolean textIsHtml = false;
    
    public static void connect () throws MessagingException
    {
    	Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");

        Session session = Session.getDefaultInstance(props, null);

        store = session.getStore("imaps");
        store.connect("imap.googlemail.com", "irfan.elfakhar@gmail.com", "lightningreveanant");

        folder = (IMAPFolder) store.getFolder("inbox");
    }
    
    public static void getAllEmail () throws MessagingException, IOException
    {
    	connect();
    	folder.open(Folder.READ_WRITE);
        //folder.open(Folder.READ_WRITE); //kalo READ_WRITE ngebuat yang belom dibaca jadi kebaca
          
        //Message[] messages = folder.getMessages();
        FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
        Message messages[] = folder.search(ft);
        
        seeEmail(messages);
    }
    
    /**
     * Return the primary text content of the message.
     */
    public static String getText(Part p) throws MessagingException, IOException {
        if (p.isMimeType("text/*"))
        {
            System.out.println("Dia text");
        	String s = (String)p.getContent();
        	System.out.println(s);
        	if (!p.isMimeType("text/html"))
        	{
        		isiFinal = s;
//            	System.out.println("Isinya: " + isiFinal);
                textIsHtml = p.isMimeType("text/html");
        	}        		
            return s;
        }

        if (p.isMimeType("multipart/alternative"))
        {
        	System.out.println("Dia multialter");
        	// prefer html text over plain text
            Multipart mp = (Multipart)p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++)
            {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain"))
                {
                	System.out.println("Dia textplain");
                	if (text == null)
                        text = getText(bp);
                    continue;
                } else if (bp.isMimeType("text/html"))
                {
                	System.out.println("Dia texthtml");
                	String s = getText(bp);
                    if (s != null)
                    {
                    	System.out.println(s);
                        return s;
                    }
                } else
                {
                    return getText(bp);
                }
            }
            return text;
        } else if (p.isMimeType("multipart/*"))
        {
            Multipart mp = (Multipart)p.getContent();
            for (int i = 0; i < mp.getCount(); i++)
            {
                String s = getText(mp.getBodyPart(i));
                if (s != null)
                {
                	System.out.println(s);
                    return s;
                }
            }
        }

        return null;
    }
    
    public static void seeEmail (Message messages[]) throws MessagingException, IOException
    {
    	haha = new EmailDaoImpl();
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
    		
    		String result = "";    		
            
        	subject = msg.getSubject();
        	//System.out.println(msg.getContent());
        	
        	if (contentType.contains("multipart"))
    		{
                //System.out.println("ini gak ada attachment");
        		// content may contain attachments
                Multipart multiPart = (Multipart) msg.getContent();
                int numberOfParts = multiPart.getCount();
                System.out.println("Punya " + numberOfParts + " parts");
                for (int partCount = 0; partCount < numberOfParts; partCount++)
                {
                	part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                	if (!textIsHtml)
                		result = getText(part);
                	System.out.println("Result part1: " + result);
                    
                    if (part.getDisposition() == null)
                    {
                    	System.out.println("Null: "  + part.getContentType());
                    	
                    	if ((part.getContentType().length() >= 10) && part.getContentType().toLowerCase().substring(0, 10).equals("text/plain"))
                    	{
                    		//part.writeTo(System.out);
                    	}
                    	else
                    	{
                    		System.out.println("ini masuk");
                    		System.out.println(part.getContent().toString());
                    		System.out.println("Other Body: " + part.getContentType());
                    		//part.writeTo(System.out);
                    	}
                    }                    
                    if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition()))
                    {
                        // this part is attachment
                        String fileName = part.getFileName();
                        etechment.add(fileName);
                        //attachFiles += fileName + ", ";
                        part.saveFile("D:/Attachment" + File.separator + fileName);
                        System.out.println("test1");
                    }
                    else
                    {
                        // this part may be the message content
                        messageContent = part.getContent().toString();
                        System.out.println(messageContent);
                        System.out.println("sadasdsad");
                    }
                    System.out.println("Nomor Part: " + partCount);
                    System.out.println("Content: " + part.getContent().toString());
                }               

                if (attachFiles.length() > 1) {
                    attachFiles = attachFiles.substring(0, attachFiles.length() - 2);
                }
            } else if (contentType.contains("text/plain")
                    || contentType.contains("text/html")) {
            	System.out.println("aaaaaa");
                Object content = msg.getContent();
                if (content != null) {
                    messageContent = content.toString();
                    System.out.println("test2");
                }
            }
        	        		
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

        	System.out.println("Resultnya: " + result);
        	//System.out.println(messageContent);
        	if (messageContent.isEmpty())
        		System.out.println("gak ada isinya");
        	else
        		System.out.println("Contentnya: " + messageContent);
        	
        	haha.hmm(subject, isiFinal, msg.getReceivedDate(), email, toAddresses, recipient, etechment);
        	
        	//System.out.println(isiFinal);
        	if (isiFinal.contains("Forwarded message") || isiFinal.contains("Pesan terusan") || isiFinal.contains("From"))
        	{
        		getForwardMessage(isiFinal);
        	}
        	
        	isiFinal = "";
        	textIsHtml = false;
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
    
    static int index = 0;
    public static void getForwardMessage (String _message)
    {
    	String result = "";
    	String from = "";
    	String date = "";
    	String subjectForward = "";
    	String to = "";
    	String isi = _message;
    	Date tanggal = null;
    	
		System.out.println("_message isinya: " + isi);
		//String output = isi.replace("\r\n", "\\r\\n");
		//System.out.println(output);
		while (isi.contains("Forwarded message"))
		{
			Matcher matcher = Pattern.compile("(?s)[-\\s\\w\\d]*-\\r\\n(.*)").matcher(isi);
			List<String> etechment = new ArrayList<String>();
			if (matcher.find())
			{
				System.out.println("_message isinya: " + isi);
				result = matcher.group(1);
				System.out.println(result + " dari regex");
			}
			else
			{
				System.out.println("not found");
			}
			
			Matcher matcherFrom = Pattern.compile("(?s).*?<(.*?)>\\r\\n.*?:.(.*?)\\r\\n.*?:.(.*?)\\r\\n.*?\\s(.*?|<.*>)\\r\\n(.*)").matcher(result);
			
			if (matcherFrom.find())
			{
				from = matcherFrom.group(1);
				date = matcherFrom.group(2);
				subjectForward = matcherFrom.group(3);
				to = matcherFrom.group(4);
				isi = matcherFrom.group(5);
				
				try
				{
					SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM dd, yyyy 'at' HH:mm a", Locale.US);				
					tanggal = formatter.parse(date);
				}
				catch (ParseException e)
				{
					try
					{
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd HH:mm z", Locale.US);
						tanggal = formatter.parse(date);
					}
					catch (ParseException pe)
					{
						pe.printStackTrace();
					}
					
				}
				
				haha.forward(subjectForward, isi, tanggal, from, to, etechment);
				
				for (int p = 1; p<=5; p++)
				{
					if (p==5)
					{
						System.out.println(matcherFrom.group(p) + " isi terahir");
					}
					else
						System.out.println(matcherFrom.group(p) + " dari regex1");
				}
			}
			else
			{
				System.out.println("not found 2");
			}
			index++;
			System.out.println(index);
		}
    }
}
