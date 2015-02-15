package xMediaPlayer;

import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class XAudioPlayer {
	String tag = "XAudioPlayer";
	//用来一个缓冲区播放完成后通知
	private Handler mNotifyHandler;
	private PlayerBuffer[] mBuffers;
	
	//about audio play.*********begin.
	private AudioTrack mAudioTrack;
	private Thread mWritePCMThread;
	private Boolean mClosePCMThread = false;
	private int mBufferIndex = 0;
	private int mBufferSize = 0;
	//about audio play.*********end.
	
	//the player status.****begin.
	static final int A_STATUS_UNKNOW = 0;
    static final int A_STATUS_OPENED = 1;
    static final int A_STATUS_PALYING = 2;
    static final int A_STATUS_STOP = 3;
    static final int A_STATUS_PAUSE = 4;
    
    private int mA_Play_Status = A_STATUS_UNKNOW;
    //the player status.****end.
    
    //the handler id begin.
    static final int A_MSG_ID_PLAY_DONE = 0;
    //the handler id end.
    public double maCurrTimestamp;
	
	public XAudioPlayer(Handler notify)
	{
		setmNotifyHandler(notify);
	}

	//set handler.**********begin.
	public Handler getmNotifyHandler() {
		return mNotifyHandler;
	}

	public void setmNotifyHandler(Handler mNotifyHandler) {
		this.mNotifyHandler = mNotifyHandler;
	}
	//set handler.*********end.
	
	//
	public int getAudioPlayStatus()
	{
		return mA_Play_Status;
	}
	
	
	//about audio buffer array.*********begin.
	//create buffers for player.
	public void createPlayerBuffers(int num, int size)
	{
		mBuffers = new PlayerBuffer[num];
		mBufferSize = size;
		for(int i = 0; i < num; i++)
		{
			mBuffers[i] = new PlayerBuffer(size,i); 
		}
	}
	
	public int getBufferNum()
	{
		if(mBuffers != null)
		{
			return mBuffers.length;
		}
		
		return 0;
	}
	
	public int getBufferSize()
	{
		return mBufferSize;
	}
	
	public void fullBufferWithIndex(byte[] buff, int index, Double Timestamp)
	{
		PlayerBuffer tplaybuffer = mBuffers[index];
		tplaybuffer.fullBuffer(buff, Timestamp);
	}
	
	class PlayerBuffer{
		public byte[] mBuffer;		//audio pcm data.
		public int mIndex;
		public Boolean mReady;		//data is ready.
		public double maTimestamp;
		public PlayerBuffer(int size, int index)
		{
			mBuffer = new byte[size];
			mIndex = index;
			mReady = false;
		}
		
		public void fullBuffer(byte[] buff, Double Timestamp)
		{
			int len = buff.length <= mBuffer.length?buff.length:mBuffer.length;
			System.arraycopy(buff, 0, mBuffer, 0, len);
			this.maTimestamp = Timestamp;
			mReady = true;
		}
	}
	//about audio buffer array.*********end.
	
	//play andio buffer and callback.********begin.
	public int open(int SampleRate, int Channals, int AFmt)
	{

		int  minBufSize = AudioTrack.getMinBufferSize(SampleRate,   
										                Channals,  
										                AFmt);  
		
		mAudioTrack =  new  AudioTrack(AudioManager.STREAM_MUSIC,  
										SampleRate,   
										Channals,  
										AFmt,   
										minBufSize,  
										AudioTrack.MODE_STREAM);  
		
		mA_Play_Status = A_STATUS_OPENED;
		return minBufSize;

	}
	
	public void start()
	{
		if(A_STATUS_OPENED != mA_Play_Status) return;
		
		mClosePCMThread = false;
		mWritePCMThread = new Thread(new AudioWriteThread());
		mWritePCMThread.setPriority(Thread.NORM_PRIORITY);
		
		mAudioTrack.play();
		mWritePCMThread.start();
		mA_Play_Status = A_STATUS_PALYING;
	}
	
	public void stop()
	{
		mClosePCMThread = true;
		Log.d(tag, "stop audio play thread begin.");
    	try
    	{
    		mWritePCMThread.join();
    	}catch (InterruptedException e) {
    		throw new RuntimeException(e);
    	}
    	Log.d(tag, "stop audio play thread end.");
    	mAudioTrack.stop();
    	mA_Play_Status = A_STATUS_STOP;
	}
	
	public void pause()
	{
		if(mA_Play_Status == A_STATUS_PALYING)
		{
			mAudioTrack.pause();
			mA_Play_Status = A_STATUS_PAUSE;
		}
	}
	
	/**
	 * if use pause use restart to replay. 
	 */
	public void restart()
	{
		if(mA_Play_Status == A_STATUS_PAUSE)
		{
			mAudioTrack.play();
			mA_Play_Status = A_STATUS_PALYING;
		}
	}
	
   class AudioWriteThread implements Runnable { 

        @Override 
        public void run() { 
        	Log.d(tag, "AudioWriteThread begin.");
        	while(!mClosePCMThread)
        	{
        		if(mBufferIndex >= mBuffers.length) mBufferIndex = 0;
        		
        		if(mA_Play_Status == A_STATUS_PAUSE) 
        		{
		      		 try  {
		      			 Thread.sleep(5);
		             }  catch  (Exception e) {  
		            	 e.printStackTrace();   
		             } 
		      		 continue;
        		}
        		
        		if(mBuffers[mBufferIndex].mReady)
        		{
        			mAudioTrack.write(
        					mBuffers[mBufferIndex].mBuffer, 
        					0, 
        					mBuffers[mBufferIndex].mBuffer.length);
        			
        			mBuffers[mBufferIndex].mReady = false;
        			mNotifyHandler.sendMessage(
        					Message.obtain(
        					mNotifyHandler, 
        					A_MSG_ID_PLAY_DONE, 
        					mBuffers[mBufferIndex]));
        			maCurrTimestamp = mBuffers[mBufferIndex].maTimestamp;
        		}else{
		      		 try  {
		      			 Thread.sleep(5);
		             }  catch  (Exception e) {  
		            	 e.printStackTrace();   
		             } 
		      		 continue;
        		}
        		
        		mBufferIndex++;
        	}
        	Log.d(tag, "AudioWriteThread End.");
        } 
    } 
   
	//play andio buffer and callback.********end.
	
	
}
