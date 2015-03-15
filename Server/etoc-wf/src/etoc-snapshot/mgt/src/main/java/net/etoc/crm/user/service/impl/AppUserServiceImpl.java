/**
 * 创建时间
 * 2015年3月14日-下午2:28:09
 * 
 * 
 */
package net.etoc.crm.user.service.impl;

import net.etoc.crm.user.service.AppUserService;
import net.etoc.wf.core.util.AppVars;
import net.etoc.wf.ctapp.base.ResponseBase;
import net.etoc.wf.ctapp.user.entity.AppCrmUserRequest;
import net.etoc.wf.ctapp.user.entity.AppCrmUserResponse;
import net.etoc.wf.ctapp.user.entity.AuthCodeRequest;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月14日 下午2:28:09
 * 
 * @version 1.0.0
 * 
 */
@Service
public class AppUserServiceImpl implements AppUserService {

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.etoc.crm.user.service.AppUserService#getAuthCode(java.util.Map)
	 */
	@Override
	public ResponseBase getAuthCode(String methodSuffix, AuthCodeRequest ar) {
		RestTemplate r = new RestTemplate();
		ResponseBase rb = r.postForObject(AppVars.getInstance().crmUrl
				+ methodSuffix, null, ResponseBase.class, ar);

		return rb;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.etoc.crm.user.service.AppUserService#register(java.lang.String,
	 * net.etoc.wf.ctapp.user.entity.AppCrmUserRequest)
	 */
	@Override
	public AppCrmUserResponse loginORregister(String methodSuffix,
			AppCrmUserRequest ar) {
		RestTemplate r = new RestTemplate();
		AppCrmUserResponse rb = r.postForObject(AppVars.getInstance().crmUrl
				+ methodSuffix, null, AppCrmUserResponse.class, ar);

		return rb;
	}
}
