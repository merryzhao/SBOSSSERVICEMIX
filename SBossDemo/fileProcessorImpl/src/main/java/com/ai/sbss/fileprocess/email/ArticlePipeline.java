package com.ai.sbss.fileprocess.email;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.FilePipeline;

public class ArticlePipeline extends FilePipeline {
	private Logger logger = LoggerFactory.getLogger(getClass());
	String pipeFileName = new String();
	public JSONObject resJson = new JSONObject();
	HashMap<String, Object> articleMap = new HashMap<String, Object>();

	@Override
	public void setPath(String path) {
		if (!path.endsWith(PATH_SEPERATOR)) {
			path += PATH_SEPERATOR;
		}
		this.path = path;
	}

	@Override
	public void process(ResultItems resultItems, Task task) {
		//获取文件存放路径及文件名，并将传入的resultItems分解后按指定的规则写入到文件中
		String path = this.path + task.getUUID() + PATH_SEPERATOR;
		try {
			PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(getFile(path + pipeFileName + ".spider")), "UTF-8"));
			//printWriter.println("url:\t" + resultItems.getRequest().getUrl());
			resJson.put("url", resultItems.getRequest().getUrl());
			for (Map.Entry<String, Object> entry : resultItems.getAll()
					.entrySet()) {
				//if (entry.getKey() != "content") {
					resJson.put(entry.getKey(), entry.getValue().toString());
				//}

				if (entry.getValue() instanceof Iterable) {
					Iterable<?> value = (Iterable<?>) entry.getValue();
					//printWriter.println(entry.getKey() + ":");
					for (Object o : value) {
						//printWriter.println(o);
					}
				} else {
					//printWriter.println(entry.getKey() + ":\t"
						//	+ entry.getValue());
				}
			}
			printWriter.print(resJson.toString().replace("{", "").replace("}", ""));
			printWriter.close();
		} catch (IOException e) {
			logger.warn("write file error", e);
		}
	}
}

