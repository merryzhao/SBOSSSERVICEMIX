package com.ai.sboss.ServiceCalendarMgr.Utils;

import java.io.IOException;
import java.util.List;

public interface IReadFile {
	
	public String readFile(String fileName) throws IOException;
	
	public List<String> readFilesByPath(String path) throws IOException;
}
