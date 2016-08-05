package com.cmis.test2;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class EmailDaoImpl {
	public static ArrayList<Email> listEmail;
	
	public EmailDaoImpl()
	{
		listEmail = new ArrayList<Email>();
	}
	
	public void hmm(int _id, String _subject, String _content, Date _receivedDate, String _from, List<String> toAddresses, String _to, List<String> etechment) throws FileNotFoundException
	{		
		Email emailMasuk = new Email(_id, _subject, _content, _receivedDate, _from, toAddresses, _to, etechment);
		listEmail.add(emailMasuk);
	}
	
	public static ArrayList<Email> getAllEmail()
	{
		return listEmail;
	}
	
	public static Email getEmail (int _id)
	{
		System.out.println(_id);
		return listEmail.get(_id);
	}
}
