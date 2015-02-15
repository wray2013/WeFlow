package com.cmmobi.looklook.activity.login;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.utils.ZStringUtils;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.LoginMainActivity;
import com.cmmobi.looklook.activity.LookLookActivity;
import com.cmmobi.looklook.activity.TitleRootActivity;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyBind;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiaryList;
import com.cmmobi.looklook.common.gson.GsonResponse3.uaResponse;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.storage.SqlMediaManager;
import com.cmmobi.looklook.common.storage.SqliteDairyManager;
import com.cmmobi.looklook.common.storage.StorageManager;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.view.Xdialog;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.CommonInfo;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.LoginSettingManager;
import com.cmmobi.looklook.info.profile.NetworkTaskInfo;
import com.cmmobi.looklook.networktask.INetworkTask;
import com.cmmobi.looklook.networktask.NetworkTaskManager;
import com.cmmobi.looklook.offlinetask.OfflineTaskManager;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.looklook.prompt.TickUpHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 微享1.0 用户切换(登陆)
 */
public class MicShareUserLoginActivity extends TitleRootActivity {

	
	private static final int HANDLER_FLAG_ENABLE_PWD = 0x012383799;
	
	private Button   mBtnGetPwd;
	private EditText mEditUsername;
	private EditText mEditpassword;
	
	
	@Override
	public int subContentViewId() {
		return R.layout.activity_micshare_user_login;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle("账户登录");
		hideRightButton();
		
		mEditUsername = (EditText) findViewById(R.id.edit_username);
		mEditpassword = (EditText) findViewById(R.id.edit_password);
		mBtnGetPwd = (Button)findViewById(R.id.btn_get_password);
		
		mBtnGetPwd.setOnClickListener(this);
		findViewById(R.id.btn_login).setOnClickListener(this);
		
		TickUpHelper.getInstance(getHandler()).init();
		
	}

