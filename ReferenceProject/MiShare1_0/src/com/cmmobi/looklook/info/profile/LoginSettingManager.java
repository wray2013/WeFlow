package com.cmmobi.looklook.info.profile;

import java.util.Arrays;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyBind;
import com.cmmobi.looklook.fragment.SettingFragment;

/*
 * 请求
 */
public class LoginSettingManager {

	public String sync_type;// 数据同步
	public String accept_friend_type;// 接收朋友类型
	public String gesturepassword;// 手势密码
	transient public Boolean isFromSetting = true; //是否从设置进入
	transient public Boolean isFromDetail = false; //是否从详情页进入
	public MyBind[] binding; //binding
	
	//will be delete
//	public String diary_type;// 谁可以看我内容（日记和评论），1全部人可见（黑名单人除外） 2关注人可见 4仅自己可见 
	//end
	
	transient public static String GET_CHECK_NO_PHONE ="2";
	transient public static String GET_CHECK_NO_PHONE_SEC ="4";
	
	transient public static String BINDING_TYPE_EMAIL = "1";
	transient public static String BINDING_TYPE_PHONE = "2";
	transient public static String BINDING_TYPE_SNS = "3";
	transient public static String BINDING_TYPE_PHONE_SEC = "4";

	
	transient public static String BINDING_SNS_TYPE_SINA = "1";
	transient public static String BINDING_SNS_TYPE_RENREN = "2";
	transient public static String BINDING_SNS_TYPE_TENCENT = "6";
	transient public static String BINDING_SNS_TYPE_QQMOBILE = "13";

	transient public static String BINDING_INFO_POINTLESS = ""; //无意义的参数，作为填充值
	
	
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
		if(gesturepassword!=null && !gesturepassword.equals("")){
			return true;
		}
		return false;
	}

	public void setBinding(MyBind[] binding) {
		this.binding = binding;
	}

	public String getSync_type() {
		if (sync_type == null) {
			sync_type = SettingFragment.SYNC_TYPE_WIFI;
		}
		return sync_type;
	}
	
	public void setSync_type(String sync_type) {
		if(sync_type == null || sync_type.equals("")){
			this.sync_type = SettingFragment.SYNC_TYPE_WIFI;
		}else{
			this.sync_type = sync_type;
		}
	}
	
	public String getAcceptFriendType() {
		if (accept_friend_type == null) {
			accept_friend_type = SettingFragment.ACCEPT_FRIEND_AUTO;
		}
		return accept_friend_type;
	}
	
	public void setAcceptFriendType(String acceptType) {
		if (acceptType == null || acceptType.equals("")) {
			accept_friend_type = SettingFragment.ACCEPT_FRIEND_AUTO;
		} else {
			accept_friend_type = acceptType;
		}
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

	
//	public String getDiary_type() {
//		return diary_type;
//	}
//
//	public void setDiary_type(String diary_type) {
//		this.diary_type = diary_type;
//	}

}
