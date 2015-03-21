/**
 * 创建时间
 * 2015年3月17日-下午10:35:03
 * 
 * 
 */
package net.etoc.ad.service;

import java.util.List;

import net.etoc.ad.entity.WfAdvertise;
import net.etoc.wf.ctapp.user.entity.CrmOderHisRequest;

import org.springframework.data.domain.Page;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月17日 下午10:35:03
 * 
 * @version 1.0.0
 * 
 */
public interface WfAdvertiseService {
	/**
	 * 保存或者修改广告
	 * 
	 * @param ad
	 *            void
	 * @exception
	 * @since 1.0.0
	 */
	public void saveorupdate(WfAdvertise ad);

	public WfAdvertise getAdById(int id);

	public void deleteById(int id);

	public Page<WfAdvertise> findByRtype(String rtype, int page, int size);

	public List<WfAdvertise> findListByIds(String methodSuffix,
			CrmOderHisRequest ar) throws RestClientException,
			JsonProcessingException;
}
