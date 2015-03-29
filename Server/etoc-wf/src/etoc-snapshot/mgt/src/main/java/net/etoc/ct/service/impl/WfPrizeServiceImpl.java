/**
 * 创建时间
 * 2015年3月22日-下午9:02:26
 * 
 * 
 */
package net.etoc.ct.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import net.etoc.crm.user.service.AppUserService;
import net.etoc.ct.entity.WfPrizeDetail;
import net.etoc.ct.entity.WfPrizeResponse.PrizeDetailResponse;
import net.etoc.ct.entity.WfPrizeResponse.PrizeHisResponse;
import net.etoc.ct.repository.WfPrizeDetailRepository;
import net.etoc.ct.service.WfPrizeService;
import net.etoc.wf.core.util.RandomUtils;
import net.etoc.wf.ctapp.base.RequestBase;
import net.etoc.wf.ctapp.user.entity.CrmOderHisRequest;
import net.etoc.wf.ctapp.user.entity.CrmOrderHisResponse;
import net.etoc.wf.ctapp.user.entity.CrmOrderRequest;
import net.etoc.wf.ctapp.user.entity.CrmOrderResponse;
import net.etoc.wf.ctapp.user.entity.OrderRel;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月22日 下午9:02:26
 * 
 * @version 1.0.0
 * 
 */
@Service
public class WfPrizeServiceImpl implements WfPrizeService {
	public static Logger logger = LoggerFactory
			.getLogger(WfPrizeServiceImpl.class);
	@Autowired
	private WfPrizeDetailRepository dao;

	@Autowired
	private AppUserService appUserService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.etoc.ct.service.WfPrizeService#saveOrupdate(net.etoc.ct.entity.
	 * WfPrizeDetail)
	 */
	@Override
	public void saveOrupdate(WfPrizeDetail bo) {
		// TODO Auto-generated method stub
		dao.save(bo);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.etoc.ct.service.WfPrizeService#findAll()
	 */
	@Override
	public List<WfPrizeDetail> findAll(String awardway) {
		return dao.findPrize(awardway);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.etoc.ct.service.WfPrizeService#rotatePrize()
	 */
	@Override
	public PrizeDetailResponse rotatePrize(String awardway, RequestBase rb)
			throws IllegalAccessException, InvocationTargetException,
			RestClientException, JsonProcessingException {
		// TODO Auto-generated method stub
		List<WfPrizeDetail> prizeList = dao.findPrize(awardway);
		List<Object> totalRange = dao.findTotalRange(awardway);
		int min = 1;
		int max = 0;
		if (totalRange.get(0) == null) {
			max = 0;
		} else {
			max = Integer.valueOf(totalRange.get(0) + "");
		}
		WfPrizeDetail rsObj = generatePrize(prizeList, min, max);

		PrizeDetailResponse wr = new PrizeDetailResponse();
		wr.setAward(rsObj);
		CrmOrderRequest ar = new CrmOrderRequest();
		BeanUtils.copyProperties(ar, rb);
		ar.setProductid(rsObj.getPrizeid() + "");

		CrmOrderResponse result = appUserService.orderLargess("orderLargess",
				ar);
		wr.setFlowcoins(result.getFlowcoins());
		wr.setStatus(result.getStatus());
		wr.setMessage(result.getMessage());
		return wr;
	}

	/**
	 * @Title: generatePrize
	 * @Description: TODO(生成奖项的算法) --
	 *               用所有奖项的概率和作为一开始随机范围的max，然后比对随机数和当前奖项概率，如果不符合当前奖项那么max
	 *               减去当前奖项概率最为新max
	 * @param @param prizeList
	 * @param @param min
	 * @param @param max
	 * @param @return 设定文件
	 * @return DgPrizeDetail 返回类型
	 * @throws
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	private WfPrizeDetail generatePrize(List<WfPrizeDetail> prizeList, int min,
			int max) {
		int tmp = 0;
		int currentProp = 0;
		for (WfPrizeDetail prize : prizeList) {
			currentProp = prize.getPrizeProba();
			tmp = RandomUtils.createRnageRndom(min, max);
			logger.info("tmp: " + tmp + " current " + currentProp);
			if (tmp <= currentProp) {
				if (prize.getPrizeCount() == 0) {
					continue;
				}
				prize.setPrizeCount(prize.getPrizeCount() - 1);

				this.saveOrupdate(prize);
				return prize;
			} else {
				max = max - currentProp;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.etoc.ct.service.WfPrizeService#findListByIds(java.lang.String,
	 * net.etoc.wf.ctapp.user.entity.CrmOderHisRequest)
	 */
	@Override
	public List<PrizeHisResponse> findListByIds(String methodSuffix,
			CrmOderHisRequest ar) throws RestClientException,
			JsonProcessingException, IllegalAccessException,
			InvocationTargetException {
		CrmOrderHisResponse crmresult = appUserService.querySubProdList(
				methodSuffix, ar);
		if (crmresult == null) {
			return null;
		}
		List<OrderRel> list = crmresult.getList();
		if (list == null || list.size() == 0) {
			return null;
		}
		List<Integer> ids = Lists.newArrayList();
		for (OrderRel bo : list) {
			ids.add(Integer.valueOf(bo.getProductid()));
		}

		List<WfPrizeDetail> rs = dao.findAll(ids);
		Map<Integer, WfPrizeDetail> tmp = Maps.newHashMap();
		for (WfPrizeDetail wd : rs) {
			tmp.put(wd.getPrizeid(), wd);
		}

		List<PrizeHisResponse> result = Lists.newArrayList();
		PrizeHisResponse phr = null;
		WfPrizeDetail wl = null;
		for (OrderRel bo : list) {
			phr = new PrizeHisResponse();
			wl = tmp.get(Integer.valueOf(bo.getProductid()));
			BeanUtils.copyProperties(phr, wl);
			phr.setTime(bo.getDate());
			phr.setTitle(bo.getTitle());
			result.add(phr);
		}

		return result;
	}
}
