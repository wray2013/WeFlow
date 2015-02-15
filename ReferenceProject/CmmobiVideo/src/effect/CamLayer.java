package effect;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.media.AudioRecord;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * This class handles the camera. In particular, the method setPreviewCallback
 * is used to receive camera images. The camera images are not processed in
 * this class but delivered to the GLLayer. This class itself does
 * not display the camera images.
 * 
 * @author Niels
 *
 */
public class CamLayer extends SurfaceView implements SurfaceHolder.Callback, PreviewCallback {
    String tag = "CamLager";
	
    //common paramer.
    private boolean autoStart = false;
    boolean mIsRecoding = false;
    
    //about video paramer.**************begin.
	Camera mCamera;
	int mCameraID = -1;
    Camera.PreviewCallback callback;
    //about video paramer.**************end.
    
    //考虑再三还是将音频采集也加到这里吧。
    //等以后有时间将音频跟视频的相关资源集中到一个类里去处理。
    //明确一点就是在这里总体处理音视频的采集工作。
    //about audio paramer. *********begin.
    private int bufferSizeInBytes = 0; 
    private AudioRecord mAudioRecord; 
    private Thread mAudioRecodeThread;
    private Boolean mCloseAudioRecodeThread = false;
    CamLayer.AudioCallback audiocallback;
    //about audio paramer.**********end.
    

    CamLayer(Context context, Camera.PreviewCallback callback, CamLayer.AudioCallback acallback) {
        super(context);
        this.callback=callback;
        this.audiocallback = acallback;
        
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        SurfaceHolder mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
    }
    //mPreview.setLayoutParams(new LayoutParams(100,100))

