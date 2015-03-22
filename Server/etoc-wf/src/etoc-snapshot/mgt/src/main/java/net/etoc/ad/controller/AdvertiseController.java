/**
 * 创建时间
 * 2015年3月18日-下午12:00:17
 * 
 * 
 */
package net.etoc.ad.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import net.etoc.ad.entity.WfAdvertise;
import net.etoc.ad.service.WfAdvertiseService;
import net.etoc.crm.user.service.AppUserService;
import net.etoc.wf.core.util.JsonUtils;
import net.etoc.wf.core.util.PType;
import net.etoc.wf.core.util.SignUtils;
import net.etoc.wf.ctapp.base.ResponseBase;
import net.etoc.wf.ctapp.base.RsCode;
import net.etoc.wf.ctapp.user.entity.AdvesterRequest;
import net.etoc.wf.ctapp.user.entity.AdvesterResponse;
import net.etoc.wf.ctapp.user.entity.CrmOderHisRequest;
import net.etoc.wf.ctapp.user.entity.CrmOrderRequest;
import net.etoc.wf.ctapp.user.entity.CrmOrderResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
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
public class AdvertiseController {
	@SuppressWarnings("rawtypes")
	@Autowired
	private JsonUtils jsonUtils;

	@Autowired
	private WfAdvertiseService wfAdvertiseService;

	@Autowired
	private AppUserService appUserService;

	@RequestMapping("/vs/api/user/videoHome")
	@ResponseBody
	public Map<String, Object> videoHome(String json, String sign) {
		Map<String, Object> rsMap = SignUtils.signForMap(json, sign);
		if (rsMap != null) {
			return rsMap;
		}
		rsMap = Maps.newHashMap();
		Page<WfAdvertise> newestlist = wfAdvertiseService
				.findByRtype("0", 0, 3);
		Page<WfAdvertise> bannerlist = wfAdvertiseService
				.findByRtype("1", 0, 3);
		Page<WfAdvertise> wonderfullist = wfAdvertiseService.findByRtype("2",
				0, 3);

		rsMap.put("newestlist", newestlist.getContent());
		rsMap.put("bannerlist", bannerlist.getContent());
		rsMap.put("wonderfullist", wonderfullist.getContent());
		rsMap.put("status", RsCode.OK.getCode());
		return rsMap;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping("/vs/api/user/wonderfulVideos")
	@ResponseBody
	public Map<String, Object> wonderfulVideos(String json, String sign)
			throws JsonParseException, JsonMappingException, IOException {
		Map<String, Object> rsMap = SignUtils.signForMap(json, sign);
		if (rsMap != null) {
			return rsMap;
		}
		rsMap = Maps.newHashMap();
		AdvesterRequest ar = ((AdvesterRequest) jsonUtils.getResult(json,
				AdvesterRequest.class));
		Page<WfAdvertise> wonderfullist = wfAdvertiseService.findByRtype("2",
				ar.getPageno(), 3);
		rsMap.put("wonderfullist", wonderfullist.getContent());
		rsMap.put("status", RsCode.OK.getCode());
		rsMap.put("hasnextpage", wonderfullist.getTotalPages());
		return rsMap;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping("/vs/api/user/videoDetail")
	@ResponseBody
	public Map<String, Object> videoDetail(String json, String sign)
			throws JsonParseException, JsonMappingException, IOException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		Map<String, Object> rsMap = SignUtils.signForMap(json, sign);
		if (rsMap != null) {
			return rsMap;
		}
		rsMap = Maps.newHashMap();
		AdvesterRequest ar = ((AdvesterRequest) jsonUtils.getResult(json,
				AdvesterRequest.class));
		WfAdvertise wd = wfAdvertiseService.getAdById(ar.getVideoid());
		AdvesterResponse rs = new AdvesterResponse();
		rs.setStatus(RsCode.OK.getCode());
		PropertyUtils.copyProperties(rs, wd);
		rsMap = PropertyUtils.describe(rs);
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
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @exception
	 * @since 1.0.0
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/vs/api/user/videoflow", method = RequestMethod.POST)
	@ResponseBody
	public CrmOrderResponse videoflow(String json, String sign)
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
	@RequestMapping("/vs/api/user/videoflowrecord")
	@ResponseBody
	public Map<String, Object> videoflowrecord(String json, String sign)
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
		cr.setType(PType.watch_movie.getValue());
		List<WfAdvertise> list = wfAdvertiseService.findListByIds(
				"querySubProdList", cr);
		rsMap.put("status", (RsCode.OK.getCode()));
		rsMap.put("recordlist", list);
		return rsMap;

	}
}
