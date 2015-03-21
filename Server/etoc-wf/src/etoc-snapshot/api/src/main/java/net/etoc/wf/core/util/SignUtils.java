/**
 * 创建时间
 * 2015年3月14日-下午12:53:52
 * 
 * 
 */
package net.etoc.wf.core.util;

import java.util.Map;

import net.etoc.wf.ctapp.base.ResponseBase;
import net.etoc.wf.ctapp.base.RsCode;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月14日 下午12:53:52
 * 
 * @version 1.0.0
 * 
 */
public class SignUtils {
	private static Logger logger = LoggerFactory.getLogger(SignUtils.class);

	public static Map<String, Object> signForMap(String json, String sign) {
		logger.info("收到客户端传来请求,[{}],[{}]", json, sign);
		Map<String, Object> rsMap = Maps.newHashMap();
		if (StringUtils.isEmpty(json)) {
			rsMap.put("status", RsCode.SignFail.getCode());
			rsMap.put("message", RsCode.SignFail.getMessage());
			logger.info("验签失败");
			return rsMap;
		}
		String tmp = MD5.encodeByMd5AndSalt(json);
		if (StringUtils.equals(tmp, sign)) {
			return null;
		} else {
			rsMap.put("status", RsCode.SignFail.getCode());
			rsMap.put("message", RsCode.SignFail.getMessage());
			logger.info("验签失败");
			return rsMap;
		}
	}

	public static ResponseBase sign(String json, String sign) {
		logger.info("收到客户端传来请求,[{}],[{}]", json, sign);
		if (StringUtils.isEmpty(json)) {
			ResponseBase rb = new ResponseBase();
			rb.setStatus(RsCode.SignFail.getCode());
			rb.setMessage(RsCode.SignFail.getMessage());
			logger.info("验签失败");
			return rb;
		}
		String tmp = MD5.encodeByMd5AndSalt(json);
		ResponseBase rb = null;
		if (StringUtils.equals(tmp, sign)) {
			return rb;
		} else {
			rb = new ResponseBase();
			rb.setStatus(RsCode.SignFail.getCode());
			rb.setMessage(RsCode.SignFail.getMessage());
			logger.info("验签失败");
			return rb;
		}
	}
}