	@Override
	protected void onStop() {
		super.onStop();
		TickUpHelper.getInstance(getHandler()).stop(0);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_get_password:   // 获取动态密码
			String username = mEditUsername.getText().toString().trim();
			
			if(checkValidUsername(username)){
				
				if(validUsernameExist(username)){
					Prompt.Alert(this, "该账户已登录");
					return;
				}
				
				ZDialog.show(R.layout.progressdialog, false, true, this);
				Requester3.requestForgetPassword(getHandler(), username, "2", "1");
			}
			break;
		case R.id.btn_login:  // 登陆
			String name = mEditUsername.getText().toString().trim();
			String pwd = mEditpassword.getText().toString().trim();
			
			// 验证用户名
			if(!checkValidUsername(name)){
				return;
			}
			
			// 验证密码
			if(!checkValidPassword(pwd)){
				return;
			}
			
			
			if(validUsernameExist(name)){
				Prompt.Alert(this, "该账户已登录");
				return;
			}
			
//			// 完成登陆
			ZDialog.show(R.layout.progressdialog, false, true, this);
			
			if(TextUtils.isEmpty(CommonInfo.getInstance().equipmentid)){
				Requester3.submitUA(handler);
			}else{
				ActiveAccount acct = ActiveAccount.getInstance(this);
				acct.snstype = "0";
				acct.snsid = name;
				acct.username = name;
				acct.password = pwd;
				Requester3.login(this, handler, acct, "2");
			}
			
			break;
		default:
			break;
		}
		super.onClick(v);
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		switch(msg.what){
		
		case Requester3.RESPONSE_TYPE_UA:
			ZDialog.dismiss();
			uaResponse uaResponse  = (GsonResponse3.uaResponse) msg.obj;
			if(uaResponse != null && uaResponse.equipmentid!=null){
				CommonInfo.getInstance().equipmentid = uaResponse.equipmentid;
				CommonInfo.getInstance().persist();
				onClick(findViewById(R.id.btn_login));
			}else{
				Prompt.Alert(this, "网络不给力");
			}
			break;
			
		// 动态密码返回
		case Requester3.RESPONSE_TYPE_FORGET_PASSWORD:
			try {
				ZDialog.dismiss();
				GsonResponse3.forgetPasswordResponse obj = (GsonResponse3.forgetPasswordResponse) (msg.obj);
			    if(obj!=null){
			    	if(obj.status.equals("0")){
		    			Prompt.Dialog(this, false, "提示", "密码已通过短信发送到该手机，请注意查收" , null);
		    			getHandler().sendEmptyMessage(HANDLER_FLAG_ENABLE_PWD);
			    	}else if(obj.crm_status!=null){
						//Prompt.Dialog(this, false, "提醒", Prompt.GetStatus(obj.status, obj.crm_status), null);
						Prompt.Alert(this, Prompt.GetStatus(obj.status, obj.crm_status));
			    	}
			    } else{
			    	//Prompt.Dialog(this, false, "提醒", "网络超时", null);
			    	Prompt.Alert(this, "网络不给力");
			    }
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
			
			// 密码90倒计时开始
		case HANDLER_FLAG_ENABLE_PWD:
			TickUpHelper.getInstance(getHandler()).start(60);
			mBtnGetPwd.setEnabled(false);
			break;
			
			// 密码90倒计时时钟
		case TickUpHelper.HANDLER_FLAG_TICK_UP:
			int tick = (Integer) msg.obj;
			mBtnGetPwd.setText("重新获取( " + (60 - tick) + "秒)");
			mBtnGetPwd.setEnabled(false);
			break;
			
			// 密码90倒计时结束
		case TickUpHelper.HANDLER_FLAG_TICK_STOP:
			mBtnGetPwd.setText("获取动态密码");
			mBtnGetPwd.setEnabled(true);
			break;
			
			// 登陆返回
		case Requester3.RESPONSE_TYPE_LOGIN : 
			try {
				
				ZDialog.dismiss();
				GsonResponse3.loginResponse obj = (GsonResponse3.loginResponse)(msg.obj);
				if(obj!=null){
					
					if(obj.status.equals("0")){
						//ok
						Prompt.Alert(this, "登录成功");
						loginSuccess(obj);
						
					}else {
//						ActiveAccount.getInstance(this).logout();
						String ser_ret = null;
						if(obj.crm_status!=null && obj.crm_status.equals("7")){
							ser_ret = Prompt.GetStatus(obj.status, "3");
						}else{
							ser_ret = Prompt.GetStatus(obj.status, obj.crm_status);
						}
						//Prompt.Dialog(this, false, "提醒", ser_ret , null);
						Prompt.Alert(this, ser_ret);
					}
				}else{
//			    	ActiveAccount.getInstance(this).logout();
			    	//Prompt.Dialog(this, false, "提醒", "网络超时", null);
			    	Prompt.Alert(this, "网络不给力");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
			
			// 合并账号数据
		case Requester3.RESPONSE_TYPE_MERGER_ACCOUNT:
			
			GsonResponse3.mergerAccountResponse obj = (GsonResponse3.mergerAccountResponse)(msg.obj);
			
			if(obj != null ){
				if("0".equals(obj.status)){
					// 未上传任务合并
//					mergeNetworkTask(obj.oldUserid, obj.newUserid);
					// 本地合并
//					new myAsyncTask().execute(obj.oldUserid, obj.newUserid);
				}
			}else{
				
			}
			
			
			break;
		}
		return false;
	}
	
	
	/**
	 * 检查账户
	 * @param username
	 * @return
	 */
	private boolean checkValidUsername(String username) {
		
		if(TextUtils.isEmpty(username)){
			Prompt.Alert(this, "账号不能为空");
			return false;
		}
		
		//if(!Prompt.checkPhoneNum(username) && !Prompt.checkMiShareNo(username)){
		if(!Prompt.checkPhoneNum(username)){
			Prompt.Alert(this, "请输入合法的账户");
			return false;
		}
		return true;
	}
	
	
	/**
	 * 检查账户
	 * @param username
	 * @return
	 */
	private boolean validUsernameExist(String username) {
		
		if(TextUtils.isEmpty(username)){
			return false;
		}
			
		String myuid = ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID();
		AccountInfo accountInfo = AccountInfo.getInstance(myuid);
		LoginSettingManager lsm = accountInfo.setmanager;
		
		String phoneno = "";
		
		MyBind mb = lsm.getBinding_type(LoginSettingManager.BINDING_TYPE_PHONE, "");
		if(mb != null){
			if(LoginSettingManager.BINDING_TYPE_PHONE.equals(mb.binding_type)){
				phoneno = mb.binding_info;
			}
		}
		
		String mishare_no = accountInfo.mishare_no;
		
		//电话号码
		if(Prompt.checkPhoneNum(username)){ 
			if(username.equals(phoneno)){
				return true;
			}
		}
		
		// 微享号
		if( Prompt.checkMiShareNo(username)){
			if(username.equals(mishare_no)){
				return true;
			}
		}
		
		return false;
	}
	
	
	/**
	 * 检查用户密码
	 * @param pwd
	 * @return
	 */
	private boolean checkValidPassword(String pwd) {
		
		if(TextUtils.isEmpty(pwd)){
			Prompt.Alert(this, "密码不能为空");
			return false;
		}
		
		/*if(!Prompt.checkYZM(pwd)){
			Prompt.Alert(this, "请输入合法密码");
			return false;
		}*/
		return true;
	}
	
	
	/**
	 * 登陆成功后操作
	 * 1. 用户已绑定。直接切换
	 * 2。 用户未绑定，需要判断是否合并
	 * @param obj
	 */
	public void loginSuccess(final GsonResponse3.loginResponse obj) {
		
//		String oldUserid = ActiveAccount.getInstance(this).getUID();
		
		// 用户没有绑定
		if(ActiveAccount.volideUserNoBinding(this)){
			
			final int diarySum = DiaryManager.getInstance().getLocalDiarySum();
			
			// 检查是否有离线任务，弹出是否合并提示框
			if( diarySum > 0 ){
				// 合并日记提示框
				Xdialog xdialog = new Xdialog.Builder(this)
				.setMessage("是否将"+ diarySum +"条内容合并到该账号内？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						
						// mishare 2014-4-8
						CmmobiClickAgentWrapper.onEvent(MicShareUserLoginActivity.this, "merge",diarySum+"");
						
						margeAccount(obj,true);
						launchLooklook(obj);
						
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						margeAccount(obj,false);
						launchLooklook(obj);
					}
				}).create();
				xdialog.setCancelable(false);;
				xdialog.show();
			}else{
				// 本地没有日记，无需合并
				margeAccount(obj,false);
				launchLooklook(obj);
			}
			
		}else{
			
			// 直接完成登陆
			margeAccount(obj,false);
			launchLooklook(obj);
			
		}
		
		
	}
	
