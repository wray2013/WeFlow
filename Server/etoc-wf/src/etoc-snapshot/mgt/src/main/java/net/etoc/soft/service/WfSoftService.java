/**
 * 创建时间
 * 2015年3月19日-下午9:21:07
 * 
 * 
 */
package net.etoc.soft.service;

import java.util.List;

import net.etoc.soft.entity.WfSoft;
import net.etoc.wf.ctapp.user.entity.CrmOderHisRequest;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月19日 下午9:21:07
 * 
 * @version 1.0.0
 * 
 */
@Service
public interface WfSoftService {
	public void saveOrupdate(WfSoft soft);

	public void delete(Integer id);

	public WfSoft findById(Integer id);

	public Page<WfSoft> findByPage(String stype, int page, int size);

	public List<WfSoft> findListByIds(String methodSuffix, CrmOderHisRequest ar)
			throws RestClientException, JsonProcessingException;
}
