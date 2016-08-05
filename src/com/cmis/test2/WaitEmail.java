package com.cmis.test2;

import javax.mail.MessagingException;

import com.sun.mail.imap.IMAPFolder;

public class WaitEmail {
	public static void startListening(IMAPFolder imapFolder) {
	    // We need to create a new thread to keep alive the connection
	    Thread t = new Thread(
	        new KeepAliveRunnable(imapFolder), "IdleConnectionKeepAlive"
	    );

	    t.start();

	    while (!Thread.interrupted()) {
	        System.out.println("Starting IDLE");
	        try {
	            imapFolder.idle();
	        } catch (MessagingException e) {
	            System.err.printf("Messaging exception during IDLE", e);
	            throw new RuntimeException(e);
	        }
	    }

	    // Shutdown keep alive thread
	    if (t.isAlive()) {
	        t.interrupt();
	    }
	}
}
