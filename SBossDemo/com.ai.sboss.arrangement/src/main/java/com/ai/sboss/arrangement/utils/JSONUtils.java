package com.ai.sboss.arrangement.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ai.sboss.arrangement.exception.BizException;

import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;
import net.sf.json.util.CycleDetectionStrategy;
import net.sf.json.util.PropertyFilter;

/**
 * 基于jackson的 对象和JSON格式的转换工具
 * @author yinwenjie
 */
@SuppressWarnings("rawtypes")
public final class JSONUtils {
	
	/**
	 * 禁止实例化
	 */
	private JSONUtils() {
		
	}
	
	public static String toString(List list) {
		return toString(list, new String());
	}

	/**
	 * 将一个对象集合转换成json格式的字符串描述。如果List为null，则会报错
	 * @param list
	 * @param filterProperties 指定对象中哪些属性名称是不需要转换的
	 * @return
	 */
	public static String toString(List list, String[] filterProperties) {
		return JSONArray.fromObject(list, setFilterPropertie(filterProperties)).toString();
	}
	
	/**
	 * @param set
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String toString(Set set) {
		List list = new ArrayList();
		list.addAll(set);
		return toString(list);
	}

	@SuppressWarnings("unchecked")
	public static String toString(Set set, String filterPropertie) {
		List list = new ArrayList();
		list.addAll(set);
		return toString(list, filterPropertie);
	}

	@SuppressWarnings("unchecked")
	public static String toString(Set set, String[] filterProperties) {
		List list = new ArrayList();
		list.addAll(set);
		return JSONArray.fromObject(list, setFilterPropertie(filterProperties)).toString();
	}

	public static String toString(Object object) {
		return toString(object, new String());
	}

	public static String toString(Object object, String filterPropertie) {
		return toString(object, new String[] { filterPropertie });
	}

	public static String toString(Object object, String[] filterPropertie) {
		return JSONObject.fromObject(object, setFilterPropertie(filterPropertie)).toString();
	}
	
	public static JSONObject toJSONObject(Object object, String[] filterPropertie) {
		return JSONObject.fromObject(object, setFilterPropertie(filterPropertie));
	}
	
	public static JSONArray toJSONArray(Object object, String[] filterPropertie) {
		return JSONArray.fromObject(object, setFilterPropertie(filterPropertie));
	}

	public static List toBean(String jsonString, Class objectClass) {
		return toBean(jsonString, objectClass, new String());
	}

	public static List toBean(String jsonString, Class objectClass, String filterPropertie) {
		JSONArray array = JSONArray.fromObject(jsonString, setFilterPropertie(filterPropertie));
		net.sf.json.util.JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpher(new String[] {"yyyy-MM-dd HH:mm:ss"}));
		return (List) JSONArray.toCollection(array, objectClass);
	}

	public static Object toBean(String jsonString, Class objectClass, Map classMap) {
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		net.sf.json.util.JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpher(new String[] {"yyyy-MM-dd HH:mm:ss"}));
		return JSONObject.toBean(jsonObject, objectClass, classMap);
	}

	public static Object toBean(String jsonString, Class objectClass, String... filterPropertie) {
		JSONObject jsonObject = JSONObject.fromObject(jsonString, setFilterPropertie(filterPropertie));
		net.sf.json.util.JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpher(new String[] {"yyyy-MM-dd HH:mm:ss"}));
		return JSONObject.toBean(jsonObject, objectClass);
	}
	
	public static Object toBean(String jsonString, Class objectClass,Map classMap, String... filterPropertie) {
		JSONObject jsonObject = JSONObject.fromObject(jsonString,setFilterPropertie(filterPropertie));
		net.sf.json.util.JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpher(new String[] {"yyyy-MM-dd HH:mm:ss"}));
		return JSONObject.toBean(jsonObject, objectClass, classMap);
	}

	public static JsonConfig setFilterPropertie(final String filterPropertie) {
		String[] str = new String[] { filterPropertie };
		return setFilterPropertie(str);
	}

	public static JsonConfig setFilterPropertie(final String[] filterProperties) {
		JsonConfig config = new JsonConfig();
		config.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		config.setJsonPropertyFilter(new PropertyFilter() {
			public boolean apply(Object source, String name, Object value) {
				boolean bool = false;
				for (int i = 0; i < filterProperties.length; i++) {
					if (name.equals(filterProperties[i])) {
						bool = true;
						break;
					}
				}
				return bool;
			}
		});
		
		//时间格式
		config.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor());
		return config;
	}
}

class DateJsonValueProcessor implements JsonValueProcessor {
	
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(DateJsonValueProcessor.class);
	
    public Object processArrayValue(Object value, JsonConfig jsonConfig)  {
        String[] obj = {};  
        if (value instanceof Date[]) {   
            Date[] dates = (Date[]) value;  
            obj = new String[dates.length];  
            for (int i = 0; i < dates.length; i++) {  
                try {
					obj[i] = DateUtils.formatDate(dates[i], "yyyy-MM-dd HH:mm:ss");
				} catch (BizException e) {
					DateJsonValueProcessor.LOGGER.warn(e.getMessage(), e);
				}
            }  
        }  
        return obj;  
    }
     
    /* (non-Javadoc)
     * @see net.sf.json.processors.JsonValueProcessor#processObjectValue(java.lang.String, java.lang.Object, net.sf.json.JsonConfig)
     */
    public Object processObjectValue(String key, Object value, JsonConfig jsonConfig) {
        if (value instanceof Date) {  
            String str = "";
			try {
				str = DateUtils.formatDate((Date)value, "yyyy-MM-dd HH:mm:ss");
			} catch (BizException e) {
				DateJsonValueProcessor.LOGGER.warn(e.getMessage(), e);
			}
            return str;  
        }  
        return value;  
    }  
}