package com.ai.sbss.fileprocess.email;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.MimeMessage;

import org.junit.Test;

import fileProcessorInterface.fileProcessorInterface.IArticleProcessor;
import fileProcessorInterface.fileProcessorInterface.IProcessMail;
import fileProcessorInterface.fileProcessorInterface.ISendMail;

public class ProcessMailTestCase {
	HashMap<String, String> mailItem = new HashMap<String, String>();
	Properties props = new Properties();

	private void initParams() {
		mailItem.put("user_name", "zhaojl");
		mailItem.put("password", "7fb520f051kiyh@!");
		mailItem.put("host_name", "mail.asiainfo.com");
		mailItem.put("protocol", "pop3");
		mailItem.put("address_to", "303301416@qq.com");
		mailItem.put("address_from", "zhaojl@asiainfo.com");
		mailItem.put("mail_subject", "This is a reply email");

		props.setProperty("mail.smtp.host", "mail.asiainfo.com");
		props.setProperty("mail.smtp.auth", "true");

	}

/*	@Test
	public void rmiSendMail() throws Exception {
		ISendMail sendMail = (ISendMail) Naming
				.lookup("rmi://192.168.1.63:8003/sendmail");
		System.out.println("-----------SendMail testing begin-------------");
		initParams();
		sendMail.sendMail(props, mailItem);
		System.out.println("-----------SendMail testing end --------------");
	}*/

	/*@Test	
	public void testRecive() throws Exception {
		//This test had been passed 
		// Init params
		initParams();
		String userName = mailItem.get("user_name");
		String password = mailItem.get("password");
		String protocol = mailItem.get("protocol");
		String hostName = mailItem.get("host_name");

		// Create session 
		Session	session = Session.getDefaultInstance(props, new SimpleAuthenticator(userName,
				password));
		URLName urlname = new URLName(protocol, hostName, 110, null, userName,
				password);
		props.getProperty("mail.smtp.host");
		Store store = session.getStore(urlname);
		store.connect();

		Folder folder = store.getFolder("INBOX");
		folder.open(Folder.READ_ONLY);
		Message msgs[] = folder.getMessages();

		int count = msgs.length;
		ProcessMail rm = null;

		// Receive each mail
		for (int i = 0; i < count; i++) {
			rm = new ProcessMail((MimeMessage) msgs[i]);
			if (!rm.isNew()) { // Only read new mail
				rm.receive(msgs[i], i);
				rm.getUrlFromMail(rm.getBodyText());
			}
		}
	}
*/
/*	@Test
	public void receiveMailTest() throws MalformedURLException,
			RemoteException, NotBoundException, Exception {
		initParams();
		IProcessMail mail = (IProcessMail) Naming
				.lookup("rmi://127.0.0.1:8002/mailprocess");

		Session session = Session.getDefaultInstance(
				props,
				new SimpleAuthenticator(mailItem.get("user_name"), mailItem
						.get("password")));
		URLName urlname = new URLName(mailItem.get("protocol"),
				mailItem.get("host_name"), 110, null,
				mailItem.get("user_name"), mailItem.get("password"));
		props.getProperty("mail.smtp.host");
		Store store = session.getStore(urlname);
		store.connect();

		Folder folder = store.getFolder("INBOX");
		folder.open(Folder.READ_ONLY);
		Message msgs[] = folder.getMessages();

		int count = msgs.length;
		mail = null;
		for (int i = 0; i < count; i++) {
			mail = new ProcessMail((MimeMessage) msgs[i]);
			if (!mail.isNew()) { // Only read new mail.
				System.out.println(i + "begin process a new mail");
				mail.receive(msgs[i], i);
				List<String> imageList = mail
						.getUrlFromMail(mail.getBodyText());
				System.out.println(imageList);
			}
		}

	}
*/
	@Test
	public void websiteProcessTest() throws MalformedURLException,
			RemoteException, NotBoundException {
		IArticleProcessor rhello = (IArticleProcessor) Naming
				.lookup("rmi://127.0.0.1:8001/fileprocess");
		String url = "http://mp.weixin.qq.com/s?__biz=MjM5ODIwNDM1NQ==&mid=209751912&idx=1&sn=2f668517c5d8c572e19328f9fd63e151#rd,content_origin:ChengDu,wx_openid:oxHYLt_tFwpqZ_uQ2wK1Ol-Jp_Qg";
		String path = "D:\\Download\\webmagic";
		String contentId = "100020";
		Long favoriteEntryId = 1111L;

		String returnValue = rhello.getMessageFromWebSite(url, contentId, path,
				favoriteEntryId);
		System.out
				.println("-------------This is a return value from getMessaeFromWebsite:--------------\n"
						+ returnValue);
		System.out.println("haha"
				+ rhello.getMessageFromWebSite(url, contentId, path,
						favoriteEntryId));
	}

}
