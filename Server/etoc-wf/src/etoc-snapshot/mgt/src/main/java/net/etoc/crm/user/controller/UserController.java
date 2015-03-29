/**
 * 创建时间
 * 2015年3月14日-下午12:43:18
 * 
 * 
 */
package net.etoc.crm.user.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServletRequest;

import net.etoc.crm.user.service.AppUserService;
import net.etoc.wf.core.util.JsonUtils;
import net.etoc.wf.core.util.PMerchant;
import net.etoc.wf.core.util.PType;
import net.etoc.wf.core.util.SignUtils;
import net.etoc.wf.ctapp.base.ResponseBase;
import net.etoc.wf.ctapp.base.RsCode;
import net.etoc.wf.ctapp.user.entity.AppCrmUserRequest;
import net.etoc.wf.ctapp.user.entity.AppCrmUserResponse;
import net.etoc.wf.ctapp.user.entity.AppProductResponse.PhoneChargeListResp;
import net.etoc.wf.ctapp.user.entity.AuthCodeRequest;
import net.etoc.wf.ctapp.user.entity.CrmBankBalance;
import net.etoc.wf.ctapp.user.entity.CrmBillResponse;
import net.etoc.wf.ctapp.user.entity.CrmFlowBankRequest;
import net.etoc.wf.ctapp.user.entity.CrmFlowBankResponse;
import net.etoc.wf.ctapp.user.entity.CrmFlowStoreAndPopResponse;
import net.etoc.wf.ctapp.user.entity.CrmOderHisRequest;
import net.etoc.wf.ctapp.user.entity.CrmOrderHisResponse;
import net.etoc.wf.ctapp.user.entity.CrmOrderRequest;
import net.etoc.wf.ctapp.user.entity.CrmOrderResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;

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
			return rs;
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
		AppCrmUserResponse result = appUserService.loginORregister("register",
				ar);
		return result;
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

		AppCrmUserResponse result = appUserService.loginORregister("login", ar);
		return result;

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

		AppCrmUserResponse result = appUserService.loginORregister("autoLogin",
				ar);
		return result;
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

		return appUserService.loginORregister("resetPassword", ar);

	}

	/**
	 * TODO app 账户基本查询
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
	@RequestMapping(value = "/vs/api/user/accountInfo", method = RequestMethod.POST)
	@ResponseBody
	public AppCrmUserResponse accountInfo(String json, String sign)
			throws JsonParseException, JsonMappingException, IOException {
		ResponseBase rs = SignUtils.sign(json, sign);
		if (rs != null) {
			AppCrmUserResponse ar = new AppCrmUserResponse();
			BeanUtils.copyProperties(rs, ar);
			return ar;
		}
		AppCrmUserRequest ar = (AppCrmUserRequest) jsonUtils.getResult(json,
				AppCrmUserRequest.class);

		AppCrmUserResponse result = appUserService.accountInfo(
				"queryAccountInfo", ar);
		if (result == null) {
			AppCrmUserResponse pp = new AppCrmUserResponse();
			pp.setStatus(RsCode.OK.getCode());
			return pp;
		} else {
			return result;
		}

	}

	@RequestMapping(value = "/vs/api/user/phoneChargeList", method = RequestMethod.POST)
	@ResponseBody
	public PhoneChargeListResp phoneChargeList(String json, String sign) {
		ResponseBase rs = SignUtils.sign(json, sign);
		if (rs != null) {
			PhoneChargeListResp ar = new PhoneChargeListResp();
			BeanUtils.copyProperties(rs, ar);
			return ar;
		}
		PhoneChargeListResp result = appUserService.findAppProduct(
				PMerchant.fl_charge.getValue(), PType.change_tc.getValue());
		if (result == null) {
			PhoneChargeListResp ar = new PhoneChargeListResp();
			ar.setStatus(RsCode.OK.getCode());
			return ar;
		} else {
			return result;
		}
	}

	/**
	 * TODO app 订购
	 * 
	 * @param json
	 * @param sign
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 *             ResponseBase
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @exception
	 * @since 1.0.0
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/vs/api/user/rechargePhone", method = RequestMethod.POST)
	@ResponseBody
	public CrmOrderResponse rechargePhone(String json, String sign)
			throws JsonParseException, JsonMappingException, IOException,
			IllegalAccessException, InvocationTargetException {
		ResponseBase rs = SignUtils.sign(json, sign);
		if (rs != null) {
			CrmOrderResponse ar = new CrmOrderResponse();
			BeanUtils.copyProperties(rs, ar);
			return ar;
		}
		CrmOrderRequest ar = (CrmOrderRequest) jsonUtils.getResult(json,
				CrmOrderRequest.class);
		CrmOrderResponse result = appUserService.orderLargess("orderLargess",
				ar);
		return result;
	}

	@RequestMapping(value = "/vs/api/user/QChargeList", method = RequestMethod.POST)
	@ResponseBody
	public PhoneChargeListResp QChargeList(String json, String sign) {
		ResponseBase rs = SignUtils.sign(json, sign);
		if (rs != null) {
			PhoneChargeListResp ar = new PhoneChargeListResp();
			BeanUtils.copyProperties(rs, ar);
			return ar;
		}
		PhoneChargeListResp result = appUserService.findAppProduct(
				PMerchant.fl_charge.getValue(), PType.change_qq.getValue());
		if (result == null) {
			PhoneChargeListResp ar = new PhoneChargeListResp();
			ar.setStatus(RsCode.OK.getCode());
			return ar;
		} else {
			return result;
		}
	}

	/**
	 * TODO app 订购
	 * 
	 * @param json
	 * @param sign
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 *             ResponseBase
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @exception
	 * @since 1.0.0
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/vs/api/user/rechargeQQ", method = RequestMethod.POST)
	@ResponseBody
	public CrmOrderResponse rechargeQQ(String json, String sign)
			throws JsonParseException, JsonMappingException, IOException,
			IllegalAccessException, InvocationTargetException {
		ResponseBase rs = SignUtils.sign(json, sign);
		if (rs != null) {
			CrmOrderResponse ar = new CrmOrderResponse();
			BeanUtils.copyProperties(rs, ar);
			return ar;
		}
		CrmOrderRequest ar = (CrmOrderRequest) jsonUtils.getResult(json,
				CrmOrderRequest.class);
		CrmOrderResponse result = appUserService.orderLargess("orderLargess",
				ar);
		return result;
	}

	@RequestMapping(value = "/vs/api/user/gamePkgList", method = RequestMethod.POST)
	@ResponseBody
	public PhoneChargeListResp gamePkgList(String json, String sign) {
		ResponseBase rs = SignUtils.sign(json, sign);
		if (rs != null) {
			PhoneChargeListResp ar = new PhoneChargeListResp();
			BeanUtils.copyProperties(rs, ar);
			return ar;
		}
		PhoneChargeListResp result = appUserService.findAppProduct(
				PMerchant.fl_charge.getValue(), PType.change_gf.getValue());
		if (result == null) {
			PhoneChargeListResp ar = new PhoneChargeListResp();
			ar.setStatus(RsCode.OK.getCode());
			return ar;
		} else {
			return result;
		}
	}

	/**
	 * TODO app 订购
	 * 
	 * @param json
	 * @param sign
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 *             ResponseBase
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @exception
	 * @since 1.0.0
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/vs/api/user/exchangeGamePkg", method = RequestMethod.POST)
	@ResponseBody
	public CrmOrderResponse exchangeGamePkg(String json, String sign)
			throws JsonParseException, JsonMappingException, IOException,
			IllegalAccessException, InvocationTargetException {
		ResponseBase rs = SignUtils.sign(json, sign);
		if (rs != null) {
			CrmOrderResponse ar = new CrmOrderResponse();
			BeanUtils.copyProperties(rs, ar);
			return ar;
		}
		CrmOrderRequest ar = (CrmOrderRequest) jsonUtils.getResult(json,
				CrmOrderRequest.class);
		CrmOrderResponse result = appUserService.orderLargess("orderLargess",
				ar);
		return result;
	}

	@RequestMapping(value = "/vs/api/user/flowPkgList", method = RequestMethod.POST)
	@ResponseBody
	public PhoneChargeListResp flowPkgList(String json, String sign) {
		ResponseBase rs = SignUtils.sign(json, sign);
		if (rs != null) {
			PhoneChargeListResp ar = new PhoneChargeListResp();
			BeanUtils.copyProperties(rs, ar);
			return ar;
		}
		PhoneChargeListResp result = appUserService.findAppProduct(
				PMerchant.app.getValue(), PType.change_wf.getValue());
		if (result == null) {
			PhoneChargeListResp ar = new PhoneChargeListResp();
			ar.setStatus(RsCode.OK.getCode());
			return ar;
		} else {
			return result;
		}

	}

	/**
	 * TODO app 订购
	 * 
	 * @param json
	 * @param sign
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 *             ResponseBase
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @exception
	 * @since 1.0.0
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/vs/api/user/exchangeFlowPkg", method = RequestMethod.POST)
	@ResponseBody
	public CrmOrderResponse exchangeFlowPkg(String json, String sign)
			throws JsonParseException, JsonMappingException, IOException,
			IllegalAccessException, InvocationTargetException {
		ResponseBase rs = SignUtils.sign(json, sign);
		if (rs != null) {
			CrmOrderResponse ar = new CrmOrderResponse();
			BeanUtils.copyProperties(rs, ar);
			return ar;
		}
		CrmOrderRequest ar = (CrmOrderRequest) jsonUtils.getResult(json,
				CrmOrderRequest.class);
		CrmOrderResponse result = appUserService.orderLargess("orderLargess",
				ar);
		return result;
	}

	@RequestMapping(value = "/vs/api/user/giftList", method = RequestMethod.POST)
	@ResponseBody
	public PhoneChargeListResp giftList(String json, String sign) {
		ResponseBase rs = SignUtils.sign(json, sign);
		if (rs != null) {
			PhoneChargeListResp ar = new PhoneChargeListResp();
			BeanUtils.copyProperties(rs, ar);
			return ar;
		}
		PhoneChargeListResp result = appUserService.findAppProduct(
				PMerchant.fl_charge.getValue(), PType.bug_gf.getValue());
		if (result == null) {
			PhoneChargeListResp ar = new PhoneChargeListResp();
			ar.setStatus(RsCode.OK.getCode());
			return ar;
		} else {
			return result;
		}
	}

	/**
	 * TODO app 订购
	 * 
	 * @param json
	 * @param sign
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 *             ResponseBase
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @exception
	 * @since 1.0.0
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/vs/api/user/exchangeGift", method = RequestMethod.POST)
	@ResponseBody
	public CrmOrderResponse exchangeGift(String json, String sign)
			throws JsonParseException, JsonMappingException, IOException,
			IllegalAccessException, InvocationTargetException {
		ResponseBase rs = SignUtils.sign(json, sign);
		if (rs != null) {
			CrmOrderResponse ar = new CrmOrderResponse();
			BeanUtils.copyProperties(rs, ar);
			return ar;
		}
		CrmOrderRequest ar = (CrmOrderRequest) jsonUtils.getResult(json,
				CrmOrderRequest.class);
		CrmOrderResponse result = appUserService.orderLargess("orderLargess",
				ar);
		return result;
	}

	@RequestMapping(value = "/vs/api/user/gameRechargeList", method = RequestMethod.POST)
	@ResponseBody
	public PhoneChargeListResp gameRechargeList(String json, String sign) {
		ResponseBase rs = SignUtils.sign(json, sign);
		if (rs != null) {
			PhoneChargeListResp ar = new PhoneChargeListResp();
			BeanUtils.copyProperties(rs, ar);
			return ar;
		}
		PhoneChargeListResp result = appUserService.findAppProduct(
				PMerchant.fl_charge.getValue(), PType.recharge_gm.getValue());
		if (result == null) {
			PhoneChargeListResp ar = new PhoneChargeListResp();
			ar.setStatus(RsCode.OK.getCode());
			return ar;
		} else {
			return result;
		}
	}

	/**
	 * TODO app 订购
	 * 
	 * @param json
	 * @param sign
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 *             ResponseBase
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @exception
	 * @since 1.0.0
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/vs/api/user/gamerecharge", method = RequestMethod.POST)
	@ResponseBody
	public CrmOrderResponse gamerecharge(String json, String sign)
			throws JsonParseException, JsonMappingException, IOException,
			IllegalAccessException, InvocationTargetException {
		ResponseBase rs = SignUtils.sign(json, sign);
		if (rs != null) {
			CrmOrderResponse ar = new CrmOrderResponse();
			BeanUtils.copyProperties(rs, ar);
			return ar;
		}
		CrmOrderRequest ar = (CrmOrderRequest) jsonUtils.getResult(json,
				CrmOrderRequest.class);
		CrmOrderResponse result = appUserService.orderLargess("orderLargess",
				ar);
		return result;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/vs/api/user/queryBank", method = RequestMethod.POST)
	@ResponseBody
	public CrmFlowBankResponse queryBank(String json, String sign)
			throws RestClientException, IOException {
		ResponseBase rs = SignUtils.sign(json, sign);
		if (rs != null) {
			CrmFlowBankResponse ar = new CrmFlowBankResponse();
			BeanUtils.copyProperties(rs, ar);
			return ar;
		}
		CrmBankBalance rb = (CrmBankBalance) jsonUtils.getResult(json,
				CrmBankBalance.class);

		CrmFlowBankResponse result = appUserService.queryBlance("queryBlance",
				rb);
		return result;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/vs/api/user/storeFlow", method = RequestMethod.POST)
	@ResponseBody
	public CrmFlowStoreAndPopResponse storeFlow(String json, String sign)
			throws RestClientException, IOException {
		ResponseBase rs = SignUtils.sign(json, sign);
		if (rs != null) {
			CrmFlowStoreAndPopResponse ar = new CrmFlowStoreAndPopResponse();
			BeanUtils.copyProperties(rs, ar);
			return ar;
		}
		CrmFlowBankRequest rb = (CrmFlowBankRequest) jsonUtils.getResult(json,
				CrmFlowBankRequest.class);

		CrmFlowStoreAndPopResponse result = appUserService.storeFlow(
				"flowBank", rb);
		return result;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/vs/api/user/popFlow", method = RequestMethod.POST)
	@ResponseBody
	public CrmFlowStoreAndPopResponse popFlow(String json, String sign)
			throws RestClientException, IOException {
		ResponseBase rs = SignUtils.sign(json, sign);
		if (rs != null) {
			CrmFlowStoreAndPopResponse ar = new CrmFlowStoreAndPopResponse();
			BeanUtils.copyProperties(rs, ar);
			return ar;
		}
		CrmFlowBankRequest rb = (CrmFlowBankRequest) jsonUtils.getResult(json,
				CrmFlowBankRequest.class);

		CrmFlowStoreAndPopResponse result = appUserService.popFlow("flowBank",
				rb);
		return result;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/vs/api/user/costFlowList", method = RequestMethod.POST)
	@ResponseBody
	public CrmOrderHisResponse costFlowList(String json, String sign)
			throws JsonParseException, JsonMappingException, IOException,
			IllegalAccessException, InvocationTargetException {
		ResponseBase rs = (ResponseBase) SignUtils.sign(json, sign);
		if (rs != null) {
			CrmOrderHisResponse chr = new CrmOrderHisResponse();
			BeanUtils.copyProperties(rs, chr);
			return chr;
		}
		CrmOrderRequest ar = (CrmOrderRequest) jsonUtils.getResult(json,
				CrmOrderRequest.class);
		CrmOderHisRequest cr = new CrmOderHisRequest();
		BeanUtils.copyProperties(ar, cr);
		CrmOrderHisResponse result = appUserService.querySubProdList(
				"querySubProdList", cr);
		return result;

	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/vs/api/user/billList", method = RequestMethod.POST)
	@ResponseBody
	public CrmBillResponse billList(String json, String sign)
			throws JsonParseException, JsonMappingException, IOException {
		ResponseBase rs = (ResponseBase) SignUtils.sign(json, sign);
		if (rs != null) {
			CrmBillResponse chr = new CrmBillResponse();
			BeanUtils.copyProperties(rs, chr);
			return chr;
		}

		CrmOderHisRequest ar = (CrmOderHisRequest) jsonUtils.getResult(json,
				CrmOderHisRequest.class);
		return appUserService.queryBillList("queryBillList", ar);
	}
}
