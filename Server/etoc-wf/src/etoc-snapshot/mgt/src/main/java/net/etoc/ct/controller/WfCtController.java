/**
 * 创建时间
 * 2015年3月22日-下午3:22:09
 * 
 * 
 */
package net.etoc.ct.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import net.etoc.ct.entity.CtInfoRequest;
import net.etoc.ct.entity.CtInfoResponse;
import net.etoc.ct.service.WfCtInfoService;
import net.etoc.wf.core.util.JsonUtils;
import net.etoc.wf.core.util.SignUtils;
import net.etoc.wf.ctapp.base.ResponseBase;
import net.etoc.wf.ctapp.user.entity.FeedBackRequest;
import net.etoc.wf.ctapp.user.entity.UserSignResponse;
import net.sf.ehcache.search.impl.BaseResult;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月22日 下午3:22:09
 * 
 * @version 1.0.0
 * 
 */
@Controller
public class WfCtController {
	@SuppressWarnings("rawtypes")
	@Autowired
	private JsonUtils jsonUtils;

	@Autowired
	private WfCtInfoService service;

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/vs/api/ua", method = RequestMethod.POST)
	@ResponseBody
	public CtInfoResponse ua(String json, String sign)
			throws IllegalAccessException, InvocationTargetException,
			JsonParseException, JsonMappingException, IOException {
		ResponseBase cs = SignUtils.sign(json, sign);
		if (cs != null) {
			CtInfoResponse cr = new CtInfoResponse();
			BeanUtils.copyProperties(cr, cs);
			return cr;
		}
		CtInfoRequest rq = (CtInfoRequest) jsonUtils.getResult(json,
				CtInfoRequest.class);
		return service.findCtInfo(rq);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/vs/api/user/feedBack", method = RequestMethod.POST)
	@ResponseBody
	public ResponseBase feedBack(String json, String sign)
			throws IllegalAccessException, InvocationTargetException,
			JsonParseException, JsonMappingException, IOException {
		ResponseBase cs = SignUtils.sign(json, sign);
		if (cs != null) {
			return cs;
		}
		FeedBackRequest fr = (FeedBackRequest) jsonUtils.getResult(json,
				FeedBackRequest.class);
		return service.feedBack("feedBack", fr);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/vs/api/user/signin", method = RequestMethod.POST)
	@ResponseBody
	public UserSignResponse signIn(String json, String sign)
			throws IllegalAccessException, InvocationTargetException,
			JsonParseException, JsonMappingException, IOException {
		ResponseBase cs = SignUtils.sign(json, sign);
		if (cs != null) {
			UserSignResponse ur = new UserSignResponse();
			BeanUtils.copyProperties(ur, cs);
			return ur;
		}
		BaseResult fr = (BaseResult) jsonUtils
				.getResult(json, BaseResult.class);
		return service.sign("signIn", fr);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/vs/api/user/signlist", method = RequestMethod.POST)
	@ResponseBody
	public UserSignResponse signlist(String json, String sign)
			throws IllegalAccessException, InvocationTargetException,
			JsonParseException, JsonMappingException, IOException {
		ResponseBase cs = SignUtils.sign(json, sign);
		if (cs != null) {
			UserSignResponse ur = new UserSignResponse();
			BeanUtils.copyProperties(ur, cs);
			return ur;
		}
		BaseResult fr = (BaseResult) jsonUtils
				.getResult(json, BaseResult.class);
		return service.sign("querySignInList", fr);
	}

}
