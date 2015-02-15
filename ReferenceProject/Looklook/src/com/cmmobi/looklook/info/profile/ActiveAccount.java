package com.cmmobi.looklook.info.profile;

import android.content.Context;
import android.util.Log;

import cn.jpush.android.api.JPushInterface;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyBind;
import com.cmmobi.looklook.common.gson.WeiboResponse.RWUserInfo;
import com.cmmobi.looklook.common.gson.WeiboResponse.SWUserInfo;
import com.cmmobi.looklook.common.gson.WeiboResponse.Sex;
import com.cmmobi.looklook.common.gson.WeiboResponse.TWUserInfo;
import com.cmmobi.looklook.common.storage.AsyncActiveAccountSaver;
import com.cmmobi.looklook.common.storage.StorageManager;
import com.cmmobi.looklook.networktask.NetworkTaskManager;
import com.cmmobi.sns.api.CmmobiSnsLib;
import com.cmmobi.sns.keep.AccessTokenKeeper;
import com.cmmobi.sns.oauthv2.OAuthV2;
import com.cmmobi.sns.utils.ConfigUtil.SHARE_TO;

/**
 * 
 * @author zhangwei
 * */
public class ActiveAccount {
	private static transient final String ActiveAccountKey = "ActiveAccountKey_LookLook";// 需要确保全局唯一性
	private static transient ActiveAccount ins = null;
	private static transient Context context = null;
	private transient String TAG = "ActiveAccount";

	/**********************************************************/
	// public boolean isLogin;//false logout true login
	/**
	 * 是looklook定义的id，不是snstype_snsid,也不是用来查找personalInfo的key，
	 * 
	 * key是 snstype_snsid
	 * 
	 * */
	private String userid; // 用户ID,若为空则说明没有登录

	public String snstype; // 0站内登录、1新浪微博、2人人微博、6腾讯微博
	public String logintype; //1邮箱登录，2手机号登录snstype=0有效
	
	//是否因密码多次输入错误而被强制登录？
	public boolean isForceLogin = false;  
	
	public String is_firstlogin ;

	// looklook user:
	public String username; // only looklook user valid
	public String password; // only looklook user valid

	// other weibo user:
	public String snsid; // 用这个微博登陆时记录下来

	// 在授权成功后，会获取个人信息，登录之前（没有userid）
	public String snsname;
	public String nickname;
	public String sns_head_pic; //首次登录时把微博头像记到这里面去
	public String sex;
	public String birthdate;
	public String address;
	public TWUserInfo twInfo;
	public SWUserInfo swInfo;
	public RWUserInfo rwInfo;

	// public RWUser rwInfo;
	/**********************************************************/

	private ActiveAccount() {
	}

	/**
	 * @author zhangwei {@literal 得到Account实例，会尝试从磁盘中加载数据信息}
	 * */
	public static ActiveAccount getInstance(Context c) {
		context = c;
		if (ins == null) {
			ActiveAccount a = (ActiveAccount) StorageManager.getInstance()
					.getItem(ActiveAccountKey, ActiveAccount.class);

			if (a != null) {
				ins = a;
			} else {
				ins = new ActiveAccount();
			}
		}
		return ins;
	}

	public boolean isLogin() {
		return (userid != null);
	}

	/**
	 * @author zhangwei {@literal 将Account数据保存磁盘，一般在onPause中调用}
	 * */
	public void persist() {
/*		StorageManager.getInstance().putItem(ActiveAccountKey, ins,
				ActiveAccount.class);*/
		AsyncActiveAccountSaver.getInstance().save(ActiveAccountKey, ins, ActiveAccount.class);
	}

	/**
	 * @author zhangwei {@literal 清空Account数据}
	 * */
	public void clean() {
		StorageManager.getInstance().deleteItem(ActiveAccountKey);
	}

	private void reset() {
		userid = null; // 用户ID,若为空则说明没有登录

		snstype = null; // 0站内登录、1新浪微博、6腾讯微博

		// looklook user:
		username = null; // only looklook user valid
		password = null; // only looklook user valid

		// other weibo user:
		snsid = null; // 用这个微博登陆时记录下来

		nickname = null;
		sex = null;
		birthdate = null;
		address = null;
		twInfo = null;
		swInfo = null;
		rwInfo = null;

		AccessTokenKeeper.tmp = null;
	}

