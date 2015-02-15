package com.cmmobi.looklook.info.profile;

import java.util.Arrays;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.activity.HomepageMyselfDiaryActivity;
import com.cmmobi.looklook.activity.SettingActivity;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyBind;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;

/*
 * 请求
 */
public class LoginSettingManager {

	public String privmsg_type;// 私信可见情况
	public String friends_type;// 谁可以看我的朋友关系，1全部人可见（黑名单人除外） 2关注人可见 4仅自己可见
	public String diary_type;// 谁可以看我内容（日记和评论），1全部人可见（黑名单人除外） 2关注人可见 4仅自己可见
	public String position_type;// 谁可以看我的位置
	public String audio_type;// 谁可以听我的语音
	public String audio_encrypt_type;// 加密类型
	public String launch_type;// 启动模式,0
	public String sysc_type;// 数据同步
	public String gesturepassword;// 手势密码
	public Boolean safeIsOn = false;// 是否开启保险箱
	transient public Boolean isFromSetting = true; //是否从设置进入
	transient public Boolean isFromDetail = false; //是否从详情页进入
	public MyBind[] binding; //binding
	
	transient public static String BINDING_REQUEST_TYPE_EMAIL = "1";
	transient public static String BINDING_REQUEST_TYPE_PHONE = "2";
	transient public static String BINDING_REQUEST_TYPE_SNS = "3";
	
	transient public static String PHONE_TYPE_MAIN = "1";
	transient public static String PHONE_TYPE_SEC = "2";
	
	transient public static String GET_CHECK_NO_PHONE ="2";
	transient public static String GET_CHECK_NO_PHONE_SEC ="4";
	
	transient public static String BINDING_TYPE_EMAIL = "1";
	transient public static String BINDING_TYPE_PHONE = "2";
	transient public static String BINDING_TYPE_SNS = "3";
	transient public static String BINDING_TYPE_PHONE_SEC = "4";
	
	transient public static String BINDING_SNS_TYPE_SINA = "1";
	transient public static String BINDING_SNS_TYPE_RENREN = "2";
	transient public static String BINDING_SNS_TYPE_TENCENT = "6";

	transient public static String BINDING_INFO_POINTLESS = "-1"; //无意义的参数，作为填充值
	
	transient public static String GROUP_MYSELF = "4";
	transient public static String GROUP_SOME = "2";
	transient public static String GROUP_ALL = "1";
	
	public Boolean getIsFromSetting() {
		return isFromSetting;
	}

	public void setIsFromSetting(Boolean isfromsetting) {
		this.isFromSetting = isfromsetting;
	}

	public Boolean getIsFromDetail(){
		return isFromDetail;
	}
	
	public void setIsFromDetail(Boolean isfromdetail){
		this.isFromDetail = isfromdetail;
	}
	
	public Boolean getSafeIsOn() {
		return safeIsOn;
	}

	public void setSafeIsOn(Boolean safeIsOn) {
		if(safeIsOn){
			CmmobiClickAgentWrapper.onEvent(MainApplication.getAppInstance(), "ma_op_safe");
		}
		LocalBroadcastManager lbc;
		lbc = LocalBroadcastManager.getInstance(MainApplication.getAppInstance());
		Intent intent = new Intent(HomepageMyselfDiaryActivity.SAVE_BOX_MODE_CHANGE);
		if(getIsFromDetail() == true){
			intent.putExtra("isfromdetail", isFromDetail);
			setIsFromDetail(false);
		}
		lbc.sendBroadcast(intent);
		this.safeIsOn = safeIsOn;
	}

	public void setBinding(MyBind[] binding) {
		this.binding = binding;
	}

	public String getPrivmsg_type() {
		return privmsg_type;
	}

	public void setPrivmsg_type(String privmsg_type) {
		this.privmsg_type = privmsg_type;
	}

	public String getFriends_type() {
		return friends_type;
	}

	public void setFriends_type(String friends_type) {
		this.friends_type = friends_type;
	}

	public String getDiary_type() {
		return diary_type;
	}

	public void setDiary_type(String diary_type) {
		this.diary_type = diary_type;
	}

	public String getPosition_type() {
		return position_type;
	}

