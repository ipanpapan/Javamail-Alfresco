package com.cmis.test2;
import java.util.Date;
import java.util.List;

public class Email {
	private String subject;
	private String content;
	private Date receivedDate;
	private String from;
	private String to;
	private List<String> cc;
	private List<String> attached;
	
	public Email (String _subject, String _content, Date _receivedDate, String _from, List<String> toAddresses, String _to, List<String> etechment)
	{
		this.subject = _subject;
		this.content = _content;
		this.receivedDate = _receivedDate;
		this.from = _from;
		this.to = _to;
		this.cc = toAddresses;
		this.attached = etechment;
	}
	
	public Email (String _subject, String _content, Date _receivedDate, String _from, String _to, List<String> etechment)
	{
		this.subject = _subject;
		this.content = _content;
		this.receivedDate = _receivedDate;
		this.from = _from;
		this.to = _to;
		this.attached = etechment;
	}
	
	public String getSubject ()
	{
		return this.subject;
	}
	public String getContent ()
	{
		return this.content;
	}
	public Date getDate ()
	{
		return this.receivedDate;
	}
	public String getFrom ()
	{
		return this.from;
	}
	public String getTo ()
	{
		return this.to;
	}
	public List<String> getCC ()
	{
		return this.cc;
	}
	public List<String> getAttached ()
	{
		return this.attached;
	}
}