	/**
	 * @author zhangwei {@literal 注销Account}
	 * */
	public void logout() {
		// 将snslib中的map清空

		if (userid != null && !userid.equals("")) {
			CmmobiSnsLib.getInstance(context).cleanup(context, userid);
			// stop all current network tasks
			NetworkTaskManager.getInstance(userid).pauseAllTask();
			NetworkTaskManager.getInstance(userid).resetTaskManager(userid);
			// save accountInfo
			AccountInfo.getInstance(userid).persist();
	        JPushInterface.stopPush(MainApplication.getAppInstance());
		} else {
			Log.e(TAG, "logout null - userid:" + userid + " snstype:" + snstype
					+ " snsid:" + snsid);
		}

		reset();

		persist();

	}

	/**
	 * {@literal 将注册成功得到的信息更新到对应用户的Account中}
	 * 
	 * @author zhangwei
	 * @param snstype 社区类型
	 * @param snsid 微博id
	 * @return true / false
	 * */
	public boolean updateRegister(boolean isPhoneNum, GsonResponse2.registerResponse obj) {
/*		if (obj == null || obj.userid == null) {
			return false;
		}

		if (snsid == null || snstype == null || snsid.equals("")
				|| snstype.equals("")) {
			// snsid是在授权成功后，回调设置过来的，若为空说明授权不成功
			return false;
		}
		
		if(isPhoneNum){
			this.logintype = "2";
		}else{
			this.logintype = "1";
		}

		
		this.snstype = "0";
		this.nickname = obj.nickname;
*/
		// String uid = obj.userid;

		// Account a = Account.getInstance(getApplicationContext());
		this.userid = obj.userid;

		AccountInfo uAI = getAccountInfo(userid);

		if (obj.nickname != null) {
			uAI.nickname = obj.nickname;
		}

		if (obj.headimageurl != null) {
			uAI.headimageurl = obj.headimageurl;
		}


		if (obj.sex != null) {
			uAI.sex = obj.sex;
		}

		if (obj.address != null) {
			uAI.address = obj.address;
		}

		if (obj.birthdate != null) {
			uAI.birthdate = obj.birthdate;
		}

		if (obj.signature != null) {
			uAI.signature = obj.signature;
		}

		if (obj.privmsg_type != null) {
			uAI.setmanager.setPrivmsg_type(obj.privmsg_type);
		}

		if (obj.friends_type != null) {
			uAI.setmanager.setFriends_type(obj.friends_type);
		}

		if (obj.diary_type != null) {
			uAI.setmanager.setDiary_type(obj.diary_type);
		}

		if (obj.app_downloadurl != null) {
			uAI.app_downloadurl = obj.app_downloadurl;
		}

		if (obj.position_type != null) {
			uAI.setmanager.setPosition_type(obj.position_type);
		}

		if (obj.audio_type != null) {
			uAI.setmanager.setAudio_type(obj.audio_type);
		}


		if (obj.launch_type != null) {
			uAI.setmanager.setLaunch_type(obj.launch_type);
		}

		if (obj.sysc_type != null) {
			uAI.setmanager.setSysc_type(obj.sysc_type);
		}


		/*
		 * if (obj.watermark != null) { uAI.watermark = obj.watermark; }
		 */

		if (AccessTokenKeeper.tmp != null) {
			uAI.updateAccessToken(AccessTokenKeeper.tmp);
		}

		// ok now put activeAccount's weibo info to AccountInfo
		if (swInfo != null) {
			uAI.swInfo = swInfo;
		}

		if (twInfo != null) {
			uAI.twInfo = twInfo;
		}

		if (rwInfo != null) {
			uAI.rwInfo = rwInfo;
		}

		// uAI.persist();

		persist();
		if (!snstype.equals("0")) {
			CmmobiSnsLib mSnsLib = CmmobiSnsLib.getInstance();
			// mSnsLib.postProcess(context, uid);
		}

		return true;
/*		if (snsid == null || snstype == null || snsid.equals("")
				|| snstype.equals("")) {
			// snsid是在授权成功后，回调设置过来的，若为空说明授权不成功
			return false;
		}

		if (obj == null || !obj.status.equals("0")) {
			return false;
		}

		if (obj.userid == null) {
			return false;
		}

		// String uid = snstype + "_" + snsid;

		// Account a = Account.getInstance(getApplicationContext());
		this.snstype = snstype;
		this.snsid = snsid;
		this.userid = obj.userid;

		AccountInfo uAI = getAccountInfo(this.userid);

		if (obj.nickname != null) {
			uAI.nickname = obj.nickname;
		}

		
		 * if (obj.portraiturl != null) { uAI.portraitUrl = obj.portraiturl; }
		 
		if (obj.headimageurl != null) {
			uAI.headimageurl = obj.headimageurl;
		}

		if (obj.address != null) {
			uAI.address = obj.address;
		}

		if (obj.sex != null) {
			uAI.sex = obj.sex;
		}

		if (obj.birthdate != null) {
			uAI.birthdate = obj.birthdate;
		}

		
		 * if (obj.tag != null) { uAI.tag = obj.tag; }
		 

		if (obj.app_downloadurl != null) {
			uAI.app_downloadurl = obj.app_downloadurl;
		}

		persist();

		return true;*/
	}

