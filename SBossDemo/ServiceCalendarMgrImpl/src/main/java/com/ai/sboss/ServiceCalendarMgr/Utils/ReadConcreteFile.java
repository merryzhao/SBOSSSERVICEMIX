package com.ai.sboss.ServiceCalendarMgr.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class ReadConcreteFile implements IReadFile {

	public String readFile(String fileName) throws IOException {
		
		File readedFile = new File(fileName);
		System.out.println("ReadConcreteFile=>"+readedFile.getAbsolutePath());
		if (readedFile.isFile() && readedFile.exists() && readedFile.canRead()) {
			
			String line = StringUtils.EMPTY;
			StringBuffer fileContent = new StringBuffer();
			InputStream is = new FileInputStream(readedFile);
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			BufferedReader reader = new BufferedReader(isr);

			while ((line = reader.readLine()) != null) {
				fileContent.append(line);
			}
			
			reader.close();
			isr.close();
			is.close();

			return fileContent.toString();

		} else {
			return StringUtils.EMPTY;
		}
	}

	public List<String> readFilesByPath(String path) throws IOException {
		File readedFile = new File(path);
		List<String> listOfFileContents = new ArrayList<String>();

		if (readedFile.isDirectory() && readedFile.exists()) {
			File[] listOfFile = readedFile.listFiles();
			for (File file : listOfFile) {
				String fileContent = readFile(file.getPath());
				if (!fileContent.isEmpty()) {
					listOfFileContents.add(fileContent);
				}
			}
		}
		return listOfFileContents;
	}
}
