package effect;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Camera.CameraInfo;
import android.util.Log;

/**
 * XVideoRecoderManager
 * @author xpg-2007
 * @功能：主要负责管理视频采集和opengl绘图相关功能。
 */

public class XVideoRecoderManager implements Camera.PreviewCallback, CamLayer.AudioCallback{
	String tag = "XVideoRecoderManager";
	
	public CamLayer 	mCamLayer;//for camera.
	public GLLayer 		mGLLayer;//for opengl.
	
	//for effects.
	public Effects 		mEffectsManager = null;	//manage effect list and make file.
	private Boolean		mFileRecoding = false;		
	
	private SensorManager mSensorManager = null;
	private Sensor mSensor = null;
  	private int mvangle = EffectType.MY_PI_ZERO;
	
	public XVideoRecoderManager(Context context)
	{
		
		synchronized (this) {
			mCamLayer = new CamLayer(context, this, this);
			mGLLayer = new GLLayer(context, EffectType.VIDEO_WIDTH, EffectType.VIDEO_HEIGHT);
			
			mEffectsManager = new Effects();	
			
	        mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);        
	        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		}
	}
	
	public void Release()
	{
		synchronized (this) {
			if(this.isFileRecod())
			{
				this.stopFileRecod();	
			}
			
			if(mEffectsManager != null)
			{
				mEffectsManager.Release();
			}
		}
	}
	
	public void onPreviewFrame(byte[] arg0, Camera arg1) {
		//Log.i(tag, "onPreviewFrame");
		//mEffectsManager.ProcessBitmap(arg0, mEffectsProssID, EffectType.VIDEO_WIDTH, EffectType.VIDEO_HEIGHT, mGLLayer.mBmpColor);
		mEffectsManager.native_ProcessVideo(arg0, EffectType.VIDEO_WIDTH, EffectType.VIDEO_HEIGHT, mGLLayer.glCameraFrame);
		mGLLayer.requestRender();
	}
	
	public void changeCamera()
	{
		if(mCamLayer != null)
		{
			mCamLayer.changeCamera();
			if(mGLLayer != null)
			{
				if(mCamLayer.getCameraFacting() == CameraInfo.CAMERA_FACING_BACK)
				{
					mGLLayer.changeBackCoords();
				}
				else if(mCamLayer.getCameraFacting() == CameraInfo.CAMERA_FACING_FRONT)
				{
					mGLLayer.changeFrontCoords();
				}
			}
		}
	}
	
	
	//get angle.  *******begin.
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
			
        }  
          
        @Override  
        public void onAccuracyChanged(Sensor sensor, int accuracy) {  
            // TODO Auto-generated method stub  
              
        }

    };  
	
    public void startGetAngle()
    {
    	if(mSensorManager != null){
    		mSensorManager.registerListener(lsn, mSensor, SensorManager.SENSOR_DELAY_GAME); 
    	}
    }
    
    public void stopGetAngle()
    {
    	if(mSensorManager != null){
    		mSensorManager.unregisterListener(lsn);  
    	}
    }
    
    public int getAngle()
    {
    	return mvangle;
    }
    //get angle.  *******end.
    
    //file recoder. ******begin.
    public Boolean isFileRecod()
    {
    	return mFileRecoding;
    }
    
    public void startFileRecod(String fileId, String path, int bsmall)
    {
    	synchronized (this) {
	    	if(mEffectsManager != null){
	    		mEffectsManager.native_StartRecoder(
	    				fileId, 
	    				path,
	    				EffectType.VIDEO_WIDTH, EffectType.VIDEO_HEIGHT, 
	    				512*1024, EffectType.VIDEO_FORMATRATE, 
	    				mvangle, 64*1024, 
	    				2, 
	    				EffectType.SAMPLERATE_IN_HZ, bsmall);
	    		mFileRecoding = true;
	    	}
    	}
    }
    
    public void stopFileRecod()
    {
    	synchronized (this) {
	    	if(mEffectsManager != null){
	    		mEffectsManager.native_StopRecoder();
	    		mFileRecoding = false;
	    	}
    	}
    }
    //file recoder. ******end.
    
    public void onAudioFrame(byte[] data, int nsize)
    {
    	//Log.i(tag, "onAudioFrame:" + nsize);
    	mEffectsManager.native_ProcessAudio(data, nsize);
    }
	
}
