package fileProcessorInterface.fileProcessorInterface;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.mail.MessagingException;
import javax.mail.Part;

public interface IProcessMail extends Remote {
	
	public void receive(Part part, int i) throws MessagingException, IOException ;
	public boolean isNew() throws MessagingException, RemoteException;
	public ArrayList<String> getUrlFromMail(String bodyText) throws MessagingException, RemoteException;
	public String getBodyText() throws MessagingException, RemoteException;

}
