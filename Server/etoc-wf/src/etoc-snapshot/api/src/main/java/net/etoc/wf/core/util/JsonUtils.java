/**
 * 创建时间
 * 2015年3月14日-下午7:02:26
 * 
 * 
 */
package net.etoc.wf.core.util;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
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
	public T getResult(String json, Class<T> objClass)
			throws JsonParseException, JsonMappingException, IOException {
		return new ObjectMapper().disable(
				DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(
				json, objClass);

	}
}
