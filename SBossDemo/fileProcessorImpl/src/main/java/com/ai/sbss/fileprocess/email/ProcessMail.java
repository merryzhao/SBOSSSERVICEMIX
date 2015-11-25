package com.ai.sbss.fileprocess.email;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import fileProcessorInterface.fileProcessorInterface.IProcessMail;

@SuppressWarnings("serial")
public class ProcessMail extends UnicastRemoteObject implements IProcessMail {

	private String saveAttchPath = "";
	private StringBuffer bodytext = new StringBuffer();
	private String dateformate = "yy-MM-dd HH:mm";

	private MimeMessage msg = null;

	public ProcessMail(MimeMessage msg) throws RemoteException {
		this.msg = msg;
	}

	public void setMsg(MimeMessage msg) {
		this.msg = msg;
	}

	public String getFrom(Boolean onlyAddress) throws MessagingException {
		InternetAddress[] address = (InternetAddress[]) msg.getFrom();
		String from = address[0].getAddress();
		if (from == null) {
			from = "";
		}
		String personal = address[0].getPersonal();
		if (personal == null) {
			personal = "";
		}
		String fromaddr = personal + "<" + from + ">";
		if (onlyAddress) {
			return from;
		} else {
			return fromaddr;
		}
	}

	/**
	 * ��ȡ�ʼ��ռ��ˣ����ͣ����͵ĵ�ַ����Ϣ�����������ݵĲ�����ͬ "to"-->�ռ���,"cc"-->�����˵�ַ,"bcc"-->���͵�ַ
	 * 
	 * @param type
	 * @return
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 */
	public String getMailAddress(String type) throws MessagingException,
			UnsupportedEncodingException {
		String mailaddr = "";
		String addrType = type.toUpperCase();
		InternetAddress[] address = null;

		if (addrType.equals("TO") || addrType.equals("CC")
				|| addrType.equals("BCC")) {
			if (addrType.equals("TO")) {
				address = (InternetAddress[]) msg
						.getRecipients(Message.RecipientType.TO);
			}
			if (addrType.equals("CC")) {
				address = (InternetAddress[]) msg
						.getRecipients(Message.RecipientType.CC);
			}
			if (addrType.equals("BCC")) {
				address = (InternetAddress[]) msg
						.getRecipients(Message.RecipientType.BCC);
			}

			if (address != null) {
				for (int i = 0; i < address.length; i++) {
					String mail = address[i].getAddress();
					if (mail == null) {
						mail = "";
					} else {
						mail = MimeUtility.decodeText(mail);
					}
					String personal = address[i].getPersonal();
					if (personal == null) {
						personal = "";
					} else {
						personal = MimeUtility.decodeText(personal);
					}
					String compositeto = personal + "<" + mail + ">";
					mailaddr += "," + compositeto;
				}
				mailaddr = mailaddr.substring(1);
			}
		} else {
			throw new RuntimeException("Error email Type!");
		}
		return mailaddr;
	}

	/**
	 * ��ȡ�ʼ�����
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws MessagingException
	 */
	public String getSubject() throws UnsupportedEncodingException,
			MessagingException {
		String subject = "";
		subject = MimeUtility.decodeText(msg.getSubject());
		if (subject == null) {
			subject = "";
		}
		return subject;
	}

	/**
	 * ��ȡ�ʼ���������
	 * 
	 * @return
	 * @throws MessagingException
	 */
	public String getSendDate() throws MessagingException {
		Date sendDate = msg.getSentDate();
		SimpleDateFormat smd = new SimpleDateFormat(dateformate);
		return smd.format(sendDate);
	}

	/**
	 * ��ȡ�ʼ���������
	 * 
	 * @return
	 */
	public String getBodyText() throws MessagingException {

		return bodytext.toString();
	}

