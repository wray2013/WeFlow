package com.cmmobi.looklook.downloader;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.HashMap;

import org.apache.http.client.utils.URIUtils;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Environment;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.URLUtil;
import android.widget.CompoundButton;
import android.widget.TextView;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.core.ZToast;
import cn.zipper.framwork.io.file.ZFileSystem;
import cn.zipper.framwork.io.file.ZFileWriter;
import cn.zipper.framwork.io.network.ZHttp2;
import cn.zipper.framwork.io.network.ZHttpReader;
import cn.zipper.framwork.io.network.ZHttpResponse;
import cn.zipper.framwork.utils.ZPercent;
import cn.zipper.framwork.utils.ZPercent.OnPercentChangedListener;
import cn.zipper.framwork.utils.ZThread;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobivideo.utils.PluginUtils;

public final class EffectLibDownloader implements Callback {
	
	private static final int MESSAGE_REQUEST_DOWNLOAD = 0xffffff01;
	private static final int MESSAGE_START_DOWNLOAD = 0xffffff02;
	
	private static final String TEMP = "TEMP_";
	
	
	private Activity activity;
	private Handler mainThreadHandler;
	private Handler handler;
	private HandlerThread thread;
	private int retryTimes;
	private long lastRepaintMills;
	private boolean needWorking;
	private boolean recreateActivityOnDownloadOK;
	private boolean isDownloadingCancel;
	private File file;
	private ZHttp2 http;
	private ZHttpReader reader;
	private ZFileWriter writer;
	private GsonResponse2.getEffectResponse effectResponse;
	
	
	/**
	 * 
	 * @param activity
	 * @param recreateActivityOnDownloadOK: 下载完成后是否重启当前Activity (以便挂载so库, 并重置软件状态);
	 */
	public EffectLibDownloader(Activity activity, boolean recreateActivityOnDownloadOK) {
		this.activity = activity;
		this.recreateActivityOnDownloadOK = recreateActivityOnDownloadOK;
		
		thread = new HandlerThread("EffectLibDownloader");
		thread.start();
		handler = new Handler(thread.getLooper(), this);
		mainThreadHandler = new Handler(Looper.getMainLooper());
	}
	
