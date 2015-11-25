/**
 * 
 */
package com.ai.sboss.datanalysis.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

/**
 * @author idot
 *
 */
public interface MixAllTextInfoInterface {
	
	/**
	 * 对于每个待分析的内容，比如一篇文章、一张图片、一段视频等，都是利用爬虫程序获取到的
	 * 如果爬虫程序的输出是html格式的代码，则需要对其进行解析，找出其中涉及到的文本信息，将其融合为一个整体
	 */
	public String decomposeContentInfo(Object content);
	
	/**
	 * 以行为单位读取文件内容
	 */
	public List<String> readFileByLines(String fileName);
	/**
	 * 判断一个keyWord是否是一个停用词
	 * @param stopWordList:停用词库
	 * @param keyWord:关键词
	 * @return
	 */
	public boolean isStopword(List<String> stopWordList, String keyWord);
	/**
	 * 判断一个结点是否为叶子结点
	 * @param catalogTree：catalog树
	 * @param catalogId
	 * @return
	 */
	public boolean isCatalogLeaf(String catalogTree, Long catalogId);
	/**
	 * 提取用于分析的服务文本信息
	 * @param offerTextJson:offering信息，包括offeringName,offeringCode,offeringComment,offeringSpecList，offeringDesc等
	 * @return
	 */
	public String offeringTextInfoExtract(String offerTextJson, String key);
	/**
	 * 从catalg结点树种查询一个结点的父结点
	 * @param catalogTree:catalog树
	 * @param catalogId
	 * @return
	 */
	public Long findParentCatalogNode(String catalogTree, Long catalogId);
	
	
	/**
	 * 获取所有的offering列表
	 * @return
	 */
	public List<JSONObject> getAllOfferings(List<JSONObject> allCatalogTrees);
	
	/**
	 * 对于每个具体的销售品，其可用的文本信息包括：
	 * 销售品名称、销售品规格特征属性、销售品评论信息、服务保障、服务约束、服务承诺等 
	 * 因此，需要将每一个销售品可利用的各种文本信息融合为一个整体
	 */
	public JSONObject mixAllOfferingTextInfo(String offeringDetailJson);
	
	
	/**
	 * 根据传入的顶层catalog编号，获取到该catalog对应的整棵目录树
	 * @return
	 */
	public List<JSONObject> getAllCatalogs(String fileName);
	/**
	 * 获取offering的文本信息
	 * @param totalOfferings
	 * @return
	 */
	public List<JSONObject> getTotalOfferingInfo(List<JSONObject> totalOfferings);
	/**
	 * hashMap按value排序功能
	 * @param oldMap
	 * @return
	 */
	public HashMap<Long, Float> sortMap(HashMap<Long, Float> oldMap);
	/**
	 * 获取服务详情
	 * @param offeringId
	 * @param offeringCode
	 * @return
	 */
	public String acquireOfferingDetail(Long offeringId, String offeringCode);

}
	
	
	
	