	/**
	 * {@literal 将登陆成功得到的信息更新到对应用户的AccountInfo中}
	 * 
	 * @author zhangwei
	 * @param snstype 社区类型
	 * @param snsid 微博id
	 * @return true / false
	 * */
	public boolean updateLogin(GsonResponse2.loginResponse obj) {
		if (obj == null || obj.userid == null) {
			return false;
		}

		if (snsid == null || snstype == null || snsid.equals("")
				|| snstype.equals("")) {
			// snsid是在授权成功后，回调设置过来的，若为空说明授权不成功
			return false;
		}

		// String uid = obj.userid;

		// Account a = Account.getInstance(getApplicationContext());
		this.userid = obj.userid;
		
		this.is_firstlogin = obj.is_firstlogin;

		AccountInfo uAI = getAccountInfo(userid);
		
		//if(snstype.equals("1") || snstype.equals("2") || snstype.equals("6")){
			//微博登陆后，把授权的信息保存到accountInfo里
		//uAI.oAuthV2Map = CmmobiSnsLib.getInstance(context).getOAuthV2Map();
		//}
		
		//从服务器获取绑定的微博授权信息
		if(obj.binding!=null && obj.binding.length>0){
			for(MyBind mybind : obj.binding){
				if(mybind.sns_token==null || mybind.sns_token.equals("")){
					continue;
				}
				
				if(mybind.sns_expiration_time==null || mybind.sns_expiration_time.equals("")){
					continue;
				}else{
					try{
						long exp_time = Long.parseLong(mybind.sns_expiration_time);
						if( exp_time < TimeHelper.getInstance().now()){
							continue;
						}
					}catch(NumberFormatException e){
						e.printStackTrace();
						continue;
					}

				}
				
				if( "1".equals(mybind.snstype)){
					//uAI.oAuthV2Map.put(SHARE_TO.SINA.ordinal(), new OAuthV2(SHARE_TO.SINA.ordinal(), mybind.snsuid, mybind.sns_token, mybind.sns_expiration_time, mybind.sns_effective_time, mybind.sns_refresh_token, mybind.sns_nickname));
					CmmobiSnsLib.getInstance(context).updateOAuthV2Map(SHARE_TO.SINA.ordinal(), new OAuthV2(SHARE_TO.SINA.ordinal(), mybind.snsuid, mybind.sns_token, mybind.sns_expiration_time, mybind.sns_effective_time, mybind.sns_refresh_token, mybind.sns_nickname));
				}else if( "2".equals(mybind.snstype)){
					CmmobiSnsLib.getInstance(context).updateOAuthV2Map(SHARE_TO.RENREN.ordinal(), new OAuthV2(SHARE_TO.RENREN.ordinal(), mybind.snsuid, mybind.sns_token, mybind.sns_expiration_time, mybind.sns_effective_time, mybind.sns_refresh_token, mybind.sns_nickname));
					//uAI.oAuthV2Map.put(SHARE_TO.RENREN.ordinal(), new OAuthV2(SHARE_TO.RENREN.ordinal(), mybind.snsuid, mybind.sns_token, mybind.sns_expiration_time, mybind.sns_effective_time, mybind.sns_refresh_token, mybind.sns_nickname));
				}else if( "6".equals(mybind.snstype)){
					CmmobiSnsLib.getInstance(context).updateOAuthV2Map(SHARE_TO.TENC.ordinal(), new OAuthV2(SHARE_TO.TENC.ordinal(), mybind.snsuid, mybind.sns_token, mybind.sns_expiration_time, mybind.sns_effective_time, mybind.sns_refresh_token, mybind.sns_nickname));
					//uAI.oAuthV2Map.put(SHARE_TO.TENC.ordinal(), new OAuthV2(SHARE_TO.TENC.ordinal(), mybind.snsuid, mybind.sns_token, mybind.sns_expiration_time, mybind.sns_effective_time, mybind.sns_refresh_token, mybind.sns_nickname));
				}
				
			}
		}



		if (obj.nickname != null) {
			uAI.nickname = obj.nickname;
		}

		if (obj.headimageurl != null) {
			uAI.headimageurl = obj.headimageurl;
		}

		if (obj.mood != null) {
			uAI.mood = obj.mood;
		}

		if (obj.sex != null) {
			uAI.sex = obj.sex;
		}

		if (obj.address != null) {
			uAI.address = obj.address;
		}

		if (obj.birthdate != null) {
			uAI.birthdate = obj.birthdate;
		}

		if (obj.signature != null) {
			uAI.signature = obj.signature;
		}

		if (obj.privmsg_type != null) {
			uAI.setmanager.setPrivmsg_type(obj.privmsg_type);
		}

		if (obj.friends_type != null) {
			uAI.setmanager.setFriends_type(obj.friends_type);
		}

		if (obj.diary_type != null) {
			uAI.setmanager.setDiary_type(obj.diary_type);
		}

		if (obj.app_downloadurl != null) {
			uAI.app_downloadurl = obj.app_downloadurl;
		}

		if (obj.position_type != null) {
			uAI.setmanager.setPosition_type(obj.position_type);
		}

		if (obj.audio_type != null) {
			uAI.setmanager.setAudio_type(obj.audio_type);
		}

		if (obj.audio_encrypt_type != null) {
			uAI.setmanager.setAudio_encrypt_type(obj.audio_encrypt_type);
		}

		if (obj.launch_type != null) {
			uAI.setmanager.setLaunch_type(obj.launch_type);
		}

		if (obj.sysc_type != null) {
			uAI.setmanager.setSysc_type(obj.sysc_type);
		}

		if (obj.gesturepassword != null) {
			uAI.setmanager.setGesturepassword(obj.gesturepassword);
		}

		if (obj.binding != null) {
			uAI.setmanager.binding = obj.binding;
			
			uAI.syncBinding(obj.binding);
		}

		/*
		 * if (obj.watermark != null) { uAI.watermark = obj.watermark; }
		 */

		if (AccessTokenKeeper.tmp != null) {
			uAI.updateAccessToken(AccessTokenKeeper.tmp);
		}

		// ok now put activeAccount's weibo info to AccountInfo
		if (swInfo != null) {
			uAI.swInfo = swInfo;
		}

		if (twInfo != null) {
			uAI.twInfo = twInfo;
		}

		if (rwInfo != null) {
			uAI.rwInfo = rwInfo;
		}

		// uAI.persist();

		persist();
		if (!snstype.equals("0")) {
			CmmobiSnsLib mSnsLib = CmmobiSnsLib.getInstance();
			// mSnsLib.postProcess(context, uid);
		}

		return true;
	}

