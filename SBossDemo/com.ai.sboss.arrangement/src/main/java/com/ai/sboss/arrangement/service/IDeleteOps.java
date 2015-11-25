package com.ai.sboss.arrangement.service;

import com.ai.sboss.arrangement.entity.JsonEntity;

/**
 * 删除操作接口类.
 * @author Chaos
 * @author yinwenjie
 */
public interface IDeleteOps {
	/**
	 * 删除指定的任务节点。删除后这个任务不能再使用了，但之前的引用都还是有效的
	 * @param process 
	 * @param itemId 
	 * @param itemType 
	 * @return 
	 */
	public JsonEntity deleteJointItem(String jointid);
	
	/**
	 * 删除指定的任务流程。删除后这个任务不能再使用了，但之前的引用都还是有效的
	 * @param arrangementid
	 * @return
	 */
	public JsonEntity deleteArrangementItem(String arrangementid);

	/**
	 * 根据特定流程ID删除相应流程.
	 * TODO	暂时没有实现
	 * @param processId 
	 * @return 
	 */
	public JsonEntity deleteProcessById(Object processId);
}