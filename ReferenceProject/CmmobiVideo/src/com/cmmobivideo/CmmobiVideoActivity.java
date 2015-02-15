package com.cmmobivideo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.sql.Date;
import java.text.SimpleDateFormat;

import xMediaPlayer.XMediaPlayer;
import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import effect.CamLayer;
import effect.EffectType;
import effect.Effects;
import effect.XVideoRecoderManager;
import effect.transCode.OnScheduleListener;

public class CmmobiVideoActivity extends Activity implements SurfaceHolder.Callback,
															PreviewCallback,
															OnClickListener,
															OnScheduleListener{
	private final String tag = "CmmobiVideoActivity";
	private final static int GRAB_IMAGE = 1;
	private final static int NEXT_GIF_IMAGE = 2;
	private final static int TRANS_CODE_S = 3;
	SurfaceHolder surfaceHolder;
	SurfaceView surfaceView1;
	private ImageView ivDisplay = null;
	private Bitmap mBmpGray;
	private Bitmap mBmpColor;
	private Bitmap mBmpFrame;
	
	private gifOpenHelper gHelper;
	private Bitmap mGifBmp;

	private int mGifDelay = 1;
	private boolean mGifStoped = false;
	
	Camera camera;
	int m_width = 320;
	int m_height = 240;
	long	totaltime = 0;
	int		totalcount = 0;
	
	private Button mBtnTestEffect;
	private Button mBtnTestEffect2;
	int 	mTestEffectTag = 5;
	int 	mTestEffect2Tag = 6;
	int 	mNextEffectType = 1;
	int 	mNextEffect2Type = 1;
	
	//audio recoder test.
    // 音频获取源 
   private int audioSource = MediaRecorder.AudioSource.MIC; 
   // 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025 
   private static int sampleRateInHz = 44100; 
   // 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道 
   private static int channelConfig = AudioFormat.CHANNEL_IN_STEREO; 
   // 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。 
   private static int audioFormat = AudioFormat.ENCODING_PCM_16BIT; 
   // 缓冲区字节大小  www.2cto.com
   private int bufferSizeInBytes = 0; 
   private Button Start; 
   private Button Stop; 
   private AudioRecord audioRecord; 
   private boolean isRecord = false;// 设置正在录制的状态 
   
   //计算方向
   //private SensorManager mSensorManager = null;
   //private Sensor mSensor = null;
   //private int mvangle = EffectType.MY_PI_ZERO;
   //opengl draw..
	private CamLayer mPreview;
	
	//video recoder.
	private XVideoRecoderManager mVideoRecoderManager;
	
	//media player
    XMediaPlayer mXMediaPlayer = null;
	
	//test button
    private TextView textTransNumb;
	private Button mBtnStartCamera;
	final int KBtnStartCameraID  = 1;
	private Button mBtnStartRecoder;
	final int KBtnStartRecoderID = 2;
	private Button mBtnChangeCamera;
	final int KBtnChangeCameraID = 3;
	private Button mBtnCameraFlash;
	final int KBtnCameraFlashID = 4;
	private Button mBtnChangeEffect;
	final int KBtnChangeEffect = 5;
	private Button mBtnPlay;
	final int KBtnPlayID =	6; 
	private Button mBtnTransCode;
	final int KBtnTransCodeID = 7;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mBmpGray = Bitmap.createBitmap(m_width,m_height, Config.ALPHA_8);
        mBmpColor = Bitmap.createBitmap(m_width,m_height, Config.ARGB_8888);
        
        //mSensorManager = (SensorManager)this.getSystemService(SENSOR_SERVICE);        
        //mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        
        mXMediaPlayer = new XMediaPlayer(this);
        
        Log.i(tag, "frame load:");
        InputStream is = null;
        InputStream isGif = null;
        try{
        	is = this.getAssets().open("frame.png");
        	isGif = this.getAssets().open("gif005.gif");
        }catch(Exception ex){
        	ex.printStackTrace();
        }
        Log.i(tag, "frame loaded");
        
        mBmpFrame = BitmapFactory.decodeStream(is);
        Log.i(tag, "frame w="+mBmpFrame.getWidth()+" h="+mBmpFrame.getConfig().toString());
        
        mBtnTestEffect = (Button)findViewById(R.id.btn_effect_test);
        mBtnTestEffect2 = (Button)findViewById(R.id.btn_effect2_test);
        mBtnTestEffect.setOnClickListener(this);
        mBtnTestEffect2.setOnClickListener(this);
        
        ivDisplay=(ImageView)findViewById(R.id.ivDisplay);
        //ivDisplay.setImageBitmap(mBmpFrame);
        
		gHelper = new gifOpenHelper();
		gHelper.read(isGif);
		mGifBmp = gHelper.getImage();// 得到第一张图片
		

        
		
        surfaceView1 = (SurfaceView)findViewById(R.id.surfaceView1);
        surfaceHolder=surfaceView1.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(this);
        //notify_handler.sendEmptyMessage(GRAB_IMAGE);
        //notify_handler.sendEmptyMessage(NEXT_GIF_IMAGE);
     
        //init audio recoder.
        initaudiorecoder();
        
        //test XVideoRecoderMamager.
        initCameraLayout();
    }
    
    public void initCameraLayout()
    {
        RelativeLayout bgloyout = new RelativeLayout(this);
        RelativeLayout.LayoutParams fill_params =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                        						ViewGroup.LayoutParams.FILL_PARENT);
        
        bgloyout.setLayoutParams(fill_params);
        //bgloyout.setPadding(22, 22, 22, 22);
        
        
        LinearLayout btnlayout = new LinearLayout(this);
        btnlayout.setGravity(Gravity.BOTTOM);
        ViewGroup.LayoutParams btnlayout_params =
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                		ViewGroup.LayoutParams.FILL_PARENT);
        btnlayout.setLayoutParams(btnlayout_params);
        btnlayout.setOrientation(LinearLayout.VERTICAL);
        btnlayout.setVisibility(View.INVISIBLE);
        
        textTransNumb = new TextView(this);
        textTransNumb.setText("0%");
        textTransNumb.setLayoutParams(new ViewGroup.LayoutParams(200, ViewGroup.LayoutParams.WRAP_CONTENT));
        
        mBtnStartCamera = new Button(this);
        mBtnStartCamera.setText("start camera");
        mBtnStartCamera.setId(KBtnStartCameraID);
        mBtnStartCamera.setOnClickListener(this);
        mBtnStartCamera.bringToFront();
        ViewGroup.LayoutParams BtnStartCamera_params =
                new ViewGroup.LayoutParams(200, ViewGroup.LayoutParams.WRAP_CONTENT);
        mBtnStartCamera.setLayoutParams(BtnStartCamera_params);
        
        mBtnStartRecoder = new Button(this);
        mBtnStartRecoder.setText("start recoder");
        mBtnStartRecoder.setId(KBtnStartRecoderID);
        mBtnStartRecoder.setOnClickListener(this);
        mBtnStartRecoder.bringToFront();
        ViewGroup.LayoutParams BtnStartRecoder_params =
                new ViewGroup.LayoutParams(200, ViewGroup.LayoutParams.WRAP_CONTENT);
        mBtnStartRecoder.setLayoutParams(BtnStartRecoder_params);
        
        mBtnChangeCamera = new Button(this);
        mBtnChangeCamera.setText("change camera");
        mBtnChangeCamera.setId(KBtnChangeCameraID);
        mBtnChangeCamera.setOnClickListener(this);
        mBtnChangeCamera.bringToFront();
        ViewGroup.LayoutParams BtnChangeCamera_params =
                new ViewGroup.LayoutParams(200, ViewGroup.LayoutParams.WRAP_CONTENT);
        mBtnChangeCamera.setLayoutParams(BtnChangeCamera_params);
        
        mBtnCameraFlash = new Button(this);
        mBtnCameraFlash.setText("camera flash");
        mBtnCameraFlash.setId(KBtnCameraFlashID);
        mBtnCameraFlash.setOnClickListener(this);
        mBtnCameraFlash.bringToFront();
        ViewGroup.LayoutParams BtnCameraFlash_params =
                new ViewGroup.LayoutParams(200, ViewGroup.LayoutParams.WRAP_CONTENT);
        mBtnCameraFlash.setLayoutParams(BtnCameraFlash_params);
        
        mBtnChangeEffect = new Button(this);
        mBtnChangeEffect.setText("change effect");
        mBtnChangeEffect.setId(KBtnChangeEffect);
        mBtnChangeEffect.setOnClickListener(this);
        mBtnChangeEffect.bringToFront();
        ViewGroup.LayoutParams BtnChangeEffect_params =
                new ViewGroup.LayoutParams(200, ViewGroup.LayoutParams.WRAP_CONTENT);
        mBtnChangeEffect.setLayoutParams(BtnChangeEffect_params);
        
        mBtnPlay = new Button(this);
        mBtnPlay.setText("video play");
        mBtnPlay.setId(KBtnPlayID);
        mBtnPlay.setOnClickListener(this);
        mBtnPlay.bringToFront();
        ViewGroup.LayoutParams BtnPlay_params =
                new ViewGroup.LayoutParams(200, ViewGroup.LayoutParams.WRAP_CONTENT);
        mBtnPlay.setLayoutParams(BtnPlay_params);
        
        mBtnTransCode = new Button(this);
        mBtnTransCode.setText("trans code");
        mBtnTransCode.setId(KBtnTransCodeID);
        mBtnTransCode.setOnClickListener(this);
        mBtnTransCode.bringToFront();
        ViewGroup.LayoutParams BtnTransCode_params =
                new ViewGroup.LayoutParams(200, ViewGroup.LayoutParams.WRAP_CONTENT);
        mBtnTransCode.setLayoutParams(BtnTransCode_params);
        
        mVideoRecoderManager = new XVideoRecoderManager(this);
        mVideoRecoderManager.mCamLayer.setAutoStart(false);
        
        //set cameralayer size to 1.
        ViewGroup.LayoutParams CamLayer_params =
                new ViewGroup.LayoutParams(1, 1);
        mVideoRecoderManager.mCamLayer.setLayoutParams(CamLayer_params);
        
        
        //ViewGroup.LayoutParams PlayGlView_params =
        //        new ViewGroup.LayoutParams(640, 400);
        //mXMediaPlayer.mGlView.setLayoutParams(PlayGlView_params);
        
        //bgloyout.addView(mXMediaPlayer.mGlView);
        //mVideoRecoderManager.mGLLayer.setBackgroundColor(0xffff0000);
        
        bgloyout.addView(mVideoRecoderManager.mGLLayer);
        bgloyout.addView(mVideoRecoderManager.mCamLayer);
        
        btnlayout.addView(textTransNumb);
        btnlayout.addView(mBtnStartCamera);
        btnlayout.addView(mBtnStartRecoder);
        btnlayout.addView(mBtnChangeCamera);
        btnlayout.addView(mBtnCameraFlash);
        btnlayout.addView(mBtnChangeEffect);
        btnlayout.addView(mBtnPlay);
        btnlayout.addView(mBtnTransCode);
     
        MediaPlayerUI mediaPlayerUI = new MediaPlayerUI(this, mXMediaPlayer);
        mXMediaPlayer.setOnXMediaPlayerStateChangedListener(mediaPlayerUI);
        
        bgloyout.addView(mediaPlayerUI.getLayout());
        bgloyout.addView(btnlayout);
        setContentView(bgloyout);	
        
        
        //add test effects.
        Effects.Effect teffect = null;
        teffect = mVideoRecoderManager.mEffectsManager.NewEffect(EffectType.KEF_TYPE_SKETCH);
        teffect.native_SetEffectTag(mTestEffectTag);
        mVideoRecoderManager.mEffectsManager.AddEffectsInFrontWithTag(
        		        		teffect, -1);
       
        
        
        teffect = null;
        /*
        teffect = mXMediaPlayer.mEffectsManager.NewEffect(EffectType.KEF_TYPE_SKETCH);
        teffect.native_SetEffectTag(mTestEffectTag);
        mXMediaPlayer.mEffectsManager.AddEffectsInFrontWithTag(
        		teffect, -1);
        */
        
