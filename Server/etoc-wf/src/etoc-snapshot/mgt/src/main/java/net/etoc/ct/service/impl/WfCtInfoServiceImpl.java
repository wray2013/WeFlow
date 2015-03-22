/**
 * 创建时间
 * 2015年3月22日-下午3:08:49
 * 
 * 
 */
package net.etoc.ct.service.impl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import net.etoc.ct.entity.CtInfoRequest;
import net.etoc.ct.entity.CtInfoResponse;
import net.etoc.ct.entity.WfCtInfo;
import net.etoc.ct.repository.WfCtInfoRepository;
import net.etoc.ct.service.WfCtInfoService;
import net.etoc.wf.core.util.AppVars;
import net.etoc.wf.core.util.JsonUtils;
import net.etoc.wf.ctapp.base.ResponseBase;
import net.etoc.wf.ctapp.base.RsCode;
import net.etoc.wf.ctapp.user.entity.FeedBackRequest;
import net.etoc.wf.ctapp.user.entity.UserSignResponse;
import net.sf.ehcache.search.impl.BaseResult;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月22日 下午3:08:49
 * 
 * @version 1.0.0
 * 
 */
@Service
public class WfCtInfoServiceImpl implements WfCtInfoService {
	public static Logger logger = LoggerFactory
			.getLogger(WfCtInfoServiceImpl.class);
	@Autowired
	private WfCtInfoRepository dao;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private JsonUtils jsonUtils;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.etoc.ct.service.WfCtInfoService#saveOrupdate(net.etoc.ct.entity.WfCtInfo
	 * )
	 */
	@Override
	public void saveOrupdate(WfCtInfo bo) {
		// TODO Auto-generated method stub
		dao.save(bo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.etoc.ct.service.WfCtInfoService#delete(int)
	 */
	@Override
	public void delete(int id) {
		// TODO Auto-generated method stub
		dao.delete(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.etoc.ct.service.WfCtInfoService#findById(int)
	 */
	@Override
	public CtInfoResponse findCtInfo(CtInfoRequest rq)
			throws IllegalAccessException, InvocationTargetException {
		// TODO Auto-generated method stub
		List<WfCtInfo> rs = dao.findAll();
		CtInfoResponse cr = new CtInfoResponse();
		if (rs == null || rs.size() == 0) {
			cr.setType(versionType.latest.getValue());
			cr.setStatus(RsCode.OK.getCode());
			return cr;
		}
		WfCtInfo tmp = rs.get(0);
		BeanUtils.copyProperties(cr, tmp);
		int serverVS = Integer.valueOf(tmp.getVersion());
		int clientVS = Integer.valueOf(rq.getAppversion());
		if ((serverVS - clientVS) > 2) {
			// 大于2个版本强制升级
			cr.setType(versionType.forceup.getValue());
		} else if ((serverVS - clientVS) == 1) {
			cr.setType(versionType.latest.getValue());
		} else if (serverVS == clientVS) {
			cr.setType(versionType.normalup.getValue());
		}
		cr.setServertime(System.currentTimeMillis());
		return cr;
	}

	public enum versionType {
		latest("0"), normalup("2"), forceup("1");
		private String value;

		versionType(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.etoc.ct.service.WfCtInfoService#feedBack(net.etoc.wf.ctapp.user.entity
	 * .FeedBackRequest)
	 */
	@Override
	public ResponseBase feedBack(String methodSuffix, FeedBackRequest fr)
			throws RestClientException, JsonProcessingException {
		String rb = restTemplate.postForObject(AppVars.getInstance().crmUrl
				+ methodSuffix, jsonUtils.getJsonResult(fr), String.class);
		logger.info("访问CRM服务器,[{}] 返回的结果 [{}]", AppVars.getInstance().crmUrl
				+ methodSuffix, rb);

		try {
			return (ResponseBase) jsonUtils.getResult(rb, ResponseBase.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("访问CRM服务器 [{}]  出现错误 [{}]",
					AppVars.getInstance().crmUrl + methodSuffix, e.getMessage());
			e.printStackTrace();
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.etoc.ct.service.WfCtInfoService#sign(java.lang.String,
	 * net.etoc.wf.ctapp.user.entity.UserSignResponse)
	 */
	@Override
	public UserSignResponse sign(String methodSuffix, BaseResult fr)
			throws RestClientException, JsonProcessingException {
		String rb = restTemplate.postForObject(AppVars.getInstance().crmUrl
				+ methodSuffix, jsonUtils.getJsonResult(fr), String.class);
		logger.info("访问CRM服务器,[{}] 返回的结果 [{}]", AppVars.getInstance().crmUrl
				+ methodSuffix, rb);

		try {
			return (UserSignResponse) jsonUtils.getResult(rb,
					UserSignResponse.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("访问CRM服务器 [{}]  出现错误 [{}]",
					AppVars.getInstance().crmUrl + methodSuffix, e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
}