	/**
	 * 询问是否下载特效库;
	 * @param button: 触发询问对话框的Button, 在对话框被关闭后, 可以自动恢复Button的选中状态 (可以传null);
	 */
	public void askForDownloadEffectLib(final CompoundButton button) {
		ZDialog.dismiss();
		ZDialog.show(R.layout.dialog_ask_download_plugin, true, true, activity, true);
		ZDialog.getZViewFinder().findTextView(R.id.yes_download_plugin).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ZDialog.dismiss();
				ZDialog.show(R.layout.dialog_download_persent, false, true, activity, true);
				ZDialog.getZViewFinder().findTextView(R.id.cancel).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						isDownloadingCancel = true;
						ZDialog.dismiss();
						stop();
					}
				});
				isDownloadingCancel = false;
				handler.sendEmptyMessage(MESSAGE_REQUEST_DOWNLOAD);
			}
		});

		ZDialog.getZViewFinder().findTextView(R.id.no_cancel).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ZDialog.dismiss();
			}
		});
		
		ZDialog.getDialog().setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				if (button != null) {
					button.setChecked(false);
				}
			}
		});
		
		ZDialog.getDialog().setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				if (button != null) {
					button.setChecked(false);
				}
			}
		});
	}
	
	
	public void stop() {
		if (reader != null) {
			reader.stop();
			reader.close();
		}
		if (writer != null) {
			writer.close();
		}
		if (http != null) {
			http.close();
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case MESSAGE_REQUEST_DOWNLOAD:
			mainThreadHandler.post(new Runnable() {
				
				@Override
				public void run() {
					Requester2.getEffects(handler, MainApplication.getAppVersionName());
				}
			});
			break;
			
		case Requester2.RESPONSE_TYPE_GET_EFFECTS:
			effectResponse = (GsonResponse2.getEffectResponse) msg.obj;
			if (effectResponse != null) {
				
				if (!isDownloadingCancel) {
					boolean b1 = effectResponse.status.equals("0");
					boolean b2 = !TextUtils.isEmpty(effectResponse.effects.effectsurl);
					boolean b3 = URLUtil.isValidUrl(effectResponse.effects.effectsurl);
					
					if (b1 && b2 && b3) {
						retryTimes = 0;
						handler.sendEmptyMessage(MESSAGE_START_DOWNLOAD);
					} else {
						ZToast.showLong(R.string.network_exception);
						ZDialog.dismiss();
					}
					
				} else {
					ZToast.showLong(R.string.cancel_install);
					ZDialog.dismiss();
				}
				
			} else {
				retryTimes ++;
				if (retryTimes < 5) {
					handler.sendEmptyMessage(MESSAGE_REQUEST_DOWNLOAD);
				} else {
					ZToast.showLong(R.string.network_exception);
					ZDialog.dismiss();
				}
			}
			break;
			
		case MESSAGE_START_DOWNLOAD:
			download();
			break;
		}
		
		return false;
	}
	
	private void download() {
		needWorking = true;
		
		while (needWorking && !isDownloadingCancel) {
			retryTimes ++;
			if (retryTimes > 5) {
				mainThreadHandler.post(new Runnable() {
					
					@Override
					public void run() {
						ZDialog.dismiss();
						ZToast.showShort(R.string.network_exception);
					}
				});
				break;
			} else {
				work();
			}
		}
	}
	
	private void work() {
		try {
			HashMap<String, String> headers = new HashMap<String, String>();
			file = new File(PluginUtils.getMainLibraryPath(), TEMP + "libeffect_Effects.so" + effectResponse.effects.effectsid);
			long fileLength = 0;
			
			if (file.exists()) {
				fileLength = file.length();
				headers.put("range", "bytes=" + fileLength + "-");
			} else {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			
			http = new ZHttp2();
			http.setHeaders(headers);
			ZHttpResponse response = http.get(effectResponse.effects.effectsurl);
			response.printHeaders();
			
			boolean wasDownloaded = false;
			
			if (response.getResponseCode() == HttpURLConnection.HTTP_OK) { // 从头下载;
				writer = new ZFileWriter(file, false, null);
				retryTimes = 0;
				ZLog.e(200);
			} else if (response.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) { // 断点续传;
				writer = new ZFileWriter(file, true, null);
				retryTimes = 0;
				ZLog.e(206);
			} else if (response.getResponseCode() == 416 && fileLength > 0) {
				wasDownloaded = true;
				retryTimes = 0;
				ZLog.e(416);
			}
			
			OnPercentChangedListener listener = new OnPercentChangedListener() {
				
				@Override
				public void onPercentChanged(ZPercent percent) {
					Object object = percent.getObject();
					if (object != null) {
						byte[] bytes = (byte[]) object;
						writer.writeBlock(0, bytes);
					}
					needWorking = false;
					updatePersent(false);
					
					if (isDownloadingCancel) {
						stop();
					}
					ZThread.sleep(10);
				}
			};
			
			long contentLength = Long.valueOf(response.getHeader(HTTP.CONTENT_LEN));
			
			reader = new ZHttpReader(response.getInputStream(), listener);
			
			if (wasDownloaded) {
				reader.getZPercent().setMaxValue(1);
				reader.getZPercent().setCurrentValue(1, null);
			} else {
				reader.getZPercent().setCurrentValue(fileLength, null);
				int readunit = (int) ((fileLength + contentLength)/20);
				readunit = readunit + 8 - readunit%8;
				needWorking = !reader.readByBlockSize2(fileLength + contentLength, readunit);
			}
			
			updatePersent(true);
			
		} catch (Exception e) {
			e.printStackTrace();
			needWorking = true;
			
		} finally {
			stop();
		}
	}
	
	/**
	 * 修改下载百分比;
	 * @param must
	 */
	private void updatePersent(boolean must) {
		boolean b = TimeHelper.getInstance().now() - lastRepaintMills >= 500;
		
		if (b || must) {
			lastRepaintMills = TimeHelper.getInstance().now();
			
			mainThreadHandler.post(new Runnable() {
				
				@Override
				public void run() {
					String string = ZApplication.getInstance().getString(R.string.downloading) + ": " + reader.getZPercent().getPercentInt() + "%";
					TextView textView = ZDialog.getZViewFinder().findTextView(R.id.text_view);
					if (textView != null) {
						textView.setText(string);
					}
					ZLog.e(string);
					if (reader.getZPercent().isOneHundredPercent()) {
						File[] files = file.listFiles();
						if (files != null) {
							for (int i=0; i<files.length; i++) {
								if (!files[i].getName().equals(file.getName())) {
									files[i].delete();
								}
							}
						}
						
						File newNameFile = new File(PluginUtils.getMainLibraryPath(), "libeffect_Effects.so");
						file.renameTo(newNameFile);
						
						ZFileSystem.copy(newNameFile, new File(PluginUtils.getSecondaryLibraryPath(), "libeffect_Effects.so"));
						
						mainThreadHandler.postDelayed(new Runnable() {

							@Override
							public void run() {
								ZDialog.dismiss();
								if (recreateActivityOnDownloadOK) {
									activity.recreate();
								}
								ZToast.showShort(R.string.install_ok);
							}
						}, 1500);
					}
				}
			});
		}
	}

}
