/**
 * 创建时间
 * 2015年3月14日-下午12:53:52
 * 
 * 
 */
package net.etoc.wf.core.util;

import net.etoc.wf.ctapp.base.ResponseBase;
import net.etoc.wf.ctapp.base.RsCode;

import org.apache.commons.lang.StringUtils;

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
	public static ResponseBase sign(String json, String sign) {
		if (StringUtils.isEmpty(json)) {
			ResponseBase rb = new ResponseBase();
			rb.setCode(RsCode.SignFail.getCode());
			rb.setMessage(RsCode.SignFail.getMessage());
			return rb;
		}
		String tmp = MD5.encodeByMd5AndSalt(json);
		ResponseBase rb = null;
		if (StringUtils.equals(tmp, sign)) {

			return rb;
		} else {
			rb = new ResponseBase();
			rb.setCode(RsCode.SignFail.getCode());
			rb.setMessage(RsCode.SignFail.getMessage());
			return rb;
		}
	}

}