	/**
	 * 合并未上传的网络任务
	 * @param srcuid
	 * @param desuid
	 */
	private void mergeNetworkTask_del(String srcuid, String desuid) {
		Log.i("==WR==", "srcuid = " + srcuid + "; desuid = " + desuid);
		AccountInfo oldAI = (AccountInfo) StorageManager.getInstance().getItem(
				AccountInfo.AccountInfoKey + srcuid, AccountInfo.class);
		AccountInfo newAI =AccountInfo.getInstance(desuid);
		List<NetworkTaskInfo> infos = new ArrayList<NetworkTaskInfo>();
		if (ZStringUtils.emptyToNull(srcuid) != null
				&& ZStringUtils.emptyToNull(desuid) != null && oldAI != null
				&& newAI != null && oldAI.tasks != null && newAI.tasks != null) {
			synchronized (oldAI.tasks) {
				for(Iterator<NetworkTaskInfo> it = oldAI.tasks.iterator(); it.hasNext();) {
					NetworkTaskInfo info = it.next();
					if(info != null) {
						info.mergeNetworkTaskInfo(oldAI, newAI, desuid);
//						newAI.updateNetworkTaskInfo(info);
						infos.add(info);
					}
				}
			}
			newAI.updateNetworkTaskInfo(infos);
			newAI.persist();
			NetworkTaskManager.getInstance(desuid).clearTasksIfNecessary();
			Log.i("==WR==", "Send Broadcast ACTION_UPDATE_NETWORKTASK!");
			Intent intent = new Intent(NetworkTaskManager.ACTION_UPDATE_NETWORKTASK);
			MainApplication.getAppInstance().sendLocalBroadcast(intent);
		}
	}
	
