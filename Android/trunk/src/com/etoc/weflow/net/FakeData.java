package com.etoc.weflow.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.etoc.weflow.net.GsonResponseObject.AccountInfoResp;
import com.etoc.weflow.net.GsonResponseObject.AdvInfo;
import com.etoc.weflow.net.GsonResponseObject.AppHomeResp;
import com.etoc.weflow.net.GsonResponseObject.AppListMoreResp;
import com.etoc.weflow.net.GsonResponseObject.GiftBannerResp;
import com.etoc.weflow.net.GsonResponseObject.GiftListResp;
import com.etoc.weflow.net.GsonResponseObject.GiftResp;
import com.etoc.weflow.net.GsonResponseObject.PhoneChargeListResp;
import com.etoc.weflow.net.GsonResponseObject.QChargeListResp;
import com.etoc.weflow.net.GsonResponseObject.RechargePhoneResp;
import com.etoc.weflow.net.GsonResponseObject.RechargeQQResp;
import com.etoc.weflow.net.GsonResponseObject.SoftInfoResp;
import com.etoc.weflow.net.GsonResponseObject.getAccInfoResponse;
import com.etoc.weflow.net.GsonResponseObject.getAdvInfoResponse;
import com.etoc.weflow.net.GsonResponseObject.getAuthCodeResponse;
import com.etoc.weflow.net.GsonResponseObject.loginResponse;

public class FakeData {
	public static final String HTTP_HOST_PREFIX = "http://test1.mishare.cn:8080/1.1/images";
//	public static final String HTTP_HOST_PREFIX = "http://railwifi.cn/1.1/images";
	
	public static Map<String, Object> map = new HashMap<String, Object>();
	static{
		getAuthCodeResponse r0 = new getAuthCodeResponse();
		r0.status = "0000";
//		map.put(Requester.RIA_INTERFACE_SENDSMS, r0);
		
		loginResponse r1 = new loginResponse();
		/*r1.status = "0";
		r1.uuid   = UUID.randomUUID().toString();
//		r1.tel    = "";
		r1.pts    = "95";
		r1.rate   = "4.31";*/
//		map.put(Requester.RIA_INTERFACE_LOGIN, r1);
		
		AccountInfoResp r2 = new AccountInfoResp();
		r2.status = "0";
		r2.menumoney = "76";
		r2.menutype = "4G全国套餐";
		r2.inflowleft = "100";
		r2.outflowleft = "0";
		r2.flowcoins = "380";
		r2.isregistration = "0";
		map.put(Requester.RIA_INTERFACE_ACCOUNT_INFO, r2);
		
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
		
		AppHomeResp r4 = new AppHomeResp();
		r4.status = "0";
		r4.bannerlist = (SoftInfoResp[])createAppBannerData().toArray(new SoftInfoResp[0]);
		r4.applist = (SoftInfoResp[])createAppData().toArray(new SoftInfoResp[0]);
		map.put(Requester.RIA_INTERFACE_APP_HOME, r4);
		
		AppListMoreResp r5 = new AppListMoreResp();
		r5.status = "0";
		r5.hasnextpage = "1";
		r5.list = (SoftInfoResp[])createAppData().toArray(new SoftInfoResp[0]);
		map.put(Requester.RIA_INTERFACE_APP_LIST, r5);
		
		GiftListResp r6 = new GiftListResp();
		r6.status = "0";
		r6.bannerlist = (GiftBannerResp[]) createGiftBannerList().toArray(new GiftBannerResp[0]);
		r6.giftlist = (GiftResp[]) createGiftData().toArray(new GiftResp [0]);
		map.put(Requester.RIA_INTERFACE_GIFT_LIST, r6);
		
		PhoneChargeListResp r7 = new PhoneChargeListResp();
		r7.status = "0";
		r7.chargelist = (RechargePhoneResp[]) createPhoneChargeList().toArray(new RechargePhoneResp[0]);
		map.put(Requester.RIA_INTERFACE_PHONE_CHARGE_LIST, r7);
		
		QChargeListResp r8 = new QChargeListResp();
		r8.status = "0";
		r8.chargelist = (RechargeQQResp[]) createQQChargeList().toArray(new RechargeQQResp[0]);
		map.put(Requester.RIA_INTERFACE_QRECHARGE_LIST, r8);
		
	}
	
	private static List<RechargeQQResp> createQQChargeList() {
		List<RechargeQQResp> list = new ArrayList<GsonResponseObject.RechargeQQResp>();
		String [] money = {"1","2","5","10","20","100"};
		
		for (int i = 0;i < 6;i++) {
			RechargeQQResp item = new RechargeQQResp();
			item.chargesid = i + "";
			item.qcoins = money[i];
			item.cost = Integer.parseInt(item.qcoins) * 100 + "";
			list.add(item);
		}
		return list;
	}
	
	private static List<RechargePhoneResp> createPhoneChargeList() {
		List<RechargePhoneResp> list = new ArrayList<GsonResponseObject.RechargePhoneResp>();
		String [] money = {"10","20","30","50","100","200"};
		
		for (int i = 0;i < 18;i++) {
			RechargePhoneResp item = new RechargePhoneResp();
			item.chargesid = i + "";
			item.money = money[i % 6];
			item.cost = Integer.parseInt(item.money) * 100 + "";
			item.type = (i / 6 + 1) + "";
			list.add(item);
		}
		
		return list;
		
	}
	