	/**
	 * 
	 * 该阶段还未完成登录，得不到userid,不能更新AccountInfo,先把信息放到ActiveAccount中
	 * */
	public boolean updateSinaAuthor(SWUserInfo sn_object) {
		if (snsid == null || snstype == null) {
			// snsid是在授权成功后，回调设置过来的，若为空说明授权不成功
			int i = 0;
			i++;
			return false;
		}

		if (sn_object == null || sn_object.idstr == null) {
			// mSnsLib.sinaAuthorize(sinalistener);
			return false;
		} else {
			// author ok: write the info
			this.snsid = sn_object.idstr;

			this.snsname = sn_object.name;
			this.nickname = sn_object.screen_name;
			this.sns_head_pic = sn_object.profile_image_url;
			setSNSNick(SHARE_TO.SINA.ordinal(), this.nickname);
			if (sn_object.gender.equals("m")) {
				// male
				this.sex = "0";
			} else if (sn_object.gender.equals("f")) {// ?
				// female
				this.sex = "1";

			} else {
				this.sex = "2";
			}

			if (sn_object.location != null) {
				this.address = sn_object.location;
			}

			// this.sinaBind = 1;
			this.swInfo = sn_object;
			
			

		}
		/*
		 * AccountInfo uAI = getAccountInfo(this.userid);
		 * 
		 * if (sn_object == null || sn_object.idstr == null) { //
		 * mSnsLib.sinaAuthorize(sinalistener); return false; } else { // author
		 * ok: write the info this.snsid = sn_object.idstr;
		 * 
		 * uAI.nickname = sn_object.screen_name; if
		 * (sn_object.gender.equals("m")) { // male uAI.sex = "0"; } else if
		 * (sn_object.gender.equals("f")) {// ? // female uAI.sex = "1";
		 * 
		 * } else { uAI.sex = "2"; }
		 * 
		 * // this.sinaBind = 1; this.username = sn_object.screen_name;
		 * uAI.swInfo = sn_object;
		 * 
		 * }
		 */

		return true;
	}

