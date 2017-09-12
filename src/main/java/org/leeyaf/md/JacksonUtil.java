package org.leeyaf.md;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonUtil {
	/** 设置忽略null值 */
	private static ObjectMapper objectMapper = new ObjectMapper()
			.setSerializationInclusion(JsonInclude.Include.NON_NULL)
			.setTimeZone(TimeZone.getDefault())
			.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

	/**
	 * javaBean,list,array convert to json string
	 */
	public static String obj2json(Object obj) throws Exception {
		return objectMapper.writeValueAsString(obj);
	}

	public static String obj2json(Object obj, String dateFormat)
			throws Exception {
		if (StringUtils.isNotBlank(dateFormat)) {
			objectMapper.setDateFormat(new SimpleDateFormat(dateFormat));
		} else
			objectMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
		return objectMapper.writeValueAsString(obj);
	}

	/**
	 * json string convert to javaBean
	 */
	public static <T> T json2pojo(String jsonStr, Class<T> clazz) throws Exception {
		return objectMapper.readValue(jsonStr, clazz);
	}

	/**
	 * json string convert to map
	 */
	@SuppressWarnings("unchecked")
	public static <T> Map<String, Object> json2map(String jsonStr) throws Exception {
		return objectMapper.readValue(jsonStr, Map.class);
	}

	/**
	 * 通过key获取value
	 * 
	 * @param jsonStr
	 * @return
	 * @throws Exception
	 */
	public static Object getValue(String jsonStr, String key) throws Exception {
		return objectMapper.readValue(jsonStr, Map.class).get(key);
	}

	/**
	 * map convert to javaBean
	 */
	@SuppressWarnings("rawtypes")
	public static <T> T map2pojo(Map map, Class<T> clazz) {
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		return objectMapper.convertValue(map, clazz);
	}

	@SuppressWarnings("rawtypes")
	public static <T> T map2pojo(Map map, Class<T> clazz, String dateFormat) {
		if (StringUtils.isNotBlank(dateFormat)) {
			objectMapper.setDateFormat(new SimpleDateFormat(dateFormat));
		} else {
			objectMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
		}

		return objectMapper.convertValue(map, clazz);
	}

	/**
	 * json array string convert to list with javaBean
	 */
	public static <T> List<T> json2list(String jsonArrayStr, Class<T> clazz) throws Exception {
		List<Map<String, Object>> list = objectMapper.readValue(jsonArrayStr,
				new TypeReference<List<T>>() {
				});
		List<T> result = new ArrayList<T>();
		for (Map<String, Object> map : list) {
			result.add(map2pojo(map, clazz));
		}
		return result;
	}

	/**
	 * json array string convert to list with map
	 */
	@SuppressWarnings("rawtypes")
	public static List<Map<String, Object>> json2list(String jsonArrayStr) throws Exception {
		return objectMapper.readValue(jsonArrayStr, new TypeReference<List>() {
		});
	}

	public static ObjectMapper getMapper() {
		return objectMapper;
	}
}