	/**
	 * �����ʼ������õ����ʼ����ݱ��浽һ��stringBuffer�����У������ʼ� ��Ҫ����MimeType�Ĳ�ִͬ�в�ͬ�Ĳ�����һ��һ���Ľ���
	 * 
	 * @param part
	 * @throws MessagingException
	 * @throws IOException
	 */
	public void getMailContent(Part part) throws MessagingException,
			IOException {

		String contentType = part.getContentType();
		int nameindex = contentType.indexOf("name");
		boolean conname = false;
		if (nameindex != -1) {
			conname = true;
		}
		System.out.println("CONTENTTYPE:" + contentType);
		if (part.isMimeType("text/plain") && !conname) {
			bodytext.append((String) part.getContent());
		} else if (part.isMimeType("text/html") && !conname) {
			bodytext.append((String) part.getContent());
		} else if (part.isMimeType("multipart/*")) {
			Multipart multipart = (Multipart) part.getContent();
			int count = multipart.getCount();
			for (int i = 0; i < count; i++) {
				getMailContent(multipart.getBodyPart(i));
			}
		} else if (part.isMimeType("message/rfc822")) {
			getMailContent((Part) part.getContent());
		}

	}

	/**
	 * �ж��ʼ��Ƿ���Ҫ��ִ�������ִ����true�����򷵻�false
	 * 
	 * @return
	 * @throws MessagingException
	 */
	public boolean getReplySign() throws MessagingException {
		boolean replySign = false;
		String needreply[] = msg.getHeader("Disposition-Notification-TO");
		if (needreply != null) {
			replySign = true;
		}
		return replySign;
	}

	/**
	 * ��ȡ���ʼ���message-id
	 * 
	 * @return
	 * @throws MessagingException
	 */
	public String getMessageId() throws MessagingException {
		return msg.getMessageID();
	}

	/**
	 * �жϴ��ʼ��Ƿ��Ѷ������δ���򷵻�false���Ѷ�����true
	 * 
	 * @return
	 * @throws MessagingException
	 */
	public boolean isNew() throws MessagingException, RemoteException {
		boolean isnew = false;
		Flags flags = ((Message) msg).getFlags();
		Flags.Flag[] flag = flags.getSystemFlags();
		System.out.println("flags's length:" + flag.length);
		for (int i = 0; i < flag.length; i++) {
			if (flag[i] == Flags.Flag.SEEN) {
				isnew = true;
				System.out.println("seen message .......");
				break;
			}
		}

		return isnew;
	}

	/**
	 * �ж����Ƿ��������
	 * 
	 * @param part
	 * @return
	 * @throws MessagingException
	 * @throws IOException
	 */
	public boolean isContainAttch(Part part) throws MessagingException,
			IOException {
		boolean flag = false;

//		String contentType = part.getContentType();
		if (part.isMimeType("multipart/*")) {
			Multipart multipart = (Multipart) part.getContent();
			int count = multipart.getCount();
			for (int i = 0; i < count; i++) {
				BodyPart bodypart = multipart.getBodyPart(i);
				String dispostion = bodypart.getDisposition();
				if ((dispostion != null)
						&& (dispostion.equals(Part.ATTACHMENT) || dispostion
								.equals(Part.INLINE))) {
					flag = true;
				} else if (bodypart.isMimeType("multipart/*")) {
					flag = isContainAttch(bodypart);
				} else {
					String conType = bodypart.getContentType();
					if (conType.toLowerCase().indexOf("appliaction") != -1) {
						flag = true;
					}
					if (conType.toLowerCase().indexOf("name") != -1) {
						flag = true;
					}
				}
			}
		} else if (part.isMimeType("message/rfc822")) {
			flag = isContainAttch((Part) part.getContent());
		}

		return flag;
	}

