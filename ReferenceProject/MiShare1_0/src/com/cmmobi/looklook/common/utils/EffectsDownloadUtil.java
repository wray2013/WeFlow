package com.cmmobi.looklook.common.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.core.ZToast;
import cn.zipper.framwork.io.network.ZHttpReader;
import cn.zipper.framwork.io.network.ZHttpResponse;
import cn.zipper.framwork.io.network.ZProxy;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.downloader.EffectLibDownloader;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobivideo.utils.PluginUtils;

import effect.XEffectJniUtils;

public class EffectsDownloadUtil implements Callback {
	public static final String APK_VERSION = "com.cmmobi.looklook";
	public static final String VERSION_DEFAULT = "-1";
	public static final String DOWNLOAD_EFFECTS_FINISHED = "download_effects_finished";
	public static String filePath;
	
	private static EffectsDownloadUtil ins = null;

	private String effectsURL;
	private Context context;
	private String effectsVersion;
	private String apkVersion;
	private Handler handler;
	private static Activity activity;

	public enum Result {
		SO_LARGER, EQUATION, APK_LARGER
	};

	public static EffectsDownloadUtil getInstance(Activity activity) {
		EffectsDownloadUtil.activity = activity;
		if(ins==null){
			ins = new EffectsDownloadUtil(activity);
		}
		return ins; 
	}

	private EffectsDownloadUtil(Context context) {
		this.context = context;

		handler = new Handler(this);
	}



	public boolean checkEffects() {
		return false;
		/*boolean needDownload = false;
		apkVersion = getAPKVersion();
		
		if (PluginUtils.isPluginMounted()) {
			effectsVersion = XEffectJniUtils.getInstance().getJniVersion();
			
			if (effectsVersion != null) {
				Result resu = copmareVersions(apkVersion, effectsVersion);
				switch (resu) {
				case APK_LARGER:
					needDownload = true;
//					Requester2.getEffects(handler, apkVersion);
					EffectLibDownloader downloader = new EffectLibDownloader(activity, false);
					downloader.askForDownloadEffectLib(null);
					break;
				case SO_LARGER:
				case EQUATION:
					needDownload = false;
					break;
				}
			} else {
				needDownload = true;
//				Requester2.getEffects(handler, apkVersion);
				EffectLibDownloader downloader = new EffectLibDownloader(activity, false);
				downloader.askForDownloadEffectLib(null);
			}
		} else {
			needDownload = true;
//			Requester2.getEffects(handler, apkVersion);
			EffectLibDownloader downloader = new EffectLibDownloader(activity, false);
			downloader.askForDownloadEffectLib(null);
		}
		
		return needDownload;*/
	}

	private Result copmareVersions(String apkVersion, String effectVersion) {
/*		if(apkVersion.length() == 3){
			apkVersion += ".0";
		}
		if(effectVersion.length() == 3){
			effectVersion += ".0";
		}*/
		apkVersion = format(apkVersion);
		effectVersion = format(effectVersion);
		
		long apkTmp = 0;
		long effectTmp = 0;
		
		try{
			apkTmp = Long.parseLong(apkVersion.replace(".", "00000").replaceFirst("^0*", ""));
			effectTmp = Long.parseLong(effectVersion.replace(".", "00000").replaceFirst("^0*", ""));
		}catch(NumberFormatException e){
			e.printStackTrace();
		}


		if (apkTmp > effectTmp) {
			return Result.APK_LARGER;
		} else if (apkTmp < effectTmp) {
			return Result.SO_LARGER;
		} else {
			return Result.EQUATION;
		}
	}
	
	private String format(String version){
		String[] array = version.split("\\.");
		if(array.length<3){
			for(int i=0; i<3-array.length; i++){
				version = version + ".0";
			}
		}
		
		return version;
	}

	/**
	 * 判断特效库是否可用，版本是否跟APK版本一致 特效库不可用时，下载特效库，下载完成后发广播
	 * 
	 * @return
	 */
/*	private boolean checkPlugin() {

		if (PluginUtils.isPluginMounted()) {
			try {
				// XAudioRecorder audioRecord = new XAudioRecorder(null);
				// audioRecord = null;
				return true;
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		return false;
	}*/

	private String getAPKVersion() {
		String version_code = VERSION_DEFAULT;
		try {
			version_code = context.getPackageManager().getPackageInfo(
					APK_VERSION, 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return version_code;
	}

	private void showPluginDownloadDialog() {
		ZDialog.dismiss();
		ZDialog.show(R.layout.dialog_ask_download_plugin, true, true, context);
		ZDialog.getZViewFinder().findTextView(R.id.yes_download_plugin)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						//Requester2.getEffects(handler, apkVersion);
						DownloadEffectsAsync dea = new DownloadEffectsAsync();
						dea.execute(filePath);
						ZDialog.dismiss();
					}
				});
		ZDialog.getZViewFinder().findTextView(R.id.no_cancel)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						ZDialog.dismiss();
					}
				});
	}

	private class DownloadEffectsAsync extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				String url = params[0];
				File webimage_file = new File(url);
				if (webimage_file.exists()) {
					webimage_file.delete();
				}
				Prompt.Alert("开始下载特效库！");
				while (1 <= 3) {
					FileOutputStream mOutput = null;
					ZHttpResponse httpResponse;
					ZHttpReader reader;
					HttpURLConnection connection = null;
					try {

						ZProxy proxy = ZProxy.getZProxy();
						if (proxy == null) {
							connection = (HttpURLConnection) new URL(url)
									.openConnection();
						} else {
							connection = (HttpURLConnection) new URL(url)
									.openConnection(proxy.toProxyObject());
						}

						connection.setConnectTimeout(10000);
						connection.setReadTimeout(5000);
						connection.connect();

						httpResponse = new ZHttpResponse();
						httpResponse.setResponseCode(connection
								.getResponseCode());
						httpResponse.setHeaders(connection.getHeaderFields());
						httpResponse
								.setInputStream(connection.getInputStream());

					} catch (Exception e) {
						e.printStackTrace();
						httpResponse = null;
					}

					try {

						reader = new ZHttpReader(httpResponse.getInputStream(),
								null);
						byte[] imageData = reader.readAll(0);

						// save file
						mOutput = new FileOutputStream(webimage_file, false);

						mOutput.write(imageData);

					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						Intent intent = new Intent(DOWNLOAD_EFFECTS_FINISHED);
						intent.putExtra("effectsurl", url);
						LocalBroadcastManager.getInstance(context)
								.sendBroadcast(intent);
						if (mOutput != null) {
							try {
								mOutput.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						
//						EffectsDownloadUtil.activity.recreate();
						System.loadLibrary(url);
						Prompt.Alert("特效库下载完成！");
					}
				}
			} else {
				ZToast.showShort("SD卡未挂载！");
			}
			return null;
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub

		switch (msg.what) {
		case Requester3.RESPONSE_TYPE_GET_EFFECTS:
			GsonResponse3.getEffectResponse response = (GsonResponse3.getEffectResponse) msg.obj;
			if (response != null) {
				if (response.effects != null) {
					effectsURL = response.effects.effectsurl;
					filePath = PluginUtils.getMainLibraryPath();
					showPluginDownloadDialog();
				}
			}
			break;

		default:
			break;
		}
	
		return false;
	}
}
