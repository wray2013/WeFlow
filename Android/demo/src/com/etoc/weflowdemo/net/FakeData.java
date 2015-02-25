package com.etoc.weflowdemo.net;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.etoc.weflowdemo.net.GsonResponseObject.*;

public class FakeData {
	public static final String HTTP_HOST_PREFIX = "http://test1.mishare.cn:8080/1.1/images";
//	public static final String HTTP_HOST_PREFIX = "http://railwifi.cn/1.1/images";
	
	public static Map<String, Object> map = new HashMap<String, Object>();
	static{
		sendSMSResponse r0 = new sendSMSResponse();
		r0.status="0";
		map.put(Requester.RIA_INTERFACE_SENDSMS, r0);
		
		loginResponse r1 = new loginResponse();
		r1.status = "0";
		r1.uuid   = UUID.randomUUID().toString();
//		r1.tel    = "";
		r1.pts    = "95";
		r1.rate   = "4.31";
		map.put(Requester.RIA_INTERFACE_LOGIN, r1);
		
		getAccInfoResponse r2 = new getAccInfoResponse();
		r2.status = "0";
		r2.suitename = "76元\n4G套餐";
		r2.innerflow = "380";
		r2.outerflow = "0";
		map.put(Requester.RIA_INTERFACE_ACC_INFO, r2);
		
		getAdvInfoResponse r3 = new getAdvInfoResponse();
		r3.status = "0";
		AdvInfo[] banners = new AdvInfo[3];
		for(int i = 0; i < banners.length; i++) {
			AdvInfo binfo = new AdvInfo();
			binfo.advid = "1" + String.valueOf(i);
			binfo.coverurl = "";   //封面
			binfo.videourl = "";   //视频广告url
			binfo.title = "";      //标题
			binfo.time = "";       //更新时间
			binfo.content = "";    //广告内容
			binfo.instruction = "";//活动说明
			binfo.flowaward = "";
			banners[0] = binfo;
		}
		AdvInfo[] newadvs = new AdvInfo[3];
		for(int i = 0; i < newadvs.length; i++) {
			AdvInfo ninfo = new AdvInfo();
			ninfo.advid = "2" + String.valueOf(i);
			ninfo.coverurl = "";   //封面
			ninfo.videourl = "";   //视频广告url
			ninfo.title = "";      //标题
			ninfo.time = "";       //更新时间
			ninfo.content = "";    //广告内容
			ninfo.instruction = "";//活动说明
			ninfo.flowaward = "";
			newadvs[0] = ninfo;
		}
		AdvInfo[] recommendadvs = new AdvInfo[3];
		for(int i = 0; i < recommendadvs.length; i++) {
			AdvInfo rinfo = new AdvInfo();
			rinfo.advid = "3" + String.valueOf(i);
			rinfo.coverurl = "";   //封面
			rinfo.videourl = "";   //视频广告url
			rinfo.title = "";      //标题
			rinfo.time = "";       //更新时间
			rinfo.content = "";    //广告内容
			rinfo.instruction = "";//活动说明
			rinfo.flowaward = "";
			recommendadvs[0] = rinfo;
		}
//		map.put(Requester.RIA_INTERFACE_ADV_INFO, r3);
	}

}