	/**
	 * 
	 * 该阶段还未完成登录，得不到userid,不能更新AccountInfo,先把信息放到ActiveAccount中
	 * */
	public boolean updateRenrenAuthor(RWUserInfo rr_object) {
		if (snsid == null || snstype == null) {
			// snsid是在授权成功后，回调设置过来的，若为空说明授权不成功
			return false;
		}

		if (rr_object == null || rr_object.response == null
				|| rr_object.response.name == null) {
			// mSnsLib.sinaAuthorize(sinalistener);
			return false;
		} else {
			// author ok: write the info
			this.snsid = String.valueOf(rr_object.response.id);

			this.nickname = rr_object.response.name;
			this.snsname = rr_object.response.name;
			if(rr_object.response.avatar!=null && rr_object.response.avatar.length>0 && rr_object.response.avatar[0]!=null && rr_object.response.avatar[0].url!=null){
				this.sns_head_pic = rr_object.response.avatar[0].url;
			}

			setSNSNick(SHARE_TO.RENREN.ordinal(), this.nickname);
			if (rr_object.response.basicInformation != null) {
				if (rr_object.response.basicInformation.sex == Sex.MALE) {
					// male
					this.sex = "0";
				} else if (rr_object.response.basicInformation.sex == Sex.FEMALE) {
					// female
					this.sex = "1";

				} else {
					this.sex = "2";
				}

				if (rr_object.response.basicInformation.birthday != null) {
					this.birthdate = rr_object.response.basicInformation.birthday;
				}

				if (rr_object.response.basicInformation.homeTown != null) {
					StringBuilder addr = new StringBuilder();
					if (rr_object.response.basicInformation.homeTown.province != null) {
						addr.append(rr_object.response.basicInformation.homeTown.province);
					}
					if (rr_object.response.basicInformation.homeTown.city != null) {
						addr.append(rr_object.response.basicInformation.homeTown.city);
					}

					this.address = addr.toString();
				}

			} else {
				this.sex = "2";
			}

			this.rwInfo = rr_object;

		}
		/*
		 * AccountInfo uAI = getAccountInfo(this.userid);
		 * 
		 * if (rr_object == null || rr_object.name == null) { //
		 * mSnsLib.sinaAuthorize(sinalistener); return false; } else { // author
		 * ok: write the info this.snsid = String.valueOf(rr_object.uid);
		 * 
		 * uAI.nickname = rr_object.name; if (rr_object.sex==1) { // male
		 * uAI.sex = "0"; } else if (rr_object.sex==0) { // female uAI.sex =
		 * "1";
		 * 
		 * } else { uAI.sex = "2"; }
		 * 
		 * // this.sinaBind = 1; this.username = rr_object.name; uAI.rwInfo =
		 * rr_object;
		 * 
		 * }
		 */

		return true;
	}

