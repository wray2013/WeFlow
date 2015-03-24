package com.etoc.weflow.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.etoc.weflow.net.GsonResponseObject.AccountInfoResp;
import com.etoc.weflow.net.GsonResponseObject.AdvInfo;
import com.etoc.weflow.net.GsonResponseObject.AppHomeResp;
import com.etoc.weflow.net.GsonResponseObject.AppListMoreResp;
import com.etoc.weflow.net.GsonResponseObject.FlowPkgListResp;
import com.etoc.weflow.net.GsonResponseObject.GameChargeListResp;
import com.etoc.weflow.net.GsonResponseObject.GameChargeProductResp;
import com.etoc.weflow.net.GsonResponseObject.GameChargeResp;
import com.etoc.weflow.net.GsonResponseObject.GameGiftProduct;
import com.etoc.weflow.net.GsonResponseObject.GameGiftResp;
import com.etoc.weflow.net.GsonResponseObject.GamePkgListResp;
import com.etoc.weflow.net.GsonResponseObject.GiftBannerResp;
import com.etoc.weflow.net.GsonResponseObject.GiftListResp;
import com.etoc.weflow.net.GsonResponseObject.GiftProduct;
import com.etoc.weflow.net.GsonResponseObject.GiftResp;
import com.etoc.weflow.net.GsonResponseObject.MobileFlowProduct;
import com.etoc.weflow.net.GsonResponseObject.MobileFlowResp;
import com.etoc.weflow.net.GsonResponseObject.PhoneChargeListResp;
import com.etoc.weflow.net.GsonResponseObject.QChargeListResp;
import com.etoc.weflow.net.GsonResponseObject.QRechargeProduct;
import com.etoc.weflow.net.GsonResponseObject.RechargePhoneResp;
import com.etoc.weflow.net.GsonResponseObject.RechargeProduct;
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
		r6.chargelist = (GiftResp[]) createGiftData().toArray(new GiftResp [0]);
		map.put(Requester.RIA_INTERFACE_GIFT_LIST, r6);
		
		PhoneChargeListResp r7 = new PhoneChargeListResp();
		r7.status = "0";
		r7.chargelist = (RechargePhoneResp[]) createPhoneChargeList().toArray(new RechargePhoneResp[0]);
		map.put(Requester.RIA_INTERFACE_PHONE_CHARGE_LIST, r7);
		
		QChargeListResp r8 = new QChargeListResp();
		r8.status = "0";
		r8.chargelist = (RechargeQQResp[]) createQQChargeList().toArray(new RechargeQQResp[1]);
		map.put(Requester.RIA_INTERFACE_QRECHARGE_LIST, r8);
		
		FlowPkgListResp r9 = new FlowPkgListResp();
		r9.status = "0";
		r9.list = (MobileFlowResp[]) createMoblieFlowList().toArray(new MobileFlowResp[0]);
		map.put(Requester.RIA_INTERFACE_FLOW_PKG_LIST, r9);
		
		GamePkgListResp r10 = new GamePkgListResp();
		r10.status = "0";
		r10.chargelist = (GameGiftResp[]) createGameGiftList().toArray(new GameGiftResp[0]);
		map.put(Requester.RIA_INTERFACE_GAME_PKG_LIST, r10);
		
		GameChargeListResp r11 = new GameChargeListResp();
		r11.status = "0";
		r11.chargelist = (GameChargeResp[]) createGameRechargeList().toArray(new GameChargeResp[0]);
		map.put(Requester.RIA_INTERFACE_GAME_RECHARGE_LIST, r11);
	}
	
	private static List<GameChargeResp> createGameRechargeList() {
		List<GameChargeResp> list = new ArrayList<GsonResponseObject.GameChargeResp>();
		String [] typenames = {"征途/巨人","完美一卡通","世纪天成","搜狐一卡通","盛大在线","猫扑一卡通"};
		for (int i = 0;i < 6;i++) {
			GameChargeResp resp = new GameChargeResp();
			resp.type = "" + (i+1);
			resp.typename = typenames[i];
			resp.products = (GameChargeProductResp[]) createGameChargeProductList(i).toArray(new GameChargeProductResp[0]);
			list.add(resp);
		}
		return list;
	}
	
	private static List<GameChargeProductResp> createGameChargeProductList(int n) {
		List<GameChargeProductResp> list = new ArrayList<GameChargeProductResp>();
		for (int i = 0;i < 6;i++) {
			GameChargeProductResp resp = new GameChargeProductResp();
			resp.chargesid = (n + 1) + "000" + i;
			resp.money = ((i + 1) * 10) + "";
			resp.cost = ((i + 1) * 1000) + "";
			list.add(resp);
		}
		return list;
	}
	
	private static List<GameGiftResp> createGameGiftList() {
		List<GameGiftResp> list = new ArrayList<GameGiftResp>();
		
		String[] imgUrls = {"http://up.ekoooo.com/uploads2/tubiao/6/20088712119375778013.png",
        		"http://up.ekoooo.com/uploads2/allimg/080730/03562737.png",
        		"http://pica.nipic.com/2007-09-18/2007918135853894_2.jpg",
        		"http://up.ekoooo.com/uploads2/allimg/080730/08455224.png",
        		"http://up.ekoooo.com/uploads2/tubiao/7/200887197170778030.png"
        		};
		
		String[] leaves = {
				"189",
				"189",
				"189",
				"189",
				"189",
		};
		GameGiftResp resp = new GameGiftResp();
		resp.type = "";
		resp.typename = "";
		List<GameGiftProduct> productList = new ArrayList<GsonResponseObject.GameGiftProduct>();
		
		for (int i = 0;i < 5;i++) {
			GameGiftProduct product = new GameGiftProduct();
			product.chargesid = i + "";
			product.icon = imgUrls[i];
			product.title = "DNF服务器喇叭";
			product.desc = leaves[i];
			product.cost = ((i + 1) * 100) + "";
			productList.add(product);
		}
		
		resp.products = (GameGiftProduct[]) productList.toArray(new GameGiftProduct[0]);
		list.add(resp);
		return list;
	}
	
	private static List<MobileFlowResp> createMoblieFlowList() {
		List<MobileFlowResp> list = new ArrayList<MobileFlowResp>();
		List<MobileFlowProduct> pList = new ArrayList<MobileFlowProduct>();
		
		String[] imgUrls = {"http://img2.imgtn.bdimg.com/it/u=2246142888,482574638&fm=21&gp=0.jpg",
        		"http://img.ithome.com/newsuploadfiles/2013/7/20130729_082806_654.jpg",
        		"http://img3.imgtn.bdimg.com/it/u=3520249570,2956679241&fm=21&gp=0.jpg",
        		"http://img2.imgtn.bdimg.com/it/u=503567143,1320843493&fm=11&gp=0.jpg",
        		"http://img2.imgtn.bdimg.com/it/u=2461699972,2986826516&fm=21&gp=0.jpg"
        		};
		String[] titles = {"10元畅享沃3G",
				"电信20元流量包",
				"300M流量包",
				"50M流量包",
				"1G流量包"
		};
		
		String[] descs = {
				"手机上网套餐全新升级，赠送流量包",
				"手机上网套餐全新升级，赠送流量包",
				"手机上网套餐全新升级，赠送流量包",
				"手机上网套餐全新升级，赠送流量包",
				"手机上网套餐全新升级，赠送流量包",
		};
		for (int i = 0;i < 5;i++) {
			MobileFlowProduct resp = new MobileFlowProduct();
			resp.flowpkgid = i + "";
			resp.imgsrc = imgUrls[i];
			resp.title = titles[i];
			resp.desc = descs[i];
			resp.cost = ((i + 1) * 1000) + "";
			pList.add(resp);
		}
		
		MobileFlowResp item = new MobileFlowResp();
		item.type = "8";
		item.typename = "夜间包";
		item.products = pList.toArray(new MobileFlowProduct[pList.size()]);
		list.add(item);
		return list;
		
	}
	
	private static List<RechargeQQResp> createQQChargeList() {
		List<RechargeQQResp> list = new ArrayList<GsonResponseObject.RechargeQQResp>();
		List<QRechargeProduct> plist = new ArrayList<GsonResponseObject.QRechargeProduct>();
		String [] money = {"1","2","5","10","20","100"};
		
		for (int i = 0;i < 6;i++) {
			QRechargeProduct p = new QRechargeProduct();
			p.chargesid = i + "";
			p.money = money[i];
			p.cost = Integer.parseInt(p.money) * 100 + "";
			plist.add(p);
		}
		RechargeQQResp item = new RechargeQQResp();
		item.type = "7";
		item.typename = "腾讯QQ";
		item.products = plist.toArray(new QRechargeProduct[plist.size()]);
		list.add(item);
		return list;
	}
	
	private static List<RechargePhoneResp> createPhoneChargeList() {
		List<RechargePhoneResp> list = new ArrayList<GsonResponseObject.RechargePhoneResp>();
		String [] money = {"10","20","30","50","100","200"};
		String [] typeNames = {"中国电信","中国联通","中国移动"};
		
		for (int i = 0;i < 3;i++) {
			RechargePhoneResp resp = new RechargePhoneResp();
			resp.type = "" + i;
			resp.typename = typeNames[i];
			List<RechargeProduct> productList = new ArrayList<GsonResponseObject.RechargeProduct>();
			for (int j = 0;j < 6;j++) {
				RechargeProduct product = new RechargeProduct();
				product.chargesid = i + "000" + j;
				product.money = money[j];
				product.cost = Integer.parseInt(product.money) * 100 + "";
				productList.add(product);
			}
			resp.products = (RechargeProduct[]) productList.toArray(new RechargeProduct[0]);
			list.add(resp);
		}
		/*for (int i = 0;i < 18;i++) {
			RechargePhoneResp item = new RechargePhoneResp();
			item.chargesid = i + "";
			item.money = money[i % 6];
			item.cost = Integer.parseInt(item.money) * 100 + "";
			item.type = (i / 6 + 1) + "";
			list.add(item);
		}*/
		
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
		GiftResp resp = new GiftResp();
		List<GiftProduct> productList = new ArrayList<GsonResponseObject.GiftProduct>();
		
		for (int i = 0;i < 5;i++) {
			GiftProduct product = new GiftProduct();
			product.chargesid = i + "";
			product.imgsrc = imgUrls[i];
			product.title = titles[i];
			product.desc = descs[i];
			product.cost = ((i + 1) * 1000) + "";
			productList.add(product);
		}
		resp.products = (GiftProduct[]) productList.toArray(new GiftProduct[0]);
		list.add(resp);
		
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
		
		String previewImgs = "http://img1.cache.netease.com/catchpic/8/87/874214114CEDF430D823A37FFAF49017.png"
				+ ",http://ent.southcn.com/8/images/attachement/jpg/site4/20140724/13/6975532463546479561.jpg"
				+ ",http://img.faruanwen.net/2015/02/12/14237124635714.jpg"
				+ ",http://himg2.huanqiu.com/attachment2010/2014/0826/20140826053243814.jpg";
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
			resp.soft = apkUrls[i];
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
