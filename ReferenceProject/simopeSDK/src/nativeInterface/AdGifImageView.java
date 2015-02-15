package nativeInterface;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import nativeInterface.AdPlayView.SendVideoStateListener;
import nativeInterface.SmoAdMultiPlay.ADDesc;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.simope.yzvideo.R;
import com.simope.yzvideo.util.StringUtils;

public class AdGifImageView extends FrameLayout {

	public static final int AUTO_FRESH_TIME_REMAIN = 0x41;

	public static final int LOAD_SUCCES = 0x42;

	public static final int LOAD_ERROR = 0x43;

	private Context mContext;
	private Handler mHandler;
	private ImageView adCancleBtn;
	private TextView timeRemaining;
	private Button adLinkBtn;
	private LinearLayout pgb;
	private RelativeLayout adBackgrd;

	private String mPath;
	private int time;
	private SendVideoStateListener sendVideoStateListener;
	private ImageView ad_img_cancle;

	private GifImageView gifImageView;
	private GifDrawable gifDrawable;
	private boolean isPause = false;
	private ADDesc ad;

	public AdGifImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.ad_gif_image_view, this, true);
		mContext = context;
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case LOAD_SUCCES:	
					if (pic != null) {				
						if(ad==null){
							savePath=null;
							return;
						}
						show();
						if (ad.ad_type == SmoAdMultiPlay.AD_TYPE_START) {
							mHandler.sendEmptyMessage(AUTO_FRESH_TIME_REMAIN);
							if (sendVideoStateListener != null) {
								sendVideoStateListener.sendVideoBegintoBuff();
							}
						}
					}
					break;
				case LOAD_ERROR:
					if (pic != null) {
						release();
						if(ad==null){
							return;
						}
						if (ad.ad_type == SmoAdMultiPlay.AD_TYPE_START) {
							if (sendVideoStateListener != null) {
								sendVideoStateListener.sendVideoBegintoBuff();
								sendVideoStateListener.sendVideoStartPlay();
							}
						}
					}
					break;
				case AUTO_FRESH_TIME_REMAIN:
					if (ad.ad_type == SmoAdMultiPlay.AD_TYPE_START) {
						if (pic != null) {
							int pos = setRemainTime();
							if (timeRemaining.isShown()) {
								msg = obtainMessage(AUTO_FRESH_TIME_REMAIN);
								if (time < -1000) {
									onComplete();
								} else {
										sendMessageDelayed(msg,
												1000 - (pos % 1000));
								}
							}
						}
					}
					break;
				}
			}
		};
	}

	protected void onFinishInflate() {
		super.onFinishInflate();
		adBackgrd = (RelativeLayout) findViewById(R.id.ad_gifview_contain);
		timeRemaining = (TextView) (findViewById(R.id.ad_gif_imageClude)
				.findViewById(R.id.ad_time_remaining));
		pgb = (LinearLayout) (findViewById(R.id.ad_gif_imageClude)
				.findViewById(R.id.ad_load_prg));
		adCancleBtn = (ImageView) (findViewById(R.id.ad_gif_imageClude)
				.findViewById(R.id.ad_cancle_btn));
		adCancleBtn.setOnClickListener(ad_cancle_btn);
		adLinkBtn = (Button) (findViewById(R.id.ad_gif_imageClude)
				.findViewById(R.id.ad_link_btn));
		adLinkBtn.setOnClickListener(show_ad_detail);
		gifImageView = (GifImageView) findViewById(R.id.ad_img_show);
		ad_img_cancle = (ImageView) findViewById(R.id.ad_img_cancle);
		ad_img_cancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				release();
				if (sendVideoStateListener != null) {
					sendVideoStateListener.imgvCloseAd();
				}
			}
		});
	}

	public void onDestroy() {
		release();
		pic = null;
		if (fileFolder == null) {
			return;
		}
		File file = new File(fileFolder);
		deleteFile(file);
	}

	private void deleteFile(File file) {
		if (file.exists()) {
			if (file.isDirectory()) {
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					files[i].delete();
				}
			}
		}
	}

	private Map<String, String> pic = new HashMap<String, String>();
	private String savePath;

	public void init(ADDesc ad) {
		this.time = ad.duration * 1000;
		this.mPath = ad.multiPlayAddress.levelurl[0];
		this.ad = ad;
		if (ad.ad_type == SmoAdMultiPlay.AD_TYPE_START) {
			adCancleBtn.setVisibility(View.VISIBLE);
			pgb.setVisibility(View.VISIBLE);
			adBackgrd.setBackgroundColor(mContext.getResources().getColor(
					R.color.sdk_background));
		} else {
			adBackgrd.setBackgroundColor(mContext.getResources().getColor(
					R.color.no_color));
		}
		if (pic.containsKey(mPath)) {
//			if (new File(pic.get(mPath)).exists()) {
				savePath=pic.get(mPath);
				mHandler.sendEmptyMessage(LOAD_SUCCES);
//			} else {
//				pic.remove(mPath);
//				getImageOrGif(mPath);
//			}
		} else {
			getImageOrGif(mPath);
		}
	}

	private void show() {
		if (ad == null) {
			return;
		}
		pgb.setVisibility(View.GONE);
		if (ad.ad_type == SmoAdMultiPlay.AD_TYPE_START) {
			timeRemaining.setVisibility(View.VISIBLE);
			adLinkBtn.setVisibility(View.VISIBLE);
		} else {
			ad_img_cancle.setVisibility(View.VISIBLE);
			initGifImageClick();
		}
		if (ad.ad_show_mode == SmoAdMultiPlay.AD_SHOW_FULL) {
			adBackgrd.setBackgroundColor(mContext.getResources().getColor(
					R.color.sdk_background));
			android.widget.RelativeLayout.LayoutParams params = new android.widget.RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			params.addRule(RelativeLayout.CENTER_IN_PARENT);
			gifImageView.setLayoutParams(params);
			gifImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
			if (mPath.endsWith(".gif")) {
				try {
					gifDrawable = new GifDrawable(
							new File(savePath));
					gifImageView.setImageDrawable(gifDrawable);
					gifDrawable.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				gifImageView
						.setImageDrawable(bitmap2Drawable(getLoacalBitmap(savePath)));
			}
		} else {
			if (ad.ad_type == SmoAdMultiPlay.AD_TYPE_START) {
				adBackgrd.setBackgroundColor(mContext.getResources().getColor(
						R.color.sdk_background));
			} else {
				adBackgrd.setBackgroundColor(mContext.getResources().getColor(
						R.color.half_transparent));
			}
			WindowManager manager = (WindowManager) mContext
					.getSystemService(Context.WINDOW_SERVICE);
			Display display = manager.getDefaultDisplay();
			int lh = display.getHeight() / 2;
			if (mPath.endsWith(".gif")) {
				try {
					gifDrawable = new GifDrawable(new File(savePath));
					int d_h = gifDrawable.getIntrinsicHeight();
					int d_w = gifDrawable.getIntrinsicWidth();
					int lw = lh * d_w / d_h;
					android.widget.RelativeLayout.LayoutParams params2 = new android.widget.RelativeLayout.LayoutParams(
							lw, lh);
					params2.addRule(RelativeLayout.CENTER_IN_PARENT);
					gifImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
					gifImageView.setLayoutParams(params2);
					gifImageView.setImageDrawable(gifDrawable);
					gifDrawable.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				Bitmap bm = getLoacalBitmap(savePath);
				int bmHeight = bm.getHeight();
				int bmWidth = bm.getWidth();
				int lw = lh * bmWidth / bmHeight;
				android.widget.RelativeLayout.LayoutParams params2 = new android.widget.RelativeLayout.LayoutParams(
						lw, lh);
				params2.addRule(RelativeLayout.CENTER_IN_PARENT);
				gifImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
				gifImageView.setLayoutParams(params2);
				gifImageView.setImageDrawable(bitmap2Drawable(bm));
			}
		}
	}

	private void initGifImageClick() {
		gifImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (sendVideoStateListener != null) {
					sendVideoStateListener.showAdWebView(ad);
				}
			}
		});
	}

	public static Drawable bitmap2Drawable(Bitmap bitmap) {
		BitmapDrawable bd = new BitmapDrawable(bitmap);
		Drawable d = (Drawable) bd;
		return d;
	}

	public static Bitmap getLoacalBitmap(String url) {
		try {
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	private int setRemainTime() {
		timeRemaining.setText(StringUtils.generateADTime(time));
		time -= 1000;
		return 0;
	}

	public void start() {
		if (ad == null) {
			return;
		}
		if (isPause) {
			return;
		}

		if (ad.ad_type == SmoAdMultiPlay.AD_TYPE_START) {
			mHandler.sendEmptyMessage(AUTO_FRESH_TIME_REMAIN);
		}

	}

	public void pause() {
		if (ad == null) {
			return;
		}
		if (this.isShown()) {
			isPause = true;
			mHandler.removeMessages(AUTO_FRESH_TIME_REMAIN);
		}
	}

	public void stop() {
		if (ad == null) {
			return;
		}
		mHandler.removeMessages(AUTO_FRESH_TIME_REMAIN);
	}

	private void onComplete() {
		setViewHide();
	}

	private String fileFolder;

	public void getImageOrGif(final String url) {
		long currentTime = System.currentTimeMillis();
		String fileName = "" + currentTime;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			fileFolder = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + File.separator + "Wswy";
			File dir = new File(fileFolder);
			if (!dir.exists())
				dir.mkdir();
			savePath = fileFolder + File.separator + fileName;
			pic.put(mPath, savePath);
		}
		new Thread() {
			public void run() {
				try {
					URL myFileUrl = new URL(url);
					HttpURLConnection conn = (HttpURLConnection) myFileUrl
							.openConnection();
					conn.setReadTimeout(6000);
					conn.setConnectTimeout(6000);
					// 打开该URL对应的资源的输入流
					InputStream is = conn.getInputStream();
					FileOutputStream os = new FileOutputStream(savePath);
					byte[] buff = new byte[1024];
					int hasRead = 0;
					while ((hasRead = is.read(buff)) > 0) {
						os.write(buff, 0, hasRead);
					}
					is.close();
					os.close();
					mHandler.sendEmptyMessage(LOAD_SUCCES);
				} catch (MalformedURLException e) {
					mHandler.sendEmptyMessage(LOAD_ERROR);
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					mHandler.sendEmptyMessage(LOAD_ERROR);
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
					mHandler.sendEmptyMessage(LOAD_ERROR);
				}
			}
		}.start();
	}

	private OnClickListener ad_cancle_btn = new OnClickListener() {
		@Override
		public void onClick(View v) {
			isPause = true;
			mHandler.removeMessages(AUTO_FRESH_TIME_REMAIN);
			if (sendVideoStateListener != null) {
				sendVideoStateListener.cancleAct();
			}
		}
	};
	private OnClickListener show_ad_detail = new OnClickListener() {
		@Override
		public void onClick(View v) {
			stop();
			if (sendVideoStateListener != null) {
				sendVideoStateListener.showAdWebView(ad);
			}
		}
	};

	private void setViewHide() {
		release();
		if (sendVideoStateListener != null) {
			sendVideoStateListener.sendVideoStartPlay();
		}
	}

	public void release() {
		adCancleBtn.setVisibility(View.GONE);
		ad_img_cancle.setVisibility(View.GONE);
		pgb.setVisibility(View.GONE);
		timeRemaining.setVisibility(View.GONE);
		adLinkBtn.setVisibility(View.GONE);
		gifImageView.setImageDrawable(null);
		gifImageView.setOnClickListener(null);
		adBackgrd.setBackgroundColor(mContext.getResources().getColor(
				R.color.no_color));
		ad = null;
		savePath=null;
		this.setVisibility(View.GONE);
		mHandler.removeMessages(AUTO_FRESH_TIME_REMAIN);
		mHandler.removeMessages(LOAD_SUCCES);
	}

	public void setSendVideoStateListener(
			SendVideoStateListener sendVideoStateListener) {
		this.sendVideoStateListener = sendVideoStateListener;
	}
}
