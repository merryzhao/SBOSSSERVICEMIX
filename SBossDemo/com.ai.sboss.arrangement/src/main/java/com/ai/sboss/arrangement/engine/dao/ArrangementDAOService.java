package com.ai.sboss.arrangement.engine.dao;

import java.util.List;
import java.util.Set;

import com.ai.sboss.arrangement.entity.PageEntity;
import com.ai.sboss.arrangement.entity.orm.ArrangementEntity;
import com.ai.sboss.arrangement.entity.orm.ArrangementJointMappingEntity;
import com.ai.sboss.arrangement.entity.orm.ArrangementSelfMappingEntity;
import com.ai.sboss.arrangement.exception.BizException;

/**
 * 该接口向模块外部提供关于编排流程数据相关的持久层服务
 * @author yinwenjie
 */
public interface ArrangementDAOService {
	/**
	 * 按照行业编号，查询这个行业下，已经定义（编排好的）的业务流程。<br>
	 * 排序会按照流程编号进行，并且不带分页。(注意这里只查询得到流程编排的基本信息)
	 * @param tradeid 指定的业务编号信息
	 * @param scope 行业定义的范围条件，这个条件可以指定也可以不指定
	 * @return 符合条件的编排信息集合将被返回；如果没有查到任何符合条件的编排信息，则返回空
	 * @throws BizException
	 */
	public List<ArrangementEntity> queryArrangementByTradeidWithoutSet(String tradeid , String scope) throws BizException;
	
	/**
	 * 按照行业编号，查询这个行业下，已经定义（编排好的）的业务流程。<br>
	 * 排序会按照流程编号进行，带分页。(注意这里只查询得到流程编排的基本信息)
	 * @param tradeid 制定的业务编号信息
	 * @param scope 行业定义的范围条件，这个条件可以指定也可以不指定
	 * @param pageNumber 当前的页码号，如果没有指定则说明是第一页（第一页的index=0）
	 * @param maxPerNumber 每页最大的数据量
	 * @return 符合条件的编排信息集合将被返回；如果没有查到任何符合条件的编排信息，则返回空。但是pageEntity不会为null。
	 * @throws BizException
	 */
	public PageEntity queryArrangementByTradeidPageWithoutSet(String tradeid , String scope , Integer pageNumber , Integer maxPerNumber) throws BizException;
	
	/**
	 * 获取一个指定的编排流程的基本信息，这些基本信息不包括jointmapping、childArrangements这样的集合信息。<br>
	 * 注意，通过这个方法获取的ArrangementEntity，如果强行取以上那样的集合信息，对象将会抛出持久层回话异常这样的错误。
	 * @param arrangementuid 指定的编排流程对应的唯一编号信息
	 * @return 如果有符合编号的编排流程信息，将以对象的形式进行返回；其他情况下返回null
	 * @throws BizException
	 */
	public ArrangementEntity getArrangementWithoutSet(String arrangementuid) throws BizException;
	
	/**
	 * 获取一个指定的编排流程的基本信息，这些基本信息包括jointmapping、childArrangements这样的集合信息。
	 * @param arrangementuid 指定的编排流程对应的唯一编号信息
	 * @return 如果有符合编号的编排流程信息，将以对象的形式进行返回；其他情况下返回null
	 * @throws BizException
	 */
	public ArrangementEntity getArrangementWithSet(String arrangementuid) throws BizException;
	
	/**
	 * 创建一个编排流程。创建一个新的编排流程时，需要注意以下的细节：
	 * 	1、这个方法本身只做必填信息的格式、值的验证，并不会进行流程数据流正误的验证。后者的验证在rule模块中实现<br>
	 * 	2、传入的arrangement对象，处了包括必须填写的属性信息外，jointmapping、childArrangements这样的集合信息也必须被传入。<br>
	 * 	3、arrangement的uid信息没有必要指定，如果指定了也无效，系统会自己生成一个全系统唯一的uid信息
	 * @param arrangement 传入的按照规则构造好的流程编排对象。
	 * @throws BizException
	 */
	public void createArrangement(ArrangementEntity arrangement) throws BizException;
	
	/**
	 * 更新指定的编排流程的基本信息。注意这个方法只更新流程的基本信息，并不包括jointmapping、childArrangements这样的集合信息。<br>
	 * 所以jointmapping、childArrangements这样的集合信息 不需要指定。要更新后者，请使用相应的bind方法。
	 * @param arrangement 指定的需要更新的流程编排信息对象，如果没有uid或者必要的信息，就会报错
	 * @throws BizException
	 */
	public void updateArrangement(ArrangementEntity arrangement) throws BizException;
	
	/**
	 * 重新绑定指定的流程编排信息所绑定的任务集合。<br>
	 * 既然是重新绑定，那么之前设定的任务集合信息都将不再起作用。
	 * @param arrangementuid 指定的需要重新绑定任务信息的编排流程uid
	 * @param jointmapping 新的任务集合
	 * @throws BizException
	 */
	public void updateArrangementJointmapping(String arrangementuid , Set<ArrangementJointMappingEntity> jointmapping) throws BizException;
	
	/**
	 * 重新绑定指定的流程编排信息所绑定的子流程集合。<br>
	 * 既然是重新绑定，那么之前设定的子流程集合信息都将不再起作用。
	 * @param arrangementuid 指定的需要重新绑定子流程信息的编排流程uid
	 * @param childArrangements 新的子流程信息集合
	 * @throws BizException 
	 */
	public void updateArrangementChildArrangements(String arrangementuid , Set<ArrangementSelfMappingEntity> childArrangements) throws BizException;
	
	/**
	 * 删除编排流程：
	 * 	arrangement的uid信息作为删除主键
	 * @param arrangementuid 需要删除的流程对应的uid。
	 * @return null
	 * @throws BizException
	 */
	public void deleteArrangement(String arrangementuid) throws BizException;
}