	/**
	 * ���渽��
	 * 
	 * @param part
	 * @throws MessagingException
	 * @throws IOException
	 */
	public void saveAttchMent(Part part) throws MessagingException, IOException {
		String filename = "";
		if (part.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) part.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				BodyPart mpart = mp.getBodyPart(i);
				String dispostion = mpart.getDisposition();
				if ((dispostion != null)
						&& (dispostion.equals(Part.ATTACHMENT) || dispostion
								.equals(Part.INLINE))) {
					filename = mpart.getFileName();
					if (filename.toLowerCase().indexOf("gb2312") != -1) {
						filename = MimeUtility.decodeText(filename);
					}
					saveFile(filename, mpart.getInputStream());
				} else if (mpart.isMimeType("multipart/*")) {
					saveAttchMent(mpart);
				} else {
					filename = mpart.getFileName();
					if (filename != null
							&& (filename.toLowerCase().indexOf("gb2312") != -1)) {
						filename = MimeUtility.decodeText(filename);
					}
					saveFile(filename, mpart.getInputStream());
				}
			}

		} else if (part.isMimeType("message/rfc822")) {
			saveAttchMent((Part) part.getContent());
		}
	}

	/**
	 * ��ñ��渽���ĵ�ַ
	 * 
	 * @return
	 */
	public String getSaveAttchPath() {
		return saveAttchPath;
	}

	/**
	 * ���ñ��渽����ַ
	 * 
	 * @param saveAttchPath
	 */
	public void setSaveAttchPath(String saveAttchPath) {
		this.saveAttchPath = saveAttchPath;
	}

	/**
	 * �������ڸ�ʽ
	 * 
	 * @param dateformate
	 */
	public void setDateformate(String dateformate) {
		this.dateformate = dateformate;
	}

	/**
	 * �����ļ�����
	 * 
	 * @param filename
	 * @param inputStream
	 * @throws IOException
	 */
	private void saveFile(String filename, InputStream inputStream)
			throws IOException {
		String osname = System.getProperty("os.name");
		String storedir = getSaveAttchPath();
		String sepatror = "";
		if (osname == null) {
			osname = "";
		}

		if (osname.toLowerCase().indexOf("win") != -1) {
			sepatror = "//";
			if (storedir == null || "".equals(storedir)) {
				storedir = "d://temp";
			}
		} else {
			sepatror = "/";
			storedir = "/temp";
		}

		File storefile = new File(storedir + sepatror + filename);
		System.out.println("storefile's path:" + storefile.toString());

		BufferedOutputStream bos = null;
		BufferedInputStream bis = null;

		try {
			bos = new BufferedOutputStream(new FileOutputStream(storefile));
			bis = new BufferedInputStream(inputStream);
			int c;
			while ((c = bis.read()) != -1) {
				bos.write(c);
				bos.flush();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			bos.close();
			bis.close();
		}

	}

	public void receive(Part part, int i) throws MessagingException,
			IOException {

		System.out.println("------------------START-----------------------");
		String subject = new String();
		if (getSubject() != null && !"".equals(getSubject())) {
			subject = getSubject();
		} else {
			subject = "No subject";
		}

		System.out.println("Message" + i + " subject:" + subject);
		System.out.println("Message" + i + " from:" + getFrom(false));
		System.out.println("Message" + i + " isNew:" + isNew());
		boolean flag = isContainAttch(part);
		System.out.println("Message" + i + " isContainAttch:" + flag);
		System.out.println("Message" + i + " replySign:" + getReplySign());
		getMailContent(part);
		String bodyText = getBodyText();
		System.out.println(bodyText);
	}

	
	//���ʼ������д�url
	public ArrayList<String> getUrlFromMail(String bodyText) throws MessagingException {
		ArrayList<String> urlList = new ArrayList<String>();
		Pattern pa = Pattern.compile("(?<=<a href=[\"]).+?(?=[\"])");
		Matcher ma = pa.matcher(bodyText);
		String temp = null;
		while (ma.find()) {
			temp = ma.group();
			if (temp != null) {
				if (temp.startsWith(">")) {
					temp = temp.substring(1);
				}
				if (temp.startsWith("http")) {
					if (temp.contains(" ")) {
						temp = temp.substring(0, temp.indexOf(" "));
					}
					if (temp.contains("\"")) {
						temp = temp.substring(0, temp.indexOf("\""));
					}
					if (temp.contains("<")) {
						temp = temp.substring(0, temp.indexOf("<"));
					}
					
					if (!temp.equalsIgnoreCase("") && !urlList.contains(temp)){
						urlList.add(temp);
					}
				}
			}
		}
		return urlList;
	}
}