	/**
	 * 合并未上传的网络任务
	 * @param srcuid
	 * @param desuid
	 */
	private void mergeNetworkTask(String srcuid, String desuid,boolean isMerge) {
		if(!isMerge)return;
		Log.i("==WR==", "srcuid = " + srcuid + "; desuid = " + desuid);
		DiaryManager diarymanager = DiaryManager.getInstance();
		AccountInfo oldAI = (AccountInfo) StorageManager.getInstance().getItem(
				AccountInfo.AccountInfoKey + srcuid, AccountInfo.class);
		AccountInfo newAI =AccountInfo.getInstance(desuid);
		List<NetworkTaskInfo> infos = new ArrayList<NetworkTaskInfo>();
		if (ZStringUtils.emptyToNull(srcuid) != null
				&& ZStringUtils.emptyToNull(desuid) != null && oldAI != null
				&& newAI != null && oldAI.tasks != null && newAI.tasks != null) {
			synchronized (oldAI.tasks) {
				for(Iterator<NetworkTaskInfo> it = oldAI.tasks.iterator(); it.hasNext();) {
					NetworkTaskInfo info = it.next();
					if(info != null) {
//						info.mergeNetworkTaskInfo(oldAI, newAI, desuid);
//						newAI.updateNetworkTaskInfo(info);
						String olddiaryuuid = info.diaryuuid;
						if(diarymanager != null && ZStringUtils.emptyToNull(olddiaryuuid)!= null) {
							MyDiary myDiary = diarymanager.findMyDiaryByUUID(olddiaryuuid);
							info = new NetworkTaskInfo(myDiary, INetworkTask.TASK_TYPE_CACHE);// 设置数据源
						}
						if(info != null) {
							infos.add(info);
						}
					}
				}
			}
			newAI.updateNetworkTaskInfo(infos);
			newAI.persist();
			NetworkTaskManager.getInstance(desuid).clearTasksIfNecessary();
			NetworkTaskManager.getInstance(desuid).pauseAllTask();
			Log.i("==WR==", "Send Broadcast ACTION_UPDATE_NETWORKTASK!");
			Intent intent = new Intent(NetworkTaskManager.ACTION_UPDATE_NETWORKTASK);
			MainApplication.getAppInstance().sendLocalBroadcast(intent);
		}
	}
	
