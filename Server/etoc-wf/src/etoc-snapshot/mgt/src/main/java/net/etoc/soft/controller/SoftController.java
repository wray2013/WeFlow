/**
 * 创建时间
 * 2015年3月18日-下午12:00:17
 * 
 * 
 */
package net.etoc.soft.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import net.etoc.crm.user.service.AppUserService;
import net.etoc.soft.entity.WfSoft;
import net.etoc.soft.service.WfSoftService;
import net.etoc.wf.core.util.JsonUtils;
import net.etoc.wf.core.util.PType;
import net.etoc.wf.core.util.SignUtils;
import net.etoc.wf.ctapp.base.ResponseBase;
import net.etoc.wf.ctapp.base.RsCode;
import net.etoc.wf.ctapp.user.entity.CrmOderHisRequest;
import net.etoc.wf.ctapp.user.entity.CrmOrderRequest;
import net.etoc.wf.ctapp.user.entity.CrmOrderResponse;
import net.etoc.wf.ctapp.user.entity.SoftRequest;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.collect.Maps;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月18日 下午12:00:17
 * 
 * @version 1.0.0
 * 
 */
@Controller
public class SoftController {
	@SuppressWarnings("rawtypes")
	@Autowired
	private JsonUtils jsonUtils;

	@Autowired
	private WfSoftService wfSoftService;

	@Autowired
	private AppUserService appUserService;

	@RequestMapping("/vs/api/user/appHome")
	@ResponseBody
	public Map<String, Object> appHome(String json, String sign) {
		Map<String, Object> rsMap = SignUtils.signForMap(json, sign);
		if (rsMap != null) {
			return rsMap;
		}
		rsMap = Maps.newHashMap();
		Page<WfSoft> applist = wfSoftService.findByPage("0", 0, 3);
		Page<WfSoft> bannerlist = wfSoftService.findByPage("1", 0, 3);

		rsMap.put("applist", applist.getContent());
		rsMap.put("bannerlist", bannerlist.getContent());
		rsMap.put("status", RsCode.OK.getCode());
		return rsMap;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping("/vs/api/user/appList")
	@ResponseBody
	public Map<String, Object> appList(String json, String sign)
			throws JsonParseException, JsonMappingException, IOException {
		Map<String, Object> rsMap = SignUtils.signForMap(json, sign);
		if (rsMap != null) {
			return rsMap;
		}
		rsMap = Maps.newHashMap();
		SoftRequest ar = ((SoftRequest) jsonUtils.getResult(json,
				SoftRequest.class));
		Page<WfSoft> list = wfSoftService.findByPage("2", ar.getPageno(), 3);
		rsMap.put("list", list.getContent());
		rsMap.put("status", RsCode.OK.getCode());
		rsMap.put("hasnextpage", list.getTotalPages());
		return rsMap;
	}

	/**
	 * TODO app 软件订购
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
	@RequestMapping(value = "/vs/api/user/appflow", method = RequestMethod.POST)
	@ResponseBody
	public CrmOrderResponse videoflow(String json, String sign)
			throws JsonParseException, JsonMappingException, IOException {
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
	@RequestMapping("/vs/api/user/app2Flow")
	@ResponseBody
	public Map<String, Object> app2Flow(String json, String sign)
			throws JsonParseException, JsonMappingException, IOException,
			IllegalAccessException, InvocationTargetException {
		Map<String, Object> rsMap = SignUtils.signForMap(json, sign);
		if (rsMap != null) {
			return rsMap;
		}
		rsMap = Maps.newHashMap();
		CrmOrderRequest ar = (CrmOrderRequest) jsonUtils.getResult(json,
				CrmOrderRequest.class);
		CrmOderHisRequest cr = new CrmOderHisRequest();
		BeanUtils.copyProperties(cr, ar);
		cr.setType(PType.down_soft.getValue());
		List<WfSoft> list = wfSoftService.findListByIds("querySubProdList", cr);
		rsMap.put("status", (RsCode.OK.getCode()));
		rsMap.put("list", list);
		return rsMap;

	}
}
