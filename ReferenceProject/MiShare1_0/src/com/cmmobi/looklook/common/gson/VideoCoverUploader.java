package com.cmmobi.looklook.common.gson;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import android.content.Intent;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.device.ZSimCardInfo;
import cn.zipper.framwork.io.file.ZFileReader;
import cn.zipper.framwork.utils.ZPercent;
import cn.zipper.framwork.utils.ZPercent.OnPercentChangedListener;
import cn.zipper.framwork.utils.ZStringUtils;

import com.cmmobi.looklook.activity.DiaryEditPreviewActivity;
import com.cmmobi.looklook.activity.LookLookActivity;
import com.cmmobi.looklook.activity.SettingPersonalInfoActivity;
import com.cmmobi.looklook.activity.SpaceCoverActivity;
import com.cmmobi.looklook.common.utils.Base64Utils;
import com.cmmobi.looklook.fragment.SettingFragment;
import com.cmmobi.looklook.fragment.ZoneBaseFragment;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;

public class VideoCoverUploader extends Thread implements Callback {

	private final static String TAG = "VideoCoverUploader";
	
	private transient Socket mSocket;
	private transient ZFileReader reader;
//	private transient DataInputStream dataInputStream;
	private transient BufferedReader dataInputStream;
	private transient DataOutputStream dataOutputStream;
	
	private int retryTimes;
	private boolean mStop;
	
	// important parameters
	private uploadParam inputParam;
	private String userid;
	private String nickname;
	private String os;
	private long uploadedSize; //已经下载的文件大小
	private String filepath;
	private Handler mHandler;
	private CountDownLatch processSignal = new CountDownLatch(1);
	
	public static class uploadParam {
		public File mfile;
		private String ip;
		private int port;
		public int rotation;
		public String filetype;
		public String businesstype;
		public String diaryid = "";
		public String attachmentid = "";
		public int height = 0;
		public int width = 0;
	}
	
	public VideoCoverUploader(uploadParam params) {
		retryTimes = 3;
		uploadedSize = 0;
		this.inputParam = params;
		userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		nickname = ActiveAccount.getInstance(ZApplication.getInstance()).nickname;
		os = "android" + ZSimCardInfo.getSystemReleaseVersion();
		filepath = getVideoCoverUpPath();
		mHandler = new Handler(this);
	}
	
	public void stopThread() {
		this.mStop = true;
	}
	
	public void excute() {
		if (this.inputParam != null) {
			this.start();
		}
	}
	
