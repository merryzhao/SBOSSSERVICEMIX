package com.ai.sboss.arrangement.utils;

import java.util.Date;
import java.util.UUID;

import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;

/**
 * 该工具负责在用户登录时，生成一个全系统唯一的Token信息
 * @author yinwenjie
 */
public final class TokenUtils {
	
	/**
	 * 禁止实例化
	 */
	private TokenUtils() {
		
	}
	
	/**
	 * 该静态方法负责根据用户id、登录设备信息、登录时间为依据，生成全系统唯一的token信息
	 * @param userjid 用户id信息
	 * @param resource 登录设备信息（如果用户登录时没有传入设备信息则可以是空字符串）
	 * @param logonTime 登录时间
	 * @return 返回一个全系统唯一的token信息
	 */
	public static String generateUUID(String userid , String resource , Date logonTime) throws BizException {
		if(logonTime == null) {
			throw new BizException("logonTime is null !", ResponseCode._402);
		}
		
		//这是生成的基准
		String tokenResource = userid + "_" + resource + "_" + logonTime.getTime();
		//全系统唯一，甚至是全球唯一的
		return UUID.nameUUIDFromBytes(tokenResource.getBytes()).toString();
	}
}