    public void surfaceCreated(SurfaceHolder holder) {
    		Log.i(tag, "surfaceCreated");
    		openCamera(holder);
    		openAudioRecoder();
    		if(autoStart) startRecod();
	}

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.asdf
        // Because the CameraDevice object is not a shared resource, it's very
        // important to release it when the activity is paused.
    	closeCamera();
    	closeAudioRecoder();
    	mIsRecoding = false;
    }
    
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

    }

    //option camera  *************begin.
    public int GetCameraIdWithFacting(int facting)
    {
    	int nret = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        CameraInfo cameraInfo = new CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
        	Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == facting) {
            	nret = i;
            	break;
            }
        }
    	return nret;
    }
    
    public void setCameraParameters()
    {
    	if(mCamera == null) return;
    	Camera.Parameters p = mCamera.getParameters();  
    	p.setPreviewSize(EffectType.VIDEO_WIDTH, EffectType.VIDEO_HEIGHT);
    	/*
    	List<Size> supportedPreviewSizes = p.getSupportedPreviewSizes();
    	List<Size> supportedPictureSizes = p.getSupportedPictureSizes(); 
    	
    	for(int i = 0; i < supportedPreviewSizes.size();i++)
    	{
    		Log.i(tag, "supportedPreviewSizes[" + i + "]:" 
    					+ supportedPreviewSizes.get(i).width 
    					+ "|" + supportedPreviewSizes.get(i).height);
    		
    	}
    	*/
    	p.setPreviewFrameRate(EffectType.VIDEO_FORMATRATE);
    	
    	mCamera.setParameters(p);
    	
    	try {
			mCamera.setPreviewDisplay(this.getHolder());
		} catch (IOException e) {
			Log.e("Camera", "mCamera.setPreviewDisplay(holder);");
		}
		mCamera.setPreviewCallback(this);
    }
    
    public void openCamera(SurfaceHolder holder)
    {
    	synchronized(this) {
	    	Log.i(tag, "openCamera");
	    	if(this.mCameraID < 0)
	    	{
	    		this.mCameraID = GetCameraIdWithFacting(CameraInfo.CAMERA_FACING_BACK);
	    	}
	    	
	    	if(this.mCameraID < 0) return;
	    	
	    	mCamera = Camera.open(this.mCameraID);
	    	setCameraParameters();
    	}
    }
    
    public void closeCamera()
    {
    	synchronized(this) {
	    	Log.i(tag, "closeCamera");
	    	try {
		    	if (mCamera!=null) {
		    		mCamera.stopPreview();
		    		mCamera.setPreviewCallback(null);
		    		mCamera.release();
		    		mCamera = null;
		    	}
	    	} catch (Exception e) {
				Log.e("Camera", e.getMessage());
	    	}
    	}
    }
    
    private void startCamera()
    {
    	Log.i(tag, "startCamera outside.");
    	synchronized(this) {
	    	if(mCamera != null)
	    	{
	    		Log.i(tag, "startCamera");
	    		setCameraParameters();
	    		
	    		mCamera.startPreview();
	    	}
    	}
    }
    
    public void stopCamera()
    {
    	synchronized(this) {
	    	if(mCamera != null)
	    	{
	    		mCamera.stopPreview();
	    	}
    	}
    }
    
    public Boolean hasFrontCamera()
    {
    	return (GetCameraIdWithFacting(CameraInfo.CAMERA_FACING_FRONT)>=0)?true:false;
    }
    
    public void changeCamera()
    {
    	if(mCamera != null)
    	{
    		if(!hasFrontCamera())
    		{
    			Log.i(tag, "do't have front camera.");
    			return;
    		}
    		
    		if(this.mCameraID >= 0)
    		{
	            CameraInfo cameraInfo = new CameraInfo();
	            Camera.getCameraInfo(this.mCameraID, cameraInfo);
	            int facting = (cameraInfo.facing==CameraInfo.CAMERA_FACING_BACK)?
	            	CameraInfo.CAMERA_FACING_FRONT:CameraInfo.CAMERA_FACING_BACK;
	            
	            int tId = this.GetCameraIdWithFacting(facting);
	            if(tId < 0) return;
	            closeCamera();
	            mCameraID = tId;
	            Log.i(tag, "mCameraID=" + mCameraID);
	            openCamera(this.getHolder());
	            startCamera();
    		}
    	}
    }
    
    public void setCameraFlash()
    {
    	if(this.mCamera == null) return;
    	Camera.Parameters parameter = mCamera.getParameters();  
    	String flashmode = parameter.getFlashMode();
    	if(flashmode == null) 
    	{
    		Log.i(tag, "the camera do't have flash.");
    		return;
    	}
    	parameter.setFlashMode((flashmode.compareTo(Camera.Parameters.FLASH_MODE_OFF)==0)
    			?Camera.Parameters.FLASH_MODE_TORCH:Camera.Parameters.FLASH_MODE_OFF);
    	
    	mCamera.setParameters(parameter);
    }
    
    public void setAutoStart(Boolean bAuto)
    {
    	autoStart = bAuto;
    }
    
    public int getCameraFacting()
    {
    	if(this.mCameraID < 0) return -1;
        CameraInfo cameraInfo = new CameraInfo();
        Camera.getCameraInfo(this.mCameraID, cameraInfo);
    	return cameraInfo.facing;
    }
    
    
    public Boolean isRecoding()
    {
    	return mIsRecoding;
    }
    
    //option camera  *************end.
    
    //about audio recoder. *********begin.
    public void openAudioRecoder()
    {
    	synchronized(this) {
    		if(mAudioRecord != null) 
    		{
    			Log.i(tag, "mAudioRecord is exit.");
    			return;
    		}
           // 获得缓冲区字节大小 
	       bufferSizeInBytes = AudioRecord.getMinBufferSize(EffectType.SAMPLERATE_IN_HZ, 
	    		   EffectType.CHANNEL_CONFIG, EffectType.AUDIO_FORMAT); 
	       // 创建AudioRecord对象 
	       mAudioRecord = new AudioRecord(EffectType.KAUDIO_SOURCE, EffectType.SAMPLERATE_IN_HZ, 
	    		   EffectType.CHANNEL_CONFIG, EffectType.AUDIO_FORMAT, bufferSizeInBytes*2); 
	       
	       // 设置线程控制变量。
	       mCloseAudioRecodeThread = false; 
	       // 开启音频文件写入线程 
	       mAudioRecodeThread = new Thread(new AudioRecordThread());
	       mAudioRecodeThread.start();
    	}
    }
    
    public void closeAudioRecoder()
    {
    	synchronized(this) {
    		//等待采集线程关闭  属于线程安全性问题。
	    	mCloseAudioRecodeThread = true;
	    	try
	    	{
	    		mAudioRecodeThread.join();
	    	}catch (InterruptedException e) {
	    		throw new RuntimeException(e);
	    	}
	    	
	        if (mAudioRecord != null) {  
	            mAudioRecord.stop(); 
	            mAudioRecord.release();//释放资源 
	            mAudioRecord = null; 
	        }
    	}
    }
    
    public void startAudioRecoder()
    {
    	synchronized(this) {
    		if(mAudioRecord != null)
    		{
    			mAudioRecord.startRecording(); 
    		}else{
    			Log.i(tag, "mAudioRecord is null.");
    		}
    	}
    }
    
    public void stopAudioRecoder()
    {
    	synchronized (this) {
    		if(mAudioRecord != null)
    		{
    			mAudioRecord.stop();
    		}else{
    			Log.i(tag, "mAudioRecord is null.");
    		}
		}
    }
    
    class AudioRecordThread implements Runnable { 

        @Override 
        public void run() { 
        	//read audio data from recoder memory.
            byte[] audiodata = new byte[bufferSizeInBytes]; 
            int readsize = 0;  
            while (!mCloseAudioRecodeThread) { 
                readsize = mAudioRecord.read(audiodata, 0, bufferSizeInBytes); 
                //Log.i(tag, "readsize=" + readsize);
                if (AudioRecord.ERROR_INVALID_OPERATION != readsize) { 
                	//Log.i(tag, "readsize=" + readsize);
                	if(readsize > 0 && audiocallback != null) {
                		audiocallback.onAudioFrame(audiodata, readsize);
                	}
                } 
            } 
        } 
    }     
    //about andio recoder. *********end.
    
    //media recoder.*********begin.
    public void startRecod()
    {
    	if(!isRecoding())
    	{
    		startCamera();
    		startAudioRecoder();
    		mIsRecoding = true;
    	}
    }
    
    public void stopRecod()
    {
    	if(isRecoding())
    	{
    		stopCamera();
    		stopAudioRecoder();
    		mIsRecoding = false;
    	}
    }
    //media recoder.*********end.
    

	public void onPreviewFrame(byte[] arg0, Camera arg1) {
    	if (callback!=null)
    		callback.onPreviewFrame(arg0, arg1);        
	}
	
    public interface AudioCallback
    {
    	void onAudioFrame(byte[] data, int nsize);
    }
    
}