	private void sendPortraitBroadcast(String picurl){
		if(this.inputParam.businesstype.equals("4") && this.inputParam.filetype.equals("4")){
			Intent intent = new Intent(SettingPersonalInfoActivity.PORTRAIT_UPLOAD_SUCCESS);
			intent.putExtra("picurl", picurl);
			LocalBroadcastManager lbc = LocalBroadcastManager.getInstance(null);
			lbc.sendBroadcast(intent);
		}else if(this.inputParam.businesstype.equals("5") && this.inputParam.filetype.equals("4")){
			Intent intent = new Intent(SpaceCoverActivity.ACTION_SPACECOVER_UPLOAD_COMPLETED);
			intent.putExtra("picurl", picurl);
			intent.putExtra("filepath", inputParam.mfile.getAbsolutePath());
			LocalBroadcastManager lbc = LocalBroadcastManager.getInstance(null);
			lbc.sendBroadcast(intent);
		}  
		else if (this.inputParam.businesstype.equals("6") && this.inputParam.filetype.equals("4")) {
			Intent intent = new Intent(DiaryEditPreviewActivity.INTENT_ATTACH_COVER_CHANGED);
			LocalBroadcastManager lbc = LocalBroadcastManager.getInstance(null);
			lbc.sendBroadcast(intent);
			System.out.println("===== cover ===== upload success");
		}
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Requester3.requestSocketIp(mHandler);
		try {
			processSignal.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		upload();
	}
	
	private void upload() {
		try {
			
			if (mSocket == null) {
				Log.d(TAG, "重新创建socket连接");
				mSocket = new Socket();
				mSocket.setKeepAlive(true);
				mSocket.setSoTimeout(10 * 1000);
				mSocket.connect(new InetSocketAddress(inputParam.ip, inputParam.port), 10 * 1000);

				dataOutputStream = new DataOutputStream(mSocket.getOutputStream());
//				dataInputStream = new DataInputStream(mSocket.getInputStream());
				dataInputStream = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
			}
			
			if (inputParam.mfile == null || !inputParam.mfile.exists()) {
				Log.d(TAG, "封面文件不存在！");
				sendPortraitBroadcast(null);
				return;
			}

			String requestHeader = makeHeader(inputParam.mfile, 0, 0,
					filepath, "1", inputParam.rotation,
					inputParam.filetype, inputParam.businesstype,
					inputParam.diaryid, inputParam.attachmentid, 0,
					inputParam.height, inputParam.width);

			dataOutputStream.write(requestHeader.getBytes());
			ZLog.e(">> Socket Request Header (file:"
					+ inputParam.mfile.getAbsolutePath() + "): "
					+ requestHeader);

			String line = dataInputStream.readLine() + ";";
			ZLog.e("<< Socket Response Header: " + line);
			if (line.equals("null;")) {
				retry();
				return;
			}

			int dataover = getIntValue(line, "dataover");

			switch (dataover) {
			case 0:
				Log.d(TAG, "头信息错误，重试！");
				retry();
				break;
			case 4:
				Log.d(TAG, "文件传输错误，重试！");
				retry();
				break;
			case 2:
				ZLog.alert();
				ZLog.e("File: " + inputParam.mfile.getAbsolutePath()
						+ " Already Uploaded.");
				sendPortraitBroadcast(getStringValue(line, "picurl"));
				break;
			case 1:
				long skip = Long.valueOf(getStringValue(line, "position"));
				OnPercentChangedListener listener = new OnPercentChangedListener() {

					@Override
					public void onPercentChanged(ZPercent percent) {
						try {
							// if (getState() == STATE_PAUSED
							// || getState() == STATE_REMOVED) {
							if (mStop) {
								Log.d(TAG, "上传过程终止");
								if (reader != null) {
									reader.stop();
								}
							}
							Object object = percent.getObject();
							if (object != null) {
								byte[] bytes = (byte[]) object;
								dataOutputStream.write(bytes);
								addStep(bytes.length);
							}
						} catch (Exception e) {
							e.printStackTrace();
							if (reader != null) {
								reader.stop();
							}
						}
					}
				};

				reader = new ZFileReader(inputParam.mfile, listener);
				reader.skip(skip);
				reader.readByBlockSize2(inputParam.mfile.length(), 1024 * 4);
				reader.close();

				if (reader.isEnding()) {
					String line2 = dataInputStream.readLine() + ";";
					dataover = getIntValue(line2, "dataover");
					ZLog.e("<< Socket Write-Finish : " + line2);

					int dataover2 = -1;
					String picurl = "";
					if (dataover == 2) {
						Log.d(TAG, "单文件上传");
						String notify = makeHeader(inputParam.mfile, 0, 1,
								filepath, "1",
								inputParam.rotation, inputParam.filetype,
								inputParam.businesstype, inputParam.diaryid,
								inputParam.attachmentid, 0, inputParam.height,
								inputParam.width);
						ZLog.e(">> Socket Upload-Finish(Single file task) Notify: "
								+ notify);
						dataOutputStream.write(notify.getBytes());
						String line3 = dataInputStream.readLine() + ";";
						dataover2 = getIntValue(line3, "dataover");
						picurl = getStringValue(line3, "picurl");
						ZLog.e("<< Socket Write-Finish : " + line3);
						if (dataover2 == 3) {
							retryTimes = 0;
							ZLog.alert();
							ZLog.e("Files were all Uploaded.");
							sendPortraitBroadcast(picurl);
							return;
						}
					} else {
						retry();
						return;
					}
				}

			default:
				break;
			}
			return;
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, "上传过程中异常！");
			sendPortraitBroadcast(null);
		}
	}
	
	private void retry() {
		closeSocket();
		retryTimes --;
		if(retryTimes > 0) {
			Log.d(TAG, "任务重试！");
			upload();
		} else {
			Log.d(TAG, "封面上传失败！");
			sendPortraitBroadcast(null);
		}
	}
	
