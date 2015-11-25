package com.ai.sboss.arrangement.utils;

import java.math.BigDecimal;

/**
 * 解决数据类型为double时的精度问题
 * @author yinwenjie
 */
public final class BigDecimalUtils {
	/**
	 * 禁止实例化
	 */
	private BigDecimalUtils() {
		
	}
	
	/**
	 * 返回保留2位小数的金额
	 * @param money 需要计算精度的金额
	 * @return
	 */
	public static double money(double money) {
		return BigDecimal.valueOf(money).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	/**
	 * 保留小数
	 * @param value 需要保留的数值
	 * @param scale 保留小数位
	 * @return
	 */
	public static double scaledValue(double value, int scale) {
		return BigDecimal.valueOf(value).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
}
