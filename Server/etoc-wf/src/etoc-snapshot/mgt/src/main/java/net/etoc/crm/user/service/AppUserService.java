/**
 * 创建时间
 * 2015年3月12日-上午10:53:07
 * 
 * 
 */
package net.etoc.crm.user.service;

import net.etoc.wf.ctapp.base.RequestBase;
import net.etoc.wf.ctapp.base.ResponseBase;
import net.etoc.wf.ctapp.user.entity.AppCrmUserRequest;
import net.etoc.wf.ctapp.user.entity.AppCrmUserResponse;
import net.etoc.wf.ctapp.user.entity.AppProductResponse.PhoneChargeListResp;
import net.etoc.wf.ctapp.user.entity.AuthCodeRequest;
import net.etoc.wf.ctapp.user.entity.CrmFlowBankResponse;
import net.etoc.wf.ctapp.user.entity.CrmOderHisRequest;
import net.etoc.wf.ctapp.user.entity.CrmOrderHisResponse;
import net.etoc.wf.ctapp.user.entity.CrmOrderRequest;
import net.etoc.wf.ctapp.user.entity.CrmOrderResponse;

import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * 
 * @author yuxuan
 *
 *         该模块主要为APP 用户注册登录提供服务 2015年3月12日 上午10:53:07
 * 
 * @version 1.0.0
 * 
 */
public interface AppUserService {
	// 获取验证码
	public ResponseBase getAuthCode(String methodSuffix, AuthCodeRequest ar)
			throws RestClientException, JsonProcessingException;

	// 登陆、注册
	public AppCrmUserResponse loginORregister(String methodSuffix,
			AppCrmUserRequest ar) throws RestClientException,
			JsonProcessingException;

	// 账号信息
	public AppCrmUserResponse accountInfo(String methodSuffix,
			AppCrmUserRequest ar) throws RestClientException,
			JsonProcessingException;

	public CrmOrderResponse orderLargess(String methodSuffix, CrmOrderRequest ar)
			throws RestClientException, JsonProcessingException;

	public CrmOrderHisResponse querySubProdList(String methodSuffix,
			CrmOderHisRequest ar) throws RestClientException,
			JsonProcessingException;

	/**
	 * TODO 客户端请求产品
	 * 
	 * @param merchant
	 * @param ptype
	 * @param pbusinessid
	 * @return AppProductResponse
	 * @exception
	 * @since 1.0.0
	 */
	public PhoneChargeListResp findAppProduct(String merchant, String ptype);

	public CrmFlowBankResponse queryBlance(String methodSuffix, RequestBase ar)
			throws RestClientException, JsonProcessingException;
}
