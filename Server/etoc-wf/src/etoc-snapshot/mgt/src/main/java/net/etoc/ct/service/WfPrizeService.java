/**
 * 创建时间
 * 2015年3月22日-下午8:54:52
 * 
 * 
 */
package net.etoc.ct.service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import net.etoc.ct.entity.WfPrizeDetail;
import net.etoc.ct.entity.WfPrizeResponse.PrizeDetailResponse;
import net.etoc.ct.entity.WfPrizeResponse.PrizeHisResponse;
import net.etoc.wf.ctapp.base.RequestBase;
import net.etoc.wf.ctapp.user.entity.CrmOderHisRequest;

import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月22日 下午8:54:52
 * 
 * @version 1.0.0
 * 
 */
public interface WfPrizeService {
	void saveOrupdate(WfPrizeDetail bo);

	List<WfPrizeDetail> findAll(String awardway);

	PrizeDetailResponse rotatePrize(String awardway, RequestBase rb)
			throws IllegalAccessException, InvocationTargetException,
			RestClientException, JsonProcessingException;

	public List<PrizeHisResponse> findListByIds(String methodSuffix,
			CrmOderHisRequest ar) throws RestClientException,
			JsonProcessingException, IllegalAccessException,
			InvocationTargetException;
}
