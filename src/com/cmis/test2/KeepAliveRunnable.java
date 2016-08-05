package com.cmis.test2;
import javax.mail.MessagingException;

import com.sun.mail.iap.ProtocolException;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.protocol.IMAPProtocol;

public class KeepAliveRunnable implements Runnable {
	private static final long KEEP_ALIVE_FREQ = 300000; // 5 minutes

    private IMAPFolder folder;

    public KeepAliveRunnable(IMAPFolder folder) {
        this.folder = folder;
    }
    
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (!Thread.interrupted()) {
            try {
                Thread.sleep(KEEP_ALIVE_FREQ);

                // Perform a NOOP just to keep alive the connection
                System.out.println("Performing a NOOP to keep alvie the connection");
                folder.doCommand(new IMAPFolder.ProtocolCommand() {
                    public Object doCommand(IMAPProtocol p)
                            throws ProtocolException {
                        p.simpleCommand("NOOP", null);
                        return null;
                    }
                });
            } catch (InterruptedException e) {
                // Ignore, just aborting the thread...
            } catch (MessagingException e) {
                // Shouldn't really happen...
            	System.err.printf("Unexpected exception while keeping alive the IDLE connection", e);
            }
        }
	}

}
