package com.cmmobi.looklook.common.gson;

import java.util.Map;




public class WeiboResponse{
	
	/******************  sina ***********************/
	//sina getSWUserInfo response struct
	public class SWUserInfo{
		public long id;// :1668486934,
		public String idstr; //:"1668486934",
		public String screen_name; //"screen_name":"艾瑞克张伟",
		public String name; //"name":"艾瑞克张伟",
		public String province; //:"42",
		public String city; //:"1",
		public String location; //:"湖北 武汉",
		public String description; //:"",
		public String url;// :"",
		public String profile_image_url; //:"http://tp3.sinaimg.cn/1668486934/50/0/1",
		public String profile_url; //:"u/1668486934",
		public String domain; //:"",
		public String weihao; //:"",
		public String gender; //":"m",
		public int followers_count; //":10,
		public int friends_count; //:54,
		public int statuses_count; //:8,
		public int favourites_count; //:0,
		public String created_at; //:"Fri Aug 19 16:07:31 +0800 2011",
		public boolean following; //:false,
		public boolean allow_all_act_msg; //:false,
		public boolean geo_enabled; //:true,
		public boolean verified; //:false,
		public int verified_type; //:-1,
		public String remark; //:"",
		public statusSinaType status; //":{},
		public boolean allow_all_comment; //":true,
		public String avatar_large; //:"http://tp3.sinaimg.cn/1668486934/180/0/1",
		public String verified_reason; //:"",
		public boolean follow_me; //":false,
		public int online_status; //:0,
		public int bi_followers_count; //:0,
		public String lang; //:"zh-cn",
		public int star; //:0,
		public int mbtype; //:0,
		public int mbrank; //:0,
		public int block_word; //:0
	}

	public class SinaFriends{
		public sinaUser[] users; 
		int total_number; // 668
	}
	
	public class statusSinaType{

		public String created_at; //:"Mon Apr 08 12:08:39 +0800 2013",
		public long id; //:3564783666048996,
		public String mid; //:"3564783666048996",
		public String idstr; //:"3564783666048996",
		public String text; //:"looklook 分享",
		public String source; //:"<a href=\"http://app.weibo.com/t/feed/3k6tMZ\" rel=\"nofollow\">looklook视频社交</a>",
		public boolean favorited; //:false,
		public boolean truncated; //:false,
		public String in_reply_to_status_id; //:"",
		public String in_reply_to_user_id; //:"",
		public String in_reply_to_screen_name; //:"",
		public String[] pic_ids; //":["63731716jw1e3i2uz29ehj"],
		public String thumbnail_pic; //:"http://ww1.sinaimg.cn/thumbnail/63731716jw1e3i2uz29ehj.jpg",
		public String bmiddle_pic; //:"http://ww1.sinaimg.cn/bmiddle/63731716jw1e3i2uz29ehj.jpg",
		public String original_pic; //:"http://ww1.sinaimg.cn/large/63731716jw1e3i2uz29ehj.jpg",
		public String geo; //---------- :null,
		public int reposts_count; //:0,
		public int comments_count; //:0,
		public int attitudes_count; //:0,
		public int mlevel; //:0,
		public visibleSinaType visible; //:{		}
	}
	
	public class visibleSinaType{
		public int type; //:0,
		public int list_id; //:0
	}
	
	
	public class sinaStatus{
        String created_at; //: "Tue May 24 18:04:53 +0800 2011",
        long id; //: 11142488790,
        String text; //: "我的相机到了。",
        String  source; //: "<a href="http://weibo.com" rel="nofollow">新浪微博</a>",
        boolean favorited; //: false,
        boolean truncated; //: false,
        String  in_reply_to_status_id; //: "",
        String  in_reply_to_user_id; //: "",
        String  in_reply_to_screen_name;//: "",
        Object geo; //: null,
        String mid; //: "5610221544300749636",
        Object[] annotations; //: [],
        int reposts_count; //: 5,
        int comments_count; //: 8
        public sinaUser user;
	}
	
	public class sinaUser{
		
