package com.cmis.test2;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.mail.MessagingException;

import org.apache.chemistry.opencmis.client.api.Folder;

public class MainEmail {
	static Folder newFolder = null;
	public static void main(String[] args) throws MessagingException, IOException {
		// TODO Auto-generated method stub
		GetEmail.getAllEmail();
		
		Folder root = CRUDEmail.connect();
		//CRUDEmail.cleanup(root, "Email");
		newFolder = CRUDEmail.createFolder(root, "Email");
		String path = newFolder.getPath();
		System.out.println(path);
		
		pushToAlfresco();
		
		GetEmail.getNewEmail();
	}
	
	public static void pushToAlfresco () throws FileNotFoundException
	{
		for (Email test : EmailDaoImpl.getAllEmail())
		{
			System.out.println("mulaaii");
			System.out.println("isi list: " + EmailDaoImpl.getAllEmail().size());
			String path = newFolder.getPath();
			System.out.println(path);
			Folder insideFolder = CRUDEmail.createFolder(newFolder, test.getSubject());
			System.out.println("test4");
			CRUDEmail.createDocument(insideFolder, test);
			if (test.getAttached().size() > 0)
			{
				for (String asd : test.getAttached())
				{
					CRUDEmail.createFile(insideFolder, asd);
				}
			}
			System.out.println("lanjuut");
		}
	}
}
