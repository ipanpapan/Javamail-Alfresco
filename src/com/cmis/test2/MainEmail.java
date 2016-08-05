package com.cmis.test2;

import java.io.Console;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import javax.mail.MessagingException;

import org.apache.chemistry.opencmis.client.api.Folder;

public class MainEmail {
	static Folder newFolder = null;
	private static Scanner scanner;
	static Console console = System.console();
	public static void main(String[] args) throws MessagingException, IOException {
		// TODO Auto-generated method stub
		System.out.print("E-mail: ");
		scanner = new Scanner(System.in);
		String emailAddressUser = scanner.nextLine();
		
		char passwordArray[] = console.readPassword("Password: ");
		String password = new String(passwordArray);
		
		GetEmail.getAllEmail(emailAddressUser, password);
		
		Folder root = CRUDEmail.connect();
		CRUDEmail.cleanup(root, "Email");
		newFolder = CRUDEmail.createFolder(root, "Email");		
		
		pushToAlfresco();
		
		GetEmail.getNewEmail();
	}
	
	public static void pushToAlfresco () throws FileNotFoundException
	{
		for (Email test : EmailDaoImpl.getAllEmail())
		{
			Folder insideFolder = CRUDEmail.createFolder(newFolder, test.getSubject());
			CRUDEmail.createDocument(insideFolder, test);
			if (test.getAttached().size() > 0)
			{
				for (String asd : test.getAttached())
				{
					CRUDEmail.createFile(insideFolder, asd);
				}
			}
		}
	}
}
