/**
 * 创建时间
 * 2015年3月17日-下午9:44:09
 * 
 * 
 */
package com.etoc.weflow.utils;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月17日 下午9:44:09
 * 
 * @version 1.0.0
 * 
 */
public enum PType {
	// 下软件
	down_soft("01"),

	// 看电影
	watch_movie("02"),

	// 玩游戏
	play_game("03"),

	// 兑换话费
	change_tc("04"),

	// 兑换qq币
	change_qq("05"),

	// 兑换流量包
	change_wf("06"),

	// 兑换游戏礼包
	change_gf("07"),

	// 游戏充值
	recharge_gm("08"),

	// 购礼券
	bug_gf("09"),

	// 游戏摇一摇
	game_shake("10"),

	// 游戏刮刮
	game_scratch("11"),
	
	// 网页
	web_page("12");

	private String value;

	PType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