	private void closeSocket() {
		try {
			if (dataInputStream != null) {
				dataInputStream.close();
				dataInputStream = null;
			}
			if (dataOutputStream != null) {
				dataOutputStream.close();
				dataOutputStream = null;
			}
			if (mSocket != null) {
				mSocket.close();
				mSocket = null;
			}
			if (reader != null) {
				reader.close();
				reader = null;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	private void addStep(long step) {
		uploadedSize += step;
	}
	
	/**
	 * 从socket响应中获取字段值;
	 * 
	 * @param response
	 * @param key
	 * @return
	 */
	private String getStringValue(String response, String key) {
		String value = null;
		int index = response.indexOf(key);
		if (index > -1) {
			value = response.substring(index + key.length() + 1,
					response.indexOf(";", index + key.length() + 1));
		}
		return value;
	}
	
	/**
	 * 从socket响应中获取字段值;
	 * 
	 * @param response
	 * @param key
	 * @return
	 */
	private int getIntValue(String response, String key) {
		int value = 0;
		String string = getStringValue(response, key);
		if (string != null) {
			value = Integer.valueOf(string);
		}
		return value;
	}
	
	/**
	 * 组装请求头;
	 * 
	 * @param file
	 *            : 要上传的文件;
	 * @param nuid
	 *            : 子文件序号 (从0开始);
	 * @param over
	 *            : 是否已经完成上传 (0: 未结束. 1: 已结束);
	 * @param fileName
	 *            : 文件物理路径;
	 * @param type
	 *            : 文件个数 (1: 只有一个文件. n: 有多个文件);
	 * @param rotation
	 *            : 旋转角度 (0/90/180/270);
	 * @param filetype
	 *            : 旋转角度 (0/90/180/270);
	 * @param businesstype
	 *            : 业务类型 (1 日记 2 评论3 私信 4 陌生人消息);
	 * @param diaryid
	 *            : 日记表id;
	 * @param attachmentid
	 *            : 附件表id(根据businesstype的不同对于不同表中的id);
	 * @param isencrypt
	 *            : 音频文件是否加密 (0未加密 1已加密);
	 * @return
	 */
	private String makeHeader(File file, int nuid, int over, String fileName,
			String type, int rotation, String filetype, String businesstype,
			String diaryid, String attachmentid, int isencrypt, int height,
			int width) {
		long fileLength = 0;

		if (file != null) {
			fileLength = file.length();
		}

		String string = "appkey=11;" + "Content-Length="
				+ fileLength
				+ ";userid="
				+ userid + ";nuid=" + nuid + ";over=" + over
				+ ";attachpath=" + fileName + ";type=" + type + ";rotation="
				+ rotation + ";filetype=" + filetype + ";businesstype="
				+ businesstype + ";diaryid=" + diaryid + ";attachmentid="
				+ attachmentid + ";isencrypt=" + isencrypt + ";nickname=" 
				+ Base64Utils.encode(nickname) + ";height=" + height + ";width="
				+ width + ";os=" + os + "\r\n";

		String head = String.format("%05d", string.length());
		return head + string;
	}
	
	/**
	 * 当businesstype=6 即视频封面时，路径类似：
	 * cover/201307/25/0942370917379243459f9a6cf80f016dcc1d.jpg
	 * 其中cover部分固定不变，201307是“年和月”，25是“日”，
	 * 0942370917379243459f9a6cf80f016dcc1d.jpg是“时分”+不带“-”的uuid
	 * 
	 * @param uuid
	 * @return
	 */
	private String getVideoCoverUpPath() {
		String ret = null;
		String uuid = UUID.randomUUID().toString().replace("-", "");
		if (uuid != null && !"".equals(uuid)) {
			Calendar ca = Calendar.getInstance();
			int year = ca.get(Calendar.YEAR);// 获取年份
			int month = ca.get(Calendar.MONTH);// 获取月份
			int day = ca.get(Calendar.DATE);// 获取日
			int minute = ca.get(Calendar.MINUTE);// 分
			int hour = ca.get(Calendar.HOUR);// 小时

			String Prefix  = null;
			if(this.inputParam.businesstype.equals("4")){
				Prefix = "head_";
			}else if(this.inputParam.businesstype.equals("5")){
				Prefix = "userbackcover_";
			}else if(this.inputParam.businesstype.equals("6")){
				Prefix = "cover_";
			}else if(this.inputParam.businesstype.equals("7")){
				Prefix = "userbackground_";
			}
					
			String YMPart  = year + String.format("%02d", month) + "_";
			String DayPart = String.format("%02d", day) + "_";
			String NamePart= String.format("%02d", hour)
					+ String.format("%02d", minute) + uuid;
			String Suffix  = ".jpg";
			ret = Prefix + YMPart + DayPart + NamePart + Suffix;
			Log.d(TAG, "视频封面上传服务器地址:" + ret);
		}
		return ret;
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what) {
		case Requester3.RESPONSE_TYPE_GET_SOCKET:
			GsonResponse3.getSocketResponse res = (GsonResponse3.getSocketResponse) msg.obj;
			if (res != null && ZStringUtils.emptyToNull(res.port) != null
					&& ZStringUtils.emptyToNull(res.ip) != null) {
				inputParam.ip = res.ip;
				try {
					inputParam.port = Integer.valueOf(res.port.trim());
				} catch(NumberFormatException e) {
					e.printStackTrace();
				}
			}
			processSignal.countDown();
			break;
		}
		return false;
	}
	
}