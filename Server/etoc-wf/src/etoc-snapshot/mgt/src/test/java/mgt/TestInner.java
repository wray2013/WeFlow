/**
 * 创建时间
 * 2015年3月21日-下午4:17:37
 * 
 * 
 */
package mgt;

import net.etoc.wf.ctapp.base.ResponseBase;
import net.etoc.wf.ctapp.user.entity.CrmOrderHisResponse;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月21日 下午4:17:37
 * 
 * @version 1.0.0
 * 
 */
public class TestInner {

	/**
	 * TODO
	 * 
	 * @param args
	 *            void
	 * @exception
	 * @since 1.0.0
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*
		 * RechargeProduct p = new RechargeProduct(); p.setChargesid("111");
		 * p.setCost("jljl"); System.out.println(p.getChargesid());
		 * List<RechargeProduct> l = new ArrayList<RechargeProduct>();
		 * RechargePhoneResp rp = new RechargePhoneResp(); rp.setProducts(l);
		 */

		ResponseBase CR = (ResponseBase) t();
		System.out.println(CR);
	}

	public static CrmOrderHisResponse t() {
		return new CrmOrderHisResponse();
	}

}