	private static List<GiftResp> createGiftData() {
		List<GiftResp> list = new ArrayList<GiftResp>();
		
		String[] imgUrls = {"http://pic7.nipic.com/20100526/3726655_170231009273_2.jpg",
        		"http://pic5.nipic.com/20100102/3759236_100017502126_2.jpg",
        		"http://pic8.nipic.com/20100722/4235094_143649006971_2.jpg",
        		"http://pic1.nipic.com/2008-12-23/2008122312587944_2.jpg",
        		"http://img1.imgtn.bdimg.com/it/u=3517413395,2250230838&fm=21&gp=0.jpg"
        		};
		String[] titles = {"庐山月饼",
				"菲尼迪100元礼券",
				"乐行仕优惠券",
				"茅台礼券",
				"阳澄湖大闸蟹"
		};
		
		String[] descs = {
				"9月6日下午14:00-16:30，新湖庐山国际将浓情上演中秋月饼DIY家庭聚会",
				"菲妮迪女装 2014秋装新款 经典简约菱格时尚撞色薄款棉衣外套",
				"乐行仕作为目前国内休闲皮鞋网络第一品牌,自成立之初,便始终对男士高档皮鞋及配套耐用",
				"大曲酱香型白酒的鼻祖，有“国酒”之称，是中国最高端白酒之一",
				"蟹身不沾泥，俗称清水大闸蟹，体大膘肥，青壳白肚，金爪黄毛",
		};
		for (int i = 0;i < 5;i++) {
			GiftResp resp = new GiftResp();
			resp.giftid = i + "";
			resp.imgsrc = imgUrls[i];
			resp.title = titles[i];
			resp.giftdesc = descs[i];
			resp.flowcoins = ((i + 1) * 1000) + "";
			list.add(resp);
		}
		
		return list;
	}
	
	private static List<GiftBannerResp> createGiftBannerList() {
		List<GiftBannerResp> list = new ArrayList<GiftBannerResp>();
		
		String[] imgUrls = {"http://www.adzop.com//uploadpic/xcp/1412/P190.rmvb_20141222_110554.306.jpg",
        		"http://www.adzop.com//uploadpic/xcp/1412/P186.rmvb_20141222_110108.278.jpg",
        		"http://www.adzop.com//uploadpic/xcp/1412/P184.rmvb_20141222_110040.713.jpg",
        		"http://www.adzop.com//uploadpic/xcp/1412/P176.rmvb_20141222_105653.404.jpg"
        		};
		for (int i = 0;i < 4;i++) {
			GiftBannerResp resp = new GiftBannerResp();
			resp.giftid = i + "";
			resp.imgsrc = imgUrls[i];
			list.add(resp);
		}
		return list;
	}
	
	private static List<SoftInfoResp> createAppData() {
		List<SoftInfoResp> list = new ArrayList<SoftInfoResp>();
		
		String[] icons = {"http://ico.ooopic.com/ajax/iconpng/?id=137122.png",
				"http://pic27.nipic.com/20130313/3849388_101225310324_2.jpg",
				"http://ico.ooopic.com/ajax/iconpng/?id=319307.png",
				"http://img0.imgtn.bdimg.com/it/u=3348450869,464969419&fm=21&gp=0.jpg",
				"http://www.sucaijiayuan.com/uploads/file/contents/2014/01/52c6dcfc0c75d.png"
		};
		String[] titles = {
				"微话","美拍","苹果商店","麦当劳","爱买"
		};
		String[] descs = {
				"沟通无极限","做最美的自己","高富帅必备","叔叔约约约","没有你买不到的。"
		};
		
		String[] previewImgs = {
				"http://img1.cache.netease.com/catchpic/8/87/874214114CEDF430D823A37FFAF49017.png",
				"http://ent.southcn.com/8/images/attachement/jpg/site4/20140724/13/6975532463546479561.jpg",
				"http://img.faruanwen.net/2015/02/12/14237124635714.jpg",
				"http://himg2.huanqiu.com/attachment2010/2014/0826/20140826053243814.jpg"
		};
		String [] apkUrls = {
				"https://raw.githubusercontent.com/Trinea/trinea-download/master/pull-to-refreshview-demo.apk",
				"http://gdown.baidu.com/data/wisegame/74fed1d1e244eb3c/shoujibaidu_16786712.apk",
				"http://gdown.baidu.com/data/wisegame/309a95d293e02508/ApiDemos.apk",
				"https://raw.githubusercontent.com/Trinea/trinea-download/master/pull-to-refreshview-demo.apk",
				"http://gdown.baidu.com/data/wisegame/309a95d293e02508/ApiDemos.apk",
		};
		for (int i = 0;i < 5;i++) {
			SoftInfoResp resp = new SoftInfoResp();
			resp.appid = i + "";
			resp.appicon = icons[i];
			resp.title = titles[i];
			resp.size = "11.2M";
			resp.version = "2.1.0";
			resp.instruction = "分享微信朋友圈可获得10流量币\n安装软件体验2分钟以上即可获得流量币";
			resp.introduction = descs[i];
			resp.flowcoins = (i*10 + 10) + "";
			resp.apppreview = previewImgs;
			resp.apkurl = apkUrls[i];
			list.add(resp);
		}
		return list;
	}
	
	private static List<SoftInfoResp> createAppBannerData() {
		List<SoftInfoResp> list = new ArrayList<SoftInfoResp>();
		
		String[] imgUrls = {"http://www.adzop.com//uploadpic/xcp/1412/P190.rmvb_20141222_110554.306.jpg",
        		"http://www.adzop.com//uploadpic/xcp/1412/P186.rmvb_20141222_110108.278.jpg",
        		"http://www.adzop.com//uploadpic/xcp/1412/P184.rmvb_20141222_110040.713.jpg",
        		"http://www.adzop.com//uploadpic/xcp/1412/P176.rmvb_20141222_105653.404.jpg"
        		};
		for (int i = 0;i < 4;i++) {
			SoftInfoResp resp = new SoftInfoResp();
			resp.appid = i + "";
			resp.appbannerpic = imgUrls[i];
			list.add(resp);
		}
		return list;
	}

}
