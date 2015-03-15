/**
 * 创建时间
 * 2015年3月14日-下午12:43:18
 * 
 * 
 */
package net.etoc.crm.user.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import net.etoc.crm.user.service.AppUserService;
import net.etoc.wf.core.util.JsonUtils;
import net.etoc.wf.core.util.SignUtils;
import net.etoc.wf.ctapp.base.ResponseBase;
import net.etoc.wf.ctapp.user.entity.AppCrmUserRequest;
import net.etoc.wf.ctapp.user.entity.AppCrmUserResponse;
import net.etoc.wf.ctapp.user.entity.AuthCodeRequest;

import org.springframework.beans.BeanUtils;
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
 *         2015年3月14日 下午12:43:18
 * 
 * @version 1.0.0
 * 
 */
@Controller
public class UserController {
	@Autowired
	private AppUserService appUserService;

	@SuppressWarnings("rawtypes")
	@Autowired
	private JsonUtils jsonUtils;

	/**
	 * 获取验证码
	 * 
	 * @param json
	 * @param sign
	 * @param request
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 *             ResponseBase
	 * @exception
	 * @since 1.0.0
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = { "/vs/api/getAuthCode" }, method = RequestMethod.POST)
	@ResponseBody
	public ResponseBase authCode(String json, String sign,
			HttpServletRequest request) throws JsonParseException,
			JsonMappingException, IOException {
		ResponseBase rs = SignUtils.sign(json, sign);
		if (rs != null) {
			// return rs;
		}

		AuthCodeRequest ar = (AuthCodeRequest) jsonUtils.getResult(json,
				AuthCodeRequest.class);

		return appUserService.getAuthCode("getAuthCode", ar);

	}

	/**
	 * TODO 验证码校验
	 * 
	 * @param json
	 * @param sign
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 *             ResponseBase
	 * @exception
	 * @since 1.0.0
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/vs/api/verifyAuthCode", method = RequestMethod.POST)
	@ResponseBody
	public ResponseBase verifyAuthCode(String json, String sign)
			throws JsonParseException, JsonMappingException, IOException {
		ResponseBase rs = SignUtils.sign(json, sign);
		if (rs != null) {
			return rs;
		}
		AuthCodeRequest ar = (AuthCodeRequest) jsonUtils.getResult(json,
				AuthCodeRequest.class);

		return appUserService.getAuthCode("verifyAuthCode", ar);
	}

	/**
	 * TODO app 用户注册
	 * 
	 * @param json
	 * @param sign
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 *             ResponseBase
	 * @exception
	 * @since 1.0.0
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/vs/api/user/register", method = RequestMethod.POST)
	@ResponseBody
	public AppCrmUserResponse register(String json, String sign)
			throws JsonParseException, JsonMappingException, IOException {
		ResponseBase rs = SignUtils.sign(json, sign);
		if (rs != null) {
			AppCrmUserResponse ar = new AppCrmUserResponse();
			BeanUtils.copyProperties(rs, ar);
			return ar;
		}
		AppCrmUserRequest ar = (AppCrmUserRequest) jsonUtils.getResult(json,
				AppCrmUserRequest.class);
		return appUserService.loginORregister("register", ar);
	}

	/**
	 * TODO app 用户登录
	 * 
	 * @param json
	 * @param sign
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 *             AppCrmUserResponse
	 * @exception
	 * @since 1.0.0
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/vs/api/user/login", method = RequestMethod.POST)
	@ResponseBody
	public AppCrmUserResponse login(String json, String sign)
			throws JsonParseException, JsonMappingException, IOException {
		ResponseBase rs = SignUtils.sign(json, sign);
		if (rs != null) {
			AppCrmUserResponse ar = new AppCrmUserResponse();
			BeanUtils.copyProperties(rs, ar);
			return ar;
		}
		AppCrmUserRequest ar = (AppCrmUserRequest) jsonUtils.getResult(json,
				AppCrmUserRequest.class);

		return appUserService.loginORregister("login", ar);

	}

	/**
	 * TODO app 用户自动登录
	 * 
	 * @param json
	 * @param sign
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 *             AppCrmUserResponse
	 * @exception
	 * @since 1.0.0
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/vs/api/user/autoLogin", method = RequestMethod.POST)
	@ResponseBody
	public AppCrmUserResponse autoLogin(String json, String sign)
			throws JsonParseException, JsonMappingException, IOException {
		ResponseBase rs = SignUtils.sign(json, sign);
		if (rs != null) {
			AppCrmUserResponse ar = new AppCrmUserResponse();
			BeanUtils.copyProperties(rs, ar);
			return ar;
		}
		AppCrmUserRequest ar = (AppCrmUserRequest) jsonUtils.getResult(json,
				AppCrmUserRequest.class);

		return appUserService.loginORregister("autoLogin", ar);

	}

	/**
	 * TODO app 用户重置密码
	 * 
	 * @param json
	 * @param sign
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 *             AppCrmUserResponse
	 * @exception
	 * @since 1.0.0
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/vs/api/user/resetPassword", method = RequestMethod.POST)
	@ResponseBody
	public ResponseBase resetPassword(String json, String sign)
			throws JsonParseException, JsonMappingException, IOException {
		ResponseBase rs = SignUtils.sign(json, sign);
		if (rs != null) {
			return rs;
		}
		AppCrmUserRequest ar = (AppCrmUserRequest) jsonUtils.getResult(json,
				AppCrmUserRequest.class);

		return appUserService.loginORregister("autoLogin", ar);

	}

}