		    public long id ;//: 1404376560,
            public String screen_name; //: "zaku",
            public String name; //: "zaku",
            public String province; //: "11",
            public String city; //: "5",
            public String location ;// : "北京 朝阳区",
            public String description; //: "人生五十年，乃如梦如幻；有生斯有死，壮士复何憾。",
            public String url; //: "http://blog.sina.com.cn/zaku", 用户博客地址
            public String profile_image_url; //用户头像地址，50×50像素: "http://tp1.sinaimg.cn/1404376560/50/0/1",
            public String domain ;//: "zaku",
            public String gender; //: "m", 性别，m：男、f：女、n：未知
            public int followers_count; //: 1204,
            public int friends_count; //: 447,
            public int statuses_count; //: 2908,
            public int favourites_count; //: 0,
            public String created_at; //: "Fri Aug 28 00:00:00 +0800 2009",
            public boolean following; //: false,
            public  boolean allow_all_act_msg; //: false,
            public String remark ;//: "",
            public boolean geo_enabled; //: true,
            public boolean verified; //: false,
            public sinaStatus status; // 
            public boolean allow_all_comment; //: true,
            public String avatar_large; //: "http://tp1.sinaimg.cn/1404376560/180/0/1",
            public String verified_reason; //: "",
            public boolean follow_me; //: false,
            public int online_status; //: 0,
            public int bi_followers_count; //: 215
        
	}
	
	
	public class SinaComments{
		public sinComment[] comments;
		public int previous_cursor; //0
		public int next_cursor; //0
		public int total_number; //7
	}
	
	public class sinComment{
		public String created_at; //Wed Jun 01 00:50:25 +0800 2011
		public long id; //12438492184
		public String text; //love your work.......
		public String source; //
		public String mid; //"202110601896455629"
		public sinaUser user;
		public sinaStatus status;
	}
	
	public class SinaCountComment{
		long id; //: 3481474642286341,
		int comments; //: 114,
		int reposts; //: 1680,
		int attitudes;//: 0
	}
	
	/********************* tencent *********************/
	
	public class TWUserInfo{
		public dataTWType data; //:null,
		public detailinfoTWType detailerrinfo; //":{},
		public int errcode; //:38,
		public String msg; //:"check sign error",
		public int ret; //:3,
		public long seqid; //":5864722398957383372
	}
	
	public class TencentFriends{
		public tencentData data; //: {},
		public detailErrInfo detailerrinfo;

		int errcode; //: 0,
		String msg; //: "ok",
		int ret; //: 0,
		long seqid; //: 1366091765
	}
	
	public class TencentComments{
		public TencentCommentData data;
		int errcode;
		String msg; //ok
		int ret; //0
		long seqid; //1371787907
	}
	
	public class TencentCountComments{
		Map<String, Integer> data; // "149814067832659": 3
		int errcode; //0
		String msg; //ok
		int ret; //0
		long seqid; //1371796419
	}
	
	public class TencentCommentData{
		int hasnext; //1
		public TencentCommentInfo[] info;
		long timestamp;
		int totalnum;//3
		Object user;
	}
	
	public class TencentCommentInfo{
		String city_code; //"1"
		int count; //:0
		String country_code; //:"1"
		int emotiontype; //:0
		String emotionurl; //:""
		String from; //:"QQ",=
		String fromurl; //http://imqq.com\u000a
		String geo; //"",
		public String head; //:"http:\/\/app.qlogo.cn\/mbloghead\/e69cfcc231febd52384a",
		String https_head; //:"https:\/\/app.qlogo.cn\/mbloghead\/e69cfcc231febd52384a",
		public String id; //:"119392075083098",
		Object image; //:null,
		int isrealname; //:2,
		int isvip; //:0,
		String jing; //"0",
		String latitude; //"0",
		String location; //"中国 武汉",
		String longitude; //"0",
		int mcount; //1,
		Object music; //null,
		public String name; //"zhangweihust",
		public String nick; //"张伟",
		String openid; //"AF5C682D095047EBB48C68DE3B311E7C",
		String origtext; //"好久不见||@dscds555: 伟哥",
		String province_code;//:"42",
		int self; //:1,
		Object source; //:{},
		int status; //0,
		public String text; //"好久不见||@dscds555: 伟哥",
		public String timestamp; //:1350017027,
		int type; //:7,
		Object video; //:null,
		String wei; //0
	}
	


	public class dataTWType{
		public int birth_day; //:11,
		public int birth_month; //:2,
		public int birth_year; //:1984,
		public String city_code; //":"",
		public Object comp; // --------  ":null,
		public String country_code; //":"1",
		public Object edu; //-------------":null,
		public String email; //:"",
		public int exp; //":183,
		int fansnum; //":38,
		int favnum; //":0,
		public String head; //":"http:\/\/app.qlogo.cn\/mbloghead\/e69cfcc231febd52384a",
		String homecity_code; //:"",
		String homecountry_code; //:"",
		String homepage; //:"",
		String homeprovince_code; //:"",
		String hometown_code; //:"",
		String https_head; //:"https:\/\/app.qlogo.cn\/mbloghead\/e69cfcc231febd52384a",
		int idolnum; //:16,
		int industry_code; //:0,
		String introduction; //:"",
		int isent; //:0,
		int ismyblack; //:0,
		int ismyfans; //:0,
		int ismyidol; //:0,
		int isrealname; //:1,
		int isvip; //:0,
		int level; //:1,
		public  String location; //:"中国",
		int mutual_fans_num; //:2,
		public String name; //:"zhangweihust",
		public String nick; //":"张伟",
		public String openid; //:"CDF9D34B180B64ECCC2006B280DDAFBD",
		String province_code; //:"",
		long regtime; //:1304495806,
		int send_private_flag; //":2,
		public int sex; //0 uninit male:1, female:2
		Object tag; //-----------:null,
		public  tweetinfoTWType[] tweetinfo; //":[		{}		],
		int tweetnum; //:21,
		String verifyinfo; //":""

	}
	