	/**
	 * 
	 * 该阶段还未完成登录，得不到userid,不能更新AccountInfo,先把信息放到ActiveAccount中
	 * */
	public boolean updateTencentAuthor(TWUserInfo tx_object) {
		if (snsid == null || snstype == null) {
			// snsid是在授权成功后，回调设置过来的，若为空说明授权不成功
			return false;
		}

		if (tx_object == null || tx_object.errcode != 0
				|| tx_object.data == null) {
			// mSnsLib.sinaAuthorize(sinalistener);
			return false;
		} else {
			// author ok: write the info
			this.snsid = String.valueOf(tx_object.data.openid);

			this.nickname = tx_object.data.nick;
			
			if(tx_object.data.head!=null && !tx_object.data.head.equals("")){
				this.sns_head_pic = tx_object.data.head + "/50";
			}

			setSNSNick(SHARE_TO.TENC.ordinal(), this.nickname);
			if (tx_object.data.sex == 1) {
				// male
				this.sex = "0";
			} else if (tx_object.data.sex == 2) {
				// female
				this.sex = "1";

			} else {
				this.sex = "2";
			}

			if (tx_object.data.location != null) {
				this.address = tx_object.data.location;
			}

			this.birthdate = "" + tx_object.data.birth_year + "-"
					+ tx_object.data.birth_month + "-"
					+ tx_object.data.birth_day;

			this.snsname = tx_object.data.name;

			this.twInfo = tx_object;

		}
		/*
		 * AccountInfo uAI = getAccountInfo(this.userid);
		 * 
		 * if (tx_object == null ||tx_object.errcode != 0 || tx_object.data ==
		 * null) { // mSnsLib.sinaAuthorize(sinalistener); return false; } else
		 * { // author ok: write the info this.snsid =
		 * String.valueOf(tx_object.data.openid);
		 * 
		 * uAI.nickname = tx_object.data.nick; if (tx_object.data.sex==1) { //
		 * male uAI.sex = "0"; } else if (tx_object.data.sex==2) { // female
		 * uAI.sex = "1";
		 * 
		 * } else { uAI.sex = "2"; }
		 * 
		 * // this.sinaBind = 1; this.username = tx_object.data.name; uAI.twInfo
		 * = tx_object;
		 * 
		 * }
		 */

		return true;
	}

	/**
	 * {@literal 将授权成功得到的信息更新到对应用户的Account中}
	 * 
	 * @author zhangwei
	 * @param snstype
	 *            社区类型
	 * @return true / false
	 * */
	/*
	 * public boolean updateAuthor(Handler handler) {
	 * 
	 * if (snsid == null || snstype == null) { //
	 * snsid是在授权成功后，回调设置过来的，若为空说明授权不成功 return false; }
	 * 
	 * String uid = snstype + "_" + snsid;
	 * 
	 * try { CmmobiSnsLib mSnsLib = CmmobiSnsLib.getInstance(uid);
	 * 
	 * Gson gson = new Gson();
	 * 
	 * AccountInfo uAI = getAccountInfo(snstype, snsid);
	 * 
	 * if (snstype.equals("1")) { SWUserInfo sn_object = gson.fromJson(
	 * mSnsLib.getSWUserInfo(uid, Long.valueOf(snsid)), SWUserInfo.class); if
	 * (sn_object == null || sn_object.idstr == null) { //
	 * mSnsLib.sinaAuthorize(sinalistener); return false; } else { // author ok:
	 * write the info this.snsid = sn_object.idstr;
	 * 
	 * uAI.nickname = sn_object.screen_name; if (sn_object.gender.equals("m")) {
	 * // male uAI.sex = "0"; } else if (sn_object.gender.equals("f")) {// ? //
	 * female uAI.sex = "1";
	 * 
	 * } else { uAI.sex = "2"; }
	 * 
	 * // this.sinaBind = 1; this.username = sn_object.screen_name; uAI.swInfo =
	 * sn_object;
	 * 
	 * } //uAI.persist();
	 * 
	 * }else if(snstype.equals("2")){ RWUserInfo[] rr_objects = gson.fromJson(
	 * mSnsLib.getRWUserInfo(uid, Long.valueOf(snsid)), RWUserInfo[].class);
	 * if(rr_objects!=null && rr_objects.length>0){ RWUserInfo rr_object =
	 * rr_objects[0]; if (rr_object==null || rr_object.name == null ||
	 * rr_object.uid==0) { // mSnsLib.sinaAuthorize(sinalistener); return false;
	 * } else { // author ok: write the info this.snsid =
	 * String.valueOf(rr_object.uid);
	 * 
	 * uAI.nickname = rr_object.name; if (rr_object.sex==1) { // male uAI.sex =
	 * "0"; } else if (rr_object.sex==0) {// ? // female uAI.sex = "1";
	 * 
	 * } else { uAI.sex = "2"; }
	 * 
	 * // this.sinaBind = 1; this.username = rr_object.name; uAI.rwInfo =
	 * rr_object;
	 * 
	 * } //uAI.persist(); }
	 * 
	 * } else if (snstype.equals("6")) { // tencent Log.e(TAG,
	 * "getTencentWeiboUserId: " + snsid); Log.e(TAG, "twUserInfo: " +
	 * mSnsLib.getTWUserInfo(uid));
	 * 
	 * TWUserInfo tc_object = gson.fromJson(mSnsLib.getTWUserInfo(uid),
	 * TWUserInfo.class); if (tc_object==null || tc_object.errcode != 0) { //
	 * mSnsLib.tencentWeiboAuthorize(tencentlistener); return false; } else { //
	 * author ok: write the info // Account a = Account.getInstance(this);
	 * this.snsid = tc_object.data.openid; uAI.nickname = tc_object.data.nick;
	 * 
	 * if (tc_object.data.sex == 1) { // male uAI.sex = "0"; } else if
	 * (tc_object.data.sex == 2) { // female uAI.sex = "1";
	 * 
	 * } else { uAI.sex = "2"; }
	 * 
	 * // this.qqBind = 1; this.username = tc_object.data.name;
	 * 
	 * uAI.twInfo = tc_object; }
	 * 
	 * //uAI.persist(); } // Log.e(TAG, " getMyTWFriendsList(): " + //
	 * mSnsLib.getMyTWFriendsList("10", "0")); } catch (Exception e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); }
	 * 
	 * persist(); return true; }
	 */

