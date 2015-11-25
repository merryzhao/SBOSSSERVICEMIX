package com.ai.sbss.fileprocess.email;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import fileProcessorInterface.fileProcessorInterface.ISendMail;

@SuppressWarnings("serial")
public class SendMail extends UnicastRemoteObject implements ISendMail {
	
	

	protected SendMail() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	public boolean sendMail(Properties props, HashMap<String, String> mailItem) throws Exception{
		System.out.println("sendMail--------------start");
		String userName = mailItem.get("user_name");
		String password = mailItem.get("password");
		String hostName = mailItem.get("host_name");
		String protocol = mailItem.get("protocol");
		String addressTo = mailItem.get("address_to");
		String addressFrom = mailItem.get("address_from");
		String mailSubject = mailItem.get("mail_subject");
		
		//Check the hashmap,if there is an empty value.
		for (String key : mailItem.keySet()) {
			String value=mailItem.get(key);			
			if (value == null || "".equals(value)){
				if (key != "mail_subject"){
					System.out.println(key + ":" + value + "is a wrong value");
					return false;
				}
				
			}
		}
		
		String mailContent = "This is a replay from " + userName;
		
		Session session = Session.getDefaultInstance(props, new SimpleAuthenticator(userName, password));
		Store store = session.getStore(protocol);
		store.connect(hostName, userName, password);
		
		
		MimeMessage replyMessage = new MimeMessage(session);
		replyMessage.setFrom(new InternetAddress(addressFrom));
		replyMessage.setRecipients(MimeMessage.RecipientType.TO, addressTo);
		
		if (mailSubject==null && "".equals(mailSubject)){
			mailSubject = "This is an automatic reply";
		}
		replyMessage.setSubject(mailSubject);
		replyMessage.setText(mailContent);
		replyMessage.saveChanges();
		
		props.put("mail.smtp.auth", "true");
		
		
		System.out.println(props.getProperty("pop3"));
		Transport transport = session.getTransport("smtp");
		transport.connect(hostName, userName, password);
		Transport.send(replyMessage);
		return true;
	}

}
