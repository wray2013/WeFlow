/**
 * 创建时间
 * 2015年3月22日-下午9:21:25
 * 
 * 
 */
package net.etoc.ct.entity;

import java.util.List;

import net.etoc.wf.ctapp.base.ResponseBase;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月22日 下午9:21:25
 * 
 * @version 1.0.0
 * 
 */
public class WfPrizeResponse extends ResponseBase {
	public static class prizeListResponse {
		private List<WfPrizeDetail> award;

		/**
		 * award
		 *
		 * @return the award
		 * @since 1.0.0
		 */

		public List<WfPrizeDetail> getAward() {
			return award;
		}

		/**
		 * @param award
		 *            the award to set
		 */
		public void setAward(List<WfPrizeDetail> award) {
			this.award = award;
		}

	}

	public static class PrizeDetailResponse {
		private WfPrizeDetail award;
		private String flowcoins;
		private String status;
		private String message;

		/**
		 * status
		 *
		 * @return the status
		 * @since 1.0.0
		 */

		public String getStatus() {
			return status;
		}

		/**
		 * @param status
		 *            the status to set
		 */
		public void setStatus(String status) {
			this.status = status;
		}

		/**
		 * message
		 *
		 * @return the message
		 * @since 1.0.0
		 */

		public String getMessage() {
			return message;
		}

		/**
		 * @param message
		 *            the message to set
		 */
		public void setMessage(String message) {
			this.message = message;
		}

		/**
		 * award
		 *
		 * @return the award
		 * @since 1.0.0
		 */

		public WfPrizeDetail getAward() {
			return award;
		}

		/**
		 * @param award
		 *            the award to set
		 */
		public void setAward(WfPrizeDetail award) {
			this.award = award;
		}

		/**
		 * flowcoins
		 *
		 * @return the flowcoins
		 * @since 1.0.0
		 */

		public String getFlowcoins() {
			return flowcoins;
		}

		/**
		 * @param flowcoins
		 *            the flowcoins to set
		 */
		public void setFlowcoins(String flowcoins) {
			this.flowcoins = flowcoins;
		}

	}

	public static class PrizeHisResponse {
		private String time;
		private String prizeid;
		private String prizename;
		private String awardway;
		private String title;

		/**
		 * title
		 *
		 * @return the title
		 * @since 1.0.0
		 */

		public String getTitle() {
			return title;
		}

		/**
		 * @param title
		 *            the title to set
		 */
		public void setTitle(String title) {
			this.title = title;
		}

		/**
		 * time
		 *
		 * @return the time
		 * @since 1.0.0
		 */

		public String getTime() {
			return time;
		}

		/**
		 * @param time
		 *            the time to set
		 */
		public void setTime(String time) {
			this.time = time;
		}

		/**
		 * prizeid
		 *
		 * @return the prizeid
		 * @since 1.0.0
		 */

		public String getPrizeid() {
			return prizeid;
		}

		/**
		 * @param prizeid
		 *            the prizeid to set
		 */
		public void setPrizeid(String prizeid) {
			this.prizeid = prizeid;
		}

		/**
		 * prizename
		 *
		 * @return the prizename
		 * @since 1.0.0
		 */

		public String getPrizename() {
			return prizename;
		}

		/**
		 * @param prizename
		 *            the prizename to set
		 */
		public void setPrizename(String prizename) {
			this.prizename = prizename;
		}

		/**
		 * awardway
		 *
		 * @return the awardway
		 * @since 1.0.0
		 */

		public String getAwardway() {
			return awardway;
		}

		/**
		 * @param awardway
		 *            the awardway to set
		 */
		public void setAwardway(String awardway) {
			this.awardway = awardway;
		}

	}
}
