package nativeInterface;



import java.io.Serializable;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class SmoAdMultiPlay {
	
	
	public final  static int CLIP_MAX_LEVEL=16;
	public final  static int AD_MAX_LEVEL=16;

	
	public final  static int PARSE_SMO_SUCCESS=1;
	public final  static int PARSE_SMO_ERROR=-2;
	public final  static int PARSE_SMO_SERVER_ERROR=-1;
	public final  static int PARSE_SMO_GET_ERROR=0;
	
	public final  static int AD_TYPE_START=1;
	public final  static int AD_TYPE_BUFFER=2;
	public final  static int AD_TYPE_PAUSE=3;
	public final  static int AD_TYPE_END=4;
	
	public final  static int AD_SHOW_FULL=1;
	public final  static int AD_SHOW_CENTER=2;
	
		

	public  MultiPlayObj multiPlayObj;
	public  ADDesc [] ad;	
	private  int ad_num;
	private Context mcontext;
	
	private Handler handler;
	private int smo_handler_tag;
	private ParseSmoCompleteListener parseSmoCompleteListener;
	
	public ParseSmoCompleteListener getParseSmoCompleteListener() {
		return parseSmoCompleteListener;
	}

	public void setParseSmoCompleteListener(
			ParseSmoCompleteListener parseSmoCompleteListener) {
		this.parseSmoCompleteListener = parseSmoCompleteListener;
	}

	static {
		System.loadLibrary("tivc7dec");
		System.loadLibrary("FFMPEG");
		System.loadLibrary("rmh265dec");
		System.loadLibrary("hsmplayerjni");
	}
		
	public SmoAdMultiPlay(Context context){
		super();			
		initHandler(null,context);
	}
	
	public SmoAdMultiPlay(String smoplayaddress,Context context){
		super();
		initHandler(smoplayaddress,context);
	}
	
	private void initHandler(String smoaddress,Context context){
		this.mcontext=context;
		 handler=new Handler(){
				public void handleMessage(Message msg) {
					if(msg.what==1){
						getSmoClientFromJni();
					}
					if(parseSmoCompleteListener!=null){
						parseSmoCompleteListener.parSmoComplete(msg.what);
					}
				}		
			};
		if(smoaddress!=null){
			smo_handler_tag=createSmoClient(smoaddress);
		}	
		
//		multiPlayObj=new MultiPlayObj();
//		multiPlayObj.levelname=new String[CLIP_MAX_LEVEL];
//		multiPlayObj.levelurl=new String[CLIP_MAX_LEVEL];
//		multiPlayObj.playname="Title";
//		for(int i=0;i<16;i++){
//			multiPlayObj.levelname[i]=""+i;
//			multiPlayObj.levelurl[i]="http://192.168.1.86/vod/ad3/index.hsm";
//		}
//		Message message = new Message();	
//		message.what = 1;
//		handler.sendMessage(message);	
	}
	
	
	//s== 1,成功
	//s==-2,获取到smo，但是解析不了数据
	//s==-1,无法连接服务器
	//s==0,服务器返回错误，404
	public void JNI_Callback(int handle,int result) {
		if(handle==smo_handler_tag){
				Message message = new Message();	
				message.what = result;
				handler.sendMessage(message);	
		}
	}
	
	public native int  createSmoClient(String smoaddress);
	public native void getSmoClient(int h);	
	public native void releaseSmoClient(int h);	
	
	
	public void getSmoClientFromJni(){
		if(smo_handler_tag!=0){
			getSmoClient(smo_handler_tag);			
		}
	}
	
	public void releaseSMOClientFromFjni(){
		if(smo_handler_tag!=0){
			releaseSmoClient(smo_handler_tag);
		}
	}
	
	
	public class MultiPlayObj{
		public String playname;
		public String []levelname;
		public String []levelurl;	
		public MultiPlayObj(){
		}
	}

	public class ADDesc implements Serializable{

		public MultiPlayObj multiPlayAddress;
		public int ad_time;
		public int duration;
		public String action_url;
		public int ad_type;
		public int ad_show_mode;
		public ADDesc(){
		}
	}
	
	public interface ParseSmoCompleteListener{
		void parSmoComplete(int result);
	}
}