	public void setPosition_type(String position_type) {
		this.position_type = position_type;
	}

	public String getAudio_type() {
		return audio_type;
	}

	public void setAudio_type(String audio_type) {
		this.audio_type = audio_type;
	}

	public String getAudio_encrypt_type() {
		return audio_encrypt_type;
	}

	public void setAudio_encrypt_type(String audio_encrypt_type) {
		this.audio_encrypt_type = audio_encrypt_type;
	}

	public String getLaunch_type() {
		return launch_type;
	}

	public void setLaunch_type(String launch_type) {
		this.launch_type = launch_type;
	}

	public String getSysc_type() {
		return sysc_type;
	}

	public void setSysc_type(String sysc_type) {
		this.sysc_type = sysc_type;
	}

	public String getGesturepassword() {
		if( gesturepassword != null && (gesturepassword.equals(BINDING_INFO_POINTLESS) || gesturepassword.equals(""))){
			gesturepassword = null;
		}
		return gesturepassword;
	}

	public void setGesturepassword(String gesturepassword) {
		this.gesturepassword = gesturepassword;
	}

	// 3 //1
	public MyBind getBinding_type(String binding_type , String snstype) {
		MyBind ret = null;
		if (binding != null && binding.length > 0) {
			for (int i = 0; i < binding.length; i++) {
				try{
					if (binding[i].binding_type.equals(binding_type)) {
						if (binding_type.equals(LoginSettingManager.BINDING_TYPE_SNS)) {
							if (snstype.equals(binding[i].snstype)) {
								ret = binding[i];
								break;
							}
						} else {
							ret = binding[i];
							break;
						}
					}
				}catch(NumberFormatException e){
					e.printStackTrace();
				}

			}
		}
		return ret;
	}

	//添加绑定信息
	public void addBindingInfo(MyBind newbind){
		if(newbind == null){
			return;
		}
		if(binding == null){
			binding = new MyBind[1];
		}else{
			binding = Arrays.copyOf(binding, binding.length+1);
		}
		binding[binding.length-1] = newbind;
		try {
			ActiveAccount activeAccount = ActiveAccount.getInstance(MainApplication.getInstance());
			activeAccount.persist();
			AccountInfo.getInstance(activeAccount.getUID()).persist();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	//删除绑定信息
	public void deleteBindingInfo(String binding_type , String snstype){
		if (binding != null && binding.length > 0) {
			for (int i = 0; i < binding.length; i++) {
				try{
					if (binding[i].binding_type.equals(binding_type)) {
						if (binding_type.equals(LoginSettingManager.BINDING_TYPE_SNS)) {
							if (snstype.equals(binding[i].snstype)) {
								deleteItem(i);
								break;
							}
						}else {
							deleteItem(i);
							break;
						}
					}
				}catch(NumberFormatException e){
					e.printStackTrace();
				}

			}
			try {
				ActiveAccount activeAccount = ActiveAccount.getInstance(MainApplication.getInstance());
				activeAccount.persist();
				AccountInfo.getInstance(activeAccount.getUID()).persist();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}


	private void deleteItem(int index) {
		MyBind[] tmpBinding = new MyBind[binding.length - 1];
		System.arraycopy(binding, 0, tmpBinding, 0, index); // n is the index of
															// the element you
															// wanna remove
		if (index != binding.length) {
			System.arraycopy(binding, index + 1, tmpBinding, index,
					binding.length - index - 1);
		}
		binding = tmpBinding;
	}



	/*
	 * 当设置项没有值时给出默认值
	 */
	public void initSettingItems(){
		if(getSysc_type()== null || getSysc_type().equals("")){
			setSysc_type(SettingActivity.SYSC_TYPE_WIFI);
		}
		
		if(getLaunch_type()==null || getLaunch_type().equals("")){
			setLaunch_type(SettingActivity.LAUNCH_TYPE_SHOOT);
		}
		
		if(getPrivmsg_type() == null || getPrivmsg_type().equals("")){
			setPrivmsg_type(GROUP_ALL);
		}
		
		if(getFriends_type()==null || getFriends_type().equals("")){
			setFriends_type(LoginSettingManager.GROUP_SOME);
		}
	}

}
