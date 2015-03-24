package com.etoc.weflow.utils;

public class PTypeTransfer {

	public static String getPTypeName(PType type) {
		String ret = "";
		switch(type) {
		case down_soft:
			ret = "下载软件";
			break;
		case watch_movie:
			ret = "看广告";
			break;
		case play_game:
			ret = "玩游戏";
			break;
		case change_tc:
			ret = "兑换话费";
			break;
		case change_qq:
			ret = "兑换Q币";
			break;
		case change_wf:
			ret = "兑换流量包";
			break;
		case change_gf:
			ret = "兑换游戏礼包";
			break;
		case recharge_gm:
			ret = "游戏充值";
			break;
		case bug_gf:
			ret = "购礼券";
			break;
		case get_award:
			ret = "获得奖品";
			break;
		}
		return ret;
	}
	
	public static String getPTypeName(String typeStr) {
		String ret = "";
		if(typeStr != null && !typeStr.equals("")) {
			if(typeStr.equals(PType.down_soft.getValue())) {
				ret = getPTypeName(PType.down_soft);
			} else if(typeStr.equals(PType.watch_movie.getValue())) {
				ret = getPTypeName(PType.watch_movie);
			} else if(typeStr.equals(PType.play_game.getValue())) {
				ret = getPTypeName(PType.play_game);
			} else if(typeStr.equals(PType.change_tc.getValue())) {
				ret = getPTypeName(PType.change_tc);
			} else if(typeStr.equals(PType.change_qq.getValue())) {
				ret = getPTypeName(PType.change_qq);
			} else if(typeStr.equals(PType.change_wf.getValue())) {
				ret = getPTypeName(PType.change_wf);
			} else if(typeStr.equals(PType.change_gf.getValue())) {
				ret = getPTypeName(PType.change_gf);
			} else if(typeStr.equals(PType.recharge_gm.getValue())) {
				ret = getPTypeName(PType.recharge_gm);
			} else if(typeStr.equals(PType.bug_gf.getValue())) {
				ret = getPTypeName(PType.bug_gf);
			} else if(typeStr.equals(PType.get_award.getValue())) {
				ret = getPTypeName(PType.get_award);
			}
		}
		return ret;
	}
	
}
