/**
 * 
 */
package com.ai.sboss.datanalysisimpl.core;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ai.sboss.datanalysis.core.DataAnalysisCoreInterface;
import com.ai.sboss.datanalysis.util.MixAllTextInfoInterface;
import com.ai.sboss.datanalysisimpl.util.MixAllTextInfoImpl;

import wordparse.IWordParsePkgSV;
import wordparse.WordParseImpl;

/**
 * @author idot
 *
 */
public class TextDataAnalysisCoreImpl implements DataAnalysisCoreInterface{

	public HashMap<String, Integer> dataAnalysisCore(Object object) {
		String objData = null;
		if(object instanceof String){
			objData = object.toString();
		}
		MixAllTextInfoInterface textProcessor = new MixAllTextInfoImpl();
		
		//读取停用词列表
		List<String> stopWordList = textProcessor.readFileByLines("stopwords.txt");
		IWordParsePkgSV wpsv = new WordParseImpl();
		String wpRes = null;
		try {
			wpRes = wpsv.wordParse(objData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String [] parRes = wpRes.split(" ");
		//System.out.println("parRes-->"+wpRes.toString());
		HashMap<String, Integer> pasResKV = new HashMap<String, Integer>();
		for(String str:parRes){
			String [] strSplited = str.split("/");
			
			//去停用词
			if(textProcessor.isStopword(stopWordList, strSplited[0])){
				continue;
			}
			//正则匹配名词和动词
			Pattern pattern = Pattern.compile("^n.*|^v.*");
			Matcher matcher = null;
			if(strSplited.length > 1){
				matcher = pattern.matcher(strSplited[1]);
				boolean b= matcher.matches();
				if(b){
					if(pasResKV.containsKey(strSplited[0])){
						pasResKV.put(strSplited[0],pasResKV.get(strSplited[0])+1);
					}else{
						pasResKV.put(strSplited[0], 1);
					}
	//				System.out.println(strSplited[0]+":"+strSplited[1]);
				}
			}
		}
		//System.out.println("pasResKV-->"+pasResKV.toString());
		return pasResKV;//key:词,value:词频
	}

	public void saveIntermediateResult(Object object) {

		
	}

}
