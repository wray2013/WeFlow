package effect;

import android.util.Log;

public class transCode {
	String tag = "transCode";
    //about media file.
    String                  mfilepath;
    
    //paramer changed by c.****************begin.
    private long                    mpfile;
    private long					moutfile;
    
	private int 					mWidth = 0;
	private int 					mHeight = 0;
    
    private int                     mvideoindex;
    private int                     maudioindex;
    
    private int 					mCurrIndex;
    
	private double 					mvCurrTimestamp;
	private double 					maCurrTimestamp;
	
	private double 					mTotalTime;			//总时间
    //paramer changed by c.****************end.
    
    int                     mopen;
    private Boolean         mwriteing = false;
    
    int                     mstop;
    
    Effects					meffectObj = null;
    
    //thread save the stream.
	private Thread mWriteThread;
	private Boolean mCloseThread = false;
    
	
	//currment transcode time.
	private double					mCurrTimestamp;
	
	private OnScheduleListener		mScheduleListener;

    public void StartSave(String infilename, String outfilename, Effects obj, OnScheduleListener listener)
    {
    	if(mwriteing) return;
    	//open infile and outfile.
    	if(native_StartSave(infilename, outfilename) != 0)
    	{
    		Log.i(tag, "native_StartSave error.");
    		return;
    	}
    	
    	meffectObj = obj;
    	mScheduleListener = listener;
    	mCurrTimestamp = 0;
    	mwriteing = true;
    	//create and start write thread... ...begin.
    	mCloseThread = false;
    	mWriteThread = new Thread(new WriteRunnable());
    	mWriteThread.setPriority(Thread.MIN_PRIORITY);
    	mWriteThread.start();
    	//create write thread... ...end.
    }
    
    public void StopSave()
    {
		mCloseThread = true;
		Log.d(tag, "stop transcode thread begin.");
    	try
    	{
    		mWriteThread.join();
    	}catch (InterruptedException e) {
    		throw new RuntimeException(e);
    	}
    	Log.d(tag, "stop transcode thread end.");
    	
    	native_StopSave();
    	mwriteing = false;
    }
    
    public Boolean IsWriteing()
    {
    	return mwriteing;
    }
    
    public interface OnScheduleListener{
    	void OnSchedule(double percent);
    	void OnFinish();
    }
    
    class WriteRunnable implements Runnable{
        @Override 
        public void run() { 
        	Log.d(tag, "WriteThread begin.");
        	while(!mCloseThread)
        	{
        		//Log.i(tag, "writing... ...");
        		byte[] streamData = native_GetNextPacket();
        		if(streamData == null)
        		{
        			//Log.i(tag, "finish......");
        			native_StopSave();
        			if(mScheduleListener != null)
        			{
        				mScheduleListener.OnFinish();
        			}
        			
        			mwriteing = false;
        			break;
        		}
        		
        		double CurrTimestamp = 0.0;
        		if(mCurrIndex == mvideoindex){
        			//Log.i(tag, "video.......");
        			byte[] tData = new byte[mWidth*mHeight*4];
        			if(meffectObj != null) {
        				meffectObj.native_ProcessVideoRGBA(streamData, mWidth, mHeight, tData);
        			}
        			native_WriteVideo(tData);
        			CurrTimestamp = mvCurrTimestamp;
        		}else if(mCurrIndex == maudioindex){
        			//Log.i(tag, "andio.......");
        			if(meffectObj != null) {
        				meffectObj.native_ProcessAudio(streamData, streamData.length);
        			}
        			native_WriteAudio(streamData);
        			CurrTimestamp = maCurrTimestamp;
        		}
        		
        		if(CurrTimestamp > mCurrTimestamp)
        		{
        			mCurrTimestamp = CurrTimestamp;
        			if(mScheduleListener != null)
        			{
        				mScheduleListener.OnSchedule(mCurrTimestamp/mTotalTime);
        			}
        		}
        		
        	}
        	Log.d(tag, "WriteThread End.");
        }
    }
    
    
    
    //native function...
    public native int native_StartSave(String infilename, String outfilename);
    public native int native_StopSave();
    
    public native byte[] native_GetNextPacket();
    
    public native int native_WriteVideo(byte[]data);
    public native int native_WriteAudio(byte[]data);
    
}
