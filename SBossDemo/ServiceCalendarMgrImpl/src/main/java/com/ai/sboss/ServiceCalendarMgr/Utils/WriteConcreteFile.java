package com.ai.sboss.ServiceCalendarMgr.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class WriteConcreteFile implements IWriteFile {

	public boolean writeFile(String filename, String fileContent) {
		try {
			File f = new File(filename);
			if (!f.exists()) {
				f.createNewFile();
				f.setWritable(true);
			}
			
			OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");
			BufferedWriter writer = new BufferedWriter(write);
			writer.write(fileContent);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}

}