	/**
	 * 切换账户
	 * 在保存新用户数据之前调用
	 * 
	 * @param obj 登陆成功返回
	 * @param isMarge 是否合并
	 */
	private void margeAccount(GsonResponse3.loginResponse obj, boolean isMarge){
		
		String userid = ActiveAccount.getInstance(this).getLookLookID();
		String newUserid = obj.userid;
		
		try {
			ActiveAccount currentaccount = ActiveAccount.getInstance(this);
			NetworkTaskManager.getInstance(currentaccount.getLookLookID()).shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		saveNewAccount(obj);
		
		
		if(isMarge)
			OfflineTaskManager.getInstance().addMergerAccountTask( newUserid, userid);
		// 本地合并
		new myAsyncTask().execute(userid,newUserid,isMarge==true?"1":"0");
	}
	
	private void mergeDiaryManager(String userid, String newUserid,boolean isMerge){
		//.获取两个账号信息
		AccountInfo oldAI = (AccountInfo) StorageManager.getInstance().getItem(
				AccountInfo.AccountInfoKey + userid, AccountInfo.class);
		AccountInfo newAI=AccountInfo.getInstance(newUserid);
		//.获取diarymanager信息
		if(oldAI!=null&&newAI!=null&&isMerge){
			//.在新账号中加入旧账户日记数据
			ArrayList<MyDiaryList> oldMyDiaryList=oldAI.dataEntities.dairyGroupList;
			ArrayList<MyDiaryList> newMyDiaryList=newAI.dataEntities.dairyGroupList;
			//合并日记组
//			newMyDiaryList.addAll(oldMyDiaryList);
			//重新排序
			Collections.sort(newMyDiaryList,
					new DiaryManager.DiaryGroupComparator());
			//.在新账号中加入旧账户日记映射
			for(int i=0;i<oldMyDiaryList.size();i++){
				MyDiaryList myDiaryList=oldMyDiaryList.get(i);
				myDiaryList.userid=newUserid;
				MyDiary myDiary=SqliteDairyManager.getInstance().getDiaryByUUID(myDiaryList.diaryuuid);
				if(myDiary!=null&&!"4".equals(myDiary.diary_source_type)){
					myDiary.userid=newUserid;
					myDiary.replaceMediaMapping(oldAI, newAI);
					newMyDiaryList.add(myDiaryList);
				}
				
			}
		}
		//删除临时账户目录
		String oldUserPath=LookLookActivity.SDCARD_PATH+"/.looklook/"+userid;
		File f=new File(oldUserPath);
		if(f.exists())
			deleteDir(f);
		//删除临时账户mediamapping（非日记相关）
		SqlMediaManager.getInstance().delMediaBat(userid, -1, -1, -1, -1);
		newAI.persist();
	}
	
	private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }
	
	class myAsyncTask extends AsyncTask<String,Void, Void>{
		
		@Override
		protected void onPreExecute() {
			Prompt.showProgressDialog(getBaseContext());
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {
			mergeDiaryManager(params[0],params[1],"1".equals(params[2])?true:false);
			// 未上传任务合并
			mergeNetworkTask(params[0],params[1],"1".equals(params[2])?true:false);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Log.d("myAsyncTask", "merge completed");
			Prompt.dimissProgressDialog();
			super.onPostExecute(result);
		}
	}
	
	

	/**
	 * 直接转到空间页
	 * @param obj
	 */
	private void launchLooklook(final GsonResponse3.loginResponse obj) {
		String UID = ActiveAccount.getInstance(this).getUID();
		MainApplication.getAppInstance().cleanAllActivity();
		NetworkTaskManager.getInstance(UID).updateLogin();
		Intent intent_exit = new Intent(LoginMainActivity.FLAG_CLOSE_ACTIVITY); 
		sendBroadcast(intent_exit);
		
		if(ImageLoader.getInstance().isInited())
			ImageLoader.getInstance().destroy();
		Intent intent = new Intent(this, LookLookActivity.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		MainApplication.getAppInstance().cleanAllActivity();
		startActivity(intent);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); 
		finish();
		
	}

	private String saveNewAccount(final GsonResponse3.loginResponse obj) {
		ActiveAccount.getInstance(this).logintype = "2";
		ActiveAccount.getInstance(this).updateLogin(obj);
		
		CmmobiClickAgentWrapper.setUserid(this, obj.userid);
		// save login data
		ActiveAccount.getInstance(this).persist();
		String UID = ActiveAccount.getInstance(this).getUID();
		if (UID != null) {
			AccountInfo.getInstance(UID).persist();
		}
		return UID;
	}
	
	
}
