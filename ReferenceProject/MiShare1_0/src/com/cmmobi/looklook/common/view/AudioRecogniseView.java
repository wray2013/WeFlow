package com.cmmobi.looklook.common.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.core.ZToast;
import cn.zipper.framwork.io.network.ZHttp2;
import cn.zipper.framwork.io.network.ZHttpResponse;
import cn.zipper.framwork.io.network.ZNetworkStateDetector;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.utils.MD5;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.MediaValue;
import com.cmmobi.looklook.info.profile.PrivateCommonMessage;
import com.cmmobi.looklook.prompt.Prompt;
import com.iflytek.msc.AudioRecognizer;

public class AudioRecogniseView extends FrameLayout implements View.OnClickListener,Callback{

	
	public AudioRecogniseView(Context context) {
		super(context);
		this.context = context;
		init(context,false);
	}
	public AudioRecogniseView(Context context,boolean noTouchRecong) {
		super(context);
		this.context = context;
		init(context,noTouchRecong);
	}
	public AudioRecogniseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init(context,false);
    }
    public AudioRecogniseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init(context,false);
    }
    
    private Context context;
	private String localPath;
	private Handler handler;
		
	/**
	 * 必须设置音频路径
	 * @param localPath
	 */
	public void setLocalPath(String localPath){
		this.localPath = localPath;
	}
	
	private void init(Context context,boolean noTouchRecong){

		View view = LayoutInflater.from(context).inflate(R.layout.view_audio_recognise, null);
		this.addView(view);
		
		// notouch = true. 无点击
		if(!noTouchRecong){
			view.findViewById(R.id.btn_audio_recognise).setOnClickListener(this);
			view.findViewById(R.id.btn_audio_recognise).setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					return false;
				}
			});
		}
		
		handler = new Handler(this);
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_audio_recognise:
			if(TextUtils.isEmpty(localPath)){
				ZToast.showShort("提示：该音频不存在");
			}else{
				if(ZNetworkStateDetector.isConnected()){
					ZDialog.show(R.layout.progressdialog, true, true, context);
					startRecognise();
				}else{
					Prompt.Alert(this.getContext(), "您的网络不给力呀");
				}
			}
			break;
		default:
			break;
		}
	}
	
	private PrivateCommonMessage msgdata;
	private View vdian;
	/**
	 * 设置音频转文字控件需要的path
	 * @param path
	 */
	public void setPrivateMsgData(PrivateCommonMessage item, View vdian){
		msgdata = item;
		this.vdian = vdian;
	}
	
	/**
	 * 阅读状态
	 * onclick
	 */
	public void readAudioState(){
		// 表示阅读的是接收到的语音。并且是未读状态
		if(msgdata.r_msg!=null && msgdata.r_msg.notReadLocalAudio ){
			msgdata.r_msg.notReadLocalAudio = false;
		}
		if(vdian!=null){
			vdian.setVisibility(View.GONE);
		}
	}
	
	/**
	 * 点击后 
	 * 如果是网络地址则下载后
	 * 转文字
	 */
	private void startRecognise(){
		
		String uid = ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID();
		MediaValue mv = AccountInfo.getInstance(uid).mediamapping.getMedia(uid, localPath);
		if (MediaValue.checkMediaAvailable(mv, 3)) {
			//  hit:
			File file = new File(Environment.getExternalStorageDirectory() + mv.localpath);
			if(file.exists() && file.isFile()){
				_startRecognizer(file.getAbsolutePath());
			}else{
				if(localPath.startsWith("http:")){
					loadAudio(localPath,4);
				}else{
					File file1 = new File(Environment.getExternalStorageDirectory() + localPath);
					if(file1.exists() && file1.isFile()){
						_startRecognizer(file1.getAbsolutePath());
					}else{
						ZToast.showShort("提示：该音频不存在");
					}
				}
			}
		} else {
			// not hit:
			if(localPath.startsWith("http:")){
				loadAudio(localPath,4);
			}else{
				File file = new File(Environment.getExternalStorageDirectory() + localPath);
				if(file.exists() && file.isFile()){
					_startRecognizer(file.getAbsolutePath());
				}else{
					ZToast.showShort("提示：该音频不存在");
				}

			}
		}
	}
	
	/**
	 *  下载录音
	 * @param url
	 * @param belong
	 */
	private void loadAudio(String url,int belong) {
		new DownloadTask().execute(url,belong);
	}
	
	private void _startRecognizer(String filePath) {
		ZLog.e( "recognizer path = " + filePath);
		AudioRecognizer a = new AudioRecognizer(handler, filePath);
		a.recognizer();
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case AudioRecognizer.HANDLER_QISR_RESULT:
			try {
				ZDialog.dismiss();
				String s = (String) msg.obj;
//				if(!TextUtils.isEmpty(s)){
				showRecognStr(s);
//				}else{
//					ZToast.showShort("转换文字没有结果");
//				}
				
				readAudioState();
			} catch (Exception e) {
			}
			break;
		default:
			break;
		}
		return false;
	}
	
	
	/**
	 * 显示转换后的文字
	 * @param str
	 */
	private void showRecognStr(String str){
		
		final PopupWindow mpop = getPopupWindow(context, R.layout.pop_show_recogn_result, android.R.style.Animation_Toast);
		View popview = mpop.getContentView();
		TextView tvResult = (TextView) popview.findViewById(R.id.tv_recogn_result);
		tvResult.setText(str);
		Button cancel = (Button) popview.findViewById(R.id.btn_recogn_cancel);
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mpop.dismiss();
			}
		});
		mpop.showAtLocation(popview, Gravity.CENTER, 0, 0);
	}
	
	
	/**
	 * 获取pop
	 * @param context
	 * @param layout
	 * @param animid
	 * @return
	 */
	public static PopupWindow getPopupWindow(Context context, int layout,
			int animid) {
		PopupWindow mPop = new PopupWindow(LayoutInflater.from(context)
				.inflate(layout, null), LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true);
		if (animid != -1) {
			mPop.setAnimationStyle(animid);
		}
		mPop.setBackgroundDrawable(new BitmapDrawable());
		return mPop;
	}
	

	
	/**
	 * 下载音频并转换文字
	 *
	 */
	class DownloadTask extends AsyncTask<Object, Void, MediaValue> {
		String url;
		int belong;

		@Override
		protected MediaValue doInBackground(Object... param) {
			String audioPath = null;
			MediaValue result=null;
			try {
				url = (String) param[0];
				belong = (Integer) param[1];
				String statisUrl = "";
				if(param.length > 2)
				{
					statisUrl = (String) param[2];
				}
				ZHttp2 http2 = new ZHttp2();
				ZHttpResponse httpResponse = http2.get(url+statisUrl);
				if(null==httpResponse)return null;
				InputStream inputStream = httpResponse.getInputStream();
				if (inputStream == null) {
					return null;
				}
				String uid = ActiveAccount.getInstance(
						MainApplication.getAppInstance()).getUID();
				String Key = MD5.encode((uid + url).getBytes());
				audioPath = Environment.getExternalStorageDirectory()
						+ Constant.SD_STORAGE_ROOT + "/" + uid + "/audio/" + Key;
				File audioFile = new File(Environment.getExternalStorageDirectory()+audioPath);
				if (!audioFile.getParentFile().exists()) {
					audioFile.getParentFile().mkdirs();
				}
				FileOutputStream os = new FileOutputStream(audioFile);
				byte buffer[] = new byte[1024];
				long size=0;
				int len;
				while ((len=inputStream.read(buffer)) != -1) {
					os.write(buffer,0,len);
					size+=len;
				}
				os.flush();
				os.close();
				result = new MediaValue();
				result.UID = uid;
				result.Belong = belong;
				result.Direction = 1;
				result.MediaType = 3;
				result.url = url;
				result.localpath = audioPath;
				result.realSize = size;
				result.Sync = 1;
				result.SyncSize = size;
				result.totalSize = size;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
			return result;
		}

		@Override
		protected void onPostExecute(MediaValue result) {
			if (!MediaValue.checkMediaAvailable(result, 3)) {  
				return;
			}
			AccountInfo.getInstance(result.UID).mediamapping.setMedia(result.UID, url, result);
			_startRecognizer(Environment.getExternalStorageDirectory()+result.localpath);
		}
	}
	
	
}
