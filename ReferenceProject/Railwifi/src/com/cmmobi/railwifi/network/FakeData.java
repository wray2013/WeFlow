package com.cmmobi.railwifi.network;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.cmmobi.railwifi.network.GsonResponseObject.AlumbElem;
import com.cmmobi.railwifi.network.GsonResponseObject.DiscoverElem;
import com.cmmobi.railwifi.network.GsonResponseObject.SubAlumbElem;
import com.cmmobi.railwifi.network.GsonResponseObject.feedBackElem;
import com.cmmobi.railwifi.network.GsonResponseObject.recommandDetail;
import com.cmmobi.railwifi.network.GsonResponseObject.surveySubElem;

public class FakeData {
	public static final String HTTP_HOST_PREFIX = "http://test1.mishare.cn:8080/1.1/images";
//	public static final String HTTP_HOST_PREFIX = "http://railwifi.cn/1.1/images";
	
	public static Map<String, Object> map = new HashMap<String, Object>();
	static{
		//2.1.1 车上虚拟注册
		GsonResponseObject.registerInfoResp r1 = new GsonResponseObject.registerInfoResp();
		r1.status="0"; 
		r1.nick_name = "游客001";
//		map.put(Requester.RIA_INTERFACE_REGISTERINFO, r1);
		
		//2.1.2 车上修改用户基本信息
		GsonResponseObject.updateInfoResp r2 = new GsonResponseObject.updateInfoResp();
		r2.status = "0";
//		map.put(Requester.RIA_INTERFACE_UPDATEINFO, r2);
		
		//2.1.3 列车服务首页图
		GsonResponseObject.serviceBannerphotoResp r3 = new GsonResponseObject.serviceBannerphotoResp();
		r3.status = "0";
		r3.list = new  GsonResponseObject.serviceBannerphotoElem[4];
		r3.list[0] = new GsonResponseObject.serviceBannerphotoElem();
		r3.list[1] = new GsonResponseObject.serviceBannerphotoElem();
		r3.list[2] = new GsonResponseObject.serviceBannerphotoElem();
		r3.list[3] = new GsonResponseObject.serviceBannerphotoElem();
		
		
		r3.list[0].img_path = HTTP_HOST_PREFIX + "/service_main/ban1.jpg";
		r3.list[0].content = "来~体验旅途中的5星级品质之旅吧！";
		r3.list[0].object_id = "1";
		r3.list[0].type = "6";
		r3.list[1].img_path = HTTP_HOST_PREFIX + "/service_main/ban2.jpg";
		r3.list[1].content = "来~体验旅途中的5星级品质之旅吧！";
		r3.list[1].object_id = "2";
		r3.list[1].type = "1";
		r3.list[2].img_path = HTTP_HOST_PREFIX + "/service_main/ban3.jpg";
		r3.list[2].content = "来~体验旅途中的5星级品质之旅吧！";
		r3.list[2].object_id = "3";
		r3.list[2].type = "6";
		r3.list[3].img_path = HTTP_HOST_PREFIX + "/service_main/ban4.jpg";
		r3.list[3].content = "求不骗";
		r3.list[3].object_id = "4";
		r3.list[3].type = "1";
//		map.put(Requester.RIA_INTERFACE_SERVICE_BANPHOTO, r3);
		
		//2.1.4 获取铁路资讯列表
		GsonResponseObject.newsListResp r4 = new GsonResponseObject.newsListResp();
		r4.status = "0";
		r4.isNextPage =  "1";
		
		r4.list = new GsonResponseObject.newsElem[10];
		for(int i=0; i<r4.list.length; i++){
			r4.list[i] = new GsonResponseObject.newsElem();
			r4.list[i].object_id = "" + (i+1);
			r4.list[i].img_path = HTTP_HOST_PREFIX + "/news_list/u" + (i+1) + ".jpg";
		}
		
		r4.list[0].title = "安徽合肥高铁新客站启用";
		r4.list[0].subheading = "合肥高铁南站首趟动车组G7684列车12日11时从呈“粉墙黛瓦、五岳朝天”建筑风格的南站发车，驶往浙江宁波。这标志着合肥铁路枢纽这座新建的现代化客站正式启用。";

		r4.list[1].title = "新疆高铁的7个“最”";
		r4.list[1].subheading = "兰新高铁新疆段16日正式通车，意味着“新丝绸之路”亚欧大陆桥建设翻开了崭新的一页。兰新高铁的运营将加速新疆和内地的互联互通，同时也进一步拉近了我国与中西亚时空距离。作为我国的丝绸之路经济带核心区，新疆的经济社会发展将迎来更大的发展潜力和空间。";
		
		r4.list[2].title = "巴西国际铁路工业展开幕 中国高铁成展会热点";
		r4.list[2].subheading = "巴西国际铁路工业展每年一届，今年的展会为期3天，由巴西RevistaFerroviaria展览公司、巴西政府及巴西交通运输部联合举办，是南美地区最重要的专业铁路行业展览会。";
		
		r4.list[3].title = "南北车合并拉开国企重组大幕 总资产将超3000亿";
		r4.list[3].subheading = "面对国际竞争需求，南车、北车再度合并成巨无霸，铁路、核电等行业国企巨头重组大幕开启";
		
		r4.list[4].title = "铁路总公司负债率扩大到64.77% 每年付利息500亿";
		r4.list[4].subheading = "中国铁路建设全面提速的大背景下，中国铁路总公司（简称中铁总）的负债继续扩大。";
		
		r4.list[5].title = "挥别百年火车站 铁道迷夜奔溪口：百里相送也值";
		r4.list[5].subheading = "寿丰乡溪口火车站正式走入历史，铁道迷闻讯，昨日一早赶到车站，用相机记录火车进站瞬间。";
		
		r4.list[6].title = "中国铁路商业的机遇与抉择";
		r4.list[6].subheading = "今天，再一次探望铁路商业之路，发现这里的黎明似乎还静悄悄，甚至这里还躺在中国铁路运营里程突破十万公里的喜悦中怡然自得，而危险就在这一个十年间，当然机遇也就在眼前。";
		
		r4.list[7].title = "携带超重行李补运费 父爱深深金钱难购买";
		r4.list[7].subheading = "花355元从佳木斯乘火车到北京看儿子，没想到出站时被告知行李超重，补票就需192元。";
		
		r4.list[8].title = "自驾游汽车运输班列 将服务工作做得更好";
		r4.list[8].subheading = "铁路部门推出“自驾游汽车运输班列”，是为了满足广大旅客携爱车异地自驾旅行的需求。";
		
		r4.list[9].title = "老年人优先下铺是一道最美的风景";
		r4.list[9].subheading = "此前饱受诟病的12306网站终于“接地气”了：昨悉，以后通过12306网站购买火车票时，多人同一订单系统自动分配相邻座位，60岁以上老人的订单凭身份证号识别后优先安排下铺。";
		
//		map.put(Requester.RIA_INTERFACE_NEWSLIST, r4);
		
		//2.1.5 获取铁路资讯详情
		GsonResponseObject.newsInfoContent r5 = new GsonResponseObject.newsInfoContent();
		r5.status = "0";
		r5.title = "网购火车票充足时老人可优先安排";
		r5.img_path = HTTP_HOST_PREFIX + "/news_list/u" + 2 + ".jpg";
		r5.content = "从明年9月1日起所有计算机票(电子票)、磁介质车票(磁卡票)车票背面使用新版“铁路旅客乘车须知”(1102版) 内容如下：\r\n1.请按票面标明的日期、车次凭票乘车，并在规定时间内至到站。直达票中途下车，未乘区间失效。通票中途换乘需中转签证。\r\n2.免费携带品：成人20千克、儿童10千克，长、宽、高之和不超过160厘米(动车组列车130厘米)，超过规定需办理托运。禁止携带危险品及违章物品进站、乘车。\r\n3.车站在开车前提前停止检票，进站安检、验证排队人数较多，请提前至指定场所候车，以免耽误旅行。\r\n4.实名制车票须凭乘车人有效身份证件原件，票、证一致方可退票、换票、中转签证;票、证、人一致方可进站、乘车。\r\n5.未尽事项请参阅《铁路旅客运输规程》。如有变化以车站公告为准，敬请关注。旅行中请关注有关安全提示。如有疑问，请拨打12306客服电话垂询。";
//		map.put(Requester.RIA_INTERFACE_NEWSINFO, r5);
		
		//2.1.6 温馨提示
		GsonResponseObject.commonContent r6 = new GsonResponseObject.commonContent();
		r6.status = "0";
		r6.title = "乘客须知";
		r6.content = "从明年9月1日起所有计算机票(电子票)、磁介质车票(磁卡票)车票背面使用新版“铁路旅客乘车须知”(1102版) 内容如下：\r\n1.请按票面标明的日期、车次凭票乘车，并在规定时间内至到站。直达票中途下车，未乘区间失效。通票中途换乘需中转签证。\r\n2.免费携带品：成人20千克、儿童10千克，长、宽、高之和不超过160厘米(动车组列车130厘米)，超过规定需办理托运。禁止携带危险品及违章物品进站、乘车。\r\n3.车站在开车前提前停止检票，进站安检、验证排队人数较多，请提前至指定场所候车，以免耽误旅行。\r\n4.实名制车票须凭乘车人有效身份证件原件，票、证一致方可退票、换票、中转签证;票、证、人一致方可进站、乘车。\r\n5.未尽事项请参阅《铁路旅客运输规程》。如有变化以车站公告为准，敬请关注。旅行中请关注有关安全提示。如有疑问，请拨打12306客服电话垂询。";
//		map.put(Requester.RIA_INTERFACE_PROMPT, r6);
		
		//2.1.7 路局简介
		GsonResponseObject.commonContent r7 = new GsonResponseObject.commonContent();
		r7.status = "0";
		r7.title = "武汉铁路局简介";
		r7.content = "    成立于2005年3月18日的武汉铁路局，所辖线路覆盖湖北省全境和豫南地区，承东启西，贯通南北，主要担负京广线、京九线、襄渝线、焦柳线、孟宝线、宁西线、武九线等铁路运输任务，线路总营业里程2975公里，线路总延长6393公里。武汉铁路局现有基层单位78个（其中运输站段32个），职工9.7万人。2007年，武汉铁路局名列中国企业500强第197名、中国服务企业500强第66名、铁路运输及辅助服务业第9名。\r\n   武汉铁路局在大力构筑快速通道方面，武汉至广州、石家庄至武汉客运专线、沪汉蓉快速通道有序推进；在改造既有线路方面，武汉至安康铁路增建二线，京九、武九、洛张铁路电气化改造如火如荼；在中心枢纽建设方面，天兴洲公铁两用长江大桥、武汉集装箱中心站、武汉站、汉口站扩建、武汉客运专线动车组检修基地、武汉综合调度中心、武汉客运专线综合维修中心等工程建设高歌猛进。\r\n   届时管内“四纵四横”的骨干网络基本形成，武汉枢纽将有两条过江通道、三大客运站、一个大型编组站，承担全路部分客运专线主要通道的调度指挥、客运专线基础设施维修和动车组检修任务，成为全国铁路路网型枢纽和客运中心。";
//		map.put(Requester.RIA_INTERFACE_INTRO, r7);
		
		//2.1.8 获取订餐列表
		GsonResponseObject.orderListResp r8 = new GsonResponseObject.orderListResp();
		r8.status  = "0";
		r8.name = new String[]{"全部", "美食", "零食"};
		r8.current_type = "全部";
		
		r8.list = new GsonResponseObject.orderElem[10];
		r8.list[0] = new GsonResponseObject.orderElem();
		r8.list[1] = new GsonResponseObject.orderElem();
		r8.list[2] = new GsonResponseObject.orderElem();
		r8.list[3] = new GsonResponseObject.orderElem();
		r8.list[4] = new GsonResponseObject.orderElem();
		r8.list[5] = new GsonResponseObject.orderElem();
		r8.list[6] = new GsonResponseObject.orderElem();
		r8.list[7] = new GsonResponseObject.orderElem();
		r8.list[8] = new GsonResponseObject.orderElem();
		r8.list[9] = new GsonResponseObject.orderElem();
		
		r8.list[0].object_id = "123";
		r8.list[0].img_path = HTTP_HOST_PREFIX + "/dinner_order/u10.jpg";
		r8.list[0].name = "麻婆豆腐";
		r8.list[0].price = "8";
		r8.list[0].type = "美食";
		
		r8.list[1].object_id = "124";
		r8.list[1].img_path = HTTP_HOST_PREFIX + "/dinner_order/u14.jpg";
		r8.list[1].name = "东北大乱炖";
		r8.list[1].price = "16";
		r8.list[1].type = "美食";
		
		r8.list[2].object_id = "125";
		r8.list[2].img_path = HTTP_HOST_PREFIX + "/dinner_order/u16.jpg";
		r8.list[2].name = "糖醋里脊";
		r8.list[2].price = "25";
		r8.list[2].type = "美食";
		
		r8.list[3].object_id = "126";
		r8.list[3].img_path = HTTP_HOST_PREFIX + "/dinner_order/u18.jpg";
		r8.list[3].name = "锅贴";
		r8.list[3].price = "8";
		r8.list[3].type = "美食";
		
		r8.list[4].object_id = "127";
		r8.list[4].img_path = HTTP_HOST_PREFIX + "/dinner_order/u20.jpg";
		r8.list[4].name = "小笼包";
		r8.list[4].price = "6";
		r8.list[4].type = "美食";
		
		r8.list[5].object_id = "128";
		r8.list[5].img_path = HTTP_HOST_PREFIX + "/dinner_order/u22.jpg";
		r8.list[5].name = "鲜虾炖鸡";
		r8.list[5].price = "35";
		r8.list[5].type = "美食";
		
		r8.list[6].object_id = "129";
		r8.list[6].img_path = HTTP_HOST_PREFIX + "/dinner_order/u23.jpg";
		r8.list[6].name = "恰恰瓜子";
		r8.list[6].price = "9.9";
		r8.list[6].type = "零食";
		
		r8.list[7].object_id = "130";
		r8.list[7].img_path = HTTP_HOST_PREFIX + "/dinner_order/u24.jpg";
		r8.list[7].name = "开心果";
		r8.list[7].price = "40";
		r8.list[7].type = "零食";
		
		r8.list[8].object_id = "131";
		r8.list[8].img_path = HTTP_HOST_PREFIX + "/dinner_order/u25.jpg";
		r8.list[8].name = "奥利奥饼干";
		r8.list[8].price = "6";
		r8.list[8].type = "零食";
		
		r8.list[9].object_id = "132";
		r8.list[9].img_path = HTTP_HOST_PREFIX + "/dinner_order/u26.jpg";
		r8.list[9].name = "沙琪玛";
		r8.list[9].price = "15";
		r8.list[9].type = "零食";
		
//		map.put(Requester.RIA_INTERFACE_ORDERLIST, r8);
		
		//2.1.9 获取城市风光信息
		GsonResponseObject.cityScopeListResp r9 = new GsonResponseObject.cityScopeListResp();
		r9.status = "0";
		r9.list = new GsonResponseObject.cityScopeElem[10];
		r9.list[0] = new GsonResponseObject.cityScopeElem();
		r9.list[1] = new GsonResponseObject.cityScopeElem();
		r9.list[2] = new GsonResponseObject.cityScopeElem();
		r9.list[3] = new GsonResponseObject.cityScopeElem();
		r9.list[4] = new GsonResponseObject.cityScopeElem();
		r9.list[5] = new GsonResponseObject.cityScopeElem();
		r9.list[6] = new GsonResponseObject.cityScopeElem();
		r9.list[7] = new GsonResponseObject.cityScopeElem();
		r9.list[8] = new GsonResponseObject.cityScopeElem();
		r9.list[9] = new GsonResponseObject.cityScopeElem();
		
		r9.list[0].img_path = HTTP_HOST_PREFIX + "/city_list/nanjing.jpg";
		r9.list[0].name = "南京";
		
		r9.list[1].img_path = HTTP_HOST_PREFIX + "/city_list/chengdu.jpg";
		r9.list[1].name = "成都";
		
		r9.list[2].img_path = HTTP_HOST_PREFIX + "/city_list/wuhan.jpg";
		r9.list[2].name = "武汉";
		
		r9.list[3].img_path = HTTP_HOST_PREFIX + "/city_list/shanghai.jpg";
		r9.list[3].name = "上海";
		
		r9.list[4].img_path = HTTP_HOST_PREFIX + "/city_list/tianjing.jpg";
		r9.list[4].name = "天津";
		
		r9.list[5].img_path = HTTP_HOST_PREFIX + "/city_list/hefei.jpg";
		r9.list[5].name = "合肥";
		
		r9.list[6].img_path = HTTP_HOST_PREFIX + "/city_list/suzhou.jpg";
		r9.list[6].name = "苏州";
		
		r9.list[7].img_path = HTTP_HOST_PREFIX + "/city_list/lasha.jpg";
		r9.list[7].name = "拉萨";
		
		r9.list[8].img_path = HTTP_HOST_PREFIX + "/city_list/yinchuan.jpg";
		r9.list[8].name = "银川";
		
		r9.list[9].img_path = HTTP_HOST_PREFIX + "/city_list/xining.jpg";
		r9.list[9].name = "西宁";
//		map.put(Requester.RIA_INTERFACE_CITYSCOPE, r9);
		
		//2.1.10 获取影音首页图
		GsonResponseObject.serviceBannerphotoResp r10 = new GsonResponseObject.serviceBannerphotoResp();
		r10.status = "0";
		
		r10.list = new GsonResponseObject.serviceBannerphotoElem[4];
		r10.list[0] = new GsonResponseObject.serviceBannerphotoElem();
		r10.list[1] = new GsonResponseObject.serviceBannerphotoElem();
		r10.list[2] = new GsonResponseObject.serviceBannerphotoElem();
		r10.list[3] = new GsonResponseObject.serviceBannerphotoElem();
		
		r10.list[0].object_id = "123";
		r10.list[0].img_path = HTTP_HOST_PREFIX + "/media_home/u4.jpg";
		r10.list[0].content = "求不骗";
		r10.list[0].type="2";
		
		r10.list[1].object_id = "125";
		r10.list[1].img_path = HTTP_HOST_PREFIX + "/media_home/u5.jpg";
		r10.list[1].content = "星际穿越";
		r10.list[1].type="5";
		
		r10.list[2].object_id = "126";
		r10.list[2].img_path = HTTP_HOST_PREFIX + "/media_home/u6.jpg";
		r10.list[2].content = "心花路放";
		r10.list[2].type="5";
		
		r10.list[3].object_id = "127";
		r10.list[3].img_path = HTTP_HOST_PREFIX + "/media_home/u7.jpg";
		r10.list[3].content = "忍者神龟:变种时代";
		r10.list[3].type="2";
		
//		map.put(Requester.RIA_INTERFACE_MEDIA_BANPHOTO, r10);
		
		//2.1.11 获取电影列表
		GsonResponseObject.mediaListResp r11 = new GsonResponseObject.mediaListResp();
		r11.status = "0";
		r11.isNextPage = "1";
		
		r11.list = new GsonResponseObject.mediaElem[4];
		r11.list[0] = new GsonResponseObject.mediaElem();
		r11.list[1] = new GsonResponseObject.mediaElem();
		r11.list[2] = new GsonResponseObject.mediaElem();
		r11.list[3] = new GsonResponseObject.mediaElem();
				
		r11.list[0].media_id = "123";
		r11.list[0].tag = "Mona推荐";
		r11.list[0].color = "1";
		r11.list[0].source = "YOD";
		r11.list[0].name = "猩球崛起II";
		r11.list[0].director = "马特·里夫斯";
		r11.list[0].actors = "安迪·瑟金斯、杰森·克拉克";
		r11.list[0].score = "8.1";
		r11.list[0].introduction = "\u3000猩球崛起是1968年的美国科幻片《猿人袭地球》的前传。电影由二十世纪福克斯影片发行，罗柏·韦斯执导。特技由WETA数码公司。";
		r11.list[0].img_path = HTTP_HOST_PREFIX + "/movie_list/u13.jpg";
		
		r11.list[1].media_id = "124";
		r11.list[1].source = "爱奇艺";
		r11.list[1].name = "黑客帝国3";
		r11.list[1].director = "沃卓斯基兄弟";
		r11.list[1].actors = "基努·里维斯、劳伦斯·费什伯恩";
		r11.list[1].score = "7.9";
		r11.list[1].introduction = "\u3000面对如潮的电子乌贼，人类城市危在旦夕，墨菲斯和崔妮蒂等欲与入侵者决一死战。此时，“救世主”尼奥的身体和思想却意外分离，后者再度陷入到“母体”中。墨菲斯和崔妮蒂也不得不带着尼奥的黑客帝国3：矩阵革命剧照黑客帝国3：矩阵革命剧照身体，回到“母体”和守护天使一起寻找他。一场大战之后，守护天使、病毒双胞胎等皆阵亡，而尼奥却在找到先知之后一无所获。\r\n    锡安的局势越来越危险，议员及指挥官相继阵亡，机器的攻击却丝毫没有因为人类的反抗而减弱。此时，被叛徒射瞎双眼的尼奥，依然想通过希望去实现预言。这时，特工史密斯控制了先知，并变得越来越强大，威胁到了整个“母体”的稳定。在返回锡安的途中，飞船遭到电子乌贼的突袭，崔妮蒂死了，重伤的尼奥被带到机器城市01。在和机器的谈判中，尼奥答应为了人类和机器的共同利益，去消灭史密斯。于是，“母体”又面临着一次翻天覆地的“重载”。";
		r11.list[1].img_path = HTTP_HOST_PREFIX + "/movie_list/u27.jpg";
	
		r11.list[2].media_id = "125";
		r11.list[2].source = "YOD";
		r11.list[2].name = "卧虎藏龙";
		r11.list[2].director = "李安";
		r11.list[2].actors = "周润发、杨紫琼、章子怡、张震";
		r11.list[2].score = "9.0";
		r11.list[2].introduction = "\u3000一代大侠李慕白有退出江湖之意，托付红颜知己俞秀莲将自己的青冥剑带到京城，作为礼物送给贝勒爷收藏。这把有四百年历史的古剑伤人无数，李慕白希望如此重大决断能够表明他离开江湖恩怨的决心。谁知当天夜里宝剑就被人盗走，俞秀莲上前阻拦与盗剑人交手，但最后盗剑人在同伙的救助下逃走。有人看见一个蒙面人消失在九门提督玉大人府内，俞秀莲也认为玉大人难逃干系。九门提督主管京城治安，玉大人刚从新疆调来赴任，贝勒爷即不相信玉大人与此有关，也不能轻举妄动以免影响大局";
		r11.list[2].img_path = HTTP_HOST_PREFIX + "/movie_list/u29.jpg";
		
		r11.list[3].media_id = "126";
		r11.list[3].source = "YOD";
		r11.list[3].name = "星际穿越";
		r11.list[3].director = "克里斯托弗·诺兰";
		r11.list[3].actors = "马修·麦康纳 / 安妮·海瑟薇 / 杰西卡·查斯坦";
		r11.list[3].score = "9.5";
		r11.list[3].introduction = "\u3000在不远的未来，随着地球自然环境的恶化，人类面临着无法生存的威胁。这时科学家们在太阳系中的土星附近发现了一 个虫洞，通过它可以打破人类的能力限制，到更遥远外太空寻找延续生命希望的机会。一个探险小组通过这个虫洞穿越到太阳系之外，他们的目标是找到一颗适合人类移民的星球。在这艘名叫做“Endurance”的飞船上，探险队员着面临着前所未有，人类思想前所未及的巨大挑战。";
		r11.list[3].img_path = HTTP_HOST_PREFIX + "/movie_list/u30.jpg";
//		map.put(Requester.RIA_INTERFACE_MEDIA_MOVIELIST, r11);
		
		//2.1.12 获取电影详情
		GsonResponseObject.mediaDetailInfoResp r12 = new GsonResponseObject.mediaDetailInfoResp();
		r12.status = "0";
		r12.media_id = "123";
		r12.tag = "Mona推荐";
		r12.color = "1";
		r12.introduction = "\u3000猩球崛起是1968年的美国科幻片《猿人袭地球》的前传。电影由二十世纪福克斯影片发行，罗柏·韦斯执导。特技由WETA数码公司。";
		r12.name = "猩球崛起II";
		r12.actors = "安迪·瑟金斯 / 加里·奥德曼";
		r12.director = "马特·里夫斯";
		r12.img_path = HTTP_HOST_PREFIX + "/movie_list/u13.jpg";
		r12.details = "\u3000人类的贪婪造成了无可避免的大自然反扑，后果是人类将失去家园及一切，并面临异族的迫害；基因科学家威尔•罗德曼为研发药物治疗阿兹海默症，对人猿进行活体基因实验，但在实验宣告失败后，他将当中一只刚出生的人猿从实验室救出并取名凯撒。凯撒还在胎中时，其母亲受到大量的药物实验，让他突变并具有智慧与情感，但后来在误伤了人类后被关进收容所，也导致凯撒与人类渐行渐远，最后反目成仇并集结人猿展开革命。随着时间的流逝，地球上两个足以主宰世界的生物——人类与人猿间对立态势渐于紧绷，战争随时一触即发，最终究竟是谁能够握有地球统治权？";
		//r12.details = "\u3000人类的贪婪造成了无可避免的大自然反扑，后果是人类将失去家园。";
		r12.label = new String[]{"动作", "科幻", "美国", "伦理"};
		r12.recommended = "\u3000作为一部以动物为主角的电影，不得不提一下影片的特效。片中猩猩的表情全部都是运用动作捕捉技术拍摄，影片中大量猩猩脸部特写的镜头就是对于这部片子特效最好的考验。其中，凯萨儿子的面部表情尤为惊艳，在凯撒死而复活之后首次与叛逆的儿子见面时的场面让人为之动容。\r\n\u3000《猩球崛起2：黎明之战》无疑是部较为中肯的续作，其电影将第一部《猩球崛起》中的诸多引线得以全面铺开，人类社会濒临灭绝，猩族逐步崛起，承上启下也为第三部的决战埋下了伏笔，这部续作的着力点以也不在是“崛起“，而是猩族内部的纷扰和面对人类不可规避的战争，这是一场来自猩你凯撒的领袖传奇。\r\n\u3000电影开篇伪纪录片的形式一直是科幻电影找代入感的不二法宝，对于这部时隔三年之久的续作，《黎明之战》的故事用十年纪录片的形式予以了时间上的铺垫，十年—人类自食恶果以近消亡边缘，而拥有了智慧的猩族，在第一部中凯撒的带领下，已然发展形成了一个原始社会，正当平和发展的猩族以为人类已然灭绝的时候，为寻找能源的人类闯入了凯撒的猩族领地，电影剧情上的冲突以以此为点徐徐铺开，凯撒猩族内部，科巴这个被人类所残害的猩猩无法摒除对人类的憎恨希望发起进攻，而自已的儿子也父子离隙诸多部众同样开始不满凯撒对人类的温和，外部咄咄逼人的绝大多数人类依旧无法接受猩猩的崛起，这一切都使得人类与猩猩并无法和平相处，电影透过人与猿矛盾的激化，层层深入的戏剧冲突紧凑合理，使得该片对比诸多科幻片更具感情和信服力，也给观众带来深层次的思考。";
		r12.score = "9.2";
		r12.src_path = "YOD url";
		r12.length = "130分钟";
		r12.language = "英语中字";
		r12.source = "YOD";
		r12.source_id = "1234";
		
//		map.put(Requester.RIA_INTERFACE_MEDIA_MOVIEINFO, r12);
		
		//2.1.13 获取电子书列表
		GsonResponseObject.mediaListResp r13 = new GsonResponseObject.mediaListResp();
		r13.status = "0";
		r13.isNextPage = "1";
		
		r13.list = new GsonResponseObject.mediaElem[1];
		r13.list[0] = new GsonResponseObject.mediaElem();

		r13.list[0].media_id = "123";
		r13.list[0].img_path = HTTP_HOST_PREFIX + "/book_list/u1.jpg";
		r13.list[0].introduction = "史蒂芬·霍金的《时间简史》自1988年首版以来的岁月里，已成为全球科学著作的里程碑。它被翻译成40种文字，销售了近1000万册，成为国际出版史上的奇观。插图本全面更新了原书的内容，把许多观测揭示的新知识，以及霍金最新的研究纳入该书，并配以大量（250幅）照片和电脑制作的三维和四维空间图。霍金曾不无得意地引用评论者的话说道：“我关于物理的著作比麦当娜关于性的书还更畅销。”不知道这个插图版本会使原来已经非常巨大的销售数字“膨胀”多少？ ";
		r13.list[0].tag = "Mona推荐";
//		map.put(Requester.RIA_INTERFACE_MEDIA_BOOKLIST, r13);
		
		//2.1.14 获取电子书详情
		GsonResponseObject.mediaDetailInfoResp r14 =new GsonResponseObject.mediaDetailInfoResp();
		r14.status = "0";
		r14.name = "时间简史";
		r14.author = "霍金";
		r14.img_path = HTTP_HOST_PREFIX + "/book_list/u1.jpg";
		r14.details = "史蒂芬·霍金的《时间简史》自1988年首版以来的岁月里，已成为全球科学著作的里程碑。它被翻译成40种文字，销售了近1000万册，成为国际出版史上的奇观。插图本全面更新了原书的内容，把许多观测揭示的新知识，以及霍金最新的研究纳入该书，并配以大量（250幅）照片和电脑制作的三维和四维空间图。霍金曾不无得意地引用评论者的话说道：“我关于物理的著作比麦当娜关于性的书还更畅销。”不知道这个插图版本会使原来已经非常巨大的销售数字“膨胀”多少？";
		r14.label =new String[]{"物理", "天文", "国外"};
//		map.put(Requester.RIA_INTERFACE_MEDIA_BOOKINFO, r14);
		
		//2.1.15 获取音乐列表
		GsonResponseObject.musicListResp r15 = new GsonResponseObject.musicListResp();
		r15.status = "0";
		r15.musicalumb = new GsonResponseObject.MusicAlumb();
		r15.musicalumb.title = "姚贝娜de专辑";
		r15.musicalumb.img_path = HTTP_HOST_PREFIX + "/music_list/yaobeina/u1.jpg";
		r15.musicalumb.alumblist = new GsonResponseObject.MusicElem[8];
		for(int i=0; i<8; i++){
			r15.musicalumb.alumblist[i] = new GsonResponseObject.MusicElem();
		}
		
		r15.musicalumb.alumblist[0].media_id = "2211";
		r15.musicalumb.alumblist[0].name = "也许在";
		r15.musicalumb.alumblist[0].content = "姚贝娜";
		r15.musicalumb.alumblist[0].src_path = HTTP_HOST_PREFIX + "/music_list/yaobeina/yexuzai.mp3";
		r15.musicalumb.alumblist[0].source = "ttpod";
		r15.musicalumb.alumblist[0].source_id = "123";
		
		r15.musicalumb.alumblist[1].media_id = "2212";
		r15.musicalumb.alumblist[1].name = "也许明天";
		r15.musicalumb.alumblist[1].content = "姚贝娜";
		r15.musicalumb.alumblist[1].src_path = HTTP_HOST_PREFIX + "/music_list/yaobeina/yexumingtian.mp3";
		r15.musicalumb.alumblist[1].source = "ttpod";
		r15.musicalumb.alumblist[1].source_id = "123";
		
		r15.musicalumb.alumblist[2].media_id = "2213";
		r15.musicalumb.alumblist[2].name = "惊鸿舞";
		r15.musicalumb.alumblist[2].content = "姚贝娜";
		r15.musicalumb.alumblist[2].src_path = HTTP_HOST_PREFIX + "/music_list/yaobeina/jinhongwu.mp3";
		r15.musicalumb.alumblist[2].source = "yod";
		r15.musicalumb.alumblist[2].source_id = "123";
		
		r15.musicalumb.alumblist[3].media_id = "2214";
		r15.musicalumb.alumblist[3].name = "红颜劫";
		r15.musicalumb.alumblist[3].content = "姚贝娜";
		r15.musicalumb.alumblist[3].src_path = HTTP_HOST_PREFIX + "/music_list/yaobeina/hongyanjie.mp3";
		r15.musicalumb.alumblist[3].source = "yod";
		r15.musicalumb.alumblist[3].source_id = "123";
		
		r15.musicalumb.alumblist[4].media_id = "2215";
		r15.musicalumb.alumblist[4].name = "菩萨蛮";
		r15.musicalumb.alumblist[4].content = "姚贝娜";
		r15.musicalumb.alumblist[4].src_path = HTTP_HOST_PREFIX + "/music_list/yaobeina/pushamang.mp3";
		r15.musicalumb.alumblist[4].source = "yod";
		r15.musicalumb.alumblist[4].source_id = "123";
		
		r15.musicalumb.alumblist[5].media_id = "2216";
		r15.musicalumb.alumblist[5].name = "采莲";
		r15.musicalumb.alumblist[5].content = "姚贝娜";
		r15.musicalumb.alumblist[5].src_path = HTTP_HOST_PREFIX + "/music_list/yaobeina/cailnian.mp3";
		r15.musicalumb.alumblist[5].source = "ddd";
		r15.musicalumb.alumblist[5].source_id = "123";
		
		r15.musicalumb.alumblist[6].media_id = "2217";
		r15.musicalumb.alumblist[6].name = "金镂衣";
		r15.musicalumb.alumblist[6].content = "姚贝娜";
		r15.musicalumb.alumblist[6].src_path = HTTP_HOST_PREFIX + "/music_list/yaobeina/jinlouyi.mp3";
		r15.musicalumb.alumblist[6].source = "ddd";
		r15.musicalumb.alumblist[6].source_id = "123";
		
		r15.musicalumb.alumblist[7].media_id = "2218";
		r15.musicalumb.alumblist[7].name = "随他吧";
		r15.musicalumb.alumblist[7].content = "姚贝娜";
		r15.musicalumb.alumblist[7].src_path = HTTP_HOST_PREFIX + "/music_list/yaobeina/suitaba.mp3";
		r15.musicalumb.alumblist[7].source = "ddd";
		r15.musicalumb.alumblist[7].source_id = "123";
		
		r15.list = new GsonResponseObject.MusicElem[6];
		r15.list[0] = new GsonResponseObject.MusicElem();
		r15.list[1] = new GsonResponseObject.MusicElem();
		r15.list[2] = new GsonResponseObject.MusicElem();
		r15.list[3] = new GsonResponseObject.MusicElem();
		r15.list[4] = new GsonResponseObject.MusicElem();
		r15.list[5] = new GsonResponseObject.MusicElem();
				
		r15.list[0].media_id = "1234";
		r15.list[0].name = "感知成长的神奇";
		r15.list[0].content = "孙俪";
		r15.list[0].src_path = HTTP_HOST_PREFIX + "/music_list/sunli/gzczdsq.mp3";
		r15.list[0].img_path = new String[]{HTTP_HOST_PREFIX + "/music_list/sunli/u1.jpg", HTTP_HOST_PREFIX + "/music_list/sunli/u2.jpg", HTTP_HOST_PREFIX + "/music_list/sunli/u3.jpg", HTTP_HOST_PREFIX + "/music_list/sunli/u4.jpg"};
		r15.list[0].source = "ttpod";
		r15.list[0].source_id = "123";
		
		r15.list[1].media_id = "1235";
		r15.list[1].name = "红豆";
		r15.list[1].content = "王菲";
		r15.list[1].src_path = HTTP_HOST_PREFIX + "/music_list/wangfei/hongdou.mp3";
		r15.list[1].img_path = new String[]{HTTP_HOST_PREFIX + "/music_list/wangfei/u1.jpg", HTTP_HOST_PREFIX + "/music_list/wangfei/u2.jpg", HTTP_HOST_PREFIX + "/music_list/wangfei/u3.jpg", HTTP_HOST_PREFIX + "/music_list/wangfei/u4.jpg"};
		r15.list[1].source = "ttpod";
		r15.list[1].source_id = "123";
		
		r15.list[2].media_id = "1236";
		r15.list[2].name = "小苹果";
		r15.list[2].content = "筷子兄弟";
		r15.list[2].src_path = HTTP_HOST_PREFIX + "/music_list/kuaizixiongdi/xiaopingguo.mp3";
		r15.list[2].img_path = new String[]{HTTP_HOST_PREFIX + "/music_list/kuaizixiongdi/u1.jpg", HTTP_HOST_PREFIX + "/music_list/kuaizixiongdi/u2.jpg", HTTP_HOST_PREFIX + "/music_list/kuaizixiongdi/u3.jpg", HTTP_HOST_PREFIX + "/music_list/kuaizixiongdi/u4.jpg"};
		r15.list[2].source = "ttpod";
		r15.list[2].source_id = "123";
		
		r15.list[3].media_id = "1237";
		r15.list[3].name = "海阔天空";
		r15.list[3].content = "黄家驹";
		r15.list[3].src_path = HTTP_HOST_PREFIX + "/music_list/huangjiaju/haikuotiankong.mp3";
		r15.list[3].img_path = new String[]{HTTP_HOST_PREFIX + "/music_list/huangjiaju/u1.jpg", HTTP_HOST_PREFIX + "/music_list/huangjiaju/u2.jpg", HTTP_HOST_PREFIX + "/music_list/huangjiaju/u3.jpg", HTTP_HOST_PREFIX + "/music_list/huangjiaju/u4.jpg"};
		r15.list[3].source = "ttpod";
		r15.list[3].source_id = "123";
		
		r15.list[4].media_id = "1238";
		r15.list[4].name = "平凡之路";
		r15.list[4].content = "朴树";
		r15.list[4].src_path = HTTP_HOST_PREFIX + "/music_list/pushu/pingfanzhilu.mp3";
		r15.list[4].img_path = new String[]{HTTP_HOST_PREFIX + "/music_list/pushu/u1.jpg", HTTP_HOST_PREFIX + "/music_list/pushu/u2.jpg", HTTP_HOST_PREFIX + "/music_list/pushu/u3.jpg", HTTP_HOST_PREFIX + "/music_list/pushu/u4.jpg"};
		
		r15.list[5].media_id = "1239";
		r15.list[5].name = "女人花";
		r15.list[5].content = "梅艳芳";
		r15.list[5].src_path = HTTP_HOST_PREFIX + "/music_list/meiyanfang/nvrenhua.mp3";
		r15.list[5].img_path = new String[]{HTTP_HOST_PREFIX + "/music_list/meiyanfang/u1.jpg", HTTP_HOST_PREFIX + "/music_list/meiyanfang/u2.jpg", HTTP_HOST_PREFIX + "/music_list/meiyanfang/u3.jpg", HTTP_HOST_PREFIX + "/music_list/meiyanfang/u4.jpg"};
		
		
//		map.put(Requester.RIA_INTERFACE_MEDIA_MUSICLIST, r15);
		
		//2.1.16 获取笑话列表
		GsonResponseObject.mediaListResp r16 = new  GsonResponseObject.mediaListResp();
		r16.status = "0";
		r16.isNextPage = "1";
		
		r16.list = new GsonResponseObject.mediaElem[10];
		r16.list[0] = new GsonResponseObject.mediaElem();
		r16.list[1] = new GsonResponseObject.mediaElem();
		r16.list[2] = new GsonResponseObject.mediaElem();
		r16.list[3] = new GsonResponseObject.mediaElem();
		r16.list[4] = new GsonResponseObject.mediaElem();
		r16.list[5] = new GsonResponseObject.mediaElem();
		r16.list[6] = new GsonResponseObject.mediaElem();
		r16.list[7] = new GsonResponseObject.mediaElem();
		r16.list[8] = new GsonResponseObject.mediaElem();
		r16.list[9] = new GsonResponseObject.mediaElem();
		
		int id = 0;
		r16.list[0].img_path = HTTP_HOST_PREFIX + "/joke_list/u0.jpg";
		r16.list[0].name = "囧哥:准爸爸出现孕吐症状获产假";
		r16.list[0].media_id = "" + (++id);
		r16.list[0].has_audio = "0";
		r16.list[0].introduction = "英国伯明翰的准爸爸阿什比在未婚妻怀孕后，同样出现孕妇不适征状，日前更因晨吐太严重而获准放产假。阿什比说，自从未婚妻怀孕后，他开始出现怀孕症状，包括重了7磅、腹部胀起、背痛及胃口转变，变得会吃以前最讨厌的盐醋味薯片。";
		r16.list[0].source = "sina";
		
		r16.list[1].img_path = HTTP_HOST_PREFIX + "/joke_list/u1.jpg";
		r16.list[1].name = "囧哥:男子翘兰花指被路人打住院";
		r16.list[1].media_id = "" + (++id);
		r16.list[1].has_audio = "0";
		r16.list[1].introduction = "\"我也说不清楚，看那个男的那样就不顺眼，就想揍他。\"石某跟两个朋友吃完饭准备离开，看到受害人跷兰花指拨头发，\"看着不顺眼，心生厌恶\"，就带两个朋友对其一顿暴打。目前，石某已被行政拘留。";
		r16.list[1].source = "sina";
		
		r16.list[2].img_path = HTTP_HOST_PREFIX + "/joke_list/u2.jpg";
		r16.list[2].name = "囧哥:杀马特逆袭成名校教授";
		r16.list[2].media_id = "" + (++id);
		r16.list[2].has_audio = "0";
		r16.list[2].introduction = "USC计算机系的助理教授。新一代人走上岗位了啊。";
		r16.list[2].source = "sina";
		
		r16.list[3].img_path = HTTP_HOST_PREFIX + "/joke_list/u3.jpg";
		r16.list[3].name = "囧哥:疯狂粉丝与偶像纸板结婚";
		r16.list[3].media_id = "" + (++id);
		r16.list[3].has_audio = "1";
		r16.list[3].introduction = "美国25岁女子Lauren Adkins购买《暮光之城》男星罗伯特-帕丁森的人形纸板模型后，爱不释手，还自掏约2万人民币举办了婚礼，又买香槟及蛋糕招待亲友，更带\"帕丁森\"到洛杉矶度蜜月，她说，\"每个人为了心爱的男人都会愿意牺牲！\"";
		r16.list[3].source = "sina";
		
		r16.list[4].img_path = HTTP_HOST_PREFIX + "/joke_list/u4.jpg";
		r16.list[4].name = "囧哥：第28个充气娃娃消失了";
		r16.list[4].media_id = "" + (++id);
		r16.list[4].has_audio = "0";
		r16.list[4].introduction = "【太机智了：业主用充气娃娃维权】昨天，温州苍南陈女士经过苍南灵溪玉苍路时发现，一栋在建商品楼外侧站着27个女子似想跳楼，走近一看脸顿时红，都是充气娃娃！个个体态丰满露大腿，仅穿一件T恤。几分钟后警方赶到。原来这是楼盘28名小股东因2000多万股金无法讨回的维权之举。股东们买了27个充气娃娃挂到楼上，希望引起政府重视。";
		r16.list[4].source = "sina";
		
		r16.list[5].img_path = HTTP_HOST_PREFIX + "/joke_list/u5.jpg";
		r16.list[5].name = "囧哥:我想去挪威作个死";
		r16.list[5].media_id = "" + (++id);
		r16.list[5].has_audio = "0";
		r16.list[5].introduction = "【挪威监狱不够用 向邻居荷兰借】挪威监狱以\"待遇好\"而闻名，非暴力罪犯通常被关押在开放式监狱中，还能享受某些人身自由、从事工作等。";
		r16.list[5].source = "sina";
		
		r16.list[6].img_path = HTTP_HOST_PREFIX + "/joke_list/u6.jpg";
		r16.list[6].name = "囧哥:小胖墩变性成辣妹";
		r16.list[6].media_id = "" + (++id);
		r16.list[6].has_audio = "1";
		r16.list[6].introduction = "【泰国小胖墩变性成辣妹 网友：惊呆了】据泰国星暹传媒9月3日报道，近日，泰国社交网站上一名变性者博得媒体关注。报道称，该变性者之前一直以女孩身份出现在大众面前，日前该变性者在脸书上晒出自己变性前还是胖墩男时的照片，着实让人不敢相信……";
		r16.list[6].source = "sina";
		
		r16.list[7].img_path = HTTP_HOST_PREFIX + "/joke_list/u7.jpg";
		r16.list[7].name = "囧哥：苹果公司推出新Logo 向被泄露艳照明星道歉";
		r16.list[7].media_id = "" + (++id);
		r16.list[7].has_audio = "0";
		r16.list[7].introduction = "日前，包括詹妮弗劳伦斯在内的好莱坞上百名女星的艳照通过苹果icloud泄露。北京时间3日凌晨，苹果公司召开道歉会，CEO库克还在此次道歉会上介绍了苹果公司的新Logo。";
		r16.list[7].source = "sina";

		r16.list[8].img_path = HTTP_HOST_PREFIX + "/joke_list/u8.jpg";
		r16.list[8].name = "囧哥：日本大妈狂买厕所纸";
		r16.list[8].media_id = "" + (++id);
		r16.list[8].has_audio = "0";
		r16.list[8].introduction = "【日吁国民储厕纸 因供应地地震高发】日本政府呼吁国民储蓄厕纸，因为日本厕纸大约４０％由静冈县厂家生产，这一地区今后很可能会遭遇地震及海啸，一旦该地震，日本厕纸产量势必锐减。2011年\"311大地震\"和1970年代石油危机爆发后，日本都发生过民众因恐慌疯抢卫生纸的情况。";
		r16.list[8].source = "sina";
		
		r16.list[9].img_path = HTTP_HOST_PREFIX + "/joke_list/u9.jpg";
		r16.list[9].name = "囧哥：云备胎服务造福女神";
		r16.list[9].media_id = "" + (++id);
		r16.list[9].has_audio = "1";
		r16.list[9].introduction = "【大学生创业开启“云备胎”服务 获千万融资】“女神”与“备胎”的供需不平衡多年来一直困扰了我国百万年轻人。但这一问题的解决指日可待。中央财经大学的几名2014届毕业生提出了“云备胎”构想，并已获得千万元人民币的融资。在该设想中，众多备胎被联网，组成一个云，当女神有需求的时候，可以随时调用一个或几个备胎。调用完毕后，备胎还在云上，等待下一个女神的调用。业内人士称，该服务的出台将极大地缓解备胎市场的信息不对称。";
		r16.list[9].source = "sina";
		
//		map.put(Requester.RIA_INTERFACE_MEDIA_JOKELIST, r16);
		
		//2.1.17 获取笑话详情
		GsonResponseObject.JokDetailInfoResp r17 = new GsonResponseObject.JokDetailInfoResp();
		r17.status = "0";
		r17.media_id = "1";
		r17.src_path = "http://125.39.224.45:8080/xs/86358.html";
//		map.put(Requester.RIA_INTERFACE_MEDIA_JOKEINFO, r17);
		
		//2.1.18 获取推荐列表
		GsonResponseObject.recmmandListResp r18 = new GsonResponseObject.recmmandListResp();
		r18.status = "0";
		r18.list = new AlumbElem[4];
		int[] arrayint = {3,4,5,6};
		String[] tagName = {"Summer","蛇精嫚","彤彤","猴子请来的逗逼"};
//		String[] colors = {"#FF4976e8","#FFf36815","#FFf68135","#FF35186F","#FFE389AD"};
		String[] colors = {"1","2","3","4","5"};
		String[] imgs = {
				FakeData.HTTP_HOST_PREFIX + "/media_main/ban1.jpg",
				FakeData.HTTP_HOST_PREFIX + "/media_main/ban2.jpg",
				FakeData.HTTP_HOST_PREFIX + "/media_main/ban3.jpg",
				FakeData.HTTP_HOST_PREFIX + "/media_main/ban4.jpg",
		};
		
		int num = 0;
		for (int i = 0;i < 4;i++) {
			r18.list[i] = new AlumbElem();
			r18.list[i].periods = "十月  第" + (i + 1) + "期";
			r18.list[i].sublist = new SubAlumbElem[arrayint[i]];
			for (int j = 0;j < arrayint[i];j++) {
				r18.list[i].sublist[j] = new SubAlumbElem();
				r18.list[i].sublist[j].object_id = "" + (num + 1);
				r18.list[i].sublist[j].color = colors[(num + j)%5];
				r18.list[i].sublist[j].introduction = "本期节操小编 " + tagName[num % 4] + " 强烈推荐影片《银河护卫队》";
				r18.list[i].sublist[j].img_path = imgs[(num + i) % 4];
				r18.list[i].sublist[j].tag = tagName[num % 4] + " 推荐";
				num++;
			}
		}
//		map.put(Requester.RIA_INTERFACE_MEDIA_RECOMMANDLIST, r18);
		
		//2.1.19 获取推荐详情
		GsonResponseObject.recmmandInfoResp r19 = new GsonResponseObject.recmmandInfoResp();
		r19.status = "0";
		r19.object_id = "1";
		r19.title = "带你看史上最NB动漫合集";
		r19.list = new recommandDetail[3];
		r19.list[0] = new recommandDetail();
		r19.list[0].content = "《千与千寻》你我都有过的梦境：\n" 
				+"我的一个朋友每看到在花丛下白给千寻吃他用魔法做的饭团那一段就泣不成声。很多人不明白，但是我并不奇怪，因为我也是一样。 也许没有童年创伤的孩子不会明白那一段的感人之处。";
		r19.list[0].img_path = FakeData.HTTP_HOST_PREFIX + "/media_main/ban1.jpg";
		r19.list[0].type = "2";
		
		r19.list[1] = new recommandDetail();
		r19.list[1].content = "《攻壳机动队》无法逾越的巅峰之作：\n"
				+"在1995年的这部作品，让习惯于消遣型或者娱乐性动漫的人们无所适从。而2004年《Innocence》更是将押井守的思想发挥到了极致，进入了嘎纳竞赛单元，创动画片之先河！";
		r19.list[1].img_path = FakeData.HTTP_HOST_PREFIX + "/media_main/ban2.jpg";
		r19.list[1].type = "2";
		
		r19.list[2] = new recommandDetail();
		r19.list[2].content = "《千与千寻》你我都有过的梦境：\n" 
				+"我的一个朋友每看到在花丛下白给千寻吃他用魔法做的饭团那一段就泣不成声。很多人不明白，但是我并不奇怪，因为我也是一样。 也许没有童年创伤的孩子不会明白那一段的感人之处。";
		r19.list[2].img_path = FakeData.HTTP_HOST_PREFIX + "/media_main/ban3.jpg";
		r19.list[2].src_path = "http://news.jiecao.fm/client/article/detail.htm?v=2.6.2&id=5vPr0aeY&down=true";
		r19.list[2].type = "4";
//		map.put(Requester.RIA_INTERFACE_MEDIA_RECOMMANDINFO, r19);
		
		//2.1.20 获取推荐人推荐内容列表
		GsonResponseObject.recmmandContentListResp r20 = new GsonResponseObject.recmmandContentListResp();
//		map.put(Requester.RIA_INTERFACE_MEDIA_RECOMMANDCONTENTLIST, r20);
		
		//2.1.22 获取旅游线路列表
		//http://lvyou.baidu.com/scene/a-xinjiang###
		GsonResponseObject.travelLineListResp r22 = new GsonResponseObject.travelLineListResp();
		r22.status = "0";
		r22.current_ciry = "全部";
		r22.name = new String[]{"全部", "北京", "武汉", "广州"};
		r22.piclist = new GsonResponseObject.LineC[6];
		r22.piclist[0] = new GsonResponseObject.LineC();
		r22.piclist[1] = new GsonResponseObject.LineC();
		r22.piclist[2] = new GsonResponseObject.LineC();
		r22.piclist[3] = new GsonResponseObject.LineC();
		r22.piclist[4] = new GsonResponseObject.LineC();
		r22.piclist[5] = new GsonResponseObject.LineC();
/*		r22.piclist[3] = new GsonResponseObject.LineC();
		r22.piclist[4] = new GsonResponseObject.LineC();
		r22.piclist[5] = new GsonResponseObject.LineC();
		r22.piclist[6] = new GsonResponseObject.LineC();
		r22.piclist[7] = new GsonResponseObject.LineC();*/
		
		r22.piclist[0].img_path = HTTP_HOST_PREFIX + "/travel_list/ic_u1.jpg";
		r22.piclist[0].line_id = "1243";
		
		r22.piclist[1].img_path = HTTP_HOST_PREFIX + "/travel_list/ic_u2.jpg";
		r22.piclist[1].line_id = "1245";
		
		r22.piclist[2].img_path = HTTP_HOST_PREFIX + "/travel_list/ic_u3.jpg";
		r22.piclist[2].line_id = "1247";
		
		r22.piclist[3].img_path = HTTP_HOST_PREFIX + "/travel_list/ic_u4.jpg";
		r22.piclist[3].line_id = "1249";
		
		r22.piclist[4].img_path = HTTP_HOST_PREFIX + "/travel_list/ic_u5.jpg";
		r22.piclist[4].line_id = "1250";
		
		r22.piclist[5].img_path = HTTP_HOST_PREFIX + "/travel_list/ic_u6.jpg";
		r22.piclist[5].line_id = "1246";
		
		r22.linelist = new GsonResponseObject.LineInfo[8];
		r22.linelist[0] = new GsonResponseObject.LineInfo();
		r22.linelist[1] = new GsonResponseObject.LineInfo();
		r22.linelist[2] = new GsonResponseObject.LineInfo();
		r22.linelist[3] = new GsonResponseObject.LineInfo();
		r22.linelist[4] = new GsonResponseObject.LineInfo();
		r22.linelist[5] = new GsonResponseObject.LineInfo();
		r22.linelist[6] = new GsonResponseObject.LineInfo();
		r22.linelist[7] = new GsonResponseObject.LineInfo();
		
		r22.linelist[0].line_id = "1243";
		r22.linelist[0].tag = "中国铁道旅行社推荐";
		r22.linelist[0].color = "1";
		r22.linelist[0].name = "北京-乌鲁木齐";
		r22.linelist[0].introduction = "纯净的天山天池，灿烂欢笑的维吾尔族姑娘，激情的乌鲁木齐为你打开走向西域的大门。";
		r22.linelist[0].img_path = HTTP_HOST_PREFIX + "/travel_list/line_u1.jpg";
		
		r22.linelist[1].line_id = "1244";
		r22.linelist[1].tag = "中国铁道旅行社推荐";
		r22.linelist[1].name = "北京-吐鲁番";
		r22.linelist[1].color = "2";
		r22.linelist[1].introduction = "吐鲁番像一个宝盆，装满了甜蜜的葡萄和美景。";
		r22.linelist[1].img_path = HTTP_HOST_PREFIX + "/travel_list/line_u2.jpg";
		
		r22.linelist[2].line_id = "1245";
		r22.linelist[2].tag = "中国铁道旅行社推荐";
		r22.linelist[2].name = "北京-喀纳斯";
		r22.linelist[2].color = "3";
		r22.linelist[2].introduction = "喀纳斯是世界少有的“人间净土”，集冰川、湖泊、森林、草原、牧场等于一体。";
		r22.linelist[2].img_path = HTTP_HOST_PREFIX + "/travel_list/line_u3.jpg";
		
		r22.linelist[3].line_id = "1246";
		r22.linelist[3].tag = "中国铁道旅行社推荐";
		r22.linelist[3].name = "北京-天山";
		r22.linelist[3].color = "4";
		r22.linelist[3].introduction = "天山是新疆著名的旅游胜地，洁白的雪峰、翠绿的云杉倒映湖中，构成了一幅美丽的图画。";
		r22.linelist[3].img_path = HTTP_HOST_PREFIX + "/travel_list/line_u4.jpg";
		
		r22.linelist[4].line_id = "1247";
		r22.linelist[4].tag = "中国铁道旅行社推荐";
		r22.linelist[4].name = "北京-克拉玛依";
		r22.linelist[4].color = "5";
		r22.linelist[4].introduction = "克拉玛依群楼林立，道路宽敞；流水潺潺，绿树成茵，一座四季皆宜的旅游名城。";
		r22.linelist[4].img_path = HTTP_HOST_PREFIX + "/travel_list/line_u5.jpg";
		
		r22.linelist[5].line_id = "1248";
		r22.linelist[5].tag = "中国铁道旅行社推荐";
		r22.linelist[5].name = "北京-伊犁";
		r22.linelist[5].color = "1";
		r22.linelist[5].introduction = "不到新疆，不知中国之大；不到伊犁，不知新疆之美。她的“绿”，在万顷荒漠中愈加显得绚丽耀眼，令人神弛。";
		r22.linelist[5].img_path = HTTP_HOST_PREFIX + "/travel_list/line_u6.jpg";
		
		r22.linelist[6].line_id = "1249";
		r22.linelist[6].tag = "中国铁道旅行社推荐";
		r22.linelist[6].name = "北京-魔鬼城";
		r22.linelist[6].color = "2";
		r22.linelist[6].introduction = "形态各异的风蚀岩群，形状或如殿、台、阁、堡，或如人、禽、兽、畜。作为奥斯卡影片《卧虎藏龙》的外景地。";
		r22.linelist[6].img_path = HTTP_HOST_PREFIX + "/travel_list/line_u7.jpg";
		
		r22.linelist[7].line_id = "1250";
		r22.linelist[7].name = "北京-喀什";
		r22.linelist[7].tag = "中国铁道旅行社推荐";
		r22.linelist[7].color = "3";
		r22.linelist[7].introduction = "这里是 “瓜果之乡”，这里有“冰山之父”，不到喀什，就不算到新疆！能歌善舞、热情好客的维吾尔族会带给...";
		r22.linelist[7].img_path = HTTP_HOST_PREFIX + "/travel_list/line_u8.jpg";
//		map.put(Requester.RIA_INTERFACE_TRAVEL_LINELIST, r22);
		
		//2.1.23 旅游线路详情
		GsonResponseObject.travelLineInfoResp r23 = new GsonResponseObject.travelLineInfoResp();
		r23.status = "0";
		r23.adult_price = "1208";
		r23.kid_price = "808";
		r23.attention = "★本线路行程含有购物安排和自费项目，如需预订需签署补充协议。";
		r23.date = "2014-12-27";
		r23.fullname = "北京出发的伊犁那拉提草原风光+丝绸之路";
		r23.in_img_path =  HTTP_HOST_PREFIX + "/travel_list/line_u6.jpg";
		r23.linepoint = "★该产品是新疆魅力之旅经典线路产品之一，客人用10天的时间不仅可以参观游览丝绸之路上最著名的景点，还有新疆著名景区天山天池，吐鲁番，并且还可游览世界四大草原之一的那拉提草原，一睹迷人的伊犁河谷。该产品还可以根据往返交通演变为双飞、双卧等多种标准，方便客人的选择。";
		r23.name = "北京-伊犁";
		r23.notice = "★该产品是新疆魅力之旅经典线路产品之一，客人用10天的时间不仅可以参观游览丝绸之路上最著名的景点，还有新疆著名景区天山天池，吐鲁番，并且还可游览世界四大草原之一的那拉提草原，一睹迷人的伊犁河谷。该产品还可以根据往返交通演变为双飞、双卧等多种标准，方便客人的选择。";
		r23.remind = "★该产品是新疆魅力之旅经典线路产品之一，客人用10天的时间不仅可以参观游览丝绸之路上最著名的景点\n★还有新疆著名景区天山天池，吐鲁番，并且还可游览世界四大草原之一的那拉提草原，一睹迷人的伊犁河谷。该产品还可以根据往返交通演变为双飞、双卧等多种标准，方便客人的选择。";
		r23.services = "★该产品是新疆魅力之旅经典线路产品之一，客人用10天的时间不仅可以参观游览丝绸之路上最著名的景点，还有新疆著名景区天山天池，吐鲁番，并且还可游览世界四大草原之一的那拉提草原，一睹迷人的伊犁河谷。该产品还可以根据往返交通演变为双飞、双卧等多种标准，方便客人的选择。";
		r23.startaddress = "北京";
		r23.tickets = "50";
		r23.tag = "中国铁道旅行社推荐";
		r23.color = "5";
		r23.introduction = "不到新疆，不知中国之大；不到伊犁，不知新疆之美。她的“绿”，在万顷荒漠中愈加显得绚丽耀眼，令人神弛。";
		r23.out_img_path = HTTP_HOST_PREFIX + "/travel_list/line_u8.jpg";
		r23.travellist = new GsonResponseObject.TravelElem[3];
		r23.travellist[0] = new GsonResponseObject.TravelElem();
		r23.travellist[1] = new GsonResponseObject.TravelElem();
		r23.travellist[2] = new GsonResponseObject.TravelElem();
		
		r23.travellist[0].address = "乌鲁木齐";
		r23.travellist[0].day = "第一天";
		r23.travellist[0].food = "自理";
		r23.travellist[0].introduction = "早乘车去赴清水河，乌鲁木齐 /奎屯/赛里木湖高速公路，途经兵团城市石河子、奎屯，感受兵团新貌！中午根据时间状况到精河县附近或高泉附近用餐（便餐自理） 午餐后途观精河县标志——精河敖包，后乘车赴国家级风景名胜区赛里木湖（门票自理约2小时。仅湖边停留照相留影用时约30分钟左右），后乘车经果子沟秀美风光，抵达清水河镇。";
		r23.travellist[0].hotel = "列车上";
		r23.travellist[1].address = "那拉提";
		r23.travellist[1].day = "第二天";
		r23.travellist[1].food = "自理";
		r23.travellist[1].introduction = "早餐后由清水河出发， 经过霍尔果斯边防检查站，需检查身份证，参观霍尔果斯口岸（如国门开放参观国门门票自理0元/人区间车30元/人。停留40分钟左右参观照相），后乘车赴历史上著名的新疆古代原始首府都城——惠远古城（门票85元/人含讲解费共三景点）伊犁将军府（用时约30分钟）惠远钟鼓楼（用时约30分钟）林则徐戍所（用时约50分钟），后到巴彦岱附近用中餐(约60分钟)，午餐后参观——伊犁河大桥（游览15分钟），后到馨美人薰衣草免费体验中国薰衣草之乡特色舒适感受——薰衣草精油泡脚（40分钟），晚到那拉提风景区，可参加那拉提盛大的仿古乌孙国迎娶细君公主大型篝火晚会（门票自理80元/人）";
		r23.travellist[1].hotel = "列车上";
		r23.travellist[1].img_list = new String[3];
		r23.travellist[1].img_list[0] = HTTP_HOST_PREFIX + "/travel_list/ic_u1.jpg";
		r23.travellist[1].img_list[1] = HTTP_HOST_PREFIX + "/travel_list/ic_u2.jpg";
		r23.travellist[1].img_list[2] = HTTP_HOST_PREFIX + "/travel_list/ic_u4.jpg";
		r23.travellist[2].address = "吐鲁番";
		r23.travellist[2].day = "第三天";
		r23.travellist[2].food = "吐鲁番太阳大饭店";
		r23.travellist[2].introduction = "乘车赴有火洲之称的吐鲁番，途径亚洲最大风力发电站——达坂城风力发电站，到吐鲁番服务大厅购票并参观丝路地毯厂（约40分钟）。游览吐鲁番与京杭大运河万里长城并称为中国古代的三大工程之一的坎儿井（40元，约30分钟）。游览交河故城（40元，约需40分钟）之后游览民间维吾尔风情—维吾尔古村（35元，约需30分钟）";
		r23.travellist[2].hotel = "列车上";
		r23.travellist[2].img_list = new String[6];
		r23.travellist[2].img_list[0] = HTTP_HOST_PREFIX + "/travel_list/ic_u1.jpg";
		r23.travellist[2].img_list[1] = HTTP_HOST_PREFIX + "/travel_list/ic_u2.jpg";
		r23.travellist[2].img_list[2] = HTTP_HOST_PREFIX + "/travel_list/ic_u3.jpg";
		r23.travellist[2].img_list[3] = HTTP_HOST_PREFIX + "/travel_list/ic_u4.jpg";
		r23.travellist[2].img_list[4] = HTTP_HOST_PREFIX + "/travel_list/ic_u5.jpg";
		r23.travellist[2].img_list[5] = HTTP_HOST_PREFIX + "/travel_list/ic_u6.jpg";
		r23.visanotice = "★本线路行程含有购物安排和自费项目，如需预订需签署补充协议\n★本线路行程含有购物安排和自费项目，如需预订需签署补充协议,本线路行程含有购物安排和自费项目，如需预订需签署补充协议\n★本线路行程含有购物安排和自费项目，如需预订需签署补充协议";
//		map.put(Requester.RIA_INTERFACE_TRAVEL_LINEINFO, r23);
		
		//2.1.24 旅游线路价格
		GsonResponseObject.travelLinePriceResp r24 = new GsonResponseObject.travelLinePriceResp();
		r24.status = "0";
		r24.lineprice = new GsonResponseObject.LinePriceElem[8];
		r24.lineprice[0] = new GsonResponseObject.LinePriceElem();
		r24.lineprice[0].date = "1417622400105";
		r24.lineprice[0].adult_price = "1208";
		r24.lineprice[0].kid_price = "808";
		
		r24.lineprice[1] = new GsonResponseObject.LinePriceElem();
		r24.lineprice[1].date = "1417968000703";
		r24.lineprice[1].adult_price = "1208";
		r24.lineprice[1].kid_price = "808";
		
		r24.lineprice[2] = new GsonResponseObject.LinePriceElem();
		r24.lineprice[2].date = "1418227200670";
		r24.lineprice[2].adult_price = "1208";
		r24.lineprice[2].kid_price = "808";
		
		r24.lineprice[3] = new GsonResponseObject.LinePriceElem();
		r24.lineprice[3].date = "1418572800155";
		r24.lineprice[3].adult_price = "1208";
		r24.lineprice[3].kid_price = "808";
		
		r24.lineprice[4] = new GsonResponseObject.LinePriceElem();
		r24.lineprice[4].date = "1418832000349";
		r24.lineprice[4].adult_price = "1208";
		r24.lineprice[4].kid_price = "808";
		
		r24.lineprice[5] = new GsonResponseObject.LinePriceElem();
		r24.lineprice[5].date = "1419177600721";
		r24.lineprice[5].adult_price = "1208";
		r24.lineprice[5].kid_price = "808";
		
		r24.lineprice[6] = new GsonResponseObject.LinePriceElem();
		r24.lineprice[6].date = "1419436800712";
		r24.lineprice[6].adult_price = "1208";
		r24.lineprice[6].kid_price = "808";
		
		r24.lineprice[7] = new GsonResponseObject.LinePriceElem();
		r24.lineprice[7].date = "1419782400465";
		r24.lineprice[7].adult_price = "1208";
		r24.lineprice[7].kid_price = "808";
		
//		map.put(Requester.RIA_INTERFACE_TRAVEL_LINEPRICE, r24);
		
		//2.1.25 支付成功显示接口
		GsonResponseObject.travelPayShowResp r25 = new GsonResponseObject.travelPayShowResp();
		r25.status = "0";
		r25.sign_address = "北京西站二楼大厅第2售票口";
		r25.hotline = "010-82801830";
		r25.complaints_hotline = "010-82812315";
		
		r25.recommendlist = new GsonResponseObject.recommendLineElem[5];
		r25.recommendlist[0] = new GsonResponseObject.recommendLineElem();
		r25.recommendlist[1] = new GsonResponseObject.recommendLineElem();
		r25.recommendlist[2] = new GsonResponseObject.recommendLineElem();
		r25.recommendlist[3] = new GsonResponseObject.recommendLineElem();
		r25.recommendlist[4] = new GsonResponseObject.recommendLineElem();
		
		r25.recommendlist[0].line_id = "1243";
		r25.recommendlist[0].img_path = HTTP_HOST_PREFIX + "/travel_list/ic_u1.jpg";
		r25.recommendlist[1].line_id = "1248";
		r25.recommendlist[1].img_path = HTTP_HOST_PREFIX + "/travel_list/ic_u2.jpg";
		r25.recommendlist[2].line_id = "1247";
		r25.recommendlist[2].img_path = HTTP_HOST_PREFIX + "/travel_list/ic_u3.jpg";
		r25.recommendlist[3].line_id = "1249";
		r25.recommendlist[3].img_path = HTTP_HOST_PREFIX + "/travel_list/ic_u4.jpg";
		r25.recommendlist[4].line_id = "1250";
		r25.recommendlist[4].img_path = HTTP_HOST_PREFIX + "/travel_list/ic_u5.jpg";
		
//		map.put(Requester.RIA_INTERFACE_TRAVEL_PAYOK_SHOW, r25);

		
		//2.1.26 支付请求服务器
		GsonResponseObject.travePayResp r26 = new GsonResponseObject.travePayResp();
		r26.status = "0";
		r26.order_no = "" + System.currentTimeMillis();
//		map.put(Requester.RIA_INTERFACE_TRAVEL_PAY, r26);
		
		//2.1.27 请求服务器确认支付成功
		GsonResponseObject.payConfirmResp r27 = new GsonResponseObject.payConfirmResp();
		r27.status = "0";
		r27.sign_address = "北京西站二楼大厅第2售票口";
		r27.hotline = "010-82801830";
		r27.complaints_hotline = "010-82812315";
		
		r27.recommendlist = new GsonResponseObject.recommendLineElem[3];
		r27.recommendlist[0] = new GsonResponseObject.recommendLineElem();
		r27.recommendlist[1] = new GsonResponseObject.recommendLineElem();
		r27.recommendlist[2] = new GsonResponseObject.recommendLineElem();
		
		r27.recommendlist[0].line_id = "1243";
		r27.recommendlist[0].img_path = HTTP_HOST_PREFIX + "/travel_list/line_u8.jpg";
		r27.recommendlist[1].line_id = "1248";
		r27.recommendlist[1].img_path = HTTP_HOST_PREFIX + "/travel_list/line_u6.jpg";
		r27.recommendlist[2].line_id = "1247";
		r27.recommendlist[2].img_path = HTTP_HOST_PREFIX + "/travel_list/line_u5.jpg";
//		map.put(Requester.RIA_INTERFACE_TRAVEL_PAYCONFIRM, r27);
		
		GsonResponseObject.feedbacklistResp r29 = new GsonResponseObject.feedbacklistResp();
		r29.status = "0";
		r29.feedbacklist = new feedBackElem[4];
		r29.feedbacklist[0] = new feedBackElem();
		r29.feedbacklist[0].feedbacktypeid = "1";
		r29.feedbacklist[0].name = "内容太少";
		
		r29.feedbacklist[1] = new feedBackElem();
		r29.feedbacklist[1].feedbacktypeid = "2";
		r29.feedbacklist[1].name = "分类不清";
		
		r29.feedbacklist[2] = new feedBackElem();
		r29.feedbacklist[2].feedbacktypeid = "3";
		r29.feedbacklist[2].name = "操作不方便";
		
		r29.feedbacklist[3] = new feedBackElem();
		r29.feedbacklist[3].feedbacktypeid = "4";
		r29.feedbacklist[3].name = "播放时卡慢";
//		map.put(Requester.RIA_INTERFACE_FEEDBACK_LIST, r29);
		
		GsonResponseObject.commonContent r30 = new GsonResponseObject.commonContent();
		r30.status = "0";
		r30.content = "提交成功";
//		map.put(Requester.RIA_INTERFACE_FEED_BACK, r30);
		
		//2.1.31 满意度条目列表接口
		GsonResponseObject.surveylistResp r31 = new GsonResponseObject.surveylistResp();
		r31.status = "0";
		r31.surveylist = new surveySubElem[6];
		r31.surveylist[0] = new surveySubElem();
		r31.surveylist[0].surveytypeid = "0";
		r31.surveylist[0].name = "乘务员服务态度";
		
		r31.surveylist[1] = new surveySubElem();
		r31.surveylist[1].surveytypeid = "1";
		r31.surveylist[1].name = "卫生间整洁度";
		
		r31.surveylist[2] = new surveySubElem();
		r31.surveylist[2].surveytypeid = "2";
		r31.surveylist[2].name = "商品送达时间";
		
		r31.surveylist[3] = new surveySubElem();
		r31.surveylist[3].surveytypeid = "3";
		r31.surveylist[3].name = "所在车厢保洁频率";
		
		r31.surveylist[4] = new surveySubElem();
		r31.surveylist[4].surveytypeid = "4";
		r31.surveylist[4].name = "环境满意度";
		
		r31.surveylist[5] = new surveySubElem();
		r31.surveylist[5].surveytypeid = "5";
		r31.surveylist[5].name = "舒适满意度";
//		map.put(Requester.RIA_INTERFACE_SURVEY_LIST, r31);
		
		//2.1.32 调查满意度接口
		GsonResponseObject.surveyResp r32 = new GsonResponseObject.surveyResp();
		r32.status = "0";
//		map.put(Requester.RIA_INTERFACE_SURVEY, r32);
		
		//2.1.38 商品提交订单接口
		GsonResponseObject.GoodOrderResp r38 = new GsonResponseObject.GoodOrderResp();
		r38.status = "0";
//		map.put(Requester.RIA_INTERFACE_GOOD_ORDER, r38);
		
		GsonResponseObject.OrderStatusResp r40 = new GsonResponseObject.OrderStatusResp();
		r40.status = "0";
//		map.put(Requester.RIA_INTERFACE_ORDER_STATUS, r40);
		
		
		
		
//		map.put(Requester.RIA_INTERFACE_DISCOVER, r43);
	}
	