	public class tweetinfoTWType{

		String city_code; //":"",
		String country_code; //":"1",
		int emotiontype; //":0,
		String emotionurl; //":"",
		String from; //:"微博开放平台",
		String fromurl; //http:\/\/wiki.open.t.qq.com\/index.php\/%E4%BA%A7%E5%93%81%E7%B1%BBFAQ#.E6.8F.90.E4.BA.A4.E5.BA.94.E7.94.A8.E6.9D.A5.E6.BA.90.E5.AD.97.E6.AE.B5.E5.AE.A1.E6.A0.B8.E8.83.BD.E5.BE.97.E5.88.B0.E4.BB.80.E4.B9.88.E5.A5.BD.E5.A4.84.EF.BC.9F\u000a
		String geo; //":"",
		String id; //:"220867065438720",
		String[] image;//":["http:\/\/app.qpic.cn\/mblogpic\/daa2e1e05613035ba494"],
		String latitude; //":"0",
		String location; //:"中国",
		String longitude; //:"0",
		Object music; //------------- :null,
		String origtext; //:"looklook 分享",
		String province_code; //":"11",
		int self; //":1,
		int status; //":0,
		String text; //":"looklook 分享",
		long timestamp; //:1365485778,
		int type; //:1,
     	Object video; //--------- ":null
		
	}
	
	public class detailinfoTWType{
	
		public String accesstoken; //":"",
		public String apiname; //":"weibo.user.info",
		public String appkey; //:"801209961",
		public String clientip; //:"211.157.169.10",
		public int cmd; //:0,
		public int proctime; //":0,
		public int ret1; //:3,
		public int ret2; //:3,
		public int ret3; //":38,
		public long ret4; //:562409738,
		public long timestamp; //":1365487091
		
		
	}

	
	

	
	public class tencentInfo{

		public int fansnum; //: 0,
        public String headurl; //: "",
        public String https_headurl ;//: "",
        public int idolnum; //: 0,
        public int isvip; //: 0,
        public String name; //: "nihaowangwebmaster",
        public String nick; //: "nihaowang",
        public String openid; //: "BE553B339B5618EF0831755B1648E7C1"
    
	}
	
	public class detailErrInfo{
		String accesstoken;
		String apiname; //"weibo.friends.mutual_list",
		String appkey; //"801209961",
		String clientip; //:"211.157.169.10",
		int cmd; //0,
		int proctime; //:0,
		int ret1; //:2,
		int ret2; //:2,
		int ret3; //:-10,
		long ret4; //:746684682,
		long timestamp; //:1366859300
	}
	
	public class tencentData{

        int curnum; //: 5,
        int hasnext; //: 1,
        public  tencentInfo[] info; 
        int nextstartpos; //: 5,
        int totalnum; //: 5
    
	}
	
	
	/******************** renren ******************************/
	public class RWUserInfo{
/*		public long uid; //int	表示用户id
		public String name; //"Ramos"
		public int sex; //int	表示性别，值1表示男性；值0表示女性
		public int star; //int	表示是否为星级用户，值“1”表示“是”；值“0”表示“不是”
		public int zidou; //int	表示是否为vip用户，值1表示是；值0表示不是
		public String birthday;
		public String email_hash;
		public String tinyurl; //string	表示头像链接 50*50大小
		public String mainurl; //string	表示头像链接 200*200大小
		public renrenHomeTown hometown_location;
		public renrenWorkHistroy[] work_history;
		public renrenUniversityHistory[] university_history;
		public renrenHsHistory[] hs_history;*/
		public RWUser response;
	}
	
	public class RWCommentsInfo{
		public RWComment[] response;
	}
	
	
	public class RWComment{
		public long id; //评论的ID
		public CommentType commentType; //评论的类型
		public long entryId; //被评论对象的ID
		public long entryOwnerId; //评论对象所有者的ID
		public long authorId; //评论者的ID
		public String content; //评论的内容
		public String time; //评论的时间
	}
	
