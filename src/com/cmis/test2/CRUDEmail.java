package com.cmis.test2;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.MimetypesFileTypeMap;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisBaseException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisContentAlreadyExistsException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.commons.lang3.StringUtils;

public class CRUDEmail {
	private static Session session;
	private static final String ALFRSCO_ATOMPUB_URL = "http://localhost:8080/alfresco/api/-default-/public/cmis/versions/1.1/atom";
	private static final String REPOSITORY_ID = "-default-";
	public static int indexEmail = 0;
	public static int indexFile = 0;
	
	static Folder connect() {
		SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
		Map<String, String> parameter = new HashMap<String, String>();
		parameter.put(SessionParameter.USER, "admin");
		parameter.put(SessionParameter.PASSWORD, "admin");
		parameter.put(SessionParameter.ATOMPUB_URL, ALFRSCO_ATOMPUB_URL);
		parameter.put(SessionParameter.BINDING_TYPE,
				BindingType.ATOMPUB.value());
		//parameter.put(SessionParameter.OBJECT_FACTORY_CLASS, "org.alfresco.cmis.client.impl.AlfrescoObjectFactoryImpl");
		parameter.put(SessionParameter.REPOSITORY_ID, REPOSITORY_ID);

		session = sessionFactory.createSession(parameter);
		//Session session = sessionFactory.getRepositories(parameter).get(0).createSession();
		return session.getRootFolder();
	}
	
	public static Document createFile (Folder target, String fileName) throws FileNotFoundException, CmisContentAlreadyExistsException
	{
		File content = new File("D:/Attachment/"+ fileName);
		Document fail = null;
		String mimeType = new MimetypesFileTypeMap().getContentType(content);
		System.out.println("Mimetype: " + mimeType);
		System.out.println("File: " + content.getAbsolutePath());
		Map<String, Object> props = new HashMap<String, Object>();
		props.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
		FileInputStream fis = new FileInputStream(content);
		DataInputStream dis = new DataInputStream(fis);
		
		ContentStream contentStream = session.getObjectFactory()
				.createContentStream(fileName, Long.valueOf(content.length()),
						mimeType, dis);
		try
		{
			
			props.put(PropertyIds.NAME, fileName);			
			fail = target.createDocument(props, contentStream, VersioningState.MAJOR);
			indexFile = 0;
		}
		catch(CmisBaseException e)
		{
            System.err.printf("error uploading file: "+ e.getMessage(), e);
        }
				
		return fail;
	}
	
	public static Folder createFolder(Folder target, String newFolderName) {
		System.out.println("test5");
		Folder subFolder = null;
		Map<String, String> props = new HashMap<String, String>();
		String result = "";
		props.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
		if (newFolderName != null)	
		{
			result = newFolderName.replaceAll("[\\-\\+\\.\\^,:/?]","");
			//props.put(PropertyIds.NAME, result);
		}			
		else
		{
			String uuid = UUID.randomUUID().toString();
			result = "Random " + uuid;
//			props.put(PropertyIds.NAME, result);
		}
		
		System.out.println(result);
		Matcher matcher = Pattern.compile("(Re\\s|Fwd\\s|Bls\\s|Trs\\s|RE\\s)(.*)").matcher(result);
		
		if (matcher.find())
		{			
			result = matcher.group(2);
			System.out.println(result + " dari regex");
		}
		
		try
		{
			props.put(PropertyIds.NAME, result);
			if (target.getPath().equals("/"))
				subFolder = (Folder) session.getObjectByPath(target.getPath() + result);
			else
				subFolder = (Folder) session.getObjectByPath("/Email" + "/" + result);
			System.out.println(subFolder.getPath());
			System.out.println("Folder already existed!");
		}
		catch (CmisObjectNotFoundException e)
		{
			props.put(PropertyIds.NAME, result);
			System.out.println("target: " + target.getPath());
			subFolder = target.createFolder(props);
			String subFolderId = subFolder.getId();
			System.out.println("Created new folder: " + subFolderId);
		}
		return subFolder;
	}
	
	public static void createDocument(Folder target, Email newDocName) throws CmisContentAlreadyExistsException {
		Map<String, Object> props = new HashMap<String, Object>();
		String result = "null";
		String tanggal;
		props.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
		
		if (newDocName.getSubject() != null)
		{
			result = newDocName.getSubject().replaceAll("[\\-\\+\\.\\^:,/?]","");
		}
		else
		{
			//INI MASIH SALAH COEG
			String uuid = UUID.randomUUID().toString();
			result = "Random " + uuid;
		}
		
		tanggal = newDocName.getDate().toString().replaceAll(":", ".");
		
		//props.put(PropertyIds.NAME, result + " - " + index);
		
		ArrayList<String> secIds = new ArrayList<String>();
		secIds.add("P:cb:statusable");
		secIds.add("P:cm:emailed");
		//secIds.add("P:cm:attachable");
		props.put(PropertyIds.SECONDARY_OBJECT_TYPE_IDS, secIds);
//		
//		props.put("cm:title", newDocName.getSubject());
		props.put("cm:subjectline", newDocName.getSubject());
		props.put("cm:sentdate", newDocName.getDate());
		props.put("cm:originator", newDocName.getFrom());
		props.put("cm:addressee", newDocName.getTo());
		props.put("cm:addressees", newDocName.getCC());
		props.put("cb:statuses", "Pending");
		
		//props.put("cmis:secondaryObjectTypeIds", "P:cm:titled");
//		props.put("cm:Subject", "asdasd");
		System.out.println("This is a test document: " + newDocName.getSubject());
		String content = newDocName.getContent();
		System.out.println("Isi content: " + content);
		byte[] buf = null;
		try {
			buf = content.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		ByteArrayInputStream input = new ByteArrayInputStream(buf);
		ContentStream contentStream = session.getObjectFactory()
				.createContentStream(result, buf.length,
						"text/plain", input);
		props.put(PropertyIds.NAME, "[" +  tanggal + "] " + result);		
		try
		{
			target.createDocument(props, contentStream, VersioningState.MAJOR);
			System.out.println("berhasil dibuat");
		}
		catch(CmisBaseException e)
		{
            System.err.printf(e.getMessage(), e);
        }
	}
	
	public static void DeleteDocument(Folder target, String delDocName) {
		try {
			CmisObject object = session.getObjectByPath(target.getPath()
					+ delDocName);
			Document delDoc = (Document) object;
			delDoc.delete(true);
		} catch (CmisObjectNotFoundException e) {
			System.err.println("Document is not found: " + delDocName);
		}
	}
	
	static void listFolder(int depth, Folder target) {
		String indent = StringUtils.repeat("\t", depth);
		for (Iterator<CmisObject> it = target.getChildren().iterator(); it
				.hasNext();) {
			CmisObject o = it.next();
			if (BaseTypeId.CMIS_DOCUMENT.equals(o.getBaseTypeId())) {
				System.out.println(indent + "[Docment] " + o.getName());
			} else if (BaseTypeId.CMIS_FOLDER.equals(o.getBaseTypeId())) {
				System.out.println(indent + "[Folder] " + o.getName());
				listFolder(++depth, (Folder) o);
			}
		}
	}
	
	public static void cleanup(Folder target, String delFolderName) {
		try {
			CmisObject object = session.getObjectByPath(target.getPath()
					+ delFolderName);
			Folder delFolder = (Folder) object;
			delFolder.deleteTree(true, UnfileObject.DELETE, true);
		} catch (CmisObjectNotFoundException e) {
			System.err.println("No need to clean up.");
		}
	}
}