	public static GsonResponseObject.DiscoverResp createDiscoverResp() {
		GsonResponseObject.DiscoverResp r43 = new GsonResponseObject.DiscoverResp();
		r43.status = "0";
		String [] types = {"1","2","3","5"};
		String [] imgPaths = {
				HTTP_HOST_PREFIX + "/service_main/ban1.jpg",
				HTTP_HOST_PREFIX + "/news_list/u" + 1 + ".jpg",
				HTTP_HOST_PREFIX + "/dinner_order/u10.jpg",
				HTTP_HOST_PREFIX + "/city_list/nanjing.jpg",
				HTTP_HOST_PREFIX + "/media_home/u4.jpg",
				HTTP_HOST_PREFIX + "/news_list/u" + 2 + ".jpg",
				HTTP_HOST_PREFIX + "/city_list/nanjing.jpg",
				HTTP_HOST_PREFIX + "/movie_list/u27.jpg",
				HTTP_HOST_PREFIX + "/movie_list/u13.jpg",
				HTTP_HOST_PREFIX + "/book_list/u1.jpg",
		};
		String [] content = {
				"为什么说，加拿大是世界上最nice的国家呢？",
				"博物馆奇妙夜",
				"无耻混蛋",
				"传统秘制东坡肉",
				"囧哥：苹果公司推出新Logo 向被泄露艳照明星道歉"
		};
		DiscoverElem [] list = new DiscoverElem[7];
		Random random = new Random();
		for (int i = 0;i < 7;i++) {
			int object_id = random.nextInt(30);
			list[i] = new DiscoverElem();
			list[i].object_id = "" + object_id;
			list[i].type = types[random.nextInt(4)];
			list[i].img_path = imgPaths[object_id%10];
			list[i].content = content[object_id%5];
			list[i].width = "" + (random.nextInt(3) + 1);
			list[i].height = "" + (random.nextInt(3) + 1);
		}
		
		r43.list = list;
		return r43;
	}

}