	/**	Name	 Type	 Description
	 *  SHARE	枚举	          分享
	 *  ALBUM	枚举	          相册
	 *  BLOG	枚举	          日志
	 *  STATUS	枚举	          状态
	 *  PHOTO	枚举	          照片
	 */
	enum CommentType{SHARE, ALBUM, BLOG, STATUS, PHOTO} 
	
	//reren user model
	public class RWUser{
		public long id; //用户ID
		public String name; //用户名
		public Image[] avatar;
		public int star;
		public BasicInformation basicInformation;
		public School[] education;
		public Work[] work;
		public Like[] like;
		public EmotionalState emotionalState;
	}
	
	public class Like{
		public LikeCatagory catagory; //喜欢的类型
		public String name;  //喜欢的东西
	}
	
	public class School{
		public String name; //学校名称
		public String year; //入学日期
	}
	
	public class BasicInformation{
		public Sex sex;
		public String birthday;
		public HomeTown homeTown;
	}
	
	public class Image{
		public ImageSize size;
		public String url;
	}
	
	public class HomeTown{
		public String province;
		public String city;
	}
	
	public class Work{
		public String name; //公司名称
		public String time; //入职日期
		public Industry industry; //行业
		public Job job; //职位
	}
	
	public class Industry{
		public String industryCategory; //行业类别
		public String industryDetail; //行业详情
	}
	
	public class Job{
		public String jobCategory; //职位类别
		public String jobDetail; //职位详情
	}
	
	/**
	 * Name	 Type	 Description
	 * SPORT	枚举	运动
	 * MOVIE	枚举	电影
	 * CARTOON	枚举	动漫
	 * GAME	枚举	游戏
	 * MUSIC	枚举	音乐
	 * BOOK	枚举	书籍
	 * INTEREST	枚举	爱好
	 * */
	public enum LikeCatagory{SPORT, MOVIE, CARTOON, GAME, MUSIC, BOOK, INTEREST }
	
	/**
	 * Name	 Type	 Description
	 * DOCTOR	枚举	博士
	 * COLLEGE	枚举	本科
	 * GVY	枚举	校工
	 * PRIMARY	枚举	小学
	 * OTHER	枚举	其他
	 * TEACHER	枚举	教师
	 * MASTER	枚举	硕士
	 * HIGHSCHOOL	枚举	高中
	 * TECHNICAL	枚举	中专技校
	 * JUNIOR	枚举	初中
	 * SECRET	枚举	保密
	 * */
	public enum EmotionalState{ DOCTOR, COLLEGE, GVY, PRIMARY, OTHER, TEACHER, MASTER, HIGHSCHOOL, TECHNICAL, JUNIOR, SECRET}
	
	public enum Sex{ FEMALE, MALE}
	
	/**
	 * Name	 Type	 Description
	 * MAIN	枚举	200pt x 600pt
	 * TINY	枚举	50pt x 50pt
	 * LARGE	枚举	720pt x 720pt
	 * HEAD	枚举	100pt x 300pt
	 * */
	public enum ImageSize{ MAIN, TINY, LARGE, HEAD}
	
	
	public class renrenHomeTown{
		public String country;
		public String province;
		public String city;
	}
	
	public class renrenWorkHistroy{
		public String company_name; //"千橡",
		public String description; //"优秀的公司",
		public String start_date; //"2008-7-7",
		public String end_date; //""
	}
	
	public class renrenUniversityHistory{
		public String name; //" 北京第二外国语学院",
		public int year; //2003,
		public String department; //" 人文学院"
	}
	
	public class renrenHsHistory{
		public String name; //" 黑龙江省实验中学",
		public int grad_year; //:1999
	}
	
	
	//好友信息：
/*public class RenrenFriends{
		public Renrenfriend[] friend;
	}*/
	
	public class Renrenfriend{
/*		public int id; //:27740660, 表示好友的用户ID
		public String name; //:"Ramos",
		public String headurl; //:"...", 表示好友的头像
		public String tinyurl; //:"...",
		public String sex; //:"1", 表示好友的性别，值1表示男性；值0表示女性；值为空表示用户没有该信息
		public String headurl_with_logo; //:"http://.....", 带有校内logo的头像
		public String tinyurl_with_logo; //:"http://....." 带有校内logo的小头像
*/	
		public RWUser[] response;
	}
	
	public class RenrenPost{
		public int post_id; //:512787643 大于0表示新鲜事id，0表示失败
	}
	
	public static class RenrenSourceLink{
		public String text; //looklook视频分享
		public String href; //http://v.looklook.cn/ua/index.htm
	}
	
}