	/**
	 * {@literal 得到PersonalInfo}
	 * 
	 * @author zhangwei
	 * @return uid用户对应的PersonalInfo
	 * */
	/*
	 * public AccountInfo getAccountInfo(String snstype, String snsid) { return
	 * AccountInfo.getInstance(snstype + "_" + snsid); }
	 */

	/**
	 * {@literal 得到PersonalInfo}
	 * 
	 * @author zhangwei
	 * @return uid用户对应的PersonalInfo
	 * */
	public AccountInfo getAccountInfo(String uid) {
		return AccountInfo.getInstance(uid);
	}

	/**
	 * {@literal 查询微博是否绑定}
	 * 
	 * @author zhangwei
	 * @param snstype
	 *            查询的sns类型
	 * @return 是否授权（合法且在有效期）
	 * */
	public boolean isSNSBind(String snstype) {
		// String uid = this.snstype + "_" + this.snsid;
		CmmobiSnsLib csb = CmmobiSnsLib.getInstance(context);

		if (snstype.equals("sina")) {
			return csb.isSinaWeiboAuthorized();
		} else if (snstype.equals("tencent")) {
			return csb.isTencentWeiboAuthorized();
		} else if (snstype.equals("renren")) {
			return csb.isRenrenWeiboAuthorized();
		}

		return false;
	}
	
	public String getSNSID(int snstype) {
		// String uid = this.snstype + "_" + this.snsid;
		CmmobiSnsLib csb = CmmobiSnsLib.getInstance(context);

		String snsid = csb.acquireOauth(snstype).getOpenid();
		
		return snsid;
	}
	
	public String getSNSNick(int snstype) {
		// String uid = this.snstype + "_" + this.snsid;
		CmmobiSnsLib csb = CmmobiSnsLib.getInstance(context);

		String nick = csb.acquireOauth(snstype).getNick();
		if(nick==null){
			nick = "N/A";
		}
		
		return nick;
	}
	
	public void setSNSNick(int snstype, String nick) {
		// String uid = this.snstype + "_" + this.snsid;
		CmmobiSnsLib csb = CmmobiSnsLib.getInstance(context);

		csb.acquireOauth(snstype).setNick(nick);

	}

	public String getUID() {
		/*
		 * if (snstype != null && snsid != null) { return snstype + "_" + snsid;
		 * } return null;
		 */
		if(userid==null){
			Log.e(TAG, "getUID userid is null");
			
		}
		return userid;
	}

	public String getLookLookID() {
		if(userid==null){
			Log.e(TAG, "getLookLookID userid is null");
			
		}
		
		return this.userid;
	}

	public void setLookLookID(String userid) {
		this.userid = userid;
	}

	@Override
	public String toString() {
		return "ActiveAccount userid:" + userid + " snstype:" + snstype
				+ " snsid:" + snsid;
	}
}
