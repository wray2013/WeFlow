/**
 * 创建时间
 * 2015年3月12日-上午10:53:07
 * 
 * 
 */
package net.etoc.crm.user.service;

import net.etoc.wf.ctapp.base.ResponseBase;
import net.etoc.wf.ctapp.user.entity.AppCrmUserRequest;
import net.etoc.wf.ctapp.user.entity.AppCrmUserResponse;
import net.etoc.wf.ctapp.user.entity.AuthCodeRequest;

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
	public ResponseBase getAuthCode(String methodSuffix, AuthCodeRequest ar);

	public AppCrmUserResponse loginORregister(String methodSuffix,
			AppCrmUserRequest ar);
}
