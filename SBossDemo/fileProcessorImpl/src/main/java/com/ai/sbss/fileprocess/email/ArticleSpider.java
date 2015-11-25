package com.ai.sbss.fileprocess.email;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import fileProcessorInterface.fileProcessorInterface.IArticleProcessor;

@SuppressWarnings("serial")
public class ArticleSpider extends UnicastRemoteObject implements
		PageProcessor, IArticleProcessor {
	public JSONObject responseJson = new JSONObject();
	public ArticleSpider() throws RemoteException {
		super();
	}

	private String defaultDirName = "SpiderFiles";
	
	

	private Site site = Site
			.me()
			.setCharset("utf-8")
			.setDomain("sbssGet")
			.setUserAgent(
					"Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)")
			.setRetryTimes(3).setSleepTime(1000);
	private Logger logger = LoggerFactory.getLogger(getClass());

	// 限定返回字符串的长度，在获取title和description的时候使用。
	private int descLength = 512;

	String contentId = new String();

	String defaultImage = "http://171.221.254.231:9000/3499e0b025d2f770ccc26d138d5c6c97.jpg";

	public Site getSite() {
		// TODO Auto-generated method stub
		return site;
	}

	//根据指定的长度（length）获取文件的标题
	public String getTitle(Page page, int length) {		
		String title = new String();
		title = page.getHtml().xpath("//title/text()").toString();
		if ("".equals(title) || title == null) {
			title = "Unamed website";
		}
		if (title.getBytes().length > length) {
			int needLength = title.length()
					- (title.getBytes().length - length) / 2;
			title = title.substring(0, needLength);
		}
		return title;
	}

	/*
	获取页面中的图片URL，这里只做了粗略的判断并没有对每一个URL进行深入的解析
	所以判断的URL的后缀，如果为png  ,jpeg 或者 jpg则判断为图片URL
	*/
	public String getImageUrl(Page page) {
		String imageUrl = new String();
		imageUrl = page.getUrl().regex("http://.*\"").toString();
		if ("".equals(imageUrl) || imageUrl == null) {
			imageUrl = page
					.getHtml()
					.xpath("//body")
					.xpath("//img")
					.regex("http?://.+png|http?://.+jpeg|http?://.+jpg\"")
					.toString();
			if ("".equals(imageUrl) || imageUrl == null) {
				imageUrl = defaultImage;
			} else {
				imageUrl = imageUrl.replace("\"", "");
			}
		}
		return imageUrl;
	}

	//获取网页中的描述性文字，由于描述性文字可能过长，导致存储产生问题，所以这里附加了长度限制。
	public String getDescription(Page page, int length) {
		String description = new String();
		String tmpDesc = new String();

		tmpDesc = page.getHtml()
				.xpath("//meta[@name='Description']|meta[@name='description']")
				.toString();
		if ("".equals(tmpDesc) || tmpDesc == null) {
			description = "This website didnt have a description!";
		} else {
			description = tmpDesc.replace("<meta ", "")
					.replaceAll("(?i)content=\"", "")
					.replaceAll("name=\"(?i)Description\"", "")
					.replace(" ", "").replace("\"/>", "");
		}
		if (description.getBytes().length > length) {
			int needLength = description.length()
					- (description.getBytes().length - length) / 2;
			description = description.substring(0, needLength);
		}
		return description;
	}

	//获取原始的内容文本
	public String getOriginalContent(Page page) {
		String content = new String();

		content = page.getHtml().xpath("//body/tidyText()").toString();
		if ("".equals(content) && content == null) {
			content = "-1";
		}
		return content;
	}

	//获取所有的内容文本，这里与原始的不同，只是调用的allText()
	public String getContent(Page page) {
		String content = new String();
		content = page.getHtml().xpath("//body/allText()").toString();
		if ("".equals(content) || content == null) {
			content = "-1";
		}

		return content;
	}

	public void process(Page page) {
		//content id
		page.putField("contentId", contentId);
		page.putField("title", getTitle(page, descLength));
		page.putField("description", getDescription(page, descLength));
		page.putField("imageUrl", getImageUrl(page));
		page.putField("content", getContent(page));

		// 如果获取不到标题，则不输出成文件
		if (page.getResultItems().get("title") == null) {
			// skip this page
			page.setSkip(true);
		}
	}

	//判断目录名称，如果目录不存在则创建，创建不成功则报错返回
	public boolean createDir(String dirName) {
		File dir = new File(dirName);
		if (dir.exists()) {
			logger.error("Create dir" + defaultDirName
					+ "failed, already exist.");
			return false;
		}
		if (!dirName.endsWith(File.separator)) {
			dirName = dirName + File.separator;
		}

		if (dir.mkdirs()) {
			logger.info("Create dir" + defaultDirName + "success.");
			return true;
		} else {
			logger.error("Create dir" + defaultDirName + "failed.");
			return false;
		}
	}

	//
	public String getMessageFromWebSite(String url, String contentId,
			String path, Long favoriteEntryId) {

		// 判断输入参数
		if ("".equals(url) || url == null) {
			logger.error("Wrong url or the url param is null");
			return null;
		} else if ("".equals(contentId) || contentId == null) {
			logger.error("The second param contentId is null");
			return null;
		}

		// 判断路径参数是否有问题
		File tmp = new File(path);
		if (("".equals(path) && path == null) || !tmp.exists()) {
			logger.error("Parameter path is not input! Try to create a temple one named SpiderFiles");
			if (createDir(defaultDirName)) {
				logger.info("Create dir success ,named SpiderFiles");
			} else {
				logger.error("Create dir failed!");
			}
		}

		//
		try {
			if (!url.substring(0, 7).equals("http://")) {
				url = "http://" + url;
			}
			path = new File(defaultDirName).getAbsolutePath();
			
			//new一个Pipeline的对象，用来存储文件
			ArticlePipeline filePip = new ArticlePipeline();
			System.out.println("write file in path: " + path);
			filePip.setPath(path);
			
			ArticleSpider arSpider = new ArticleSpider();
			arSpider.contentId = contentId;
			filePip.pipeFileName = contentId;
			
			//开启子线程，进行抓取并写文件（filePip）
			Spider.create(arSpider).addUrl(url).addPipeline(filePip).thread(3)
					.run();
			
			//返回抓取到的内容url,content,imageUrl,favorite_id,title,description等。
			filePip.resJson.put("favorite_id", favoriteEntryId);
			return filePip.resJson.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public static void main(String[] args) throws RemoteException {
		// String url = "http://www.ptbus.com/view/323362/";
		String url = "http://static.oschina.net/news/43879/webmagic-0-3-0?p=2";
		String path = "E:\\Download\\webmagic";
		String contentId = "100020";
		Long favoriteEntryId = 1111L;

		ArticleSpider ar = new ArticleSpider();
		System.out.println(ar.getMessageFromWebSite(url, contentId, path,
				favoriteEntryId));

	}
}