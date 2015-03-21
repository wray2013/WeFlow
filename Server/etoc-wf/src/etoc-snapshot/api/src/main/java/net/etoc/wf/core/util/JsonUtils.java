/**
 * 创建时间
 * 2015年3月14日-下午7:02:26
 * 
 * 
 */
package net.etoc.wf.core.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月14日 下午7:02:26
 * 
 * @version 1.0.0
 * 
 */
@Component
public class JsonUtils<T> {
	private static Logger logger = LoggerFactory.getLogger(JsonUtils.class);

	public T getResult(String json, Class<T> objClass)
			throws JsonParseException, JsonMappingException, IOException {
		return new ObjectMapper().disable(
				DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(
				json, objClass);

	}

	public MultiValueMap<String, String> getJsonResult(T objClass)
			throws JsonProcessingException {
		String json = new ObjectMapper().disable(
				DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
				.writeValueAsString(objClass);
		MultiValueMap<String, String> mp = new LinkedMultiValueMap<String, String>();
		mp.add("requestApp", json);
		logger.info("发送给CRM的信息：[{}]", json);
		return mp;
	}

}
