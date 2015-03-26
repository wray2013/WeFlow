package com.etoc.weflow.utils;

import com.etoc.weflow.activity.ExpenseFlowActivity;
import com.etoc.weflow.activity.MakeFlowActivity;

import android.content.Context;
import android.content.Intent;

public class PTypeTransfer {

	public static String getPTypeName(PType type) {
		String ret = "";
		switch(type) {
		case down_soft:
			ret = "下载软件";
			break;
		case watch_movie:
			ret = "看视频";
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
	
	public static Intent createColumnIntent(Context ctx, PType type, boolean isLogin) {
		Intent intent = new Intent();
		switch(type) {
		case down_soft:
			intent.setClass(ctx, MakeFlowActivity.class);
			intent.putExtra("isLogin", isLogin);
			intent.putExtra(ConStant.INTENT_MAKE_FLOW, 0xffeecc02 & 0xff);
			break;
		case watch_movie:
			intent.setClass(ctx, MakeFlowActivity.class);
			intent.putExtra("isLogin", isLogin);
			intent.putExtra(ConStant.INTENT_MAKE_FLOW, 0xffeecc01 & 0xff);
			break;
		case play_game:
			intent.setClass(ctx, MakeFlowActivity.class);
			intent.putExtra("isLogin", isLogin);
			intent.putExtra(ConStant.INTENT_MAKE_FLOW, 0xffeecc03 & 0xff);
			break;
		case change_tc:
		case change_qq:
			intent.setClass(ctx, ExpenseFlowActivity.class);
			intent.putExtra("isLogin", isLogin);
			intent.putExtra(ConStant.INTENT_EXPENSE_FLOW, 0xffeedd01 & 0xff);
			break;
		case change_wf:
			intent.setClass(ctx, ExpenseFlowActivity.class);
			intent.putExtra("isLogin", isLogin);
			intent.putExtra(ConStant.INTENT_EXPENSE_FLOW, 0xffeedd02 & 0xff);
			break;
		case change_gf:
		case recharge_gm:
			intent.setClass(ctx, ExpenseFlowActivity.class);
			intent.putExtra("isLogin", isLogin);
			intent.putExtra(ConStant.INTENT_EXPENSE_FLOW, 0xffeedd03 & 0xff);
			break;
		case bug_gf:
			intent.setClass(ctx, ExpenseFlowActivity.class);
			intent.putExtra("isLogin", isLogin);
			intent.putExtra(ConStant.INTENT_EXPENSE_FLOW, 0xffeedd04 & 0xff);
			break;
		case get_award:
			break;
		default:
			break;
		}
		return intent;
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
	
	public static PType getPType(String typeStr) {
		PType ptype = null;
		if(typeStr != null && !typeStr.equals("")) {
			if(typeStr.equals(PType.down_soft.getValue())) {
				ptype = PType.down_soft;
			} else if(typeStr.equals(PType.watch_movie.getValue())) {
				ptype = PType.watch_movie;
			} else if(typeStr.equals(PType.play_game.getValue())) {
				ptype = PType.play_game;
			} else if(typeStr.equals(PType.change_tc.getValue())) {
				ptype = PType.change_tc;
			} else if(typeStr.equals(PType.change_qq.getValue())) {
				ptype = PType.change_qq;
			} else if(typeStr.equals(PType.change_wf.getValue())) {
				ptype = PType.change_wf;
			} else if(typeStr.equals(PType.change_gf.getValue())) {
				ptype = PType.change_gf;
			} else if(typeStr.equals(PType.recharge_gm.getValue())) {
				ptype = PType.recharge_gm;
			} else if(typeStr.equals(PType.bug_gf.getValue())) {
				ptype = PType.bug_gf;
			} else if(typeStr.equals(PType.get_award.getValue())) {
				ptype = PType.get_award;
			}
		}
		return ptype;
	}
	
}
