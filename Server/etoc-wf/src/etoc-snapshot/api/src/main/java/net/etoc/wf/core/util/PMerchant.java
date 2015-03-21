/**
 * 创建时间
 * 2015年3月17日-下午9:44:09
 * 
 * 
 */
package net.etoc.wf.core.util;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月17日 下午9:44:09
 * 
 * @version 1.0.0
 * 
 */
public enum PMerchant {
	// app本身
	app("000"),

	// 软件提供方
	soft_offer("001"),

	// 视频提供方
	vedio_offer("002"),

	// 游戏提供方
	game_offer("003"),

	// 福禄充值
	fl_charge("004"),

	// 联通
	chinaunicom("005"),

	// 兑换游戏礼包
	chinamobile("006"),

	// 游戏充值
	chinatelecom("007");

	private String value;

	PMerchant(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
