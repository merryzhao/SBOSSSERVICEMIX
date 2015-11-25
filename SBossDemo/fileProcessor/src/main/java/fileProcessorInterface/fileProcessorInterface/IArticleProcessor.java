package fileProcessorInterface.fileProcessorInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IArticleProcessor extends Remote{

	/*public String getImageUrl(Page page)  throws RemoteException;;*/

	// 根据url,contentId, path获取网站的title、description、content并写入path目录,返回文件名
	public String getMessageFromWebSite(String url, String contentId,
			String path, Long favoriteEntryId) throws RemoteException;
}
