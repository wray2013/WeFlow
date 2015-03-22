/**
 * 创建时间
 * 2015年3月22日-下午9:28:53
 * 
 * 
 */
package net.etoc.ct.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import net.etoc.ct.entity.WfPrizeResponse.PrizeDetailResponse;
import net.etoc.ct.entity.WfPrizeResponse.PrizeHisResponse;
import net.etoc.ct.entity.WfPrizeResponse.prizeListResponse;
import net.etoc.ct.entity.WfprizeRequest;
import net.etoc.ct.service.WfPrizeService;
import net.etoc.wf.core.util.JsonUtils;
import net.etoc.wf.core.util.PType;
import net.etoc.wf.core.util.SignUtils;
import net.etoc.wf.ctapp.base.RequestBase;
import net.etoc.wf.ctapp.base.ResponseBase;
import net.etoc.wf.ctapp.base.RsCode;
import net.etoc.wf.ctapp.user.entity.CrmOderHisRequest;
import net.etoc.wf.ctapp.user.entity.CrmOrderRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
 *         2015年3月22日 下午9:28:53
 * 
 * @version 1.0.0
 * 
 *
 */
@Controller
public class WfPrizeController {
	@SuppressWarnings("rawtypes")
	@Autowired
	private JsonUtils jsonUtils;

	@Autowired
	private WfPrizeService service;

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/vs/api/user/getawards", method = RequestMethod.POST)
	@ResponseBody
	public prizeListResponse signlist(String json, String sign)
			throws IllegalAccessException, InvocationTargetException,
			JsonParseException, JsonMappingException, IOException {
		ResponseBase cs = SignUtils.sign(json, sign);
		if (cs != null) {
			prizeListResponse ur = new prizeListResponse();
			BeanUtils.copyProperties(ur, cs);
			return ur;
		}
		WfprizeRequest fr = (WfprizeRequest) jsonUtils.getResult(json,
				WfprizeRequest.class);
		prizeListResponse wr = new prizeListResponse();
		wr.setAward(service.findAll(fr.getAwardway()));

		return wr;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/vs/api/user/shakeflow", method = RequestMethod.POST)
	@ResponseBody
	public PrizeDetailResponse shakeflow(String json, String sign)
			throws IllegalAccessException, InvocationTargetException,
			JsonParseException, JsonMappingException, IOException {
		ResponseBase cs = SignUtils.sign(json, sign);
		if (cs != null) {
			PrizeDetailResponse ur = new PrizeDetailResponse();
			BeanUtils.copyProperties(ur, cs);
			return ur;
		}
		RequestBase fr = (RequestBase) jsonUtils.getResult(json,
				RequestBase.class);
		return service.rotatePrize(prizeType.shakeflow.getValue(), fr);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/vs/api/user/scratchflow", method = RequestMethod.POST)
	@ResponseBody
	public PrizeDetailResponse scratchflow(String json, String sign)
			throws IllegalAccessException, InvocationTargetException,
			JsonParseException, JsonMappingException, IOException {
		ResponseBase cs = SignUtils.sign(json, sign);
		if (cs != null) {
			PrizeDetailResponse ur = new PrizeDetailResponse();
			BeanUtils.copyProperties(ur, cs);
			return ur;
		}
		RequestBase fr = (RequestBase) jsonUtils.getResult(json,
				RequestBase.class);
		return service.rotatePrize(prizeType.scratch.getValue(), fr);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping("/vs/api/user/awardrecord")
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
		cr.setType(PType.play_game.getValue());
		List<PrizeHisResponse> list = service.findListByIds("querySubProdList",
				cr);
		rsMap.put("status", (RsCode.OK.getCode()));
		rsMap.put("recordlist", list);
		return rsMap;

	}

	public enum prizeType {
		shakeflow("1"), scratch("2");
		private String value;

		prizeType(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}
}