//        teffect = mXMediaPlayer.mEffectsManager.NewEffect(EffectType.KEF_TYPE_TWIRL);
//        teffect.native_SetEffectTag(mTestEffectTag);
//        teffect.native_SetTableRect(0, 0, 480, 360);
//        mXMediaPlayer.mEffectsManager.AddEffectsInFrontWithTag(
//        		teffect, -1);
        	
    }
    
    //orientaion change.
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        int o=getRequestedOrientation();//获取手机的朝向
        
        Log.e(tag, "onConfigurationChanged: ori=" + o);
        super.onConfigurationChanged(newConfig);
     }
    
    /*
    SensorEventListener lsn = new SensorEventListener() {  
        
        @Override  
        public void onSensorChanged(SensorEvent event) {  
        	float X, Y, Z; 
        	int orientation = -1;
            X = event.values[SensorManager.DATA_X];  
            Y = event.values[SensorManager.DATA_Y];  
            Z = event.values[SensorManager.DATA_Z];  
                  
            float magnitude = X*X + Y*Y;
            // Don't trust the angle if the magnitude is small compared to the y value
            if (magnitude * 4 >= Z*Z) {
                float OneEightyOverPi = 57.29577957855f;
                float angle = (float)Math.atan2(-Y, X) * OneEightyOverPi;
                orientation = 90 - (int)Math.round(angle);
                // normalize to 0 - 359 range
                while (orientation >= 360) {
                    orientation -= 360;
                } 
                while (orientation < 0) {
                    orientation += 360;
                }
            }
           
			if (orientation>45&&orientation<135) {
				mvangle = EffectType.MY_PI_ZERO;
			}else if (orientation>135&&orientation<225){
				mvangle = EffectType.MY_PI_1P2;
			}else if (orientation>225&&orientation<315){
				mvangle = EffectType.MY_PI_;
			}else if ((orientation>315&&orientation<360)||(orientation>0&&orientation<45)){
				mvangle = EffectType.MY_PI_3P2;
			}
			
			//Log.i(tag, "mvangle=" + mvangle);
        }  
          
        @Override  
        public void onAccuracyChanged(Sensor sensor, int accuracy) {  
            // TODO Auto-generated method stub  
              
        }

    };  
    */
    @Override  
    public void onResume(){  
    	
        //mSensorManager.registerListener(lsn, mSensor, SensorManager.SENSOR_DELAY_GAME); 
    	if(mVideoRecoderManager != null) mVideoRecoderManager.startGetAngle(); 
       Log.i(tag, "onResume");
        /*
        mVideoRecoderManager = new XVideoRecoderManager(this);
        
        setContentView(mVideoRecoderManager.mGLLayer);
        addContentView(mVideoRecoderManager.mCamLayer, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT)); 
        */
    	
        //mVideoRecoderManager.mCamLayer.startCamera();
        /*
        glView=new GLLayer(this);
		mPreview = new CamLayer(this, glView);

        setContentView(glView);
        addContentView(mPreview, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT)); 
        */ 
        super.onResume();  
    }  
      
      
    @Override  
    public void onPause(){  
        //mSensorManager.unregisterListener(lsn);  
        if(mVideoRecoderManager != null) mVideoRecoderManager.stopGetAngle();  
        super.onPause();  
    }  
    
    
    
    
    //about audio recoder*********************begin.
    private void initaudiorecoder()
    {
       Start = (Button) this.findViewById(R.id.btn_start); 
       Stop = (Button) this.findViewById(R.id.btn_stop); 
       
       Start.setOnClickListener(new TestAudioListener()); 
       Stop.setOnClickListener(new TestAudioListener()); 
       
       CreateAudioRecord();
       Log.i(tag, "bufferSizeInBytes="+bufferSizeInBytes);
    }
    
    private void CreateAudioRecord()
    {
        // 获得缓冲区字节大小 
       bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, 
               channelConfig, audioFormat); 
       // 创建AudioRecord对象 
       audioRecord = new AudioRecord(audioSource, sampleRateInHz, 
               channelConfig, audioFormat, bufferSizeInBytes*2); 	
    }
    
    class TestAudioListener implements OnClickListener { 
        @Override 
        public void onClick(View v) { 
            if (v == Start) { 
            	startRecode(); 
            } 
            if (v == Stop) { 
            	stopRecoder(); 
            } 
        } 
    } 
    
    
    public void startRecode()
    {
    	if(isRecord) 
    	{
    		Log.i(tag, "is record..");
    		return;
    	}
    	
    	
    	//start video recode.....
		//Log.i(tag, "surfaceCreated surfaceView1.w="+surfaceView1.getWidth()+" h="+surfaceView1.getHeight());
		//Log.i(tag, "Camera open begin...");
		camera=Camera.open();
		//Log.i(tag, "Camera open end...");
		try {
			//initFromCameraParameters(camera);
			Camera.Parameters parameters=camera.getParameters();
			//parameters.setPreviewSize(cameraResolution.x, cameraResolution.y);
			parameters.setPreviewSize(m_width, m_height);
			parameters.setPreviewFrameRate(15);
			//parameters.setPictureFormat(ImageFormat.NV21);
			//parameters.setPreviewSize(320, 240);
			
//			parameters.setRotation(90);
			camera.setParameters(parameters);
//			if(checkbox1.isChecked())
			//camera.setDisplayOrientation(90);
			//camera.setPreviewCallbackWithBuffer(this);
			//camera.addCallbackBuffer(callback_buffer);
			camera.setPreviewCallback(this);
			//设置参数
			camera.setPreviewDisplay(surfaceHolder);
			//camera.setPreviewDisplay(null);
			//摄像头画面显示在Surface上
			camera.startPreview();
		} catch (IOException e) {
			e.printStackTrace();
		}
		CreateAudioRecord();
       //recode audio
    	audioRecord.startRecording(); 
       // 让录制状态为true 
       isRecord = true; 
       // 开启音频文件写入线程 
       new Thread(new AudioRecordThread()).start();
    }
  
    public void stopRecoder()
    {
    	if(!isRecord)
    	{
    		Log.i(tag, "is do'nt record...");
    		return;
    	}
    	isRecord = false;//停止文件写入 
        if (audioRecord != null) { 
            System.out.println("stopRecord"); 
            audioRecord.stop(); 
            audioRecord.release();//释放资源 
            audioRecord = null; 
        }
        
		if(camera != null){
			camera.stopPreview();
			camera.setPreviewCallback(null);
			//关闭预览
			camera.release();
			camera = null;
		}
    }
    
    class AudioRecordThread implements Runnable { 

        @Override 
        public void run() { 
        	//read audio data from recoder memory.
            byte[] audiodata = new byte[bufferSizeInBytes]; 
            int readsize = 0;  
            while (isRecord == true) { 
                readsize = audioRecord.read(audiodata, 0, bufferSizeInBytes); 
                //Log.i(tag, "readsize=" + readsize);
                if (AudioRecord.ERROR_INVALID_OPERATION != readsize) { 
                	Log.i(tag, "readsize=" + readsize);

                } 
            } 
        } 
    } 
    
    
    @Override
    public void OnSchedule(double schedule)
    {
    	//Log.i(tag, "schedule=" + schedule);
    	//textTransNumb.setText(schedule*100 + "%");
    	notify_handler.sendMessage(Message.obtain(notify_handler, TRANS_CODE_S, (int)(schedule*100), 0));
    }
    
    @Override
    public void OnFinish()
    {
    	//Log.i(tag, "OnFinish");
    	notify_handler.sendMessage(Message.obtain(notify_handler, TRANS_CODE_S, 100, 0));
    }
    
    //about audio recoder****************end.
    @Override
    public void onClick(View v)
    {
    	switch(v.getId())
    	{
    	case KBtnStartCameraID:
    		if(mVideoRecoderManager != null)
    		{
    			if(!mVideoRecoderManager.mCamLayer.isRecoding())
    			{
    				mVideoRecoderManager.mCamLayer.startRecod();
    			}else
    			{
    				mVideoRecoderManager.mCamLayer.stopRecod();
    			}
    		}
    		break;
    	case KBtnStartRecoderID:
    		if(mVideoRecoderManager != null)
    		{
    			if(!mVideoRecoderManager.isFileRecod())
    			{
    		    	SimpleDateFormat format=new SimpleDateFormat("yyyyMMddHHmmss");
    		    	String nowTime=format.format(new Date(System.currentTimeMillis()));	
    				mVideoRecoderManager.startFileRecod(nowTime, "/mnt/sdcard/video", 0);
    			}else{
    				mVideoRecoderManager.stopFileRecod();
    			}
    		}
    		break;
    	case KBtnChangeCameraID:
    		mVideoRecoderManager.changeCamera();
    		break;
    	case KBtnCameraFlashID:
    		mVideoRecoderManager.mCamLayer.setCameraFlash();
    		break;
    	case KBtnChangeEffect:
    		//changeEffect(mVideoRecoderManager.mEffectsManager, EffectType.VIDEO_WIDTH, EffectType.VIDEO_HEIGHT);
    		changeEffect(mXMediaPlayer.mEffectsManager, 480, 360);
    		break;
    	case KBtnPlayID:
    		if(mXMediaPlayer.isPlaying())
    		{
    			mXMediaPlayer.stop();
    		}else{
    			if(!mXMediaPlayer.isOpen())
    			{
    				try {
						mXMediaPlayer.open("/mnt/sdcard/rrrr1.mp4");
    					//mXMediaPlayer.open("http://192.168.100.111:8080/looklook/transcode/2013/02/20/f0be5cc94b514ef6902247fd4020867c.mp4");
    					//mXMediaPlayer.open("/mnt/sdcard/video/test0.mp4");
					} catch (Exception e) {
						e.printStackTrace();
					}
    			}
    			//mXMediaPlayer.play();
    		}
    		break;
    	case KBtnTransCodeID:
    		if(mXMediaPlayer.isPlaying())
    		{
    			mXMediaPlayer.stop();
    		}
	    	SimpleDateFormat format=new SimpleDateFormat("yyyyMMddHHmmss");
	    	String nowTime=format.format(new Date(System.currentTimeMillis()));	
    		mXMediaPlayer.StartTransCode("/mnt/sdcard/video/test0.mp4", 
    				"/mnt/sdcard/video/test0_" + nowTime +".mp4", this);
    		break;
    	default:
    		break;
    	}
    	
    	
    	if(v == mBtnTestEffect)
    	{
    		/*
            File file = new File(getVideoPath() + "//test.mp4"); 

            if (!file.exists()) { 

                try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
					Log.i(tag, "out file create error.");
				} 

            } 
            */
    	SimpleDateFormat format=new SimpleDateFormat("yyyyMMddHHmmss");
    	String nowTime=format.format(new Date(System.currentTimeMillis()));	
		//objEffects.StartRecoder(effectlist, nowTime, m_width, m_height, 512*1024, 15, mvangle, 64*1024, 2, 44100, 1);
    	}
    	else if(v == mBtnTestEffect2)
    	{
    		//objEffects.StopRecoder(effectlist);
    	}
    	return;

    }
    
    public void changeEffect(Effects obj, int width, int height)
    {
    	
    	int ntype = 1;
    	int ntag = 5;
    	
    	ntag = mTestEffectTag;
    	ntype = mNextEffectType;

    	Effects objEffects = obj;//mVideoRecoderManager.mEffectsManager;
    	
    	Log.i(tag, "onClick function.");
    	Effects.Effect teffect = null;
    	Log.i(tag, "change effect1 :" + mNextEffectType + "    effect2 :" + mNextEffect2Type);
    	switch (ntype) {
		case EffectType.KEF_TYPE_GRAY:
		case EffectType.KEF_TYPE_BW://                //黑白特效
		case EffectType.KEF_TYPE_GRAYFILM://          //黑白底片效果
		case EffectType.KEF_TYPE_FLIP://              //上下颠倒特效
		case EffectType.KEF_TYPE_FOLD_LR://           //左右折叠
		case EffectType.KEF_TYPE_FOLD_TB://           //上下折叠
		case EffectType.KEF_TYPE_RELIEVO://           //浮雕特效
		case EffectType.KEF_TYPE_SKETCH://            //素描特效
		case EffectType.KEF_TYPE_PAINTING://          //油画效果
		case EffectType.KEF_TYPE_LIGHTRING://         //光斑效果
		case EffectType.KEF_TYPE_CWR_90://             //顺时针转90+翻转  【暂时先占一个位置吧】
	        teffect = objEffects.NewEffect(ntype);
	        teffect.native_SetEffectTag(ntag);
			break;
		case EffectType.KEF_TYPE_ADDRGB://            //增强颜色值特效
	        teffect = objEffects.NewEffect(ntype);
	        teffect.native_SetEffectTag(ntag);
	        teffect.native_SetEffectColor(22, 44, 72);
			break;
		case EffectType.KEF_TYPE_LUM_CT://            //亮度对比度特效
	        teffect = objEffects.NewEffect(ntype);
	        teffect.native_SetEffectTag(ntag);
	        teffect.native_SetEffectLumAndCon(100, 2.5);
		    break;    
		case EffectType.KEF_TYPE_TWIRL://             //扭曲特效
		case EffectType.KEF_TYPE_PINCH://             //凹特效  
		case EffectType.KEF_TYPE_POUCH://             //凸特效  
		case EffectType.KEF_TYPE_FLASH://             //放射特效
	        teffect = objEffects.NewEffect(ntype);
	        teffect.native_SetEffectTag(ntag);
	        teffect.native_SetTableRect(0, 0, width, height);
		    break;   
		case EffectType.KEF_TYPE_MOSAIC://            //马赛克特效
	        teffect = objEffects.NewEffect(ntype);
	        teffect.native_SetEffectTag(ntag);
	        teffect.native_SetEffectMosaic(5);
	        teffect.native_SetProcessRect(20, 20, 50, 50);
			break;
		case EffectType.KEF_TYPE_PHOTOFRAME://        //相框
	        teffect = objEffects.NewEffect(ntype);
	        teffect.native_SetEffectTag(ntag);
	        teffect.native_SetEffectFrame(mBmpFrame);
	        break;
		case EffectType.KEF_TYPE_ANIMATION://         //动画
	        teffect = objEffects.NewEffect(ntype);
	        teffect.native_SetEffectTag(ntag);
	        teffect.native_SetEffectFrame(mBmpFrame);
	        teffect.native_SetProcessRect(100, 100, 320, 240);
			break;
			
		case EffectType.KEF_TYPE_AUDIOSYNTHESIS://    //音频合成
			break;
		        
		case EffectType.KEF_TYPE_ZOOM://              //数字变焦  [暂时算一个特效]			
	        teffect = objEffects.NewEffect(ntype);
	        teffect.native_SetEffectTag(ntag);
	        teffect.native_SetEffectFactor(0.3);
			break;
		default:
			break;
		}
    	
    	if(teffect != null)
    	{
    		objEffects.ReplaceEffectsWithTag(teffect, ntag);
    	}
    	
    	++mNextEffectType;
    	
    	if(mNextEffectType > 20)
    	{
    		mNextEffectType = 1;
    	} 	
    	
    }

    public boolean hasSdcard() {
		String status = Environment.getExternalStorageState();
		 if (status.equals(Environment.MEDIA_MOUNTED)) {
		       return true;
		 } else {
		        return false;
		 }
	}
    
    public String getVideoPath()
    {
    	String path = null;
    	if(!hasSdcard())
    	{
    		Log.e(tag, "no sd card.");
    		return path;
    	}
    	
    	path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getPath();
    	
    	return path;
    }
    
	public void onPreviewFrame(byte[] data, Camera cam) {
		//Log.i(tag,"onPreviewFrame data="+data.length + " mGifBmp="+mGifBmp.getConfig().toString());	
		long start = System.nanoTime();				
		//加静太边框
		//objEffects.FrameBitmap(data, m_width, m_height, mBmpFrame,mBmpColor);
		//ivDisplay.setImageBitmap(mBmpColor);
		
		//加动态边框
//		FrameBitmap(data, m_width, m_height, mGifBmp,mBmpColor);
//		ivDisplay.setImageBitmap(mBmpColor);
		
		// 怀旧
//		OldBitmap(data, m_width, m_height, null,mBmpColor);
//		ivDisplay.setImageBitmap(mBmpColor);
		
		//灰度图
//		GrayBMP(data, m_width, m_height, null,mBmpGray);
//		ivDisplay.setImageBitmap(mBmpGray);
		
		//flip
//		FlipBitmap(data, m_width, m_height, null,mBmpColor);
//		ivDisplay.setImageBitmap(mBmpColor);		
		
		//twirl
		//objEffects.TwirlBitmap(data, m_width, m_height, null,mBmpColor);
		//ivDisplay.setImageBitmap(mBmpColor);
		
		/*
		if(0 != effectlist)
		{
			objEffects.ProcessBitmap(data, effectlist, m_width, m_height, mBmpColor);
			ivDisplay.setImageBitmap(mBmpColor);
		}
		*/		
//		FlashBitmap(data, m_width, m_height, null,mBmpColor);
//		ivDisplay.setImageBitmap(mBmpColor);
		
//		QuickFlashBitmap(data, m_width, m_height, null,mBmpColor);
//		ivDisplay.setImageBitmap(mBmpColor);
		
//		ZoomBitmap(data, m_width, m_height, 3,null,mBmpColor);
//		ivDisplay.setImageBitmap(mBmpColor);		
		long end = System.nanoTime() - start;
		totaltime += end;
		totalcount++;
		//Log.i(tag, "avg="+totaltime/(totalcount*1000000));
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.i(tag, "surfaceView1.w="+surfaceView1.getWidth()+" h="+surfaceView1.getHeight());
		Log.i(tag, "surfaceChanged w="+width+" h="+height);	
	}

	public void surfaceCreated(SurfaceHolder arg0) {
		/*
		Log.i(tag, "surfaceCreated surfaceView1.w="+surfaceView1.getWidth()+" h="+surfaceView1.getHeight());
		Log.i(tag, "Camera open begin...");
		camera=Camera.open();
		Log.i(tag, "Camera open end...");
		try {
			//initFromCameraParameters(camera);
			Camera.Parameters parameters=camera.getParameters();
			//parameters.setPreviewSize(cameraResolution.x, cameraResolution.y);
			parameters.setPreviewSize(m_width, m_height);
			parameters.setPreviewFrameRate(15);
			//parameters.setPictureFormat(ImageFormat.NV21);
//			parameters.setPreviewSize(320, 240);
			
//			parameters.setRotation(90);
			camera.setParameters(parameters);
//			if(checkbox1.isChecked())
			//camera.setDisplayOrientation(90);
			//camera.setPreviewCallbackWithBuffer(this);
			//camera.addCallbackBuffer(callback_buffer);
			camera.setPreviewCallback(this);
			//设置参数
			camera.setPreviewDisplay(surfaceHolder);
			//摄像头画面显示在Surface上
			camera.startPreview();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/		
	}

	public void surfaceDestroyed(SurfaceHolder arg0) {
		Log.i(tag, "surfaceDestroyed");
		/*
		if(camera != null){
			camera.setPreviewCallback(null);
			camera.stopPreview();
			//关闭预览
			camera.release();
		}
		*/		
	}
	
	@Override
	protected void onDestroy() {
		notify_handler.removeMessages(GRAB_IMAGE);
		notify_handler.removeMessages(NEXT_GIF_IMAGE);
	
		stopRecoder();
		
		if(mVideoRecoderManager != null)
		{
			mVideoRecoderManager.Release();
		}
		
		if(mXMediaPlayer != null)
		{
			mXMediaPlayer.Release();
		}
		
		super.onDestroy();
	}

	private Handler notify_handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if(msg.what==GRAB_IMAGE){
//		        BitmapFactory.Options options = new BitmapFactory.Options();
//		        options.inPreferredConfig = Config.ARGB_8888;
//		        options.inTargetDensity = value.density;
		        //bitmapOrig = BitmapFactory.decodeResource(this.getResources(), R.drawable.test320x240,options);
				//Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.test320x240,options);
				Bitmap bmp = decodeResource(getResources(), R.drawable.test320x240);
				//Bitmap lbmp = Bitmap.createBitmap(m_width,m_height, Config.ALPHA_8);
				int len = bmp.getRowBytes()*bmp.getHeight();
				Log.i(tag, "config:"+bmp.getConfig().toString()+" len="+len+" w="+bmp.getWidth()+" h="+bmp.getHeight()+" d="+bmp.getDensity());
				ByteBuffer bb = ByteBuffer.allocate(len);
				bmp.copyPixelsToBuffer(bb);
				//GrayBMP(bb.array(), bmp.getWidth(), bmp.getHeight(),bmp, mBmpGray);
				ivDisplay.setImageBitmap(mBmpGray);
				sendEmptyMessageDelayed (GRAB_IMAGE, 100);//
			}else if(msg.what == NEXT_GIF_IMAGE){
				/*
				//下一帧延迟时间
				int liDelay = gHelper.nextDelay();
				Log.i(tag, "gif next bitmap delay="+liDelay);
				mGifBmp = gHelper.nextBitmap();
				//切换动画图片
				
		        long teffect = objEffects.CreateEffect(EffectType.KEF_TYPE_ANIMATION);
		        objEffects.SetEffectTag(teffect, mTestEffect2Tag);
		        objEffects.SetEffectFrame(teffect, mGifBmp);
		        objEffects.ReplaceEffectsWithTag(effectlist, teffect, mTestEffect2Tag);
				sendEmptyMessageDelayed(NEXT_GIF_IMAGE, liDelay);
				*/
		        int o=getRequestedOrientation();//获取手机的朝向
		        Log.e(tag, "onConfigurationChanged: ori=" + o);
				sendEmptyMessageDelayed(NEXT_GIF_IMAGE, 1000);
			}else if(msg.what == TRANS_CODE_S)
			{
				textTransNumb.setText(msg.arg1 + "%");
			}
			super.handleMessage(msg);
		}
	};
	private Bitmap decodeResource(Resources resources, int id) {
	    TypedValue value = new TypedValue();
	    resources.openRawResource(id, value);
	    BitmapFactory.Options opts = new BitmapFactory.Options();
	    opts.inPreferredConfig = Config.ARGB_8888;
	    opts.inTargetDensity = value.density;
	    return BitmapFactory.decodeResource(resources, id, opts);
	}
    
}